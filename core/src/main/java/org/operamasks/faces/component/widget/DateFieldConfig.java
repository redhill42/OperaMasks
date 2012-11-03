/*
 * $Id: DateFieldConfig.java,v 1.6 2007/12/11 04:20:12 jacky Exp $
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

public class DateFieldConfig extends TextFieldConfig {

    private static final long serialVersionUID = 6008152004422068064L;
    
    public String getFormat() {
        return get("format", "Y/m/d");
    }

    public void setFormat(String format) {
        set("format", format);
    }
    
    public String getDisabledDays() {
        return get("disabledDays", null);
    }

    public void setDisabledDays(String disabledDays) {
        set("disabledDays", disabledDays);
    }
    
    public String getDisabledDaysText() {
        return get("disabledDaysText", null);
    }

    public void setDisabledDaysText(String disabledDaysText) {
        set("disabledDaysText", disabledDaysText);
    }
    
    public String getDisabledDates() {
        return get("disabledDates", null);
    }

    public void setDisabledDates(String disabledDates) {
        set("disabledDates", disabledDates);
    }
    
    public String getDisabledDatesText() {
        return get("disabledDatesText", null);
    }

    public void setDisabledDatesText(String disabledDatesText) {
        set("disabledDatesText", disabledDatesText);
    }
    
    public String getMinValue() {
        return get("minValue", null);
    }

    public void setMinValue(String minValue) {
        set("minValue", minValue);
    }
    
    public String getMaxValue() {
        return get("maxValue", null);
    }

    public void setMaxValue(String maxValue) {
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
    
    public String getInvalidText() {
        return get("invalidText", null);
    }

    public void setInvalidText(String invalidText) {
        set("invalidText", invalidText);
    }
    
    public String getTriggerClass() {
        return get("triggerClass", null);
    }

    public void setTriggerClass(String triggerClass) {
        set("triggerClass", triggerClass);
    }
    public String toScript(String exclusion) {
        String ret = super.toScript(exclusion);
        
        if (ret != null && !ret.equals(""))
        	ret += ",";
        
        if (!isSet("format")) {
            ret += "\nformat:'" + getFormat() + "'";
        }
        return ret;
    }
    @Override
    protected void scriptOnStr(StringBuilder buf, String propName, String propValue) {
        if (propName.equals("disabledDays") || propName.equals("disabledDates")) {            
            buf.append(propValue);            
        } else {
            super.scriptOnStr(buf, propName, propValue);
        }
    }    
}
