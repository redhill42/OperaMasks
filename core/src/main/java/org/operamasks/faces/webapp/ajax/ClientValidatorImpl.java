/*
 * $Id: ClientValidatorImpl.java,v 1.6 2008/02/28 02:15:05 yangdong Exp $
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

package org.operamasks.faces.webapp.ajax;

import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.StateHolder;

import org.operamasks.faces.validator.ClientValidator;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.resources.Resources.*;

public class ClientValidatorImpl
    implements ClientValidator, Validator, StateHolder
{
    private String message;
    private String script;

    public ClientValidatorImpl() {
        super();
    }

    public ClientValidatorImpl(String message, String script) {
        this.message = message;
        this.script = script;
    }

    public String getValidationMessage() {
        return message;
    }

    public void setValidationMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
    	return message;
    }
    
    public void setMessage(String message) {
    	this.message = message;
    }

    public String getValidationScript() {
        return script;
    }

    public void setValidationScript(String script) {
        this.script = script;
    }

    public void validate(FacesContext context, UIComponent component, Object value) {
        // No server side validation performed
    }

    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        String message = this.message;
        if (message == null) {
            message = (String)component.getAttributes().get("validatorMessage");
            if (message == null) {
                message = _T(JSF_CLIENT_VALIDATE_ERROR, FacesUtils.getLabel(context, component));
            }
        }

        String display = FacesUtils.getMessageComponentId(context, component);
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

    public Object saveState(FacesContext context) {
        return new Object[] {
            message, script
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        message = (String)values[0];
        script = (String)values[1];
    }

    private boolean transientFlag = false;

    public boolean isTransient() {
        return this.transientFlag;
    }

    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }
}
