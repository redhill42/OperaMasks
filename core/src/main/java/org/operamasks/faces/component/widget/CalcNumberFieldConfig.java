/*
 * $Id: CalcNumberFieldConfig.java,v 1.3 2007/07/02 07:37:43 jacky Exp $
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

public class CalcNumberFieldConfig extends TextFieldConfig {
    
    private static final long serialVersionUID = -4575952248805057212L;

    public Boolean getAllowDecimals() {
        return get("allowDecimals", null);
    }

    public void setAllowDecimals(Boolean allowDecimals) {
        set("allowDecimals", allowDecimals);
    }
    
    public Boolean getAllowNegative() {
        return get("allowNegative", null);
    }

    public void setAllowNegative(Boolean allowNegative) {
        set("allowNegative", allowNegative);
    }    
    
    public Integer getMinValue() {
        return get("minValue", null);
    }

    public void setMinValue(Integer minValue) {
        set("minValue", minValue);
    }
    
    public Integer getMaxValue() {
        return get("maxValue", null);
    }

    public void setMaxValue(Integer maxValue) {
        set("maxValue", maxValue);
    }
    
    public String getMinText() {
        return get("minText", null);
    }

    public void setMinText(String minText) {
        set("minText", minText);
    }
    
    public String getMaxText() {
        return get("maxText", null);
    }

    public void setMaxText(String maxText) {
        set("maxText", maxText);
    }
    
    public String getNanText() {
        return get("invalidText", null);
    }

    public void setNanText(String nanText) {
        set("nanText", nanText);
    }
    
    public String getTriggerClass() {
        return get("triggerClass", null);
    }

    public void setTriggerClass(String triggerClass) {
        set("triggerClass", triggerClass);
    }
}
