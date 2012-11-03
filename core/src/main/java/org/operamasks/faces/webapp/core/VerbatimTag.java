/*
 * $Id: VerbatimTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

public class VerbatimTag extends UIComponentELTag
{
    private ValueExpression escape;

    public String getRendererType() {
        return "javax.faces.Text";
    }

    public String getComponentType() {
        return javax.faces.component.UIOutput.COMPONENT_TYPE;
    }

    public void setEscape(ValueExpression escape) {
        this.escape = escape;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (escape != null) {
            component.setValueExpression("escape", escape);
        } else {
            component.getAttributes().put("escape", Boolean.FALSE);
        }
        component.setTransient(true);
    }

    public int doAfterBody()
        throws JspException
    {
        if (getBodyContent() != null) {
            String value = getBodyContent().getString();
            if (value != null) {
                UIOutput output = (UIOutput)getComponentInstance();
                output.setValue(value);
                getBodyContent().clearBody();
            }
        }
        return SKIP_BODY;
    }

    public void release() {
        super.release();
        escape = null;
    }
}
