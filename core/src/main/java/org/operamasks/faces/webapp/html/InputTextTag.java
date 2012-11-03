/*
 * $Id: InputTextTag.java,v 1.5 2007/07/02 07:38:07 jacky Exp $
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
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.validator.MethodExpressionValidator;
import javax.faces.event.MethodExpressionValueChangeListener;

public class InputTextTag extends HtmlBasicELTag
{
    private MethodExpression validator;
    private MethodExpression valueChangeListener;

    public String getRendererType() {
        return "javax.faces.Text";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlInputText.COMPONENT_TYPE;
    }
    
    /**
     * @jsp.attribute required = "false" type = "javax.faces.convert.Converter"
     */
    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setConverterMessage(ValueExpression converterMessage) {
        setValueExpression("converterMessage", converterMessage);
    }

    /**
     * @jsp.attribute required = "false" type = "boolean"
     */
    public void setImmediate(ValueExpression immediate) {
        setValueExpression("immediate", immediate);
    }

    /**
     * @jsp.attribute required = "false" type = "boolean"
     */
    public void setRequired(ValueExpression required) {
        setValueExpression("required", required);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setRequiredMessage(ValueExpression requiredMessage) {
        setValueExpression("requiredMessage", requiredMessage);
    }

    /**
     * @jsp.attribute required = "false" method-signature="void validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)"
     */
    public void setValidator(MethodExpression validator) {
        this.validator = validator;
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setValidatorMessage(ValueExpression validatorMessage) {
        setValueExpression("validatorMessage", validatorMessage);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute required = "false" method-signature = "void valueChange(javax.faces.event.ValueChangeEvent)"
     */
    public void setValueChangeListener(MethodExpression valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
    
    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setAccesskey(ValueExpression accesskey) {
        setValueExpression("accesskey", accesskey);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setAlt(ValueExpression alt) {
        setValueExpression("alt", alt);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setAutocomplete(ValueExpression autocomplete) {
        setValueExpression("autocomplete", autocomplete);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    /**
     * @jsp.attribute required = "false" type = "boolean"
     */
    public void setDisabled(ValueExpression disabled) {
        setValueExpression("disabled", disabled);
    }
    
    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    /**
     * @jsp.attribute required = "false" type = "int"
     */
    public void setMaxlength(ValueExpression maxlength) {
        setValueExpression("maxlength", maxlength);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnblur(ValueExpression onblur) {
        setValueExpression("onblur", onblur);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnchange(ValueExpression onchange) {
        setValueExpression("onchange", onchange);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnfocus(ValueExpression onfocus) {
        setValueExpression("onfocus", onfocus);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnkeydown(ValueExpression onkeydown) {
        setValueExpression("onkeydown", onkeydown);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnkeypress(ValueExpression onkeypress) {
        setValueExpression("onkeypress", onkeypress);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnkeyup(ValueExpression onkeyup) {
        setValueExpression("onkeyup", onkeyup);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnmousedown(ValueExpression onmousedown) {
        setValueExpression("onmousedown", onmousedown);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnmousemove(ValueExpression onmousemove) {
        setValueExpression("onmousemove", onmousemove);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnmouseout(ValueExpression onmouseout) {
        setValueExpression("onmouseout", onmouseout);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnmouseover(ValueExpression onmouseover) {
        setValueExpression("onmouseover", onmouseover);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnmouseup(ValueExpression onmouseup) {
        setValueExpression("onmouseup", onmouseup);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setOnselect(ValueExpression onselect) {
        setValueExpression("onselect", onselect);
    }

    /**
     * @jsp.attribute required = "false" type = "boolean"
     */
    public void setReadonly(ValueExpression readonly) {
        setValueExpression("readonly", readonly);
    }

    /**
     * @jsp.attribute required = "false" type = "int"
     */
    public void setSize(ValueExpression size) {
        setValueExpression("size", size);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setTabindex(ValueExpression tabindex) {
        setValueExpression("tabindex", tabindex);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIInput input = (UIInput)component;
        if (validator != null)
            input.addValidator(new MethodExpressionValidator(validator));
        if (valueChangeListener != null)
            input.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
    }

    public void release() {
        super.release();
        validator = null;
        valueChangeListener = null;
    }
}
