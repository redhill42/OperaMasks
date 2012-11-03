/*
 * $Id: RequestParamBinding.java,v 1.6 2007/10/28 08:03:16 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.convert.Converter;
import javax.faces.component.UIInput;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.validator.Validator;
import javax.el.ValueExpression;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.factories.CustomizingConverterFactory;
import org.operamasks.faces.binding.factories.CustomizingValidatorFactory;
import static org.operamasks.faces.util.FacesUtils.*;

class RequestParamBinding extends PropertyBinding
{
    private String id;
    private ConverterFactory converterFactory;
    private String converterMessage;
    private List<ValidatorFactory> validators;
    private boolean required;
    private String requiredMessage;

    RequestParamBinding(String viewId) {
        super(viewId);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
            this.validators = new ArrayList<ValidatorFactory>();
        }
        this.validators.add(factory);
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

    public void applyRequestValue(FacesContext ctx, UIComponent comp) {
        String requestValue = ctx.getExternalContext().getRequestParameterMap().get(this.id);
        if (requestValue  == null) {
            return;
        }

        ((EditableValueHolder)comp).setSubmittedValue(requestValue);
        comp.processValidators(ctx);
        comp.processUpdates(ctx);
    }

    @Override
    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        if (mbc.getPhaseId() == PhaseId.RENDER_RESPONSE && !isPostback(ctx)) {
            UIComponent adapter = createAdapter(ctx, mbc.getModelBean());

            ctx.getViewRoot().getChildren().add(adapter);
            try {
                applyRequestValue(ctx, adapter);
            } finally {
                ctx.getViewRoot().getChildren().remove(adapter);
            }
        }
    }

    private UIComponent createAdapter(FacesContext ctx, ModelBean bean) {
        UIInput adapter = new UIInput();

        adapter.setId(this.id);
        adapter.setValueExpression("value", new PropertyValueAdapter(this, bean));

        addConverter(ctx, adapter, bean);
        addValidators(adapter, bean);

        return adapter;
    }

    private void addConverter(FacesContext ctx, UIInput adapter, ModelBean bean) {
        ConverterFactory factory = this.converterFactory;
        Converter converter = null;

        if (factory != null) {
            if (factory instanceof CustomizingConverterFactory) {
                converter = ((CustomizingConverterFactory)factory).createConverter(bean, this.type);
            } else {
                converter = factory.createConverter(this.type);
            }
        } else if (this.type != String.class && this.type != Object.class) {
            converter = ctx.getApplication().createConverter(this.type);
        }

        if (converter != null) {
            adapter.setConverter(converter);
        }

        setText(adapter, bean, this.converterMessage, "converterMessage");
    }

    private void addValidators(UIInput adapter, ModelBean bean) {
        if (this.required) {
            adapter.setRequired(true);
            setText(adapter, bean, this.requiredMessage, "requiredMessage");
        }

        if (this.validators != null) {
            for (ValidatorFactory factory : this.validators) {
                Validator validator;
                if (factory instanceof CustomizingValidatorFactory) {
                    validator = ((CustomizingValidatorFactory)factory).createValidator(bean);
                } else {
                    validator = factory.createValidator();
                }

                if (validator != null) {
                    adapter.addValidator(validator);
                }
            }
        }

        setText(adapter, bean, null, "validatorMessage");
    }

    private void setText(UIComponent comp, ModelBean bean, String text, String messageId) {
        if (text == null || text.length() == 0) {
            text = getLocalString(getDeclaringClass(), this.getName() + "." + messageId);
        }

        if (text != null) {
            ValueExpression ve = BindingUtils.createValueWrapper(bean, text, String.class);
            comp.setValueExpression(messageId, ve);
        }
    }
}
