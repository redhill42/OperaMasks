/*
 * $Id: SliderTag.java,v 1.6 2007/07/02 07:37:58 jacky Exp $
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
import javax.faces.validator.MethodExpressionValidator;
import javax.faces.event.MethodExpressionValueChangeListener;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.UISlider;

/**
 * @jsp.tag name="slider" body-content="JSP"
 */
public class SliderTag extends HtmlBasicELTag
{
    private MethodExpression validator;
    private MethodExpression valueChangeListener;

    public String getRendererType() {
        return "org.operamasks.faces.widget.Slider";
    }

    public String getComponentType() {
        return UISlider.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOrientation(ValueExpression orientation) {
        setValueExpression("orientation", orientation);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setMinimum(ValueExpression minimum) {
        setValueExpression("minimum", minimum);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setMaximum(ValueExpression maximum) {
        setValueExpression("maximum", maximum);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setUnitIncrement(ValueExpression unitIncrement) {
        setValueExpression("unitIncrement", unitIncrement);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setBlockIncrement(ValueExpression blockIncrement) {
        setValueExpression("blockIncrement", blockIncrement);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute type="javax.faces.convert.Converter"
     */
    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setConverterMessage(ValueExpression converterMessage) {
        setValueExpression("converterMessage", converterMessage);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setImmediate(ValueExpression immediate) {
        setValueExpression("immediate", immediate);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setRequired(ValueExpression required) {
        setValueExpression("required", required);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRequiredMessage(ValueExpression requiredMessage) {
        setValueExpression("requiredMessage", requiredMessage);
    }

    /**
     * @jsp.attribute method-signature="void validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)"
     */
    public void setValidator(MethodExpression validator) {
        this.validator = validator;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setValidatorMessage(ValueExpression validatorMessage) {
        setValueExpression("validatorMessage", validatorMessage);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
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
    public void setProxy(ValueExpression proxy) {
        setValueExpression("proxy", proxy);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLink(ValueExpression link) {
        setValueExpression("link", link);
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
