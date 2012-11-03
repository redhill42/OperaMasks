/*
 * $Id: DataGridRenderer.java,v 1.14 2008/01/05 06:56:36 yangdong Exp $
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

import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.encodeArrayData;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.encodeRecordDefinition;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getColumnHeader;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getIdColumn;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getInputColumns;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getOutputColumns;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.registerSelectionModel;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIInputColumn;
import org.operamasks.faces.component.widget.grid.UIDataGrid.SelectionModelType;
import org.operamasks.faces.event.CellSelectEvent;
import org.operamasks.faces.event.RowSelectEvent;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.HtmlEncoder;

public class DataGridRenderer extends HtmlRenderer
    implements ResourceProvider, ToolBarContainer
{
    protected static final String SELECTED_ROW_PARAM = NamingContainer.SEPARATOR_CHAR + "_selectedRow";
    protected static final String SELECTED_COL_PARAM = NamingContainer.SEPARATOR_CHAR + "_selectedCol";
    protected static final String ROW_SELECTIONS_PARAM = NamingContainer.SEPARATOR_CHAR + "_selections";
    
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource.register(rm, "Ext.grid.Grid", "Ext.data.Store");
    }

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIDataGrid grid = (UIDataGrid)component;
        grid.setRowIndex(-1); // reset grid state

        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();

        // decode row or cell select event
        SelectionModelType sm = grid.getSelectionModel();
        String row = paramMap.get(clientId + SELECTED_ROW_PARAM);
        String col = paramMap.get(clientId + SELECTED_COL_PARAM);

        if (sm == SelectionModelType.row) {
            if (row != null) {
                try {
                    int rowIndex = Integer.parseInt(row);
                    grid.setSelectedRow(rowIndex);
                    component.queueEvent(new RowSelectEvent(grid, rowIndex));
                } catch (NumberFormatException ex) {/*ignored*/}
            }
        } else if (sm == SelectionModelType.cell) {
            if (row != null && col != null) {
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

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIDataGrid grid = (UIDataGrid)component;
        grid.setRowIndex(-1); // reset grid state

        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter();

        // encode grid container
        out.startElement("div", component);
        out.writeAttribute("id", clientId, "clientId");
        renderPassThruAttributes(out, component, "rows");
        out.endElement("div");

        // encode hidden fields to transfer selected row and column index
        SelectionModelType sm = grid.getSelectionModel();
        int row = grid.getSelectedRow();
        int col = grid.getSelectedColumn();

        if (sm == SelectionModelType.row || sm == SelectionModelType.cell) {
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", clientId + SELECTED_ROW_PARAM, "clientId");
            out.writeAttribute("name", clientId + SELECTED_ROW_PARAM, "clientId");
            out.writeAttribute("value", row, "selectedRow");
            out.endElement("input");
        }

        if (sm == SelectionModelType.cell) {
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", clientId + SELECTED_COL_PARAM, "clientId");
            out.writeAttribute("name", clientId + SELECTED_COL_PARAM, "clientId");
            out.writeAttribute("value", col, "selectedColumn");
            out.endElement("input");
        }

        // encode data grid script, must do it here to synchronize with pagers
        encodeScript(context, component);
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        UIComponent toolBar = getToolBar(component);
        if(toolBar != null) {
            if (context == null)
                throw new NullPointerException();
            if (!toolBar.isRendered())
                return;
            toolBar.encodeAll(context);
        }
    }

    public void encodeScript(FacesContext context, UIComponent component) {
        ResourceManager rm = ResourceManager.getInstance(context);
        YuiExtResource resource = (YuiExtResource)rm.getRegisteredResource(YuiExtResource.RESOURCE_ID);
        assert resource != null;

        UIDataGrid grid = (UIDataGrid)component;
        String clientId = component.getClientId(context);
        List<UIInputColumn> inputColumns = getInputColumns(component);
        List<UIColumn> outPutColumns = getOutputColumns(component);
        String idColumn = getIdColumn(component);

        String jsvar = resource.allocVariable(component);
        String cmvar = resource.allocTempVariable();
        String dsvar = resource.allocTempVariable();
        String smvar = resource.allocTempVariable();

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        // Create column model
        fmt.format("%s = new Ext.grid.ColumnModel([", cmvar);
        encodeColumnModel(buf, inputColumns, outPutColumns);
        buf.append("]);\n");

        // Create data store
        fmt.format("%s = new Ext.data.SimpleStore({data:[", dsvar);
        buf.append(encodeArrayData(context, grid, outPutColumns));
        buf.append("],fields:[");
        buf.append(encodeRecordDefinition(outPutColumns));
        buf.append("]");
        if (idColumn != null)
            fmt.format(",id:'%s'", idColumn);
        buf.append("});\n");

        // Create selection model
        encodeSelectionModel(fmt, grid, smvar);

        // Create the grid component
        fmt.format("%s = new Ext.grid.Grid('%s', {ds:%s,cm:%s,sm:%s",
                   jsvar, clientId, dsvar, cmvar, smvar);
        encodeGridConfig(fmt, grid, resource);
        fmt.format("});\n%s.render();\n%s.load();\n", jsvar, dsvar);

        // Initialize selection model
        encodeSelectionModelInit(fmt, context, grid, jsvar, smvar);

        resource.releaseVariable(smvar);
        resource.releaseVariable(dsvar);
        resource.releaseVariable(cmvar);
        resource.releaseVariable(jsvar);
        resource.addInitScript(buf.toString());
    }

    protected void encodeColumnModel(StringBuilder buf, List<UIInputColumn> inputColumns, List<UIColumn> outputColumns) {
        if (outputColumns.size() == 0) {
            return;
        }

        Formatter fmt = new Formatter(buf);

        for (UIColumn column : outputColumns) {
            Map<String,Object> a = column.getAttributes();
            String  columnId   = column.getId();
            String  header     = getColumnHeader(column);
            String  tooltip    = (String)a.get("tooltip");
            Integer width      = (Integer)a.get("width");
            String  align      = (String)a.get("align");
            Boolean locked     = (Boolean)a.get("locked");
            Boolean fixed      = (Boolean)a.get("fixed");
            Boolean sortable   = (Boolean)a.get("sortable");
            Boolean hidden     = (Boolean)a.get("hidden");
            String  renderer   = (String)a.get("clientFormatter");
            String  editor     = (String)a.get("editor");
            UIInputColumn inputColumn = getInputColumnById(editor, inputColumns);

            fmt.format("{dataIndex:'%s'", columnId); // referenced by RecordDefinition
            if (header != null)
                fmt.format(",header:%s", HtmlEncoder.enquote(header));
            if (tooltip != null)
                fmt.format(",tooltip:%s", HtmlEncoder.enquote(tooltip));
            if (width != null && width >= 0)
                fmt.format(",width:%d", width);
            if (align != null)
                fmt.format(",align:'%s'", align);
            if (locked != null && locked)
                buf.append(",locked:true");
            if (fixed != null && fixed)
                buf.append(",fixed:true");
            if (sortable != null && sortable)
                buf.append(",sortable:true");
            if (hidden != null && hidden)
                buf.append(",hidden:true");
            if (renderer != null)
                fmt.format(",renderer:%s", renderer);
            if (inputColumn != null)
                inputColumn.render(buf);
            buf.append("},");
        }

        buf.setLength(buf.length()-1); // remove last comma
    }

    private UIInputColumn getInputColumnById(String editor, List<UIInputColumn> inputColumns) {
        for (UIInputColumn column : inputColumns) {
            String columnId = ((UIComponent)column).getId();
            if ((column instanceof UIInputColumn) && columnId != null && columnId.equals(editor)) {
                return (UIInputColumn) column;
            }
        }
        return null;
    }
    
    protected UIComponent getToolBar(UIComponent component) {
        for (UIComponent child : component.getChildren()) {
            if (child instanceof UIToolBar) {
                return child;
            }
        }
        
        for (UIComponent child : component.getChildren()) {
            if (child instanceof UIPagingToolbar) {
                return child;
            }
        }
        
        return null;
    }

    private void encodeSelectionModel(Formatter fmt, UIDataGrid grid, String smvar) {
        SelectionModelType sm = grid.getSelectionModel();

        if (sm == SelectionModelType.row) {
            fmt.format("%s = new Ext.grid.RowSelectionModel({singleSelect:%b});\n",
                       smvar, grid.isSingleSelect());
        } else if (sm == SelectionModelType.cell) {
            fmt.format("%s = new Ext.grid.CellSelectionModel({singleSelect:%b});\n",
                       smvar, grid.isSingleSelect());
        } else {
            registerSelectionModel();
            fmt.format("%s = new Ext.grid.NoneSelectionModel();\n", smvar);
        }
    }

    private void encodeSelectionModelInit(Formatter fmt,
                                          FacesContext context,
                                          UIDataGrid grid,
                                          String jsvar,
                                          String smvar)
    {
        SelectionModelType sm = grid.getSelectionModel();
        String clientId = grid.getClientId(context);
        int row = grid.getSelectedRow();
        int col = grid.getSelectedColumn();
        StringBuilder buf = (StringBuilder)fmt.out();

        if (sm == SelectionModelType.row) {
            // Monitor row select event
            String onrowselect = (String)grid.getAttributes().get("onrowselect");
            fmt.format("%s.on('rowselect', function(_1,rowIndex){" +
                       "document.getElementById('%s').value = rowIndex;",
                       smvar, clientId + SELECTED_ROW_PARAM);
            if (onrowselect != null && onrowselect.length() != 0) {
                buf.append(onrowselect);
                if (!onrowselect.endsWith(";"))
                    buf.append(";");
            }
            fmt.format("},%s);\n", jsvar);

            // Select the row
            if (row >= 0) {
                fmt.format("%s.selectRow(%d);\n", smvar, row);
            }
        } else if (sm == SelectionModelType.cell) {
            // Monitor cell select event
            String oncellselect = (String)grid.getAttributes().get("oncellselect");
            fmt.format("%s.on('cellselect', function(_1,rowIndex,colIndex){" +
                       "document.getElementById('%s').value = rowIndex;" +
                       "document.getElementById('%s').value = colIndex;",
                       smvar, clientId + SELECTED_ROW_PARAM, clientId + SELECTED_COL_PARAM);
            if (oncellselect != null && oncellselect.length() != 0) {
                buf.append(oncellselect);
                if (!oncellselect.endsWith(";"))
                    buf.append(";");
            }
            fmt.format("},%s);\n", jsvar);

            // Select the cell
            if (row >= 0 && col >= 0) {
                fmt.format("%s.select(%d,%d);\n", smvar, row, col);
            }
        }
    }

    protected void encodeGridConfig(Formatter fmt, UIDataGrid grid, YuiExtResource resource) {
        if (grid.isAutoHeightSet())
            fmt.format(",autoHeight:%b", grid.isAutoHeight());
        if (grid.isAutoSizeColumnsSet())
            fmt.format(",autoSizeColumns:%b", grid.isAutoSizeColumns());
        if (grid.isMinColumnWidthSet())
            fmt.format(",minColumnWidth:%d", grid.getMinColumnWidth());
        if (grid.isEnableColumnHideSet())
            fmt.format(",enableColumnHide:%b", grid.isEnableColumnHide());
        if (grid.isEnableColumnMoveSet())
            fmt.format(",enableColumnMove:%b", grid.isEnableColumnMove());
        if (grid.isEnableDragDropSet())
            fmt.format(",enableDragDrop:%b", grid.isEnableDragDrop());
        if (grid.isTrackMouseOverSet())
            fmt.format(",trackMouseOver:%b", grid.isTrackMouseOver());
        if (grid.isStripeRowsSet())
            fmt.format(",stripeRows:%b", grid.isStripeRows());
        if (grid.getLoadMask())
            resource.addPackageDependency("Ext.LoadMask");
        fmt.format(",loadMask:%b", grid.getLoadMask());
        if (grid.getContextMenu())
            resource.addPackageDependency("Ext.menu.Menu");
        fmt.format(",enableCtxMenu:%b", grid.getContextMenu());
    }
    
    @Override
    public String convertClientId(FacesContext context, String clientId) {
        // if client contains ':', style with identify will not work ;
        // for example: #myform:mygrid { color: #FF0000 }
        if( clientId != null && clientId.indexOf(":") != -1 ) {
            clientId = clientId.replaceAll(":", "_") ;
        }
        return clientId ;
    }
}
