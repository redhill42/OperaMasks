/*
 * $Id: ValidatorAdapter.java,v 1.4 2007/10/19 02:07:58 daniel Exp $
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

import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.StateHolder;
import javax.faces.application.FacesMessage;

import java.lang.reflect.Method;
import org.operamasks.faces.validator.ClientValidator;
import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.binding.ModelBean;
import static org.operamasks.faces.util.FacesUtils.*;
import static org.operamasks.resources.Resources.*;

public class ValidatorAdapter implements Validator, ClientValidator, StateHolder
{
    private ModelBean bean;
    private Method method;
    private String script;
    private String message;

    public ValidatorAdapter(ModelBean bean, Method method, String script, String message) {
        this.bean = bean;
        this.method = method;
        this.script = script;
        this.message = message;
    }

    public ModelBean getModelBean() {
        return this.bean;
    }

    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        if (value != null) {
            this.bean.inject(context);

            Object result = null;

            try {
                Class[] paramTypes = this.method.getParameterTypes();
                if (paramTypes.length == 1) {
                    // single argument method
                    value = coerce(value, paramTypes[0]);
                    result = this.bean.invoke(this.method, value);
                } else if (paramTypes.length == 3) {
                    // standard validate method:
                    value = coerce(value, paramTypes[2]);
                    result = this.bean.invoke(this.method, context, component, value);
                }
            } catch (ValidatorException ex) {
                throw ex;
            } catch (Exception ex) {
                String errInfo = ex.getMessage();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                        errInfo,
                                                        errInfo);
                throw new ValidatorException(message, ex.getCause());
            }

            if (result != null) {
                // Simplify validator implementation.
                if (result instanceof String) {
                    // If the method returns a string value then throw
                    // ValidatorException with the returned string value
                    // as validation message.
                    String messageStr = (String)result;
                    if (isValueExpression(messageStr)) {
                        messageStr = this.bean.evaluateExpression(messageStr, String.class);
                    }
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                            messageStr, messageStr);
                    throw new ValidatorException(message);
                } else if (Boolean.FALSE.equals(result)) {
                    // If the method returns "false" then throw ValidatorException
                    // with the message configured in the meta data.
                    String messageStr = this.message;
                    if (messageStr == null || messageStr.length() == 0) {
                        messageStr = (String)component.getAttributes().get("validatorMessage");
                    } else if (isValueExpression(messageStr)) {
                        messageStr = this.bean.evaluateExpression(messageStr, String.class);
                    }
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                            messageStr, messageStr);
                    throw new ValidatorException(message);
                }
            }
        }
    }

    private Object coerce(Object value, Class type) {
        if ((value != null && type != value.getClass()) ||
            (value == null && type.isPrimitive())) {
            value = Coercion.coerce(value, type);
        }
        return value;
    }

    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        if (this.script != null && this.script.length() != 0) {
            String message = this.message;
            if (message == null || message.length() == 0) {
                message = (String)component.getAttributes().get("validatorMessage");
                if (message == null) {
                    message = _T(JSF_CLIENT_VALIDATE_ERROR, getLabel(context, component));
                }
            } else if (isValueExpression(message)) {
                message = this.bean.evaluateExpression(message, String.class);
            }

            String display = getMessageComponentId(context, component);
            if (display != null) {
                display = HtmlEncoder.enquote(display);
            }

            return "new ClientValidator('" +
                   component.getClientId(context) + "'," +
                   HtmlEncoder.enquote(message) + "," +
                   display + "," +
                   "function(value){" + script + "}" +
                   ")";
        }

        return null;
    }

    public Object saveState(FacesContext context) {
        // no need to save state, restored by injector
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        // no need to save state, restored by injector
    }

    public boolean isTransient() {
        return true;
    }

    public void setTransient(boolean newTransientValue) {
        // noop
    }
}
