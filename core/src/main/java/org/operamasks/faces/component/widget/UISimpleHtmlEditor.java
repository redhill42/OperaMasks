/*
 * $Id: UISimpleHtmlEditor.java,v 1.3 2008/03/11 03:21:00 lishaochuan Exp $
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

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.form.impl.UISimpleHtmlEditor代替
 */
@Deprecated
public class UISimpleHtmlEditor extends HtmlInputText {
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.SimpleHtmlEditor";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.SimpleHtmlEditor";
    private Map<String, Object> attrs = new HashMap<String, Object>();
    
    public UISimpleHtmlEditor() {
        setRendererType(RENDERER_TYPE);        
    }
    public UISimpleHtmlEditor(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
    private Object getProperty(String key){
        Object value = this.attrs.get(key);
        if (value != null) {
            return value;
        }
        ValueExpression ve = getValueExpression(key);
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }
    private void setProperty(String key, Object value){
        this.attrs.put(key, value);
    }
    
    public String getJsvar() {
        return (String)getProperty("jsvar");
    }
    public void setJsvar(String jsvar) {
        setProperty("jsvar", jsvar);
    }
    
    public Integer getWidth() {
        return (Integer)getProperty("width");
    }
    public void setWidth(Integer width) {
        setProperty("width", width);
    }
    
    public Integer getHeight() {
        return (Integer)getProperty("height");
    }
    public void setHeight(Integer height) {
        setProperty("height", height);
    }
    
    public Boolean getEnableAlignments() {
        return (Boolean) getProperty("enableAlignments");
    }
    public void setEnableAlignments(Boolean enableAlignments) {
        setProperty("enableAlignments", enableAlignments);
    }

    public Boolean getEnableColors() {
        return (Boolean) getProperty("enableColors");
    }
    public void setEnableColors(Boolean enableColors) {
        setProperty("enableColors", enableColors);
    }

    public Boolean getEnableFont() {
        return (Boolean) getProperty("enableFont");
    }
    public void setEnableFont(Boolean enableFont) {
        setProperty("enableFont", enableFont);
    }

    public Boolean getEnableFontSize() {
        return (Boolean) getProperty("enableFontSize");
    }
    public void setEnableFontSize(Boolean enableFontSize) {
        setProperty("enableFontSize", enableFontSize);
    }

    public Boolean getEnableFormat() {
        return (Boolean) getProperty("enableFormat");
    }
    public void setEnableFormat(Boolean enableFormat) {
        setProperty("enableFormat", enableFormat);
    }

    public Boolean getEnableLinks() {
        return (Boolean) getProperty("enableLinks");
    }
    public void setEnableLinks(Boolean enableLinks) {
        setProperty("enableLinks", enableLinks);
    }

    public Boolean getEnableLists() {
        return (Boolean) getProperty("enableLists");
    }
    public void setEnableLists(Boolean enableLists) {
        setProperty("enableLists", enableLists);
    }

    public Boolean getEnableSourceEdit() {
        return (Boolean) getProperty("enableSourceEdit");
    }
    public void setEnableSourceEdit(Boolean enableSourceEdit) {
        setProperty("enableSourceEdit", enableSourceEdit);
    }
    
    public Boolean getAutoHeight() {
        return (Boolean) getProperty("autoHeight");
    }
    public void setAutoHeight(Boolean autoHeight) {
        setProperty("autoHeight", autoHeight);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            attrs
        };
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        this.attrs = (Map<String, Object>)values[i++];
    }
    public Map<String, Object> getAttrs() {
        return attrs;
    }
}
