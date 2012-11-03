/*
 * $Id: CheckMenuItemTag.java,v 1.3 2007/07/02 07:37:59 jacky Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectBoolean;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.validator.MethodExpressionValidator;

import org.operamasks.faces.component.widget.menu.UICheckMenuItem;

/**
 * @jsp.tag name="checkMenuItem" body-content="JSP"
 */
public class CheckMenuItemTag extends MenuItemTag
{
    private MethodExpression validator;
    private MethodExpression valueChangeListener;

    public String getComponentType() {
        return UICheckMenuItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UICheckMenuItem.DEFAULT_RENDERER_TYPE;
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
     * @jsp.attribute method-signature="void valueChangeListener(javax.faces.event.ValueChangeEvent)"
     */
    public void setValueChangeListener(MethodExpression valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnbeforechange(ValueExpression onbeforechange) {
        setValueExpression("onbeforechange", onbeforechange);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnchange(ValueExpression onchange) {
        setValueExpression("onchange", onchange);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UISelectBoolean selectBoolean = (UISelectBoolean)component;
        if (validator != null)
            selectBoolean.addValidator(new MethodExpressionValidator(validator));
        if (valueChangeListener != null)
            selectBoolean.addValueChangeListener(new MethodExpressionValueChangeListener(valueChangeListener));
    }

    public void release() {
        super.release();
        validator = null;
        valueChangeListener = null;
    }
}
