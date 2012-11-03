/*
 * $Id: DialogTag.java,v 1.7 2008/01/07 11:25:24 lishaochuan Exp $
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

import javax.el.ValueExpression;

import org.operamasks.faces.component.widget.dialog.UIDialog;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="dialog" body-content="JSP"
 *          description_zh_CN="对话框组件，可内嵌jsp页面"
 * 
 * @author jacky
 */
public class DialogTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return UIDialog.RENDERER_TYPE;
    }

    public String getComponentType() {
        return UIDialog.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression( "jsvar" , jsvar );
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression( "title" , title );
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression( "width" , width );
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setHeight(ValueExpression height) {
        setValueExpression( "height" , height );
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLeft(ValueExpression left) {
        setValueExpression( "left" , left );
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTop(ValueExpression top) {
        setValueExpression( "top" , top );
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setContentStyle(ValueExpression contentStyle) {
        setValueExpression( "contentStyle" , contentStyle );
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setContentStyleClass(ValueExpression contentStyleClass) {
        setValueExpression( "contentStyleClass" , contentStyleClass );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setDraggable(ValueExpression draggable) {
        setValueExpression( "draggable" , draggable );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setResizable(ValueExpression resizable) {
        setValueExpression( "resizable" , resizable );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setCollapsible(ValueExpression collapsible) {
        setValueExpression( "collapsible" , collapsible );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setClosable(ValueExpression closable) {
        setValueExpression( "closable" , closable );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setAutoScroll(ValueExpression autoScroll) {
        setValueExpression( "autoScroll" , autoScroll );
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setModal(ValueExpression modal) {
        setValueExpression( "modal" , modal );
    }
    
    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setShow(ValueExpression show) {
        setValueExpression("show", show);
    }
}
