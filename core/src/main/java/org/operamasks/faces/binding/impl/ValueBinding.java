/*
 * $Id: ValueBinding.java,v 1.24 2008/04/11 03:16:06 patrick Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.operamasks.faces.binding.impl;

import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIData;
import javax.faces.component.UISelectOne;
import javax.faces.component.UISelectMany;
import javax.faces.convert.Converter;
import javax.faces.event.PhaseId;
import javax.faces.validator.Validator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;

import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.factories.CustomizingValidatorFactory;
import org.operamasks.faces.binding.factories.CustomizingConverterFactory;
import org.operamasks.faces.component.widget.grid.UIOutputColumn;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.util.FacesUtils.*;
import static org.operamasks.faces.binding.impl.BindingUtils.*;

class ValueBinding extends PropertyBinding
{
    private String id;
    private String attribute;
    private String expr;
    private String label;
    private String description;
    private ConverterFactory converterFactory;
    private String converterMessage;
    private List<ValidatorFactory> validators;
    private boolean required;
    private String requiredMessage;
    private LocalStringBinding localString;
    private SelectItemsBinding selectItems;
    private RequestParamBinding requestParam;
    private ModelSecurity security;

    ValueBinding(String viewId) {
        super(viewId);
    }

    @Override
    public void init(Class<? extends Annotation> metaType, Class<?> targetClass, AnnotatedElement elem) {
        super.init(metaType, targetClass, elem);
        this.security = ModelSecurity.scan(elem);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getExpression() {
        return this.expr;
    }

    public void setExpression(String expr) {
        this.expr = expr;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConverterFactory getConverterFactory() {
        return this.converterFactory;
    }

    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public String getConverterMessage() {
        return this.converterMessage;
    }

    public void setConverterMessage(String message) {
        this.converterMessage = message;
    }

    public void addValidatorFactory(ValidatorFactory factory) {
        if (this.validators == null) {
            validators = new ArrayList<ValidatorFactory>();
        }
        validators.add(factory);
    }

    public List<ValidatorFactory> getValidatorFactories() {
        if (this.validators == null) {
            return Collections.emptyList();
        } else {
            return this.validators;
        }
    }
    
    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRequiredMessage() {
        return this.requiredMessage;
    }

    public void setRequiredMessage(String message) {
        this.requiredMessage = message;
    }

    public LocalStringBinding getLocalString() {
        return this.localString;
    }

    public void setLocalString(LocalStringBinding binding) {
        this.localString = binding;
    }

    public SelectItemsBinding getSelectItems() {
        return this.selectItems;
    }

    public void setSelectItems(SelectItemsBinding binding) {
        this.selectItems = binding;
    }

    public RequestParamBinding getRequestParam() {
        return this.requestParam;
    }

    public void setRequestParam(RequestParamBinding requestParam) {
        this.requestParam = requestParam;
    }

    @Override
    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        UIComponent comp = mbc.getComponent(this.id);
        if (comp == null) {
            return;
        }

        PhaseId phaseId = mbc.getPhaseId();
        ModelBean bean = mbc.getModelBean();

        ValueExpression previous = comp.getValueExpression(this.attribute);
        if (previous instanceof CompositeDataItemAdapter) {
            return; // a data item already bound
        }

        if ((previous == null) || (previous instanceof CompositeValueAdapter)) {
            CompositeValueAdapter adapter;
            if ((previous == null) || (phaseId != ((CompositeValueAdapter)previous).getPhaseId())) {
                adapter = new CompositeValueAdapter();
                adapter.setPhaseId(phaseId);
                comp.setValueExpression(this.attribute, adapter);
            } else {
                adapter = (CompositeValueAdapter)previous;
            }

            ValueExpression binding = new PropertyValueAdapter(this, bean);
            adapter.addValueBinding(binding);

            ELContext elctx = ctx.getELContext();
            if ("binding".equals(this.attribute)) {
                binding.setValue(elctx, comp);
            } else if (this.expr != null && binding.getValue(elctx) == null) {
                Object value = bean.evaluateExpression(this.expr, this.type);
                binding.setValue(elctx, value);
            }
        }

        if ("value".equals(this.attribute)) {
            if (comp instanceof ValueHolder) {
                applyConverter(bean, (ValueHolder)comp);
                if (comp instanceof EditableValueHolder) {
                    applyValidators(bean, phaseId, (EditableValueHolder)comp);
                }
            }
        }

        if (this.requestParam != null && phaseId == PhaseId.RENDER_RESPONSE && !isPostback(ctx)) {
            if ((comp instanceof EditableValueHolder) && "value".equals(this.attribute)) {
                this.requestParam.applyRequestValue(ctx, comp);
            } else {
                this.requestParam.applyGlobal(ctx, mbc);
            }
        }

        if (this.selectItems != null) {
            // TODO: check type
            //if ((comp instanceof UISelectOne) || (comp instanceof UISelectMany)) {
                this.selectItems.apply(comp, bean);
            //}
        }
        
        if (comp instanceof UIInput) {
        	UIInput in = (UIInput) comp;
        	String msg = this.getRequiredMessage();
        	if (msg != null && msg.length() > 0 && in.getRequiredMessage() == null) {
        		in.setRequiredMessage(convertStringValue(bean, this.getRequiredMessage()));
        	}
        }

        UIComponent labelComp = findLabelComponent(ctx, comp);
        if (labelComp != null) {
            setText(labelComp, bean, this.label, "label", "value");
        }
        setText(comp, bean, this.description, "description", "title");
        
        if (this.security != null && comp.getValueExpression("disabled") == null) {
            boolean disabled = !this.security.isUserInRole(ctx);
            comp.setValueExpression("disabled", new ValueWrapper(disabled, Boolean.TYPE));
            if (labelComp != null) {
                labelComp.setValueExpression("disabled", new ValueWrapper(disabled, Boolean.TYPE));
            }
        }
    }
    
    private String convertStringValue(ModelBean scope, String value) {
        if (scope != null) {
            return (String) scope.evaluateExpression(value, String.class);
        } else {
            return (String) FacesUtils.evaluateExpressionGet(value, String.class);
        }
    }

    @Override
    public void applyDataItem(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        UIComponent comp = mbc.getComponent(this.id);
        if (comp == null) {
            return;
        }

        PhaseId phaseId = mbc.getPhaseId();

        ValueExpression previous = comp.getValueExpression(this.attribute);
        if ((previous == null) || (previous instanceof CompositeDataItemAdapter)) {
            CompositeDataItemAdapter adapter;
            if ((previous == null) || (phaseId != ((CompositeDataItemAdapter)previous).getPhaseId())) {
                adapter = new CompositeDataItemAdapter(data);
                adapter.setPhaseId(phaseId);
                comp.setValueExpression(this.attribute, adapter);
            } else {
                adapter = (CompositeDataItemAdapter)previous;
            }

            adapter.addDataItem(new PropertyDataItem(this));
        }

        if ("value".equals(this.attribute)) {
            if (comp instanceof ValueHolder) {
                applyConverter(null, (ValueHolder)comp);
                if (comp instanceof EditableValueHolder) {
                    applyValidators(null, phaseId, (EditableValueHolder)comp);
                }
            }
        }

        ValueExpression rowData = new RowDataValueAdapter(data);
        if (comp instanceof UIOutputColumn) {
            setText(comp, rowData, this.label, "label", "columnHeader");
        } else {
            UIComponent labelComp = findLabelFacet(ctx, comp, true);
            if (labelComp != null) {
                setText(labelComp, rowData, this.label, "label", "value");
            }
        }
        setText(comp, rowData, this.description, "description", "title");

        if (this.security != null && comp.getValueExpression("disabled") == null) {
            boolean disabled = !this.security.isUserInRole(ctx);
            comp.setValueExpression("disabled", new ValueWrapper(disabled, Boolean.TYPE));
        }
    }

    protected void applyConverter(ModelBean bean, ValueHolder comp) {
        ConverterFactory factory = this.converterFactory;
        if (factory == null) {
            return;
        }

        Converter previous = comp.getConverter();
        if ((previous == null) || (previous instanceof ConverterAdapter)) {
            // create converter from meta data
            Converter converter;
            if (factory instanceof CustomizingConverterFactory) {
                converter = ((CustomizingConverterFactory)factory).createConverter(bean, this.type);
            } else {
                converter = factory.createConverter(this.type);
            }

            if (converter != null) {
                ConverterAdapter adapter;
                if (previous == null) {
                    adapter = new ConverterAdapter();
                    comp.setConverter(adapter);
                } else {
                    adapter = (ConverterAdapter)previous;
                }
                adapter.setFallback(converter);
            }
        }
    }

    protected void applyValidators(ModelBean bean, PhaseId phaseId, EditableValueHolder comp) {
        if (this.required) {
            comp.setRequired(true);
        }

        if (this.validators != null) {
            CompositeValidator composite = CompositeValidator.getCompositeValidator(comp, phaseId);

            for (ValidatorFactory factory : this.validators) {
                Validator validator;
                if (bean != null && (factory instanceof CustomizingValidatorFactory)) {
                    validator = ((CustomizingValidatorFactory)factory).createValidator(bean);
                } else {
                    validator = factory.createValidator();
                }

                if (validator != null) {
                    composite.addValidator(validator);
                }
            }
        }
    }

    private void setText(UIComponent comp, Object scope, String text, String messageId, String attribute) {
        if (comp.getValueExpression(attribute) == null) {
            if (text == null || text.length() == 0) {
                text = FacesUtils.getLocalString(getDeclaringClass(), this.id + "." + messageId);
                if (text == null && messageId.equals("label")) {
                    text = FacesUtils.toCamelCase(this.id); // for debugging perpose
                }
            }

            if (text != null) {
                ValueExpression ve = createValueWrapper(scope, text, String.class);
                comp.setValueExpression(attribute, ve);
            }
        }
    }

    @Override
    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        if (this.isWriteable() && this.localString != null) {
            ModelBean bean = mbc.getModelBean();
            if (getModelValue(bean) == null) {
                Object value = this.localString.getValue(bean);
                if (value != null) {
                    setModelValue(bean, value);
                }
            }
        }
    }
}
