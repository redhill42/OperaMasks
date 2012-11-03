/*
 * $Id: UICalcNumberField.java,v 1.5 2007/12/11 04:20:12 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

public class UICalcNumberField extends UITextField {
    public static final java.lang.String COMPONENT_TYPE = "org.operamasks.faces.widget.CalcNumberField";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.CalcNumberField";
    private CalcNumberFieldConfig calcFieldConfig = new CalcNumberFieldConfig();
        
    public UICalcNumberField() {
        setRendererType(DEFAULT_RENDERER_TYPE);        
    }    
    
    public UICalcNumberField(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    /**
     * Ext属性，这个数组中的元素必须是按字母排序的。
     */
    private static final String[] EXT_CONFIGS = {
        "allowDecimals",
        "allowNegative",
        "maxText",
        "maxValue",
        "minText",
        "minValue",
        "nanText",
        "triggerClass"};
    
    protected String[] getExtConfigElements() {
        String[] parentArray = super.getExtConfigElements();
        String[] newArray = new String[EXT_CONFIGS.length + parentArray.length];
        System.arraycopy(EXT_CONFIGS, 0, newArray, 0, EXT_CONFIGS.length);
        System.arraycopy(parentArray, 0, newArray, EXT_CONFIGS.length, parentArray.length);
        Arrays.sort(newArray);
        return newArray;
    }
    
    public Boolean getAllowDecimals() {
        Boolean result = calcFieldConfig.getAllowDecimals();
        if (result != null) {
            return result;
        }
        
        return FacesUtils.getExpressionValue(this, "allowDecimals");
    }

    public void setAllowDecimals(Boolean allowDecimals) {
        calcFieldConfig.setAllowDecimals(allowDecimals);
    }
    
    public Boolean getAllowNegative() {
        Boolean result = calcFieldConfig.getAllowDecimals();
        if (result != null) {
            return result;
        }
        
        return FacesUtils.getExpressionValue(this, "allowNegative");
    }

    public void setAllowNegative(Boolean allowNegative) {
        calcFieldConfig.setAllowDecimals(allowNegative);
    }
    
    public Integer getMinValue() {
        return calcFieldConfig.getMinValue();
    }

    public void setMinValue(Integer minValue) {
        calcFieldConfig.setMinValue(minValue);
    }
    
    public Integer getMaxValue() {
        return calcFieldConfig.getMaxValue();
    }

    public void setMaxValue(Integer maxValue) {
        calcFieldConfig.setMaxValue(maxValue);
    }
    
    public String getMinText() {
        return calcFieldConfig.getMinText();
    }

    public void setMinText(String minText) {
        calcFieldConfig.setMinText(minText);
    }
    
    public String getMaxText() {
        return calcFieldConfig.getMaxText();
    }

    public void setMaxText(String maxText) {
        calcFieldConfig.setMaxText(maxText);
    }
    
    public String getNanText() {
        return calcFieldConfig.getNanText();
    }

    public void setNanText(String invalidText) {
        calcFieldConfig.setNanText(invalidText);
    }
    
    public String getTriggerClass() {
        return calcFieldConfig.getTriggerClass();
    }

    public void setTriggerClass(String triggerClass) {
        calcFieldConfig.setTriggerClass(triggerClass);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, calcFieldConfig)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        calcFieldConfig = (CalcNumberFieldConfig)restoreAttachedState(context, values[i++]);
    }
    
    protected CalcNumberFieldConfig getExtConfig() {
        return getCalcNumberConfig();
    }
    
    public CalcNumberFieldConfig getCalcNumberConfig() {
        calcFieldConfig.merge(getTextConfig());
        return calcFieldConfig;
    }

    public void setCalcNumberConfig(CalcNumberFieldConfig calcFieldConfig) {
        this.calcFieldConfig = calcFieldConfig;
    }
}
