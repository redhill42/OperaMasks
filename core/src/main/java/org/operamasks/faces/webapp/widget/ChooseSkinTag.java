/*
 * $Id: ChooseSkinTag.java,v 1.4 2007/07/02 07:37:58 jacky Exp $
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
import javax.faces.component.UIInput;
import javax.faces.event.MethodExpressionValueChangeListener;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.UIChooseSkin;

/**
 * @jsp.tag name="chooseSkin" body-content="JSP"
 */
public class ChooseSkinTag extends HtmlBasicELTag
{
    private MethodExpression valueChangeListener;

    public String getComponentType() {
        return UIChooseSkin.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null; // determined by component
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute method-signature="void valueChange(javax.faces.event.ValueChangeEvent)"
     */
    public void setValueChangeListener(MethodExpression valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setCookie(ValueExpression cookie) {
        setValueExpression("cookie", cookie);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setCookieMaxAge(ValueExpression cookieMaxAge) {
        setValueExpression("cookieMaxAge", cookieMaxAge);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAccesskey(ValueExpression accesskey) {
        setValueExpression("accesskey", accesskey);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
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
    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
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
    public void setOnblur(ValueExpression onblur) {
        setValueExpression("onblur", onblur);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnchange(ValueExpression onchange) {
        setValueExpression("onchange", onchange);
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
    public void setOnfocus(ValueExpression onfocus) {
        setValueExpression("onfocus", onfocus);
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
    public void setOnselect(ValueExpression onselect) {
        setValueExpression("onselect", onselect);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setReadonly(ValueExpression readonly) {
        setValueExpression("readonly", readonly);
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
     * @jsp.attribute type="int"
     */
    public void setTabindex(ValueExpression tabindex) {
        setValueExpression("tabindex", tabindex);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIInput input = (UIInput)component;
        if (valueChangeListener != null)
            input.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
    }

    public void release() {
        super.release();
        valueChangeListener = null;
    }
}
