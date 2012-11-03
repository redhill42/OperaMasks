/*
 * $Id: DrawImageTag.java,v 1.5 2007/07/02 07:37:58 jacky Exp $
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
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.UIDrawImage;

/**
 * @jsp.tag name="drawImage" body-content="JSP"
 */
public class DrawImageTag extends HtmlBasicELTag
{
    private MethodExpression drawMethod;

    public String getComponentType() {
        return UIDrawImage.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIDrawImage.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setType(ValueExpression type) {
        setValueExpression("type", type);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute required="false" method-signature="void draw(java.awt.Graphics,int,int)"
     */
    public void setDraw(MethodExpression drawMethod) {
        this.drawMethod = drawMethod;
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setHeight(ValueExpression height) {
        setValueExpression("height", height);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setAlpha(ValueExpression alpha) {
        setValueExpression("alpha", alpha);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setInline(ValueExpression inline) {
        setValueExpression("inline", inline);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAlt(ValueExpression alt) {
        setValueExpression("alt", alt);
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
    public void setIsmap(ValueExpression ismap) {
        setValueExpression("ismap", ismap);
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
    public void setLongdesc(ValueExpression longdesc) {
        setValueExpression("longdesc", longdesc);
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
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setUsemap(ValueExpression usemap) {
        setValueExpression("usemap", usemap);
    }

    public void setProperties(UIComponent component) {
        super.setProperties(component);
        ((UIDrawImage)component).setDrawMethod(drawMethod);
    }

    public void release() {
        super.release();
        drawMethod = null;
    }
}
