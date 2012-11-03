/*
 * $Id 
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

import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.createRecord;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.encodeRecordValue;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getOutputColumns;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIEditDataGrid;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;
import org.operamasks.org.json.simple.JSONValue;

public class AjaxEditDataGridRenderer extends AjaxDataGridRenderer{
    private static final String MODIFIED_DATA_FIELD = "_modifiedData";
    private static final String REMOVED_DATA_FIELD = "_removedData";
    private static final String OUTTER_CONTAINER = "_outter";
    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String modifiedDataValue = paramMap.get(component.getClientId(context) + MODIFIED_DATA_FIELD);
        String removedDataValue = paramMap.get(component.getClientId(context) + REMOVED_DATA_FIELD);
        UIEditDataGrid grid = (UIEditDataGrid)component;
        if (null != modifiedDataValue || removedDataValue != null) {
            JSONArray modifiedData = null;
            JSONArray removedData = null;
            JSONObject transData = null;
            try {
                modifiedData = (JSONArray) JSONValue.parse(modifiedDataValue);
                removedData = (JSONArray) JSONValue.parse(removedDataValue);
                transData = new JSONObject();
                transData.put("modifed", modifiedData);
                transData.put("removed", removedData);
                grid.setTransData(transData);
            } catch (Throwable e) {
                // Ignore;
            }
            EditDataGridHelper.applyData(context, grid, new EditDataGridHelper.ChangedData(modifiedData, removedData));
            if(isAjaxHtmlResponse(context)) {
                AjaxHtmlResponseWriter out = (AjaxHtmlResponseWriter)context.getResponseWriter();
                out.setViewStateChanged();
            }
            if(isAjaxResponse(context)) {
                AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
                out.setViewStateChanged();
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        UIEditDataGrid grid = (UIEditDataGrid)component;
        if (isAjaxResponse(context)) {
            Formatter fmt = new Formatter();
            String jsvar = FacesUtils.getJsvar(context, grid);

            if (grid.isCommit()) {
                fmt.format("%s.getStore().commitChanges();\n",jsvar);   
                fmt.format("%s.reset();\n",jsvar);   
                AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
                grid.setCommit(false);
                grid.setAddedData(null);
                grid.setModifiedData(null);
                grid.setRemovedData(null);
                out.setViewStateChanged();
                out.writeActionScript(fmt.toString());
            }

            if (grid.getInsertRow() != -1) {
                fmt.format("%s.stopEditing();\n",jsvar);
                List<UIColumn> outPutColumns = getOutputColumns(component);
                fmt.format("var newRecordDefinition = Ext.data.Record.create(%s);\n",createRecord(outPutColumns));
                String modifiedVar = jsvar + "_modified";
                StringBuilder modified = new StringBuilder();

                fmt.format("var %s=[];\n", modifiedVar);
                fmt.format("var newRecord = new newRecordDefinition(%s);\n", encodeRecordValue(outPutColumns, grid.getInsertRowData(), modified, modifiedVar));
                fmt.format("newRecord.dirty = true;\n");
                fmt.format(modified.toString());
                fmt.format("newRecord.modified = %s;\n", modifiedVar);
                fmt.format("%s.getStore().insert(%d, newRecord);\n",jsvar, grid.getInsertRow());   
                fmt.format("%s.getStore().afterEdit(newRecord);\n",jsvar);
                fmt.format("%s.startEditing(0, 0);\n",jsvar);   
                grid.insertRow(-1);
                AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
                out.setViewStateChanged();
                out.writeActionScript(fmt.toString());
            } 
            else {
                super.encodeEnd(context, component);
            }
            
        }
        else {
            ResponseWriter out = context.getResponseWriter();
            String clientId = component.getClientId(context);
            //modified data filed
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", clientId + MODIFIED_DATA_FIELD, "clientId");
            out.writeAttribute("name", clientId + MODIFIED_DATA_FIELD, "clientId");
            out.endElement("input");

            //removed data filed
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", clientId + REMOVED_DATA_FIELD, "clientId");
            out.writeAttribute("name", clientId + REMOVED_DATA_FIELD, "clientId");
            out.endElement("input");

            // encode grid container
            out.startElement("div", component);
            out.writeAttribute("id", component.getClientId(context) + OUTTER_CONTAINER, "clientId");
            out.writeAttribute("style", "padding-bottom:3px;", "style");
            super.encodeEnd(context, component);
            out.endElement("div");
            out.write("\n");
        }
    }
    
    protected void encodeGridDefinition(
            Formatter fmt, 
            UIDataGrid grid,
            YuiExtResource resource, 
            StringBuilder buf, 
            String jsvar, 
            String clientId,
            String dsvar, 
            String cmvar, 
            String smvar) {
        fmt.format("%s = new Ext.grid.FacesEditorGrid({store:%s,cm:%s,sm:%s,layout:'fit'", jsvar,
                dsvar, cmvar, smvar);
        encodeGridConfig(fmt, grid, resource);
        //fmt.format(",renderTo:'%s'", clientId);
        fmt.format(",enableColLock:false");
        fmt.format(",modifiedDataField:'%s'" , clientId + MODIFIED_DATA_FIELD);
        fmt.format(",removedDataField:'%s'" , clientId + REMOVED_DATA_FIELD);
        fmt.format(",plugins: new Ext.ux.plugins.XGrid()");
        buf.append("});\n");
        //encodeEditorListener(buf, grid, jsvar);
        fmt.format("%s.on('load', function() {%s.reset();});", dsvar, jsvar);
        FacesContext context = FacesContext.getCurrentInstance();
        UIForm form = getParentForm(grid);
        if (form != null) {
            fmt.format("var _parentForm = document.forms['%s'];\nif(!_parentForm._validators){_parentForm._validators=[];}\n", form.getClientId(context));
            fmt.format("_parentForm._validators.push(%s.getFormValidator());", jsvar);
        }
    }
    
    protected String getRenderScript(String jsvar, String clientId) {
		return String.format("%s.render('%s');\n", jsvar, clientId);
	}
    
    @Override
    public void provideResource(final ResourceManager rm, final UIComponent component) {
    	YuiExtResource.register(rm, "Ext.grid.FacesEditorGrid");
        String id = "urn:ajaxEditDataGrid:" + component.getClientId(FacesContext.getCurrentInstance());
        rm.registerResource(new AbstractResource(id) {
            /**
             * render grid scripts at last 
             */
            public int getPriority() {
                return LOW_PRIORITY - 300;
            }

            @Override
            public void encodeBegin(FacesContext context) throws IOException {
                AjaxEditDataGridRenderer.super.provideResource(rm, component);
            }
        });
    }
}
