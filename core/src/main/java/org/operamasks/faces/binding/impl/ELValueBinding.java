/*
 * $Id: ELValueBinding.java,v 1.8 2008/01/31 04:12:24 daniel Exp $
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UISelectOne;
import javax.faces.component.UISelectMany;
import javax.faces.event.PhaseId;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.el.ELContext;
import javax.el.ValueExpression;

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.factories.CustomizingConverterFactory;
import org.operamasks.faces.binding.factories.CustomizingValidatorFactory;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.binding.impl.BindingUtils.*;

class ELValueBinding extends Binding
{
    private Closure closure;
    private Class<?> type;
    private String id;
    private String attribute;
    private ConverterFactory converterFactory;
    private String converterMessage;
    private List<ValidatorFactory> validators;
    private boolean required;
    private String requiredMessage;
    private ELLocalStringBinding localString;
    private ELSelectItemsBinding selectItems;

    ELValueBinding(Closure closure, Class<?> type, String id, String attribute) {
        super(null);
        this.closure = closure;
        this.type = type;
        this.id = id;
        this.attribute = attribute;
    }

    public Closure getClosure() {
        return this.closure;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getAttribute() {
        return this.attribute;
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

    public ELLocalStringBinding getLocalString() {
        return this.localString;
    }

    public void setLocalString(ELLocalStringBinding binding) {
        this.localString = binding;
    }

    public ELSelectItemsBinding getSelectItems() {
        return this.selectItems;
    }

    public void setSelectItems(ELSelectItemsBinding binding) {
        this.selectItems = binding;
    }

    @Override
    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        UIComponent comp = mbc.getComponent(this.id);
        if (comp == null) {
            return;
        }

        PhaseId phaseId = mbc.getPhaseId();
        ELiteBean bean = (ELiteBean)mbc.getModelBean();

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

            ValueExpression binding = new ClosureValueAdapter(closure, null);
            adapter.addValueBinding(binding);

            ELContext elctx = ctx.getELContext();
            if ("binding".equals(this.attribute)) {
                UIComponent oldValue = (UIComponent)binding.getValue(elctx);
                if (oldValue != null && oldValue != comp) {
                    comp.restoreState(ctx, oldValue.saveState(ctx));
                }
                binding.setValue(elctx, comp);
            }
        }

        if ("value".equals(this.attribute)) {
            if (comp instanceof ValueHolder) {
                applyConverter((ValueHolder)comp);
                if (comp instanceof EditableValueHolder) {
                    applyValidators(phaseId, (EditableValueHolder)comp);
                }
            }
        }

        if (selectItems != null) {
            if ((comp instanceof UISelectOne) || (comp instanceof UISelectMany)) {
                selectItems.apply(comp, bean);
            }
        }

        UIComponent label = findLabelComponent(ctx, comp);
        if (label != null)
            setText(label, bean, "label", "value");
        setText(comp, bean, "description", "title");
    }

    private void applyConverter(ValueHolder comp) {
        ConverterFactory factory = this.converterFactory;
        if (factory == null) {
            return;
        }

        Converter previous = comp.getConverter();
        if ((previous == null) || (previous instanceof ConverterAdapter)) {
            // create converter from meta data
            Converter converter;
            if (factory instanceof CustomizingConverterFactory) {
                converter = ((CustomizingConverterFactory)factory).createConverter(null, this.type);
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

    private void applyValidators(PhaseId phaseId, EditableValueHolder comp) {
        if (this.required) {
            comp.setRequired(true);
        }

        if (this.validators != null) {
            CompositeValidator composite = CompositeValidator.getCompositeValidator(comp, phaseId);
            for (ValidatorFactory vf : this.validators) {
                Validator validator;
                if (vf instanceof CustomizingValidatorFactory) {
                    validator = ((CustomizingValidatorFactory)vf).createValidator(null);
                } else {
                    validator = vf.createValidator();
                }
                if (validator != null) {
                    composite.addValidator(validator);
                }
            }
        }
    }

    private void setText(UIComponent comp, ELiteBean bean, String key, String attribute) {
        if (comp.getValueExpression(attribute) == null) {
            String text = bean.getLocalString(this.id + "." + key);
            if (text == null && key.equals("label")) {
                text = FacesUtils.toCamelCase(this.id);
            }

            if (text != null) {
                ValueExpression ve = createValueWrapper(null, text, String.class);
                comp.setValueExpression(attribute, ve);
            }
        }
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        if (localString != null) {
            if (closure.getValue(ctx.getELContext()) == null) {
                ELiteBean bean = (ELiteBean)mbc.getModelBean();
                Object value = localString.getValue(bean);
                if (value != null) {
                    closure.setValue(ctx.getELContext(), value);
                }
            }
        }
    }
}
