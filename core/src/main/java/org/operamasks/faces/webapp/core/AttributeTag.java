/*
 * $Id: AttributeTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.el.ValueExpression;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import static org.operamasks.resources.Resources.*;

public class AttributeTag extends TagSupport
{
    private ValueExpression name;
    private ValueExpression value;

    public void setName(ValueExpression name) {
        this.name = name;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    public int doStartTag()
        throws JspException
    {
        UIComponentClassicTagBase tag = UIComponentELTag.getParentUIComponentClassicTagBase(pageContext);
        if (tag == null)
            throw new JspException(_T(JSF_NOT_NESTED_IN_FACES_TAG, "f:attribute"));

        UIComponent component = tag.getComponentInstance();
        if (component == null)
            throw new JspException(_T(JSF_NO_COMPONENT_FOR_FACES_TAG, tag.getClass().getName()));

        if (name != null && value != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            String nameVal = (String)name.getValue(context.getELContext());
            if (!component.getAttributes().containsKey(nameVal)) {
                component.setValueExpression(nameVal, value);
            }
        }

        return SKIP_BODY;
    }

    public void release() {
        super.release();
        name = null;
        value = null;
    }
}
