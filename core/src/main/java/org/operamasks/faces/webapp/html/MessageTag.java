/*
 * $Id: MessageTag.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

package org.operamasks.faces.webapp.html;

import javax.el.ValueExpression;

public class MessageTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "javax.faces.Message";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlMessage.COMPONENT_TYPE;
    }

    public void setFor(ValueExpression _for) {
        setValueExpression("for", _for);
    }

    public void setShowDetail(ValueExpression showDetail) {
        setValueExpression("showDetail", showDetail);
    }

    public void setShowSummary(ValueExpression showSummary) {
        setValueExpression("showSummary", showSummary);
    }

    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setErrorClass(ValueExpression errorClass) {
        setValueExpression("errorClass", errorClass);
    }

    public void setErrorStyle(ValueExpression errorStyle) {
        setValueExpression("errorStyle", errorStyle);
    }

    public void setFatalClass(ValueExpression fatalClass) {
        setValueExpression("fatalClass", fatalClass);
    }

    public void setFatalStyle(ValueExpression fatalStyle) {
        setValueExpression("fatalStyle", fatalStyle);
    }

    public void setWarnClass(ValueExpression warnClass) {
        setValueExpression("warnClass", warnClass);
    }

    public void setWarnStyle(ValueExpression warnStyle) {
        setValueExpression("warnStyle", warnStyle);
    }

    public void setInfoClass(ValueExpression infoClass) {
        setValueExpression("infoClass", infoClass);
    }

    public void setInfoStyle(ValueExpression infoStyle) {
        setValueExpression("infoStyle", infoStyle);
    }

    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    public void setTooltip(ValueExpression tooltip) {
        setValueExpression("tooltip", tooltip);
    }
}
