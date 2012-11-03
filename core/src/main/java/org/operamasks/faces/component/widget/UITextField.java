/*
 * $Id: UITextField.java,v 1.10 2008/03/11 03:21:00 lishaochuan Exp $
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

package org.operamasks.faces.component.widget;

import java.util.Arrays;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.form.impl.UITextField代替
 */
@Deprecated
public class UITextField extends HtmlInputText {
    public static final java.lang.String COMPONENT_TYPE = "org.operamasks.faces.widget.TextField";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.TextField";
    private TextFieldConfig textConfig = new TextFieldConfig();
    
    private String jsvar;    
    
    public UITextField() {
        setRendererType(DEFAULT_RENDERER_TYPE);        
    }
    
    public UITextField(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }
    
    /**
     * Ext属性，这个数组中的元素必须是按字母排序的。
     */
    private static final String[] EXT_CONFIGS = {
        "allowBlank",
        "blankText",
        "disableKeyFilter",
        "emptyClass",
        "emptyText",
        "extValidator",
        "grow",
        "growMax",
        "growMin",
        "maskRe",
        "maxlength", // Ext的属性为 maxLength, Html input text 元素的属性为 maxlength
        "maxLengthText",
        "minLength",
        "minLengthText",
        "regex",
        "regexText",
        "selectOnFocus",
        "vtype",
        "width",
        "inputType"
    	};
    protected String[] getExtConfigElements() {
        return EXT_CONFIGS;
    }
    @Override
    public void setValueExpression(String name, ValueExpression binding) {
        super.setValueExpression(name, binding);
        
        ExtConfig extConfig = getExtConfig();
        if (Arrays.binarySearch(getExtConfigElements(), name) >= 0) {
            extConfig.set(name, binding);
            if (binding != null && binding.isLiteralText()) {
                ELContext context = FacesContext.getCurrentInstance().getELContext();
                try {
                    // 用实际的值覆盖前面设置的 ValueExpression
                    extConfig.set(name, binding.getValue(context));
                } catch (ELException ele) {
                    throw new FacesException(ele);
                }                
            }
        }
    }

    public Boolean getGrow() {
        return textConfig.getGrow();
    }

    public void setGrow(Boolean grow) {
        textConfig.setGrow(grow);
    }
    
    public Integer getGrowMin() {
        return textConfig.getGrowMin();
    }

    public void setGrowMin(Integer growMin) {
        textConfig.setGrowMin(growMin);
    }
    
    public Integer getGrowMax() {
        return textConfig.getGrowMax();
    }

    public void setGrowMax(Integer value) {
        textConfig.setGrowMax(value);
    }
    
    public String getVtype() {
        return textConfig.getVtype();
    }

    public void setVtype(String value) {
        textConfig.setVtype(value);
    }
    
    public String getMaskRe() {
        return textConfig.getMaskRe();
    }

    public void setMaskRe(String value) {
        textConfig.setMaskRe(value);
    }
    
    public Boolean getDisableKeyFilter() {
        return textConfig.getDisableKeyFilter();
    }

    public void setDisableKeyFilter(Boolean value) {
        textConfig.setDisableKeyFilter(value);
    }
    
    public Boolean getAllowBlank() {
        return textConfig.getAllowBlank();
    }

    public void setAllowBlank(Boolean value) {
        textConfig.setAllowBlank(value);
    }
    
    public Integer getMinLength() {
        return textConfig.getMinLength();
    }

    public void setMinLength(Integer value) {
        textConfig.setMinLength(value);
    }
    
    public int getMaxlength() {
        return super.getMaxlength();
    }
    
    public void setMaxlength(int value) {
        super.setMaxlength(value);
        textConfig.setMaxLength(value);
    }
    public String getMinLengthText() {
        return textConfig.getMinLengthText();
    }

    public void setMinLengthText(String value) {
        textConfig.setMinLengthText(value);
    }
    
    public String getMaxLengthText() {
        return textConfig.getMaxLengthText();
    }

    public void setMaxLengthText(String value) {
        textConfig.setMaxLengthText(value);
    }
    
    public Boolean getSelectOnFocus() {
        return textConfig.getSelectOnFocus();
    }

    public void setSelectOnFocus(Boolean value) {
        textConfig.setSelectOnFocus(value);
    }
    
    public String getBlankText() {
        return textConfig.getBlankText();
    }

    public void setBlankText(String value) {
        textConfig.setBlankText(value);
    }
    
    public String getExtValidator() {
        return textConfig.getExtValidator();
    }

    public void setExtValidator(String value) {
        textConfig.setExtValidator(value);
    }
    
    public String getRegex() {
        return textConfig.getRegex();
    }

    public void setRegex(String value) {
        textConfig.setRegex(value);
    }
    
    public String getRegexText() {
        return textConfig.getRegexText();
    }

    public void setRegexText(String value) {
        textConfig.setRegexText(value);
    }
    
    public String getEmptyText() {
        return textConfig.getEmptyText();
    }

    public void setEmptyText(String value) {
        textConfig.setEmptyText(value);
    }
    
    public String getEmptyClass() {
        return textConfig.getEmptyClass();
    }

    public void setEmptyClass(String value) {
        textConfig.setEmptyClass(value);
    }    
    
    public Integer getWidth() {
        return textConfig.getWidth();
    }
    
    public void setWidth(Integer value) {
        textConfig.setWidth(value);
    }
    
    public String getInputType(){
    	return this.textConfig.getInputType();
    }
    
    public void setInputType(String inputType){
    	this.textConfig.setInputType(inputType);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            saveAttachedState(context, textConfig)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = ((String)values[i++]);
        textConfig = (TextFieldConfig)restoreAttachedState(context, values[i++]);
    }
    
    protected TextFieldConfig getExtConfig() {
        return getTextConfig();
    }
    
    public TextFieldConfig getTextConfig() {
        return textConfig;
    }

    public void setTextConfig(TextFieldConfig textConfig) {
        this.textConfig = textConfig;
    }
}
