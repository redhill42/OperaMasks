/*
 * $Id: GridLayoutTag.java,v 1.5 2007/07/02 07:38:10 jacky Exp $
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

package org.operamasks.faces.webapp.layout;

import javax.el.ValueExpression;
import org.operamasks.faces.component.layout.GridLayout;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="gridLayout" body-content="JSP"
 */
public class GridLayoutTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.layout.GridLayout";
    }

    public String getComponentType() {
        return GridLayout.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setBgcolor(ValueExpression bgcolor) {
        setValueExpression("bgcolor", bgcolor);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setBorder(ValueExpression border) {
        setValueExpression("border", border);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setCaptionClass(ValueExpression captionClass) {
        setValueExpression("captionClass", captionClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setCaptionStyle(ValueExpression captionStyle) {
        setValueExpression("captionStyle", captionStyle);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setCellpadding(ValueExpression cellpadding) {
        setValueExpression("cellpadding", cellpadding);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setCellspacing(ValueExpression cellspacing) {
        setValueExpression("cellspacing", cellspacing);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setColumnClasses(ValueExpression columnClasses) {
        setValueExpression("columnClasses", columnClasses);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setColumns(ValueExpression columns) {
        setValueExpression("columns", columns);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setFooterClass(ValueExpression footerClass) {
        setValueExpression("footerClass", footerClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setFrame(ValueExpression frame) {
        setValueExpression("frame", frame);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setHeaderClass(ValueExpression headerClass) {
        setValueExpression("headerClass", headerClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeydown(ValueExpression onkeydown) {
        setValueExpression("onkeydown", onkeydown);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeypress(ValueExpression onkeypress) {
        setValueExpression("onkeypress", onkeypress);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeyup(ValueExpression onkeyup) {
        setValueExpression("onkeyup", onkeyup);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmousedown(ValueExpression onmousedown) {
        setValueExpression("onmousedown", onmousedown);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmousemove(ValueExpression onmousemove) {
        setValueExpression("onmousemove", onmousemove);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseout(ValueExpression onmouseout) {
        setValueExpression("onmouseout", onmouseout);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseover(ValueExpression onmouseover) {
        setValueExpression("onmouseover", onmouseover);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseup(ValueExpression onmouseup) {
        setValueExpression("onmouseup", onmouseup);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRowClasses(ValueExpression rowClasses) {
        setValueExpression("rowClasses", rowClasses);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRules(ValueExpression rules) {
        setValueExpression("rules", rules);
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
    public void setSummary(ValueExpression summary) {
        setValueExpression("summary", summary);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }
}
