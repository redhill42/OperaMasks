/*
 * $Id: ValidateLongRangeTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

package org.operamasks.faces.webapp.core;

import javax.faces.validator.Validator;
import javax.faces.validator.LongRangeValidator;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

public class ValidateLongRangeTag extends ValidatorTagSupport
{
    private ValueExpression minimum;
    private ValueExpression maximum;

    public void setMinimum(ValueExpression minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(ValueExpression maximum) {
        this.maximum = maximum;
    }

    protected Validator createValidator()
        throws JspException
    {
        FacesContext context = FacesContext.getCurrentInstance();
        LongRangeValidator validator = (LongRangeValidator)super.createValidator("javax.faces.LongRange");
        if (validator != null) {
            if (minimum != null)
                validator.setMinimum((Long)minimum.getValue(context.getELContext()));
            if (maximum != null)
                validator.setMaximum((Long)maximum.getValue(context.getELContext()));
        }
        return validator;
    }

    public void release() {
        super.release();
        minimum = null;
        maximum = null;
    }
}
