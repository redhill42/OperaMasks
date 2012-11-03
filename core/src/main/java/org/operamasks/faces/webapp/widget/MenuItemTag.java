/*
 * $Id: MenuItemTag.java,v 1.3 2007/07/02 07:37:59 jacky Exp $
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

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.menu.UITextMenuItem;
import javax.el.ValueExpression;

/**
 * @jsp.tag name="menuItem" body-content="JSP"
 */
public class MenuItemTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UITextMenuItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UITextMenuItem.DEFAULT_RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setImage(ValueExpression image) {
        setValueExpression("image", image);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setDisabled(ValueExpression disabled) {
        setValueExpression("disabled", disabled);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setDisabledClass(ValueExpression disabledClass) {
        setValueExpression("disabledClass", disabledClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setActiveClass(ValueExpression activeClass) {
        setValueExpression("activeClass", activeClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }
}
