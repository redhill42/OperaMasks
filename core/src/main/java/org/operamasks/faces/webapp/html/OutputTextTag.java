/*
 * $Id: OutputTextTag.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

public class OutputTextTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "javax.faces.Text";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlOutputText.COMPONENT_TYPE;
    }

    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setEscape(ValueExpression escape) {
        setValueExpression("escape", escape);
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
}
