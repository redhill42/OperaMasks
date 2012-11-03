/*
 * $Id: AjaxDataGridRenderer.java,v 1.29 2008/04/21 07:40:49 lishaochuan Exp $
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

import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.encodeRecordDefinition;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getIdColumn;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getInputColumns;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getOutputColumns;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.registerSelectionModel;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIInputColumn;
import org.operamasks.faces.component.widget.grid.UIDataGrid.SelectionModelType;
import org.operamasks.faces.event.CellSelectEvent;
import org.operamasks.faces.event.RowSelectEvent;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.delegate.ViewDelegate;
import org.operamasks.faces.render.delegate.ViewDelegateManager;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxDataGridRenderer extends DataGridRenderer
{
    private static final int DEFAULT_PAGE_SIZE = 20;

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (!AjaxRenderKitImpl.isAjaxResponse(context))
            return;
        
        UIDataGrid grid = (UIDataGrid)component;
        if (grid.getRowIndex() != -1) {
            grid.setRowIndex(-1); // reset grid state
        }

        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);

        // decode load data event
        String requestId = paramMap.get(UIDataGrid.REQUEST_DATA_PARAM);
        if (requestId != null && requestId.equals(clientId)) {
            try {
                // prevent concurrent access to underlying data model
                synchronized (grid.getLock()) {
                    DataRendererHelper.loadData(context, grid);
                }
            } catch (IOException ex) {
                throw new FacesException(ex);
            }
            return; // all done, a JSON response has been generated
        }

        // decode row or cell select event
        SelectionModelType sm = grid.getSelectionModel();
        String row = paramMap.get(clientId + SELECTED_ROW_PARAM);
        String col = paramMap.get(clientId + SELECTED_COL_PARAM);
        String selectionsParam = paramMap.get(clientId + ROW_SELECTIONS_PARAM);

        if (sm == SelectionModelType.row) {
            if (row != null && row.length() != 0) {
                try {
                    String[] selectionsValue = selectionsParam.split(",");
                    int[] selections = new int[selectionsValue.length];
                    int i = 0;
                    for (String s : selectionsValue) {
                        selections[i++] = Integer.parseInt(s);
                    }
                    grid.setSelections(selections);
                } catch (Exception e) {/*ignored*/};
                try {
                    int rowIndex = Integer.parseInt(row);
                    grid.setSelectedRow(rowIndex);
                    component.queueEvent(new RowSelectEvent(grid, rowIndex));
                } catch (NumberFormatException ex) {/*ignored*/}
            }
            
        } else if (sm == SelectionModelType.cell) {
            if ((row != null && row.length() != 0) && (col != null && col.length() != 0)) {
                try {
                    String[] selectionsValue = selectionsParam.split(",");
                    int[] selections = new int[selectionsValue.length];
                    int i = 0;
                    for (String s : selectionsValue) {
                        selections[i++] = Integer.parseInt(s);
                    }
                    grid.setSelections(selections);
                } catch (Exception e) {/*ignored*/};
                try {
                    int rowIndex = Integer.parseInt(row);
                    int colIndex = Integer.parseInt(col);
                    grid.setSelectedRow(rowIndex);
                    grid.setSelectedColumn(colIndex);
                    component.queueEvent(new CellSelectEvent(grid, rowIndex, colIndex));
                } catch (NumberFormatException ex) {/*ignored*/}
            }
        }
    }
    
    private void saveComponentState(FacesContext context, UIComponent component) {
        // put component into session for dataGridViewDelegate
        Map<String, Object> sessionmap = context.getExternalContext().getSessionMap();
        sessionmap.put(component.getId() + UIDataGrid.GRID_COMPONENT_KEY, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        
        UIDataGrid grid = (UIDataGrid)component;
        String clientId;

        synchronized (grid.getLock()) {
            if (grid.getRowIndex() != -1)
                grid.setRowIndex(-1); // reset grid state
            clientId = grid.getClientId(context);
        }

        saveComponentState(context, component);

        if (isAjaxHtmlResponse(context)) {
            AjaxHtmlResponseWriter out = (AjaxHtmlResponseWriter)context.getResponseWriter();
            
            // encode grid container
            out.startElement("div", component);
            out.writeAttribute("id", component.getClientId(context), "clientId");
            renderPassThruAttributes(out, component, "rows");
            out.endElement("div");
            out.write("\n");

            out.setViewStateChanged(false);
        } else if (isAjaxResponse(context)) {
            Formatter fmt = new Formatter();
            String jsvar = FacesUtils.getJsvar(context, grid);

            fmt.format("OM.ajax.removeRequestParameter('%s');\n", clientId + SELECTED_ROW_PARAM);
            fmt.format("OM.ajax.removeRequestParameter('%s');\n", clientId + SELECTED_COL_PARAM);

            if (grid.isNeedReload()) {
                // reload data, select row or cell when data loaded
                //fmt.format("%s.getDataSource().load();\n", jsvar);
                fmt.format("%s.getStore().load({params:{start: %d, limit: %d}});\n", jsvar, grid.getFirst(), grid.getRows());
                grid.setNeedReload(false);
            } else {
                // select row or cell
                SelectionModelType sm = grid.getSelectionModel();
                int r[] = getRowRange(context, grid);
                int row = grid.getSelectedRow();
                int col = grid.getSelectedColumn();
                
                if (sm == SelectionModelType.row) {
                    if (row >= r[1] && (r[2] < 0 || row - r[1] < r[2])) { // row must in page range
                        int[] rows = grid.getSelections();
                        if (rows == null || rows.length == 0) {
                            rows = new int[]{row};
                        }
                        String rowsArray = "["; 
                        for (int i : rows) {
                            if(rowsArray.length() > 1)
                                rowsArray += "," + i;
                            else 
                                rowsArray += i;
                        }
                        rowsArray += "]";
                        fmt.format("%s.getSelectionModel().internalSelectRows(%s);\n", jsvar, rowsArray);
                    }
                } else if (sm == SelectionModelType.cell) {
                    if ((row >= r[1] && (r[2] < 0 || row - r[1] < r[2])) && col >= 0) {
                        fmt.format("%s.getSelectionModel().internalSelect(%d,%d);\n",jsvar, row, col);
                    }
                }
            }

            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.setViewStateChanged(false);
            out.writeActionScript(fmt.toString());
        }
    }
    
    @Override
    public void provideResource(final ResourceManager rm, final UIComponent component) {
        final YuiExtResource resource = YuiExtResource.register(rm, "Ext.grid.Grid", "Ext.data.FacesProxy");
        String id = "urn:dataGrid:" + component.getClientId(FacesContext.getCurrentInstance());
        rm.registerResource(new AbstractResource(id) {
            /**
             * render grid scripts at last 
             */
            public int getPriority() {
                return LOW_PRIORITY - 300;
            }

            @Override
            public void encodeBegin(FacesContext context) throws IOException {
                encodeResource(rm, resource, component);
            }
        });
    }

    protected void encodeResource(ResourceManager rm, YuiExtResource resource, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIDataGrid grid = (UIDataGrid)component;
        String clientId;

        synchronized (grid.getLock()) {
            if (grid.getRowIndex() != -1)
                grid.setRowIndex(-1); // reset grid state
            clientId = grid.getClientId(context);
        }

        List<UIInputColumn> inputColumns = getInputColumns(component);
        List<UIColumn> outPutColumns = getOutputColumns(component);
        String idColumn = getIdColumn(component);

        String jsvar = resource.allocVariable(component);

        String cmvar = jsvar + "_cm";
        String dsvar = jsvar + "_ds";
        String smvar = jsvar + "_sm";
        String topBar = jsvar + "_topbar";
        String bottomBar = jsvar + "_bottombar";

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);
        UIForm form = getParentForm(component);

        // Create column model
        fmt.format("%s = new Ext.grid.ColumnModel([ new Ext.grid.RowNumberer(),", cmvar);
        encodeColumnModel(buf, inputColumns, outPutColumns);
        buf.append("]);\n");

        // Create ajax data store
        fmt.format("%s = new Ext.data.Store({", dsvar);
        // using FacesProxy, url is the action URL of current JSF page,
        // the extra parameters contains request parameter passed with our client id.
        fmt.format("proxy: new Ext.data.FacesScriptProxy({url: %s, extraParams: {%s:'%s'",
                HtmlEncoder.enquote(getActionURL(context)),
                   UIDataGrid.REQUEST_DATA_PARAM, grid.getId());
        if (form != null) {
            // must include postback field otherwise can't decode.
            fmt.format(",'%s':''", FormRenderer.getPostbackFieldName(context, form));
        }
        buf.append("}}),");

        // using FacesReader
        fmt.format("reader: new Ext.data.FacesReader({root:'%s',totalProperty:'%s'",
                   DataRendererHelper.JSON_ROOT, DataRendererHelper.JSON_TOTAL);
        if (idColumn != null)
            fmt.format(",id:'%s'", idColumn);
        buf.append("},[");
        buf.append(encodeRecordDefinition(outPutColumns));
        buf.append("])});\n");

        String ondataready = (String)grid.getAttributes().get("ondataready");
        if (ondataready != null) {
            fmt.format("%s.on('load', function(){%s}, %s);\n", dsvar, ondataready, jsvar);
        }
        
        // Create selection model
        encodeSelectionModel(fmt, grid, smvar);

        // Create the grid component
        encodeGridDefinition(fmt, grid, resource, buf, jsvar, clientId, dsvar, cmvar, smvar);

        // Initialize selection model
        encodeSelectionModelInit(fmt, context, grid, jsvar, dsvar, smvar);

        if (grid.isPaged()) {
            // create paging toolbar and load initial page
            resource.addPackageDependency("Ext.PagingToolbar");
            encodePager(fmt, grid, jsvar, dsvar, topBar, bottomBar);
        } else {
            // bind to existing pager component if there is one
            List<UIPager> pagers = UIPager.getAllPagersFor(context, grid);
            if (pagers.size() > 0) {
                // flush current script code
                resource.addInitScript(buf.toString());
                buf.setLength(0);

                // all pagers must have same start and pageSize attribute
                int start = pagers.get(0).getStart();
                int pageSize = pagers.get(0).getPageSize();
                grid.setFirst(start);
                grid.setRows(pageSize);
                for (UIPager pager : pagers) {
                    pager.setStart(start);
                    pager.setPageSize(pageSize);
                    pager.bind(context, dsvar);
                }
            }
        }
        
        encodeToolBar(fmt, grid, jsvar, topBar, bottomBar);

        // load initial data
        fmt.format("%s.load();\n", dsvar);
        fmt.format(getRenderScript(jsvar, clientId));

        resource.addInitScript(buf.toString());
    }

    protected String getRenderScript(String jsvar, String clientId) {
		return String.format("%s.render();\n", jsvar);
	}

	private void encodeToolBar(Formatter fmt, UIDataGrid grid, String jsvar,
            String topBar, String bottomBar) {
        UIComponent toolBar = getToolBar(grid);
        
        if (toolBar == null)
        	return;
        
        YuiExtResource resource;
        
        if (toolBar instanceof UIToolBar) {
        	resource = YuiExtResource.register(ResourceManager.getInstance(
        			FacesContext.getCurrentInstance()), "Ext.Toolbar");
        } else {
        	resource = YuiExtResource.register(ResourceManager.getInstance(
        			FacesContext.getCurrentInstance()), "Ext.PagingToolbar");
        }
        
        String toolBarJsvar = resource.allocVariable(toolBar);
        
        String position = grid.getToolBarPosition();
        
        if ("top".equals(position)) {
         	fmt.format("\nif (typeof %s != 'undefined') {%s = %s;}",
         			topBar, toolBarJsvar, topBar);
        } else if ("bottom".equals(position) || null == position) {
          	fmt.format("\nif (typeof %s != 'undefined') {%s = %s;}",
          			bottomBar, toolBarJsvar, bottomBar);
        } else if ("both".equals(position)) {
        	// TODO Adding toolbar on 'both' position
        	fmt.format("\nif (typeof %s != 'undefined') {%s = %s;}",
        			bottomBar, toolBarJsvar, bottomBar);
        }
        
		if ("top".equals(position))
			fmt.format("Ext.apply(%1$s, {tbar: %2$s});\n%1$s.initComponent();",
					jsvar,
					toolBarJsvar);
		else
			fmt.format("Ext.apply(%1$s, {bbar: %2$s});\n%1$s.initComponent();",
					jsvar,
					toolBarJsvar);
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
        fmt.format("%s = new Ext.grid.GridPanel({store:%s,cm:%s,sm:%s,layout:'fit'", jsvar,
                dsvar, cmvar, smvar);
        fmt.format(",el:'%s'", clientId);
        encodeGridConfig(fmt, grid, resource);
        fmt.format(",plugins: new Ext.ux.plugins.XGrid()");
        buf.append("});\n");
        //fmt.format("%s.render();\n", jsvar);
    }

    private void encodeSelectionModel(Formatter fmt, UIDataGrid grid, String smvar) {
        SelectionModelType sm = grid.getSelectionModel();
        registerSelectionModel();

        if (sm == SelectionModelType.row) {
            fmt.format("%s = new Ext.grid.RowSelectionModel2({singleSelect:%b});\n",
                       smvar, grid.isSingleSelect());
        } else if (sm == SelectionModelType.cell) {
            fmt.format("%s = new Ext.grid.CellSelectionModel2({singleSelect:%b});\n",
                       smvar, grid.isSingleSelect());
        } else {
            fmt.format("%s = new Ext.grid.NoneSelectionModel();\n", smvar);
        }
    }

    private void encodeSelectionModelInit(Formatter fmt,
                                          FacesContext context,
                                          UIDataGrid grid,
                                          String jsvar,
                                          String dsvar,
                                          String smvar)
    {
        SelectionModelType selModel = grid.getSelectionModel();
        String clientId = grid.getClientId(context);
        StringBuilder buf = (StringBuilder)fmt.out();

        if (selModel == SelectionModelType.row) {
            // Monitor row select event, save current selected row index
            // into global AJAX request parameters which will send back
            // to server at next AJAX transaction.
            String onrowselect = (String)grid.getAttributes().get("onrowselect");
            fmt.format("%s.on('rowselect', function(_sm,rowIndex){\n" +
                       "var currRow = _sm.rowOfIndex(rowIndex);\n" +
                       "OM.ajax.addRequestParameter('%s',currRow);\n",
                       smvar, clientId + SELECTED_ROW_PARAM);
            fmt.format("OM.ajax.addRequestParameter('%s',_sm.getSelectedRows());\n", clientId + ROW_SELECTIONS_PARAM);
            if (onrowselect != null && onrowselect.length() != 0) {
                buf.append(onrowselect);
                if (!onrowselect.endsWith(";"))
                    buf.append(";");
            }
            fmt.format("},%s);\n", jsvar);

            // Register for dblclick event.
            String ondblclick = (String)grid.getAttributes().get("ondblclick");
            if(ondblclick != null && ondblclick.trim().length() > 0) {
                fmt.format("%s.on('dblclick', function(_sm,rowIndex){\n" +
                           "var currRow = _sm.rowOfIndex(rowIndex);\n" +
                           "OM.ajax.addRequestParameter('%s',currRow);\n",
                           smvar, clientId + SELECTED_ROW_PARAM);
                buf.append(ondblclick);
                if (!ondblclick.endsWith(";"))
                    buf.append(";");
                fmt.format("},%s);\n", jsvar);
            }

            // Select row when data is load, save the first row attribute
            // for next AJAX transaction.
            fmt.format("%s.on('load', function(d,r,o){" +
                       "var row=Number(o.params && o.params.row);" +
                       "if(!isNaN(row) && row>=0){" +
                           "this.getSelectionModel().internalSelectRow(row);" +
                       "}},%s);\n",
                       dsvar, jsvar);
        } else if (selModel == SelectionModelType.cell) {
            // Monitor cell select event, save current selected row and
            // column index into global AJAX request parameters which will
            // send back to server at next AJAX transaction.
            String oncellselect = (String)grid.getAttributes().get("oncellselect");
            fmt.format("%s.on('cellselect', function(_sm,rowIndex,colIndex){" +
                       "var currRow = _sm.rowOfIndex(rowIndex);\n" +
                       "OM.ajax.addRequestParameter('%s',currRow);" +
                       "OM.ajax.addRequestParameter('%s',colIndex);",
                       smvar, clientId + SELECTED_ROW_PARAM, clientId + SELECTED_COL_PARAM);
            if (oncellselect != null && oncellselect.length() != 0) {
                buf.append(oncellselect);
                if (!oncellselect.endsWith(";"))
                    buf.append(";");
            }
            fmt.format("},%s);\n", jsvar);

            // Register for dblclick event.
            String ondblclick = (String)grid.getAttributes().get("ondblclick");
            if(ondblclick != null && ondblclick.trim().length() > 0) {
                fmt.format("%s.on('dblclick', function(_sm,rowIndex,colIndex){\n" +
                           "var currRow = _sm.rowOfIndex(rowIndex);\n" +
                           "OM.ajax.addRequestParameter('%s',currRow);\n" +
                           "OM.ajax.addRequestParameter('%s',colIndex);\n",
                           smvar, clientId + SELECTED_ROW_PARAM, clientId + SELECTED_COL_PARAM);
                buf.append(ondblclick);
                if (!ondblclick.endsWith(";"))
                    buf.append(";");
                fmt.format("},%s);\n", jsvar);
            }

            // Select cell when data is load, save the first row attribute
            // for next AJAX transaction.
            fmt.format("%s.on('load', function(d,r,o){" +
                       "var row=Number(o.params && o.params.row);" +
                       "var col=Number(o.params && o.params.col);" +
                       "if(!isNaN(row) && row>=0 && !isNaN(col) && col>=0){" +
                           "this.getSelectionModel().internalSelect(row,col);" +
                       "}},%s);\n",
                       dsvar, jsvar);
        }
    }

    private void encodePager(Formatter fmt, UIDataGrid grid, String jsvar, String dsvar, String topBar, String bottomBar) {
        String position = grid.getToolBarPosition();
        int pageSize = grid.getRows();
        if (pageSize <= 0)
            pageSize = DEFAULT_PAGE_SIZE;

        if ("top".equals(position) || "both".equals(position)) {
            fmt.format("%s = new Ext.PagingToolbar({store:%s, pageSize:%d});\n",
                       topBar, dsvar, pageSize);
            fmt.format("Ext.apply(%s,{tbar:%s});", jsvar, topBar);
            fmt.format("%s.initComponent();", jsvar);
        }

        if (position == null || "bottom".equals(position) || "both".equals(position)) {
           fmt.format("%s = new Ext.PagingToolbar({store:%s, pageSize:%d});\n",
                    bottomBar, dsvar, pageSize);
           fmt.format("Ext.apply(%s,{bbar:%s});", jsvar, bottomBar);
           fmt.format("%s.initComponent();", jsvar);
        }
    }

    // return 3 element array:
    //  [0]: 1 - paged, 0 - not paged
    //  [1]: first row
    //  [2]: displayed rows
    private int[] getRowRange(FacesContext context, UIDataGrid grid) {
        int paged, first, rows;

        first = grid.getFirst();
        if (first < 0)
            first = 0;

        if (grid.isPaged()) {
            paged = 1;
            rows = grid.getRows();
        } else {
            UIPager pager = UIPager.getPagerFor(context, grid);
            if (pager != null) {
                paged = 1;
                rows = pager.getPageSize();
            } else {
                paged = 0;
                rows = grid.getRows();
            }
        }

        int total = grid.getRowCount();
        if (total != -1) {
            int remaining = total - first;
            if (rows > remaining) {
                rows = remaining;
            } else if (paged == 1 && rows <= 0) {
                rows = DEFAULT_PAGE_SIZE;
            }
        } else {
            if (paged == 1 && rows <= 0) {
                rows = DEFAULT_PAGE_SIZE;
            }
        }

        return new int[] { paged, first, rows };
    }
}
