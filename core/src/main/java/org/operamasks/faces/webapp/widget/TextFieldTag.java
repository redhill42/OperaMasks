/*
 * $Id: TextFieldTag.java,v 1.9 2008/03/14 05:44:08 lishaochuan Exp $
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

import org.operamasks.faces.component.widget.UITextField;
import org.operamasks.faces.webapp.html.InputTextTag;

public class TextFieldTag extends InputTextTag {
    @Override
    public String getComponentType() {
        return UITextField.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return "org.operamasks.faces.widget.TextField";
    }

    /**
     * @jsp.attribute type="java.lang.String"
     * description_zh_CN="客户端脚本使用的javascript变量名，引用脚本中TextField对象。"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} grow True if this field should automatically grow and shrink to it's content"
     * description_zh_CN="如果为true，表示这个域应该随着它的内容的长短自动伸缩。"
     */
    public void setGrow(ValueExpression grow) {
        setValueExpression("grow", grow);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} growMin The minimum width in pixels to allow when grow = true (defaults to 30)"
     * description_zh_CN="当grow=true时，允许的最小宽度，单位为象素，缺省是30。"
     */
    public void setGrowMin(ValueExpression growMin) {
        setValueExpression("growMin", growMin);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} growMax The maximum width in pixels to allow when grow = true (defaults to 800)"
     * description_zh_CN="当grow=true时，允许的最大宽度，单位为象素，缺省是800。"
     */
    public void setGrowMax(ValueExpression growMax) {
        setValueExpression("growMax", growMax);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} vtype A client validation type name as defined 
     *   in {@link Ext.form.VTypes} (defaults to null) 
     *   example: 'alpha, alphanum, email, url'.
     *   <p>definition:
     *   <ul>
     *   <li>alpha = /^[a-zA-Z_]+$/;"
     *   <li>alphanum = /^[a-zA-Z0-9_]+$/;
     *   <li>email = /^([\w]+)(.[\w]+)*@([\w]+)(.[\w]{2,4}){1,2}$/;
     *   <li>url = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;
     *   </ul>"
     * description_zh_CN="一个客户端验证类型名，定义在Ext.form.VTypes中。
     *   例子: 'alpha, alphanum, email, url'，
     *   <p>以下是上面几个类型的定义：
     *   <ul>
     *   <li>alpha = /^[a-zA-Z_]+$/;"
     *   <li>alphanum = /^[a-zA-Z0-9_]+$/;
     *   <li>email = /^([\w]+)(.[\w]+)*@([\w]+)(.[\w]{2,4}){1,2}$/;
     *   <li>url = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;
     *   </ul>"
     */
    public void setVtype(ValueExpression vtype) {
        setValueExpression("vtype", vtype);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} maskRe An input JavaScript mask regular expression 
     *   that will be used to filter keystrokes that don't match (defaults to null)"
     * description_zh_CN="一个客户端JavaScript屏蔽正则表达式，允许使用Perl风格的语法，可以做到不匹配该表达式的字符无法输入的效果。"
     */
    public void setMaskRe(ValueExpression maskRe) {
        setValueExpression("maskRe", maskRe);
    }
    
    /**
     * @jsp.attribute type="boolean" description="{Boolean} disableKeyFilter True to disable input keystroke filtering (defaults to false)"
     */
    public void setDisableKeyFilter(ValueExpression disableKeyFilter) {
        setValueExpression("disableKeyFilter", disableKeyFilter);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} allowBlank False to validate that the value length > 0 (defaults to true)"
     * description_zh_CN="如果为false，会检查输入值的长度必须 > 0，缺省值为true。"
     */
    public void setAllowBlank(ValueExpression allowBlank) {
        setValueExpression("allowBlank", allowBlank);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} minLength Minimum input field length required (defaults to 0)"
     * description_zh_CN="输入文本的最小长度，缺省为0。"
     */
    public void setMinLength(ValueExpression minLength) {
        setValueExpression("minLength", minLength);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} maxlength Maximum input field length allowed (defaults to Number.MAX_VALUE)"
     * description_zh_CN="输入文本的最大长度。"
     */
    public void setMaxlength(ValueExpression maxLength) {
        setValueExpression("maxlength", maxLength);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} minLengthText Error text to display if the minimum length validation fails (defaults to "The minimum length for this field is {minLength}")"
     * description_zh_CN="最小长度验证失败时显示的错误提示文本。缺省是\"此域的最小长度是 {minLength}\"。"
     */
    public void setMinLengthText(ValueExpression minLengthText) {
        setValueExpression("minLengthText", minLengthText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" description="{String} maxLengthText Error text to display if the maximum length validation fails (defaults to "The maximum length for this field is {maxLength}")"
     * description_zh_CN="最大长度验证失败时显示的错误提示文本。缺省是\"此域的最大长度是 {maxLength}\"。"
     */
    public void setMaxLengthText(ValueExpression maxLengthText) {
        setValueExpression("maxLengthText", maxLengthText);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} selectOnFocus True to automatically select any existing field text when the field receives input focus (defaults to false)"
     * description_zh_CN="如果为true，表示当域接收到输入焦点时自动选择任何存在的域文本，缺省为false。"
     */
    public void setSelectOnFocus(ValueExpression selectOnFocus) {
        setValueExpression("selectOnFocus", selectOnFocus);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} blankText Error text to display if the allow blank validation fails (defaults to "This field is required")"
     * description_zh_CN="允许空验证失败时显示的错误提示文本。缺省是\"此域必须输入\"。"
     */
    public void setBlankText(ValueExpression blankText) {
        setValueExpression("blankText", blankText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{Function} validator A custom validation function to be called during field validation (defaults to null).
     *   If available, this function will be called only after the basic validators all return true, and will be passed the
     *   current field value and expected to return boolean true if the value is valid or a string error message if invalid."
     * description_zh_CN="一个客户自己写的用于验证的JavaScript函数，在域验证时被调用，
     *   如果提供了该函数，那么它仅当基本的验证器都返回true时才会被调用，调用时会传给它当前的域值，
     *   并且，对它的返回值的要求是：当验证通过时，返回布尔值true；当验证不通过时，返回一个错误消息的字符串。"  
     */
    public void setExtValidator(ValueExpression extValidator) {
        setValueExpression("extValidator", extValidator);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{RegExp} regex A JavaScript RegExp object to be tested against the field value during validation (defaults to null).
     *   If available, this regex will be evaluated only after the basic Ext validators all return true, and will be passed the
     *   current field value.  If the test fails, the field will be marked invalid using {@link #regexText}."
     * description_zh_CN="一个JavaScript RegExp 对象，允许使用Perl风格的语法，用于在验证时测试域值。
     *   如果提供了该属性值，这个正则表达式仅当基本Ext验证器都返回true时被调用，调用时会传给它当前的域值。
     *   如果测试失败，这个域将用{@link #regexText}指定的文本标记为不合法。"
     */
    public void setRegex(ValueExpression regex) {
        setValueExpression("regex", regex);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} regexText The error text to display if {@link #regex} is used and the test fails during validation (defaults to "")"
     * description_zh_CN="当使用了{@link #regex}并且验证失败时显示的错误提示文本，缺省为\"\"。"
     */
    public void setRegexText(ValueExpression regexText) {
        setValueExpression("regexText", regexText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} emptyText The default text to display in an empty field (defaults to null)."
     * description_zh_CN="显示在空域中的缺省文本，缺省为null。"
     */
    public void setEmptyText(ValueExpression emptyText) {
        setValueExpression("emptyText", emptyText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} emptyClass The CSS class to apply to an empty field to style the {@link #emptyText} (defaults to
     *   'x-form-empty-field').  This class is automatically added and removed as needed depending on the current field value."
     * description_zh_CN="应用到空域的设定{@link #emptyText}的CSS类，缺省为'x-form-empty-field'，
     *   依据当前的域值，这个类会被自动的增加和删除。"
     */
    public void setEmptyClass(ValueExpression emptyClass) {
        setValueExpression("emptyClass", emptyClass);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="The type attribute for input fields -- e.g. radio, text, password (defaults to "text")."
     * description_zh_CN="指定文本框的类型，例如：radio,text,password(默认是"text")"
     */
    public void setInputType(ValueExpression inputType) {
        setValueExpression("inputType", inputType);
    }
    
    ////////////////////////
    // ?    
    /**
     * @jsp.attribute type="int"
     * description="文本框的宽度，单位是象素。"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }
}
