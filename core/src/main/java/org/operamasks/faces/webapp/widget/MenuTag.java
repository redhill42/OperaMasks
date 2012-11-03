/*
 * $Id: MenuTag.java,v 1.8 2008/04/08 06:04:54 lishaochuan Exp $
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import org.operamasks.faces.component.widget.menu.UIMenu;

/**
 * Menu or sub-menu.
 * 
 * @jsp.tag name="menu" body-content="JSP"
 */
public class MenuTag extends MenuTagBase
{
    public String getRendererType() {
        return UIMenu.RENDERER_TYPE;
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
    
    private MethodExpression menuAction;
    /**
     * @jsp.attribute method-signature="java.lang.Object menuAction()"
     */
    public void setMenuAction(MethodExpression menuAction) {
        this.menuAction = menuAction;
    }
}
