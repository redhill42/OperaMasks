/*
 * $Id: DateFieldTag.java,v 1.7 2008/03/14 05:44:08 lishaochuan Exp $
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

import org.operamasks.faces.component.widget.UIDateField;

public class DateFieldTag extends TextFieldTag {

    @Override
    public String getComponentType() {
        return UIDateField.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return "org.operamasks.faces.widget.DateField";
    }    
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} format
     *   The default date format string which can be overriden for localization support.  
     *   The format must be valid according to {@link Date#parseDate} (defaults to 'Y/m/d')."
     * description_zh_CN="缺省的日期格式，可以有本地化的格式。
     *   这个格式必须符合JavaScript的{@link Date#parseDate}，缺省是'Y/m/d'。"
     */
    public void setFormat(ValueExpression format) {
        setValueExpression("format", format);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{Array} disabledDays An array of days to disable, 0 based. 
     *   For example, [0, 6] disables Sunday and Saturday (defaults to null)."
     * description_zh_CN="一个JavaScript数组形式的值，表示一个星期中被禁止的天。
     *   以0为基。例如，[0, 6] 表示禁止星期天和星期六，缺省为null。"
     */
    public void setDisabledDays(ValueExpression disabledDays) {
        setValueExpression("disabledDays", disabledDays);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} disabledDaysText The tooltip to display 
     *   when the date falls on a disabled day (defaults to 'Disabled')"
     * description_zh_CN="当所选日期是被禁止的时候，显示的工具提示信息，缺省是\"被禁止\"。"
     */
    public void setDisabledDaysText(ValueExpression disabledDaysText) {
        setValueExpression("disabledDaysText", disabledDaysText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{Array} disabledDates
     *   An array of "dates" to disable, as strings. These strings will be used to build a dynamic regular
     *   expression so they are very powerful. Some examples:
     *   <ul>
     *   <li>['03/08/2003', '09/16/2003'] would disable those exact dates</li>
     *   <li>['03/08', '09/16'] would disable those days for every year</li>
     *   <li>['^03/08'] would only match the beginning (useful if you are using short years)</li>
     *   <li>['03/../2006'] would disable every day in March 2006</li>
     *   <li>['^03'] would disable every day in every March</li>
     *   </ul>
     *   In order to support regular expressions, if you are using a date format that has "." in it, you will have to
     *   escape the dot when restricting dates. For example: [\"03\\.08\\.03\"]."
     * description_zh_CN="一个JavaScript数组，表示被禁止的日期。
     *   数组成员为字符串，这些字符串将被用来构建一个动态的正则表达式，因此功能很强大。一些例子：
     *   <ul>
     *   <li>['03/08/2003', '09/16/2003'] 将禁止这些明确指定的日期</li>
     *   <li>['03/08', '09/16'] 将禁止每年的这些日期</li>
     *   <li>['^03/08'] 所有以'03/08'开头的日期将被禁止</li>
     *   <li>['03/../2006'] 将禁止2006年3月的每一天</li>
     *   <li>['^03'] 将禁止每个三月的每一天</li>
     *   </ul>
     *   为了支持正则表达式，如果你使用了一个内部含有\".\"的日期格式来限制日期，你必须对其进行转义。
     *   例如：[\"03\\.08\\.03\"]。"
     */
    public void setDisabledDates(ValueExpression disabledDates) {
        setValueExpression("disabledDates", disabledDates);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} disabledDatesText
     *   The tooltip text to display when the date falls on a disabled date (defaults to 'Disabled')"
     * description_zh_CN="当所选日期是被禁止的时候，显示的工具提示信息，缺省是\"被禁止\"。"
     */
    public void setDisabledDatesText(ValueExpression disabledDatesText) {
        setValueExpression("disabledDatesText", disabledDatesText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} minValue
     *   The minimum allowed date. A string date in a
     *   valid format (defaults to null)."
     * description_zh_CN="允许选择的最小日期。必须是一个合法日期格式的字符串。"
     */
    public void setMinValue(ValueExpression minValue) {
        setValueExpression("minValue", minValue);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} maxValue
     *   The maximum allowed date. A string date in a
     *   valid format (defaults to null)."
     * description_zh_CN="允许选择的最大日期。必须是一个合法日期格式的字符串。"
     */
    public void setMaxValue(ValueExpression maxValue) {
        setValueExpression("maxValue", maxValue);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} minText
     *   The error text to display when the date in the cell is before minValue (defaults to
     *   'The date in this field must be after {minValue}')."
     * description_zh_CN="当选择的日期比minValue指定的值还靠前时显示的错误提示信息，缺省是'日期值必须在 {minValue} 之后'"
     */
    public void setMinText(ValueExpression minText) {
        setValueExpression("minText", minText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} maxText
     *   The error text to display when the date in the cell is after maxValue (defaults to
     *   'The date in this field must be before {maxValue}')."
     * description_zh_CN="当选择的日期比minValue指定的值还靠后时显示的错误提示信息，缺省是'日期值必须在 {maxValue} 之前'"
     */
    public void setMaxText(ValueExpression maxText) {
        setValueExpression("maxText", maxText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} invalidText
     *   The error to display when the date in the field is invalid (defaults to
     *   '{value} is not a valid date - it must be in the format {format}')."
     * description_zh_CN="当域内的日期格式不合法时显示的错误提示信息，缺省是'{value} 不是一个正确的日期 - 它必须是 {format} 的格式'"  
     */
    public void setInvalidText(ValueExpression invalidText) {
        setValueExpression("invalidText", invalidText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} triggerClass
     *   An additional CSS class used to style the trigger button.  The trigger will always get the
     *   class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified (defaults to 'x-form-date-trigger'
     *   which displays a calendar icon)."
     * description_zh_CN="一个附加的CSS类，用于设定下拉按钮的风格。
     *   下拉按钮将总是使用\"x-form-trigger\"作为CSS类，triggerClass如果指定，将被附加到其后。
     *   该属性的缺省值为\"x-form-date-trigger\", 显示一个日历图标。"
     */
    public void setTriggerClass(ValueExpression triggerClass) {
        setValueExpression("triggerClass", triggerClass);
    }
}
