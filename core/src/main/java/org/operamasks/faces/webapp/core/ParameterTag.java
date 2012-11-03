/*
 * $Id: ParameterTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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
import javax.el.ValueExpression;

public class ParameterTag extends UIComponentELTag
{
    private ValueExpression name;
    private ValueExpression value;

    public void setName(ValueExpression name) {
        this.name = name;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return javax.faces.component.UIParameter.COMPONENT_TYPE;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (name != null)
            component.setValueExpression("name", name);
        if (value != null)
            component.setValueExpression("value", value);
    }

    public void release() {
        super.release();
        name = null;
        value = null;
    }
}
