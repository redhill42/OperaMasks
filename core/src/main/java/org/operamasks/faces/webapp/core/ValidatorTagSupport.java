/*
 * $Id: ValidatorTagSupport.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.faces.webapp.ValidatorELTag;
import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

public abstract class ValidatorTagSupport extends ValidatorELTag
{
    private ValueExpression binding;

    public void setBinding(ValueExpression binding) {
        this.binding = binding;
    }

    public void release() {
        super.release();
        binding = null;
    }

    protected Validator createValidator(String validatorId)
        throws JspException
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Validator validator = null;

        if (binding != null) {
            validator = (Validator)binding.getValue(context.getELContext());
            if (validator != null) return validator;
        }

        if (validatorId != null) {
            validator = context.getApplication().createValidator(validatorId);
            if (validator != null && binding != null)
                binding.setValue(context.getELContext(), validator);
        }

        return validator;
    }
}
