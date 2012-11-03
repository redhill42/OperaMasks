/*
 * $Id: ComboConfig.java,v 1.8 2008/03/11 03:21:00 lishaochuan Exp $
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

import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class ComboConfig extends TextFieldConfig {

    private static final long serialVersionUID = -4820213619727535062L;
    
    public Integer getListWidth() {
        return get("listWidth", null);
    }

    public void setListWidth(Integer listWidth) {
        set("listWidth", listWidth);
    }
    
    public String getListClass() {
        return get("listClass", null);
    }

    public void setListClass(String listClass) {
        set("listClass", listClass);
    }
    
    public String getSelectedClass() {
        return get("selectedClass", null);
    }

    public void setSelectedClass(String selectedClass) {
        set("selectedClass", selectedClass);
    }
    
    public String getTriggerClass() {
        return get("triggerClass", null);
    }

    public void setTriggerClass(String triggerClass) {
        set("triggerClass", triggerClass);
    }
    
    public String getShadow() {
        return get("shadow", null);
    }

    public void setShadow(String shadow) {
        set("shadow", shadow);
    }
    
    public String getListAlign() {
        return get("listAlign", null);
    }

    public void setListAlign(String listAlign) {
        set("listAlign", listAlign);
    }
    
    public Integer getMaxHeight() {
        return get("maxHeight", null);
    }

    public void setMaxHeight(Integer maxHeight) {
        set("maxHeight", maxHeight);
    }
    
    public Integer getMinChars() {
        return get("minChars", null);
    }

    public void setMinChars(Integer minChars) {
        set("minChars", minChars);
    }
    
    public Boolean getTypeAhead() {
        return get("typeAhead", null);
    }

    public void setTypeAhead(Boolean typeAhead) {
        set("typeAhead", typeAhead);
    }
    
    public Integer getQueryDelay() {
        return get("queryDelay", null);
    }

    public void setQueryDelay(Integer queryDelay) {
        set("queryDelay", queryDelay);
    }
    
    public Boolean getSelectOnFocus() {
        return get("selectOnFocus", null);
    }

    public void setSelectOnFocus(Boolean selectOnFocus) {
        set("selectOnFocus", selectOnFocus);
    }
    
    public Boolean getResizable() {
        return get("resizable", null);
    }

    public void setResizable(Boolean resizable) {
        set("resizable", resizable);
    }
    
    public Integer getHandleHeight() {
        return get("handleHeight", null);
    }

    public void setHandleHeight(Integer handleHeight) {
        set("handleHeight", handleHeight);
    }
    
    public Boolean getEditable() {
        return get("editable", null);
    }

    public void setEditable(Boolean editable) {
        set("editable", editable);
    }
    
    public Integer getMinListWidth() {
        return get("minListWidth", null);
    }

    public void setMinListWidth(Integer minListWidth) {
        set("minListWidth", minListWidth);
    }
    
    public Boolean getForceSelection() {
        return get("forceSelection", null);
    }

    public void setForceSelection(Boolean forceSelection) {
        set("forceSelection", forceSelection);
    }
    
    public Integer getTypeAheadDelay() {
        return get("typeAheadDelay", null);
    }

    public void setTypeAheadDelay(Integer typeAheadDelay) {
        set("typeAheadDelay", typeAheadDelay);
    }
    
    public String getValueNotFoundText() {
        return get("valueNotFoundText", null);
    }

    public void setValueNotFoundText(String valueNotFoundText) {
        set("valueNotFoundText", valueNotFoundText);
    }
    
    public String getOnTriggerClick() {
        return get("onTriggerClick", null);
    }

    public void setOnTriggerClick(String onTriggerClick) {
        set("onTriggerClick", onTriggerClick);
    }
    
    public String getEmptyText() {
        return get("emptyText", null);
    }

    public void setEmptyText(String emptyText) {
        set("emptyText", emptyText);
    }
    
    public Integer getWidth() {
        return get("width", null);
    }

    public void setWidth(Integer width) {
        set("width", width);
    }
    
    protected void scriptOnStr(StringBuilder buf, String propName, String propValue) {
        if (propName.equals("shadow")) {
            String strValue = (String)propValue;
            if (strValue.equals("true") || strValue.equals("false")) {
                buf.append(strValue);
            } else {
                buf.append(HtmlEncoder.enquote(strValue, '"'));
            }
        } else {
            super.scriptOnStr(buf, propName, propValue);
        }
    }    
}
