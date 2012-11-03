/*
 * $Id: CommandMenuItemTag.java,v 1.7 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.event.MethodExpressionActionListener;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;

/**
 * @jsp.tag name="commandMenuItem" body-content="JSP"
 */
public class CommandMenuItemTag extends MenuItemTag
{
    private MethodExpression action;
    private MethodExpression actionListener;

    public String getComponentType() {
        return UICommandMenuItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UICommandMenuItem.MENU_RENDERER_TYPE;
    }

    /**
     * @jsp.attribute method-signature="java.lang.Object action()"
     */
    public void setAction(MethodExpression action) {
        this.action = action;
    }

    /**
     * @jsp.attribute method-signature="void actionListener(javax.faces.event.ActionEvent)"
     */
    public void setActionListener(MethodExpression actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setImmediate(ValueExpression immediate) {
        setValueExpression("immediate", immediate);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UICommand command = (UICommand)component;
        if (action != null)
            command.setActionExpression(action);
        if (actionListener != null)
            command.addActionListener(new MethodExpressionActionListener(actionListener));
    }

    public void release() {
        super.release();
        action = null;
        actionListener = null;
    }
}
