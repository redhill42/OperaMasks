/*
 * $Id: ComboRenderer.java,v 1.26 2007/12/29 11:11:37 yangdong Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.operamasks.faces.component.widget.UICombo;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.UISelectRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class ComboRenderer extends UISelectRenderer implements ResourceProvider {
    protected static final String STORE_VAR_SUFFIX = "_store";
    
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
    
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource.register(rm, "Ext.form.ComboBox", "Ext.QuickTips"); 
        addInitScript(FacesContext.getCurrentInstance(), component);
    }
    
    private void encodeCombo(StringBuilder buf, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();        
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        String clientId = component.getClientId(context);
        
        buf.append(jsvar_store).append("=new Ext.data.SimpleStore({\n");
        buf.append("fields: ['text', 'value'], data: []});\n");
        
        buf.append(jsvar).append("=new Ext.form.ComboBox({\n");
        buf.append("store: ").append(jsvar_store).append(",\n");
        buf.append("displayField:'text',\n");
        buf.append("valueField:'value',\n");
        buf.append("hiddenName:'").append(clientId).append("',\n");
        buf.append("mode: 'local',\n");
        buf.append("triggerAction: 'all',\n");
        buf.append(String.format("disabled: %b", component.getAttributes().get("disabled")));
        if (component instanceof UICombo) {            
            UICombo combo = (UICombo)component;
            String s = combo.getComboConfig().toScript();
            if (s.trim().length() > 0) {
                buf.append(",\n");
                buf.append(s);
            }
        } else {
            // JSF标准的下拉列表框不能编辑
            buf.append(", editable: false");
        }
        
        buf.append("});\n");
        
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
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;    
        
        renderInput(context, component);
    }
    protected void renderInput(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter out = context.getResponseWriter();
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
    }
    
    private void addInitScript(FacesContext context, UIComponent component) {
        YuiExtResource resource = (YuiExtResource)ResourceManager.getInstance(context).getRegisteredResource(YuiExtResource.RESOURCE_ID);
        if (resource == null) {
            resource = YuiExtResource.register(ResourceManager.getInstance(context), "Ext.form.ComboBox");
        }
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        resource.addVariable(jsvar);
        resource.addVariable(jsvar_store);
        
        StringBuilder buf = new StringBuilder();
        encodeCombo(buf, component);        
        encodeOptions(buf, context, component, getSelectItems(context, component));
        
        resource.addInitScript(buf.toString());        
        resource.releaseVariable(jsvar);
        resource.releaseVariable(jsvar_store);
    }
    
    private String getVisibleId(String clientId) {
        return clientId + "_visible";
    }

    protected void encodeOptions(StringBuilder buf, FacesContext context, UIComponent component, List<SelectItem> items) {
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
        String jsvar = FacesUtils.getJsvar(context, component);
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
}
