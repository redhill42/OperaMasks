/*
 * $Id: ButtonTag.java,v 1.11 2008/04/02 01:47:00 lishaochuan Exp $
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
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.MethodExpressionActionListener;

import org.operamasks.faces.component.widget.UIButton;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="button" body-content="empty"
 */
public class ButtonTag extends HtmlBasicELTag
{
    private MethodExpression action;
    private MethodExpression actionListener;

    public String getComponentType() {
        return UIButton.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return "org.operamasks.faces.widget.Button";
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }
    
    /**
     * @jsp.attribute type="org.operamasks.faces.component.action.Action"
     */
    public void setActionBinding(ValueExpression actionBinding) {
        setValueExpression("actionBinding", actionBinding);
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

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
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
     * @jsp.attribute type="boolean"
     */
    public void setDisabled(ValueExpression disabled) {
        setValueExpression("disabled", disabled);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setImage(ValueExpression image) {
        setValueExpression("image", image);
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

//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyle(ValueExpression style) {
//        setValueExpression("style", style);
//    }

//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyleClass(ValueExpression styleClass) {
//        setValueExpression("styleClass", styleClass);
//    }

    /**
     * @jsp.attribute type="java.lang.String"
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

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTooltip(ValueExpression tooltip) {
        setValueExpression("tooltip", tooltip);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UICommand command = (UICommand)component;
        if (action != null)
            command.setActionExpression(action);
        if (actionListener != null)
            command.addActionListener(new MethodExpressionActionListener(actionListener));
    }
}
