/*
 * $Id: InputHiddenTag.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

public class InputHiddenTag extends HtmlBasicELTag
{
    private MethodExpression validator;
    private MethodExpression valueChangeListener;

    public String getRendererType() {
        return "javax.faces.Hidden";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlInputHidden.COMPONENT_TYPE;
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
