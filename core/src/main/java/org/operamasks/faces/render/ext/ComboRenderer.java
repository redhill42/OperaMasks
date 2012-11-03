/*
 * $Id: ComboRenderer.java,v 1.10 2008/01/26 03:22:57 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.operamasks.faces.component.widget.UICombo;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class ComboRenderer extends SelectRenderer {
    protected final static String EXT_CLASS = "Ext.form.ComboBox";
    protected static final String SELECT_ITEMS_CHECKSUM = "org.operamasks.faces.SELECT_ITEMS_CHECKSUM";
    protected static final String STORE_VAR_SUFFIX = "_store";

    @Override
    protected String getExtClass(UIComponent component) {
        return EXT_CLASS;
    }

    @Override
    protected String[] getPackageDependencies() {
        return new String[]{
            "Ext.form.ComboBox", 
            "Ext.QuickTips"
        };
    }
    
    private static final String[] options = new String[] {
    	"disabled",
    	"listWidth",
    	"listClass",
    	"selectedClass",
    	"triggerClass",
    	"shadow",
    	"listAlign",
    	"maxHeight",
    	"minChars",
    	"typeAhead",
    	"queryDelay",
    	"selectOnFocus",
    	"resizable",
    	"handleHeight",
    	"editable",
    	"minListWidth",
    	"forceSelection",
    	"typeAheadDelay",
    	"valueNotFoundText",
    	"onTriggerClick",
    	"emptyText",
    	"width"
    };
    
    @Override
    protected ConfigOptions getConfigOptions(FacesContext context,
            UIComponent component) {
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        ConfigOptions configOptions =
        	createConfigOptions(component).
        		addItem("store", jsvar_store).
        		add("displayField", "text").
        		add("valueField", "value").
        		add("hiddenName", component.getClientId(context)).
        		add("mode", "local").
        		add("triggerAction","all");
        
        if (component instanceof UICombo) {            
        	for (String option : options) {
        		configOptions.add(option);
        	}
        } else {
            configOptions.add("editable", false);
        }
        
        return configOptions;
    }

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        String clientId = component.getClientId(context);        
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();        
        String newValue = paramMap.get(clientId);
        setSubmittedValue(component, newValue);
    }
    
    @Override
    protected String getBeginScript(YuiExtResource resource, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        resource.addVariable(jsvar);
        resource.addVariable(jsvar_store);
        
        StringBuilder buf = new StringBuilder();
        buf.append(jsvar_store).append("=new Ext.data.SimpleStore({\n");
        buf.append("fields: ['text', 'value'], data: []});\n");
        resource.releaseVariable(jsvar);
        resource.releaseVariable(jsvar_store);
        
        return buf.toString();
    }
    
    @Override
    protected String getInitScript(UIComponent component, String jsvar) {
    	StringBuilder buf = new StringBuilder();
        
        encodeCombo(buf, component, jsvar);
        FacesContext context = FacesContext.getCurrentInstance();
        encodeOptions(buf, context, component, getSelectItems(context, component), jsvar);
        
        return buf.toString();
    }
    
    public void encodeEnd(FacesContext context, UIComponent component,
            HtmlResponseWriter out) throws IOException {
        String clientId = component.getClientId(context);
        String visibleId = getVisibleId(clientId);
        out.startElement("div", component);
        out.startElement("input", component);
        out.writeAttribute("id", visibleId, "clientId");
        out.writeAttribute("name", clientId, null);
        out.writeAttribute("type", "text", null);
        renderPassThruAttributes(out, component);
        out.writeText("\n", null);        
        out.endElement("input");
        out.endElement("div");

        List<SelectItem> items = getSelectItems(context, component);
        // save checksum of select items to detect changes
        byte[] checksum = computeSelectItemsChecksum(context, component, items);
        component.getAttributes().put(SELECT_ITEMS_CHECKSUM, checksum);
    }
    
    private String getVisibleId(String clientId) {
        return clientId + "_visible";
    }

    protected static byte[] computeSelectItemsChecksum(FacesContext context, UIComponent component, List<SelectItem> items) {
        try {
            String itemsString = getSelectItemsString(context, component, items);
            MessageDigest md5 = FacesUtils.getMD5();
            byte[] result = md5.digest(itemsString.getBytes("UTF-8"));
            FacesUtils.returnMD5(md5);
            return result;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private static String getSelectItemsString(FacesContext context, UIComponent component, List<SelectItem> items) {
        StringBuilder buf = new StringBuilder();
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                buf.append("OPTGROUP\n");
                for (SelectItem groupItem : ((SelectItemGroup) item).getSelectItems()) {
                    addItemString(buf, context, component, groupItem);
                }
            } else {
                addItemString(buf, context, component, item);
            }
        }
        return buf.toString();
    }

    private static void addItemString(StringBuilder buf, FacesContext context, UIComponent component, SelectItem item) {
        buf.append("OPTION\n");
        buf.append(item.getLabel());
        buf.append('\n');
        buf.append(FacesUtils.getFormattedValue(context, component, item.getValue()));
        buf.append('\n');
        if (item.isDisabled())
            buf.append("disabled\n");
    }
    
    protected void encodeOptions(StringBuilder buf, FacesContext context,
    		UIComponent component, List<SelectItem> items, String jsvar) {
        StringBuilder bufContent = new StringBuilder();
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
            buf.append("var records = new Array();");
            buf.append(bufContent);            
            buf.append(jsvar_store).append(".add(records);\n");            
        }
        encodeSelectValue(buf, context, component, selectedValue);
    }

    protected String encodeOption(StringBuilder buf, FacesContext context, UIComponent component, SelectItem item, int[] index) {
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
    
    protected void encodeSelectValue(StringBuilder buf, FacesContext context, UIComponent component, String selectedValue) {
        String jsvar = FacesUtils.getJsvar(context, component);
        if (selectedValue != null) {
            buf.append(jsvar).append(".setValue(").append(HtmlEncoder.enquote(selectedValue, '"')).append(");");
        } else {
            Object value = getCurrentValue(context, component);
            if (value != null) {
                buf.append(jsvar).append(".setValue(").append(HtmlEncoder.enquote(value.toString(), '"')).append(");");
            } else {
                buf.append(jsvar).append(".reset();\n");
            }
        }
    }
    
    private void encodeCombo(StringBuilder buf, UIComponent component, String jsvar) {
        FacesContext context = FacesContext.getCurrentInstance();        
        String clientId = component.getClientId(context);
        
        // add onchange event support
        String onchange = (String)component.getAttributes().get("onchange");
        ValueChangeListener[] listeners = ((UIInput)component).getValueChangeListeners();
        if (listeners != null && listeners.length > 0) {
            String onchangeEvent = String.format("%s.on('select', function(_src, record, index){\n" +
                    "OM.ajax.addRequestParameter('%s',record.data['value']);\n",
                    jsvar, clientId );
            buf.append(onchangeEvent);
            UIForm form = getParentForm(component);
            String handler = String.format(
                "OM.ajax.action(%s,%s,null,%b);\n",
                ((form == null) ? "null" : "document.forms['" + form.getClientId(context) + "']"),
                HtmlEncoder.enquote(HtmlRenderer.getActionURL(context)),
                false
            );
            buf.append(handler);
            buf.append(String.format("},%s);\n", jsvar));
        }
        else if (onchange != null) {
            String onchangeEvent = String.format("%s.on('select', function(_src, record, index){\n" +
                    "OM.ajax.addRequestParameter('%s',record.data['value']);\n",
                    jsvar, clientId );
            buf.append(onchangeEvent);
            buf.append(onchange);
            if (!onchange.endsWith(";"))
                buf.append(";");
            buf.append(String.format("},%s);\n", jsvar));
        }

        ExtJsUtils.applyToContainer(buf, FacesContext.getCurrentInstance(),
                jsvar, component, getVisibleId(clientId));    
    }
}
