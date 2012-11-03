/*
 * $Id: ComboTag.java,v 1.12 2008/03/14 05:44:08 lishaochuan Exp $
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
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UICombo;
import org.operamasks.faces.webapp.html.SelectOneMenuTag;

public class ComboTag extends SelectOneMenuTag {

    @Override
    public String getComponentType() {
        return UICombo.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return "org.operamasks.faces.widget.Combo";
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     * description_zh_CN="客户端脚本使用的javascript变量名，引用脚本中ComboBox对象。"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} listWidth The width in pixels of the dropdown list (defaults to the width of the ComboBox field)"
     * description_zh_CN="下拉列表的宽度,单位为象素，缺省是下拉列表框的宽度。"
     */
    public void setListWidth(ValueExpression listWidth) {
        setValueExpression("listWidth", listWidth);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} listClass CSS class to apply to the dropdown list element (defaults to '')"
     * description_zh_CN="CSS类名，应用于下拉列表元素。"
     */
    public void setListClass(ValueExpression listClass) {
        setValueExpression("listClass", listClass);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} selectedClass CSS class to apply to the selected item in the dropdown list (defaults to 'x-combo-selected')"
     * description_zh_CN="CSS类名，应用于下拉列表中选中的元素，缺省是\"x-combo-selected\"。"
     */
    public void setSelectedClass(ValueExpression selectedClass) {
        setValueExpression("selectedClass", selectedClass);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} triggerClass An additional CSS class used to style the trigger button.  The trigger will always get the
     *   class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified (defaults to 'x-form-arrow-trigger'
     *   which displays a downward arrow icon)."
     * description_zh_CN="一个附加的CSS类，用于设定下拉按钮的风格。
     *   下拉按钮将总是使用\"x-form-trigger\"作为CSS类，triggerClass如果指定，将被附加到其后。
     *   该属性的缺省值为\"x-form-arrow-trigger\", 显示一个向下的箭头图标。"
     */
    public void setTriggerClass(ValueExpression triggerClass) {
        setValueExpression("triggerClass", triggerClass);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{Boolean/String} shadow true or "sides" for the default effect, "frame" for 4-way shadow, and "drop" for bottom-right"
     * description_zh_CN="指出下拉列表是否显示阴影，以及阴影的样式。
     *   <p>
     *   如果值为\"false\"，表示不显示阴影；
     *   如果值为\"true\"或者\"sides\""，表示显示缺省效果；
     *   如果值为\"frame\"，表示显示4个方向的阴影；
     *   如果值为\"drop\"，表示显示右下方的阴影。"
     */
    public void setShadow(ValueExpression shadow) {
        setValueExpression("shadow", shadow);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} listAlign A valid anchor position value. See {@link Ext.Element#alignTo} for details on supported
     *   anchor positions (defaults to 'tl-bl')"
     * description_zh_CN="一个合法的锚点位置值（缺省是\"tl-bl\"），描述下拉列表的显示位置。
     *   关于锚点位置值的具体细节，请参考Ext文档{@link Ext.Element#alignTo}。"
     */
    public void setListAlign(ValueExpression listAlign) {
        setValueExpression("listAlign", listAlign);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} maxHeight The maximum height in pixels of the dropdown list before scrollbars are shown (defaults to 300)"
     * description_zh_CN="在滚动条出现以前，下拉列表的最大高度，单位为象素，缺省为300。"
     */
    public void setMaxHeight(ValueExpression maxHeight) {
        setValueExpression("maxHeight", maxHeight);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} minChars The minimum number of characters the user must type before autocomplete and typeahead activate
     *   (defaults to 4, does not apply if editable = false)"
     * description_zh_CN="在自动完成动作激活以前要求输入的最少字符数，缺省是4，如果editable=false，该属性无效。"
     */
    public void setMinChars(ValueExpression minChars) {
        setValueExpression("minChars", minChars);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} typeAhead True to populate and autoselect the remainder of the text being typed after a configurable
     *   delay (typeAheadDelay) if it matches a known value (defaults to false)"
     * description_zh_CN="如果为true，当找到一个已知的匹配，并且经过typeAheadDelay指定的延迟时间后，
     *   会自动完成输入并且自动选择余下的文本。缺省值为false。"
     */
    public void setTypeAhead(ValueExpression typeAhead) {
        setValueExpression("typeAhead", typeAhead);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} queryDelay The length of time in milliseconds to delay between the start of typing and sending the
     *   query to filter the dropdown list (defaults to 500 if mode = 'remote' or 10 if mode = 'local')"
     * description_zh_CN="在开始键入字符和开始查询下拉列表之间的延迟时间长度值，单位为毫秒。
     *   远程装载数据的情况缺省值为500，本地装载数据的情况缺省值为10。（本组件目前尚未支持远程装载数据）"
     */
    public void setQueryDelay(ValueExpression queryDelay) {
        setValueExpression("queryDelay", queryDelay);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} selectOnFocus True to select any existing text in the field immediately on focus.  Only applies
     *   when editable = true (defaults to false)"
     * description_zh_CN="如果为true，当组件获得焦点，立即选中文本框中的文本。
     *   该属性尽当 editable=true 时有效。缺省值为false。"
     */
    public void setSelectOnFocus(ValueExpression selectOnFocus) {
        setValueExpression("selectOnFocus", selectOnFocus);
    }    
    
    // Ext有bug，暂时去掉该属性。
    ///**
    // * @jsp.attribute type="boolean" 
    // * description="{Boolean} resizable True to add a resize handle to the bottom of the dropdown list (defaults to false)"
    // * description_zh_CN=""
    // */
    //public void setResizable(ValueExpression resizable) {
    //    setValueExpression("resizable", resizable);
    //}
    //
    ///**
    // * @jsp.attribute type="int" 
    // * description="{Number} handleHeight The height in pixels of the dropdown list resize handle if resizable = true (defaults to 8)"
    // */
    //public void setHandleHeight(ValueExpression handleHeight) {
    //    setValueExpression("handleHeight", handleHeight);
    //}
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} editable False to prevent the user from typing text directly into the field, just like a
     *   traditional select (defaults to true)"
     * description_zh_CN="下拉列表框是否可编辑。缺省为true。"  
     */
    public void setEditable(ValueExpression editable) {
        setValueExpression("editable", editable);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} minListWidth The minimum width of the dropdown list in pixels (defaults to 70, will be ignored if
     *   listWidth has a higher value)"
     * description_zh_CN="下拉列表的最小宽度，单位为象素。缺省值是70，如果该属性的值小于listWidth属性的值，将被忽略。"
     */
    public void setMinListWidth(ValueExpression minListWidth) {
        setValueExpression("minListWidth", minListWidth);
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} forceSelection True to restrict the selected value to one of the values in the list, false to
     *   allow the user to set arbitrary text into the field (defaults to false)"
     * description_zh_CN="如果为true，表示选择的值一定要是列表中的值之一；
     *   如果为false，表示允许用户设置任何文本到文本框。缺省为false。"
     */
    public void setForceSelection(ValueExpression forceSelection) {
        setValueExpression("forceSelection", forceSelection);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} typeAheadDelay The length of time in milliseconds to wait until the typeahead text is displayed
     *   if typeAhead = true (defaults to 250)"
     * description_zh_CN="该属性当typeAhead=true时生效，表示等待直到\"自动完成\"动作发生的时间长度，单位为毫秒，缺省值为250。"
     */
    public void setTypeAheadDelay(ValueExpression typeAheadDelay) {
        setValueExpression("typeAheadDelay", typeAheadDelay);
    }    
    
    //////////////////////
    // Ext.form.TextField
        
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
     * @jsp.attribute type="boolean" 
     * description="{Boolean} disableKeyFilter True to disable input keystroke filtering (defaults to false)"
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
    public void setMaxLength(ValueExpression maxLength) {
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
     * description_zh_CN="显示在空下拉框中的缺省文本，缺省为null。"
     */
    public void setEmptyText(ValueExpression emptyText) {
        setValueExpression("emptyText", emptyText);
    }
    
    /**
     * @jsp.attribute type="java.lang.String" 
     * description="{String} emptyClass The CSS class to apply to an empty field to style the {@link #emptyText} (defaults to
     *   'x-form-empty-field').  This class is automatically added and removed as needed depending on the current field value."
     * description_zh_CN="应用到空下拉框的设定{@link #emptyText}的CSS类，缺省为'x-form-empty-field'，
     *   依据当前的域值，这个类会被自动的增加和删除。"
     */
    public void setEmptyClass(ValueExpression emptyClass) {
        setValueExpression("emptyClass", emptyClass);
    }
        
    //////////////////////
    // Ext.form.Field
    // TODO
    
    //////////////////////
    // Ext.Component
    // TODO
    
    ////////////////////////
    // ?    
    /**
     * @jsp.attribute type="int"
     * description="文本框的宽度，单位是象素。"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }
    
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
    }
}
