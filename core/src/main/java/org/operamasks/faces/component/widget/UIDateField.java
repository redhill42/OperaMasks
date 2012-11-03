/*
 * $Id: UIDateField.java,v 1.13 2008/03/11 03:21:00 lishaochuan Exp $
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

/**
 * @deprecated 此类已经被org.operamasks.faces.component.form.impl.UIDateField代替
 */
@Deprecated
public class UIDateField extends UITextField {
    public static final java.lang.String COMPONENT_TYPE = "org.operamasks.faces.widget.DateField";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.DateField";
    private DateFieldConfig dateConfig = new DateFieldConfig();
        
    public UIDateField() {
        setRendererType(DEFAULT_RENDERER_TYPE);
        String format = getDateConfig().getFormat();
        if (format == null)
        	format = new DateFieldConfig().getFormat();
        
        DateValidator dateValidator = new DateValidator();
        dateValidator.setFormat(format);
        addValidator(dateValidator);
    }    
    
    public UIDateField(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    /**
     * Ext属性，这个数组中的元素必须是按字母排序的。
     */
    private static final String[] EXT_CONFIGS = {
        "disabledDates",
        "disabledDatesText",
        "disabledDays",
        "disabledDaysText",
        "format",
        "invalidText",
        "maxText",
        "maxValue",
        "minText",
        "minValue",
        "triggerClass"};
    protected String[] getExtConfigElements() {
        String[] parentArray = super.getExtConfigElements();
        String[] newArray = new String[EXT_CONFIGS.length + parentArray.length];
        System.arraycopy(EXT_CONFIGS, 0, newArray, 0, EXT_CONFIGS.length);
        System.arraycopy(parentArray, 0, newArray, EXT_CONFIGS.length, parentArray.length);
        Arrays.sort(newArray);
        return newArray;
    }
    public String getFormat() {
        String result = dateConfig.getFormat();
        if (result != null) {
            return result;
        }
        
        return FacesUtils.getExpressionValue(this, "format");
    }

    public void setFormat(String format) {
        dateConfig.setFormat(format);
    }
    
    public String getDisabledDays() {
        String result = dateConfig.getDisabledDays();
        if (result != null) {
            return result;
        }
        return FacesUtils.getExpressionValue(this, "disabledDays");
    }

    public void setDisabledDays(String disabledDays) {
        dateConfig.setDisabledDays(disabledDays);
    }
    
    public String getDisabledDaysText() {
        String result = dateConfig.getDisabledDaysText();
        if (result != null) {
            return result;
        }
        return FacesUtils.getExpressionValue(this, "disabledDaysText");
    }

    public void setDisabledDaysText(String disabledDaysText) {
        dateConfig.setDisabledDaysText(disabledDaysText);
    }
    
    public String getDisabledDates() {
        return dateConfig.getDisabledDates();
    }

    public void setDisabledDates(String disabledDates) {
        dateConfig.setDisabledDates(disabledDates);
    }
    
    public String getDisabledDatesText() {
        return dateConfig.getDisabledDatesText();
    }

    public void setDisabledDatesText(String disabledDatesText) {
        dateConfig.setDisabledDatesText(disabledDatesText);
    }
    
    public String getMinValue() {
        return dateConfig.getMinValue();
    }

    public void setMinValue(String minValue) {
        dateConfig.setMinValue(minValue);
    }
    
    public String getMaxValue() {
        return dateConfig.getMaxValue();
    }

    public void setMaxValue(String maxValue) {
        dateConfig.setMaxValue(maxValue);
    }
    
    public String getMinText() {
        return dateConfig.getMinText();
    }

    public void setMinText(String minText) {
        dateConfig.setMinText(minText);
    }
    
    public String getMaxText() {
        return dateConfig.getMaxText();
    }

    public void setMaxText(String maxText) {
        dateConfig.setMaxText(maxText);
    }
    
    public String getInvalidText() {
        return dateConfig.getInvalidText();
    }

    public void setInvalidText(String invalidText) {
        dateConfig.setInvalidText(invalidText);
    }
    
    public String getTriggerClass() {
        return dateConfig.getTriggerClass();
    }

    public void setTriggerClass(String triggerClass) {
        dateConfig.setTriggerClass(triggerClass);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, dateConfig)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        dateConfig = (DateFieldConfig)restoreAttachedState(context, values[i++]);
    }
    
    protected DateFieldConfig getExtConfig() {
        return getDateConfig();
    }
    
    public DateFieldConfig getDateConfig() {
        dateConfig.merge(getTextConfig());
        return dateConfig;
    }

    public void setDateConfig(DateFieldConfig dateConfig) {
        this.dateConfig = dateConfig;
    }
}
