/*
 * $Id: CalcNumberFieldTag.java,v 1.4 2007/07/02 07:38:00 jacky Exp $
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

import org.operamasks.faces.component.widget.UICalcNumberField;

/**
 * @jsp.tag name="calcNumberField" body-content="JSP"
 * description_zh_CN="一个可以通过点击下拉按钮弹出计算器的数字输入框。" 
 */
public class CalcNumberFieldTag extends TextFieldTag {

    @Override
    public String getComponentType() {
        return UICalcNumberField.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return "org.operamasks.faces.widget.CalcNumberField";
    }    
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} allowDecimals False to disallow decimal values (defaults to true)"
     * description_zh_CN="如果为false，表示不允许小数值。缺省为true。"
     */
    public void setAllowDecimals(ValueExpression allowDecimals) {
        setValueExpression("allowDecimals", allowDecimals);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} allowNegative False to require only positive numbers (defaults to true)"
     * description_zh_CN="如果为false，表示仅允许正数。缺省为true。"
     */
    public void setAllowNegative(ValueExpression allowNegative) {
        setValueExpression("allowNegative", allowNegative);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} minValue The minimum allowed value (defaults to Number.NEGATIVE_INFINITY)"
     * description_zh_CN="允许的最小值。缺省为JavaScript中的Number.NEGATIVE_INFINITY。"
     */
    public void setMinValue(ValueExpression minValue) {
        setValueExpression("minValue", minValue);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} maxValue The maximum allowed value (defaults to Number.MAX_VALUE)"
     * description_zh_CN="允许的最大值。缺省为JavaScript中的Number.MAX_VALUE。"
     */
    public void setMaxValue(ValueExpression maxValue) {
        setValueExpression("maxValue", maxValue);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} minText Error text to display if the minimum value 
     *   validation fails (defaults to "The minimum value for this field is {minValue}")"
     * description_zh_CN="当最小值验证失败时显示的错误提示文本，缺省为\"此域的最小值是 {minValue}\"。"
     */
    public void setMinText(ValueExpression minText) {
        setValueExpression("minText", minText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} maxText Error text to display if the maximum value 
     *   validation fails (defaults to "The maximum value for this field is {maxValue}")"
     * description_zh_CN="当最大值验证失败时显示的错误提示文本，缺省为\"此域的最大值是 {maxValue}\"。"
     */
    public void setMaxText(ValueExpression maxText) {
        setValueExpression("maxText", maxText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} nanText Error text to display if the value is not 
     *   a valid number.  For example, this can happen if 
     *   a valid character like '.' or '-' is left in the field 
     *   with no number (defaults to "{value} is not a valid number")"
     * description_zh_CN="当输入框中的值不是一个合法的数值时显示的错误提示文本。
     *   例如，当输入框中只有像'.'或'-'这样的合法字符，但没有数字的情况。
     *   缺省为\"{value} 不是一个正确的数值"。"
     */
    public void setNanText(ValueExpression nanText) {
        setValueExpression("nanText", nanText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} triggerClass
     *   An additional CSS class used to style the trigger button.  The trigger will always get the
     *   class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified (defaults to 'x-form-calc-trigger'
     *   which displays a calculator icon)."
     * description_zh_CN="一个附加的CSS类，用于设定下拉按钮的风格。
     *   下拉按钮将总是使用\"x-form-trigger\"作为CSS类，triggerClass如果指定，将被附加到其后。
     *   该属性的缺省值为\"x-form-calc-trigger\", 显示一个计算器图标。"
     */
    public void setTriggerClass(ValueExpression triggerClass) {
        setValueExpression("triggerClass", triggerClass);
    }
}
