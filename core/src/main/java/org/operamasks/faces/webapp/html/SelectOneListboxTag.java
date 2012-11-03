/*
 * $Id: SelectOneListboxTag.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.validator.MethodExpressionValidator;
import javax.faces.event.MethodExpressionValueChangeListener;

public class SelectOneListboxTag extends HtmlBasicELTag
{
    private MethodExpression validator;
    private MethodExpression valueChangeListener;

    public String getRendererType() {
        return "javax.faces.Listbox";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlSelectOneListbox.COMPONENT_TYPE;
    }

    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    public void setConverterMessage(ValueExpression converterMessage) {
        setValueExpression("converterMessage", converterMessage);
    }

    public void setImmediate(ValueExpression immediate) {
        setValueExpression("immediate", immediate);
    }

    public void setRequired(ValueExpression required) {
        setValueExpression("required", required);
    }

    public void setRequiredMessage(ValueExpression requiredMessage) {
        setValueExpression("requiredMessage", requiredMessage);
    }

    public void setValidator(MethodExpression validator) {
        this.validator = validator;
    }

    public void setValidatorMessage(ValueExpression validatorMessage) {
        setValueExpression("validatorMessage", validatorMessage);
    }

    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    public void setValueChangeListener(MethodExpression valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public void setAccesskey(ValueExpression accesskey) {
        setValueExpression("accesskey", accesskey);
    }

    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setDisabled(ValueExpression disabled) {
        setValueExpression("disabled", disabled);
    }

    public void setDisabledClass(ValueExpression disabledClass) {
        setValueExpression("disabledClass", disabledClass);
    }

    public void setEnabledClass(ValueExpression enabledClass) {
        setValueExpression("enabledClass", enabledClass);
    }

    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
    }

    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    public void setOnblur(ValueExpression onblur) {
        setValueExpression("onblur", onblur);
    }

    public void setOnchange(ValueExpression onchange) {
        setValueExpression("onchange", onchange);
    }

    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    public void setOnfocus(ValueExpression onfocus) {
        setValueExpression("onfocus", onfocus);
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

    public void setOnselect(ValueExpression onselect) {
        setValueExpression("onselect", onselect);
    }

    public void setReadonly(ValueExpression readonly) {
        setValueExpression("readonly", readonly);
    }

    public void setSize(ValueExpression size) {
        setValueExpression("size", size);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setTabindex(ValueExpression tabindex) {
        setValueExpression("tabindex", tabindex);
    }

    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UISelectOne selectOne = (UISelectOne)component;
        if (validator != null)
            selectOne.addValidator(new MethodExpressionValidator(validator));
        if (valueChangeListener != null)
            selectOne.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
    }

    public void release() {
        super.release();
        validator = null;
        valueChangeListener = null;
    }
}
