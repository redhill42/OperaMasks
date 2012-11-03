/*
 * $Id: ComboRenderHandler.java,v 1.4 2008/04/22 01:23:08 lishaochuan Exp $
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

package org.operamasks.faces.render.form;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;


@ExtClass("Ext.form.ComboBox")
@DependPackages( { "Ext.form", "Ext.data.Store", "Ext.View" })
public class ComboRenderHandler extends AbstractFieldRenderHandler {
    protected static final String STORE_VAR_SUFFIX = "_store";
    protected static final String VALUE_VAR_SUFFIX = "_value";

	@Override
	protected String getHtmlMarkup(){
		return "input";
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        UIField field = (UIField)component;
        if (Boolean.TRUE.equals(field.getReadOnly()) || Boolean.TRUE.equals(field.getDisabled()))
            return;

        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String newValue = requestMap.get(clientId + VALUE_VAR_SUFFIX);
        setSubmittedValue(component, newValue);
    }
	
	@Override
	protected void processExtConfig(FacesContext context, UIComponent component, ExtConfig config) {
		String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        config.remove("value");
		config.set("store", jsvar_store, true);
		config.set("displayField","text");
		config.set("valueField","value");
		config.set("mode","local");
		config.set("triggerAction","all");
		config.set("hiddenName",component.getClientId(context) + VALUE_VAR_SUFFIX);
    }
    

    protected boolean isSelected(FacesContext context, UIComponent component,
                                 Object itemValue, String itemStr)
    {
    	Object currentValue = getCurrentValue(context, component);
        if (currentValue == null) {
            return itemValue == null;
        } else {
            Class itemType = currentValue.getClass();
            itemValue = Coercion.coerce(itemValue, itemType);
            return (currentValue.equals(itemValue));
        }
    }
    
    @Override
	public void resourceBegin(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
    	YuiExtResource resource = getResourceInstance(rm);
    	StringBuffer buf = new StringBuffer();
        String jsvar = FacesUtils.getJsvar(context, component);
        encodeDataStore(resource, context, component);
        super.resourceBegin(context, component, rm);
        encodeOptions(buf, context, component, SelectItemsUtil.getSelectItems(component), jsvar);
        resource.addInitScript(buf.toString());
    }
    
    private void encodeDataStore(YuiExtResource resource, FacesContext context,  UIComponent component){
        StringBuffer buf = new StringBuffer();
    	String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        
        buf.append(jsvar_store);
        buf.append("=new Ext.data.SimpleStore({fields: ['text', 'value'], data: []});\n");
        resource.addInitScript(buf.toString());
    }

    protected void encodeOptions(StringBuffer buf, FacesContext context,
    		UIComponent component, List<SelectItem> items, String jsvar) {
    	StringBuffer bufContent = new StringBuffer();
        int[] index = {0};
        String selectedValue = null;
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                SelectItem[] groupItems = ((SelectItemGroup) item).getSelectItems();                
                for (int i = 0; i < groupItems.length; i++) {
                    String v = encodeOption(bufContent, context, component, groupItems[i], index);
                    if (v != null) {
                        selectedValue = v;
                    }
                }                
            } else {
                String v = encodeOption(bufContent, context, component, item, index);
                if (v != null) {
                    selectedValue = v;
                }
            }
        }

        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        
        if (bufContent.length() > 0) {
            buf.append("var records = new Array();\n");
            buf.append(bufContent);            
            buf.append(jsvar_store).append(".add(records);\n");            
        }
        encodeSelectValue(buf, context, component, selectedValue);
    }
    protected String encodeOption(StringBuffer buf, FacesContext context, UIComponent component, SelectItem item, int[] index) {
        String selectedValue = null;
        String itemValueStr = getFormattedValue(context, component, item.getValue());
        if (isSelected(context, component, item.getValue(), itemValueStr)) {
            selectedValue = itemValueStr;
        }
        buf.append("records[").append(index[0]++).append("] = new Ext.data.Record({");
        buf.append("text:").append(HtmlEncoder.enquote(item.getLabel(), '"')).append(",");
        buf.append("value:").append(HtmlEncoder.enquote(itemValueStr, '"'));
        buf.append("});\n");
        return selectedValue;
    }
    protected void encodeSelectValue(StringBuffer buf, FacesContext context, UIComponent component, String selectedValue) {
        String jsvar = FacesUtils.getJsvar(context, component);
        String script = "%s.setValue('%s');\n";
        if (selectedValue != null) {
        	buf.append(String.format(script, jsvar, selectedValue));
        } else {
            ValueExpression valueEl = component.getValueExpression("value");
            Object value = null;
            if(valueEl != null){
                value = valueEl.getValue(context.getELContext());
                if (value != null) {
                    buf.append(String.format(script, jsvar, value.toString()));
                } else {
                    buf.append(jsvar).append(".reset();\n");
                }
            }
        }
    }
    
    @EncodeAjaxBegin
    protected void renderAjaxResponse(FacesContext context, UIComponent component) throws IOException {
    	List<SelectItem> items = SelectItemsUtil.getSelectItems(component);
    	String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        StringBuffer buf = new StringBuffer(jsvar_store).append(".removeAll();\n");           
        encodeOptions(buf, context, component, items, jsvar);            
        out.writeScript(buf.toString());
    }


}
