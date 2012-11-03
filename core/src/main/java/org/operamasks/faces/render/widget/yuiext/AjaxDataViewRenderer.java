/*
 * $Id: AjaxDataViewRenderer.java,v 1.23 2008/04/21 07:40:49 lishaochuan Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIColumn;
import javax.faces.component.UIForm;
import javax.faces.FacesException;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Formatter;
import java.util.Set;

import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.delegate.ViewDelegate;
import org.operamasks.faces.render.delegate.ViewDelegateManager;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.component.widget.UIDataView;
import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.*;

public class AjaxDataViewRenderer extends DataViewRenderer
{
    private static final String JSON_TOTAL = "totalcount";
    private static final String JSON_ROOT = "records";
    private static final String JSON_VIEWSTATE = "viewState";
    private static final String JSON_PARAMS = "params";

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (!AjaxRenderKitImpl.isAjaxResponse(context))
            return;

        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);

        // decode data load request
        String requestId = paramMap.get(UIDataView.REQUEST_DATA_PARAM);
        if (requestId != null && requestId.equals(clientId)) {
            try {
                loadData(context, (UIDataView)component);
            } catch (IOException ex) {
                throw new FacesException(ex);
            }
            return; // all done, a JSON response has been generated
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIDataView view = (UIDataView)component;
        String clientId;
        synchronized (view) {
            view.setRowIndex(-1); // reset data
            clientId = component.getClientId(context);
        }

        if (isAjaxHtmlResponse(context)) {
            AjaxHtmlResponseWriter out = (AjaxHtmlResponseWriter)context.getResponseWriter();
            out.setViewStateChanged(false);
            String containerId = view.getContainer();
            if(containerId == null) {
                // encode view container
                out.startElement("div", component);
                out.writeAttribute("id", clientId, "clientId");
                renderPassThruAttributes(out, component, "rows");
                out.endElement("div");
            }
        } else if (isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.setViewStateChanged(false);

            if (view.isNeedReload()) {
                // reload data
                String jsvar = FacesUtils.getJsvar(context, view);
                out.writeActionScript(jsvar + ".store.load();\n");
                view.setNeedReload(false);
            }
        }
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();

        String template = getTemplate(context, component);
        if (template == null) {
            throw new FacesException("Missing required template attribute or facet.");
        }

        UIDataView view = (UIDataView)component;
        String clientId;
        synchronized (view) {
            view.setRowIndex(-1); // reset data
            clientId = component.getClientId(context);
        }
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.View", "Ext.data.FacesProxy");
        List<UIColumn> columns = getColumns(component);
        UIForm form = getParentForm(component);

        String jsvar = resource.allocVariable(component);
        String dsvar = resource.allocTempVariable();

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);
        
        if(view.getAsync()) {
            // Create ajax data store
            fmt.format("%s = new Ext.data.Store({", dsvar);

            // using FacesProxy, url is the action URL of current JSF page.
            // the extra parameters contains request parameter passed with our client id.
            fmt.format("proxy: new Ext.data.FacesScriptProxy({url:%s, extraParams:{%s:'%s'",
                       HtmlEncoder.enquote(getActionURL(context)),
                       UIDataView.REQUEST_DATA_PARAM, view.getId());
            if (form != null) {
                // must include postback field otherwise can't decode.
                fmt.format(",'%s':''", FormRenderer.getPostbackFieldName(context, form));
            }
            buf.append("}}),");

            // using FacesReader
            fmt.format("reader: new Ext.data.FacesReader({root:'%s',totalProperty:'%s'},[",
                       JSON_ROOT, JSON_TOTAL);
            buf.append(encodeRecordDefinition(columns));
            buf.append("])});\n");
        } else {
            // Create data store
            fmt.format("%s = new Ext.data.SimpleStore({data:[\n", dsvar);
            buf.append(encodeArrayData(context, view, columns));
            buf.append("],\nfields:[");
            buf.append(encodeRecordDefinition(columns));
            buf.append("]});\n");
            //reset data
            view.setValue(null);
        }
        
        // Load initial data
        fmt.format("%s.load();\n", dsvar);

        String containerId = view.getContainer();
        if(containerId == null) {
            containerId = clientId;
        }
        // Create the data view component
        fmt.format("%s = new Ext.DataView({itemSelector:'',store:%s,tpl:new Ext.XTemplate('<tpl for=\".\">',%s,'</tpl>')", 
        		jsvar, dsvar, HtmlEncoder.enquote(template.trim()));
        encodeViewConfig(fmt, view, resource);
        buf.append("});\n");
        
        // Create the panel component
        fmt.format("new Ext.Panel({renderTo:'%s', items:%s, border:false, bodyBorder:false", containerId, jsvar);
        buf.append("});\n");
        

        // Bind to a pager component if there is one
        List<UIPager> pagers = UIPager.getAllPagersFor(context, view);
        if (pagers.size() > 0) {
            // flush current script code
            resource.addInitScript(buf.toString());
            buf.setLength(0);

            // all pagers must have same start and pageSize attribute
            int start = pagers.get(0).getStart();
            int pageSize = pagers.get(0).getPageSize();
            view.setFirst(start);
            view.setRows(pageSize);
            for (UIPager pager : pagers) {
                pager.setStart(start);
                pager.setPageSize(pageSize);
                pager.bind(context, dsvar);
            }
        }

        // Refresh view when data is loaded.
//        String ondataready = (String)view.getAttributes().get("ondataready");
//        if (ondataready == null) ondataready = "";
//        fmt.format("%s.on('load', function(){this.refresh();%s}, %s);\n",
//                   dsvar, ondataready, jsvar);

        

        resource.releaseVariable(dsvar);
        resource.releaseVariable(jsvar);
        resource.addInitScript(buf.toString());
    }

    @SuppressWarnings("unchecked")
    private void loadData(FacesContext context, UIDataView data)
        throws IOException
    {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();

        int totalRows = data.getRowCount();
        int rows, rowIndex;

        String startParam = paramMap.get("start");
        if (startParam != null) {
            rowIndex = Integer.parseInt(startParam);
            data.setFirst(rowIndex);
        } else {
            rowIndex = data.getFirst();
        }

        String limitParam = paramMap.get("limit");
        if (limitParam != null) {
            rows = Integer.parseInt(limitParam);
            data.setRows(rows);
        } else {
            rows = data.getRows();
        }

        List<UIColumn> columns = getColumns(data);
        JSONArray viewData = encodeJsonData(context, data, columns, rowIndex, rows);

        JSONObject json = new JSONObject();
        json.put(JSON_TOTAL, totalRows);
        json.put(JSON_ROOT, viewData);

        String[] state = FacesUtils.getViewState(context);
        if (state[0] != null) {
            json.put(JSON_VIEWSTATE, state[0]);
        }

        // extra params
        JSONObject params = new JSONObject();
        params.put("start", rowIndex);
        json.put(JSON_PARAMS, params);

        sendJsonData(context, json);
    }
}
