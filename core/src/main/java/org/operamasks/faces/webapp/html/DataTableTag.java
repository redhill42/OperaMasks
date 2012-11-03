/*
 * $Id: DataTableTag.java,v 1.4 2007/07/02 07:38:08 jacky Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

public class DataTableTag extends HtmlBasicELTag
{
    private String var;

    public String getRendererType() {
        return "javax.faces.Table";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlDataTable.COMPONENT_TYPE;
    }

    public void setFirst(ValueExpression first) {
        setValueExpression("first", first);
    }

    public void setRows(ValueExpression rows) {
        setValueExpression("rows", rows);
    }

    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setBgcolor(ValueExpression bgcolor) {
        setValueExpression("bgcolor", bgcolor);
    }

    public void setBorder(ValueExpression border) {
        setValueExpression("border", border);
    }

    public void setCaptionClass(ValueExpression captionClass) {
        setValueExpression("captionClass", captionClass);
    }

    public void setCaptionStyle(ValueExpression captionStyle) {
        setValueExpression("captionStyle", captionStyle);
    }

    public void setCellpadding(ValueExpression cellpadding) {
        setValueExpression("cellpadding", cellpadding);
    }

    public void setCellspacing(ValueExpression cellspacing) {
        setValueExpression("cellspacing", cellspacing);
    }

    public void setColumnClasses(ValueExpression columnClasses) {
        setValueExpression("columnClasses", columnClasses);
    }

    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setFooterClass(ValueExpression footerClass) {
        setValueExpression("footerClass", footerClass);
    }

    public void setFrame(ValueExpression frame) {
        setValueExpression("frame", frame);
    }

    public void setHeaderClass(ValueExpression headerClass) {
        setValueExpression("headerClass", headerClass);
    }

    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    public void setOnkeydown(ValueExpression onkeydown) {
        setValueExpression("onkeydown", onkeydown);
    }

    public void setOnkeypress(ValueExpression onkeypress) {
        setValueExpression("onkeypress", onkeypress);
    }

    public void setOnkeyup(ValueExpression onkeyup) {
        setValueExpression("onkeyup", onkeyup);
    }

    public void setOnmousedown(ValueExpression onmousedown) {
        setValueExpression("onmousedown", onmousedown);
    }

    public void setOnmousemove(ValueExpression onmousemove) {
        setValueExpression("onmousemove", onmousemove);
    }

    public void setOnmouseout(ValueExpression onmouseout) {
        setValueExpression("onmouseout", onmouseout);
    }

    public void setOnmouseover(ValueExpression onmouseover) {
        setValueExpression("onmouseover", onmouseover);
    }

    public void setOnmouseup(ValueExpression onmouseup) {
        setValueExpression("onmouseup", onmouseup);
    }

    public void setRowClasses(ValueExpression rowClasses) {
        setValueExpression("rowClasses", rowClasses);
    }

    public void setRules(ValueExpression rules) {
        setValueExpression("rules", rules);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setSummary(ValueExpression summary) {
        setValueExpression("summary", summary);
    }

    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIData data = (UIData)component;
        data.setVar(var);
    }

    public void release() {
        super.release();
        var = null;
    }
}
