/*
 * $Id: TextFieldConfig.java,v 1.10 2008/03/11 03:21:00 lishaochuan Exp $
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

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class TextFieldConfig extends ExtConfig {

    private static final long serialVersionUID = -7549988648411347365L;

    public Boolean getGrow() {
        return get("grow", null);
    }

    public void setGrow(Boolean grow) {
        set("grow", grow);
    }
    
    public Integer getGrowMin() {
        return get("growMin", null);
    }

    public void setGrowMin(Integer value) {
        set("growMin", value);
    }
    
    public Integer getGrowMax() {
        return get("growMax", null);
    }

    public void setGrowMax(Integer value) {
        set("growMax", value);
    }
    
    public String getVtype() {
        return get("vtype", null);
    }

    public void setVtype(String value) {
        set("vtype", value);
    }
    
    public String getMaskRe() {
        return get("maskRe", null);
    }

    public void setMaskRe(String value) {
        set("maskRe", value);
    }
    
    public Boolean getDisableKeyFilter() {
        return get("disableKeyFilter", null);
    }

    public void setDisableKeyFilter(Boolean value) {
        set("disableKeyFilter", value);
    }
    
    public Boolean getAllowBlank() {
        return get("allowBlank", null);
    }

    public void setAllowBlank(Boolean value) {
        set("allowBlank", value);
    }
    
    public Integer getMinLength() {
        return get("minLength", null);
    }

    public void setMinLength(Integer value) {
        set("minLength", value);
    }
    
    public Integer getMaxLength() {
        return get("maxlength", null);
    }

    public void setMaxLength(Integer value) {
        set("maxlength", value);
    }
    
    public String getMinLengthText() {
        return get("minLengthText", null);
    }

    public void setMinLengthText(String value) {
        set("minLengthText", value);
    }
    
    public String getMaxLengthText() {
        return get("maxLengthText", null);
    }

    public void setMaxLengthText(String value) {
        set("maxLengthText", value);
    }
    
    public Boolean getSelectOnFocus() {
        return get("selectOnFocus", null);
    }

    public void setSelectOnFocus(Boolean value) {
        set("selectOnFocus", value);
    }
    
    public String getBlankText() {
        return get("blankText", null);
    }

    public void setBlankText(String value) {
        set("blankText", value);
    }
    
    public String getExtValidator() {
        return get("extValidator", null);
    }

    public void setExtValidator(String value) {
        set("extValidator", value);
    }
    
    public String getRegex() {
        return get("regex", null);
    }

    public void setRegex(String value) {
        set("regex", value);
    }
    
    public String getRegexText() {
        return get("regexText", null);
    }

    public void setRegexText(String value) {
        set("regexText", value);
    }
    
    public String getEmptyText() {
        return get("emptyText", null);
    }

    public void setEmptyText(String value) {
        set("emptyText", value);
    }
    
    public String getEmptyClass() {
        return get("emptyClass", null);
    }

    public void setEmptyClass(String value) {
        set("emptyClass", value);
    }
    
    public Integer getWidth() {
        return get("width", null);
    }

    public void setWidth(Integer width) {
        set("width", width);
    }
    
    public String getInputType(){
    	return get("inputType",null);
    }
    
    public void setInputType(String inputType){
    	set("inputType", inputType);
    }
    
    protected String getReplacedName(String name) {
        if ("extValidator".equals(name)) {
            return "validator";
        } else if ("maxlength".equals(name)) {
            return "maxLength";
        } else {
            return super.getReplacedName(name);
        }
    }
    
    @Override
    protected void scriptOnStr(StringBuilder buf, String propName, String propValue) {
        if (propName.equals("regex") || propName.equals("maskRe") || propName.equals("extValidator")) { 
            buf.append(propValue);            
        } else {
            super.scriptOnStr(buf, propName, propValue);
        }
    }
}
