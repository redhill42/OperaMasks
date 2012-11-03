/*
 * $Id: LinkMenuItemTag.java,v 1.6 2007/07/02 07:38:00 jacky Exp $
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

package org.operamasks.faces.webapp.widget;

import org.operamasks.faces.component.widget.menu.UILinkMenuItem;
import javax.el.ValueExpression;

/**
 * @jsp.tag name="linkMenuItem" body-content="JSP"
 */
public class LinkMenuItemTag extends MenuItemTag
{
    public String getComponentType() {
        return UILinkMenuItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UILinkMenuItem.DEFAULT_RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="javax.faces.convert.Converter"
     */
    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTarget(ValueExpression target) {
        setValueExpression("target", target);
    }
}
