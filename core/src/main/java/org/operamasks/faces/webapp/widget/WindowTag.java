/*
 * $Id: WindowTag.java,v 1.5 2007/07/02 07:37:58 jacky Exp $
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
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.UIWindow;

public class WindowTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.widget.Window";
    }

    public String getComponentType() {
        return UIWindow.COMPONENT_TYPE;
    }

    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    public void setIcon(ValueExpression icon) {
        setValueExpression("icon", icon);
    }

    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
    }

    public void setShowIcon(ValueExpression showIcon) {
        setValueExpression("showIcon", showIcon);
    }

    public void setCanMove(ValueExpression canMove) {
        setValueExpression("canMove", canMove);
    }

    public void setCanResize(ValueExpression canResize) {
        setValueExpression("canResize", canResize);
    }

    public void setCanMinimize(ValueExpression canMinimize) {
        setValueExpression("canMinimize", canMinimize);
    }

    public void setCanMaximize(ValueExpression canMaximize) {
        setValueExpression("canMaximize", canMaximize);
    }

    public void setCanClose(ValueExpression canClose) {
        setValueExpression("canClose", canClose);
    }

    public void setLeft(ValueExpression left) {
        setValueExpression("left", left);
    }

    public void setTop(ValueExpression top) {
        setValueExpression("top", top);
    }

    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    public void setHeight(ValueExpression height) {
        setValueExpression("height", height);
    }

    public void setShow(ValueExpression show) {
        setValueExpression("show", show);
    }

    public void setOnbeforeclose(ValueExpression onbeforeclose) {
        setValueExpression("onbeforeclose", onbeforeclose);
    }

    public void setOnclose(ValueExpression onclose) {
        setValueExpression("onclose", onclose);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setCaptionStyle(ValueExpression captionStyle) {
        setValueExpression("captionStyle", captionStyle);
    }

    public void setCaptionStyleClass(ValueExpression captionStyleClass) {
        setValueExpression("captionStyleClass", captionStyleClass);
    }

    public void setLabelStyle(ValueExpression labelStyle) {
        setValueExpression("labelStyle", labelStyle);
    }

    public void setLabelStyleClass(ValueExpression labelStyleClass) {
        setValueExpression("labelStyleClass", labelStyleClass);
    }

    public void setContentPaneStyle(ValueExpression contentPaneStyle) {
        setValueExpression("contentPaneStyle", contentPaneStyle);
    }

    public void setContentPaneStyleClass(ValueExpression contentPaneStyleClass) {
        setValueExpression("contentPaneStyleClass", contentPaneStyleClass);
    }
}
