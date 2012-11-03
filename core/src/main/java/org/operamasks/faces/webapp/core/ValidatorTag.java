/*
 * $Id: ValidatorTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

public class ValidatorTag extends ValidatorTagSupport
{
    private ValueExpression validatorId;

    public void setValidatorId(ValueExpression validatorId) {
        this.validatorId = validatorId;
    }

    protected Validator createValidator()
        throws JspException
    {
        String id = null;
        if (validatorId != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            id = (String)validatorId.getValue(context.getELContext());
        }
        return super.createValidator(id);
    }

    public void release() {
        super.release();
        validatorId = null;
    }
}

