/*
 * $Id: DoubleRangeValidator.java,v 1.7 2007/12/19 01:57:07 daniel Exp $
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

package org.operamasks.faces.validator;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;

import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import static org.operamasks.resources.Resources.*;

public class DoubleRangeValidator extends javax.faces.validator.DoubleRangeValidator
    implements ClientValidator
{
    private boolean minimumSet, maximumSet;
    private String message;

    public DoubleRangeValidator() {
        super();
    }

    public DoubleRangeValidator(double maximum) {
        super(maximum);
    }

    public DoubleRangeValidator(double maximum, double minimum) {
        super(maximum, minimum);
    }

    public void setMinimum(double minimum) {
        minimumSet = true;
        super.setMinimum(minimum);
    }

    public void setMaximum(double maximum) {
        maximumSet = true;
        super.setMaximum(maximum);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        try {
            super.validate(context, component, value);
        } catch (ValidatorException ex) {
            FacesMessage facesMessage = ex.getFacesMessage();
            if (facesMessage != null && this.message != null) {
                facesMessage.setSummary(this.message);
                facesMessage.setDetail(this.message);
            }
            throw ex;
        }
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        String message = (String)component.getAttributes().get("validatorMessage");
        if (message == null) {
            message = this.message;
        }

        if (minimumSet && maximumSet) {
            if (message == null) {
                message = _T(JSF_VALIDATE_DOUBLE_RANGE_NOT_IN_RANGE,
                             FacesUtils.getLabel(context, component),
                             String.valueOf(this.getMinimum()),
                             String.valueOf(this.getMaximum()));
            }

            String display = FacesUtils.getMessageComponentId(context, component);
            if (display != null) {
                display = HtmlEncoder.enquote(display);
            }

            return "new FloatValidator('" +
                   component.getClientId(context) + "'," +
                   HtmlEncoder.enquote(message) + "," +
                   display + "," +
                   this.getMinimum() + "," + this.getMaximum() +
                   ")";
        }

        if (minimumSet) {
            if (message == null) {
                message = _T(JSF_VALIDATE_DOUBLE_RANGE_MINIMUM,
                             FacesUtils.getLabel(context, component),
                             String.valueOf(this.getMinimum()));
            }

            String display = FacesUtils.getMessageComponentId(context, component);
            if (display != null) {
                display = HtmlEncoder.enquote(display);
            }

            return "new FloatValidator('" +
                   component.getClientId(context) + "'," +
                   HtmlEncoder.enquote(message) + "," +
                   display + "," +
                   this.getMinimum() + ",null)";
        }

        if (maximumSet) {
            if (message == null) {
                message = _T(JSF_VALIDATE_DOUBLE_RANGE_MAXIMUM,
                             FacesUtils.getLabel(context, component),
                             String.valueOf(this.getMaximum()));
            }

            String display = FacesUtils.getMessageComponentId(context, component);
            if (display != null) {
                display = HtmlEncoder.enquote(display);
            }

            return "new FloatValidator('" +
                   component.getClientId(context) + "'," +
                   HtmlEncoder.enquote(message) + "," +
                   display + "," +
                   "null," + this.getMaximum() + ")";
        }

        return null;
    }
}
