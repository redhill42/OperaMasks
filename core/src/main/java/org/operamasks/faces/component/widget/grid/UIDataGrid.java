/*
 * $Id: UIDataGrid.java,v 1.15 2008/01/09 08:52:45 jacky Exp $
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

package org.operamasks.faces.component.widget.grid;

import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.page.PagedUIData;
import org.operamasks.faces.event.CellSelectListener;
import org.operamasks.faces.event.RowSelectListener;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.application.StateAware;

public class UIDataGrid extends PagedUIData implements StateAware
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.DataGrid";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Grid";
    public static final Object SERVER_ROW_INDEX = "_serverRowIndex";
    public static final String FIRST_ROW_KEY = "_grid_first_row";
    public static final String GRID_ROWS_KEY = "_grid_rows";

    public static final String REQUEST_DATA_PARAM = "_ajaxDataProxy_grid";
    public static final String GRID_COMPONENT_KEY = "_gridComponent";

    public UIDataGrid() {
        setRendererType(RENDERER_TYPE);
    }

    private String jsvar;

    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    // Used to synchronize concurrent access to underlying data model,
    // Default locker is the grid component itself.
    private Object lock;

    public synchronized Object getLock() {
        Object lock = this.lock;
        if (lock == null) {
            ValueExpression ve = getValueExpression("lock");
            if (ve != null) {
                lock = ve.getValue(getFacesContext().getELContext());
            }
        }
        if (lock == null) {
            lock = this;
        }
        return lock;
    }

    public synchronized void setLock(Object lock) {
        this.lock = lock;
    }

    // Selection Model
    public enum SelectionModelType { row, cell, none }

    private SelectionModelType selectionModel;
    private Boolean singleSelect;
    private int selectedRow;
    private int selectedColumn;
    private boolean selectedRowSet;
    private boolean selectedColumnSet;
    private int[] selections;
    private Object selectedRowData;

    public SelectionModelType getSelectionModel() {
        if (this.selectionModel != null) {
            return this.selectionModel;
        }
        ValueExpression ve = getValueExpression("selectionModel");
        if (ve != null) {
            return (SelectionModelType)ve.getValue(getFacesContext().getELContext());
        } else {
            return SelectionModelType.row;
        }
    }

    public void setSelectionModel(SelectionModelType selectionModel) {
        this.selectionModel = selectionModel;
    }

    public boolean isSingleSelect() {
        if (this.singleSelect != null) {
            return this.singleSelect;
        }
        ValueExpression ve = getValueExpression("singleSelect");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setSingleSelect(boolean singleSelect) {
        this.singleSelect = singleSelect;
    }

    public int getSelectedRow() {
        if (this.selectedRowSet) {
            return this.selectedRow;
        }
        ValueExpression ve = getValueExpression("selectedRow");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return -1;
        }
    }

    public void setSelectedRow(int row) {
        int rowCount = getRowCount(); // may be -1 for unknow row count

        if (row < 0) {
            row = -1;
        } else if (rowCount != -1) {
            if (row > rowCount) {
                row = rowCount - 1;
            }
        }

        this.selectedRow = row;
        this.selectedRowSet = true;
        updateSelectedRowData(row);
    }

    public int getSelectedColumn() {
        if (this.selectedColumnSet) {
            return this.selectedColumn;
        }
        ValueExpression ve = getValueExpression("selectedColumn");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return -1;
        }
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
        this.selectedColumnSet = true;
    }

    public int[] getSelections() {
        if (this.selections != null) {
            return this.selections;
        }
        ValueExpression ve = getValueExpression("selections");
        if (ve != null) {
            return (int[])ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setSelections(int[] selections) {
        this.selections = selections;
    }

    public Object getSelectedRowData() {
        if (this.selectedRowData != null) {
            return this.selectedRowData;
        }

        ValueExpression ve = getValueExpression("selectedRowData");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return value;
            }
        }

        // retrieve row data from current selected row index
        int rowIndex = getSelectedRow();
        Object rowData = null;
        if (rowIndex != -1) {
            try {
                int previousRow = getRowIndex();
                setRowIndex(rowIndex);
                if (isRowAvailable()) {
                    rowData = getRowData();
                }
                setRowIndex(previousRow);
            } catch (IllegalStateException ex) {
                // ignored
            }
        }

        setSelectedRowData(rowData);
        return rowData;
    }

    protected void setSelectedRowData(Object rowData) {
        this.selectedRowData = rowData;
    }

    private void updateSelectedRowData(int rowIndex) {
        ValueExpression ve = getValueExpression("selectedRowData");
        if (ve != null) {
            Object rowData = null;
            try {
                if (rowIndex != -1) {
                    int previousRow = getRowIndex();
                    setRowIndex(rowIndex);
                    if (isRowAvailable()) {
                        rowData = getRowData();
                    }
                    setRowIndex(previousRow);
                }
            } catch (IllegalStateException ex) {
                // ignored
            }
            ve.setValue(getFacesContext().getELContext(), rowData);
        }

        this.selectedRowData = null;
    }

    public void selectRow(int row) {
        if (row != this.getSelectedRow()) {
            setSelectedRow(row);
        }
    }

    public void selectFirstRow() {
        int row = getSelectedRow();
        if (row != 0) {
            setSelectedRow(0);
        }
    }

    public void setLastRow() {
        int row = getSelectedRow();
        int rowCount = getRowCount();
        if (rowCount != -1 && row != rowCount-1) {
            selectRow(rowCount-1);
        }
    }

    public void selectNextRow() {
        int row = getSelectedRow();
        int rowCount = getRowCount();
        if (rowCount == -1 || row < rowCount-1) {
            setSelectedRow(row+1);
        }
    }

    public void selectPreviousRow() {
        int row = getSelectedRow();
        if (row > 0) {
            setSelectedRow(row - 1);
        }
    }

    public void addRowSelectListener(RowSelectListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        RowSelectListener[] listeners = getRowSelectListeners();
        for (int i = 0; i < listeners.length; i++) {
            if (listener.equals(listeners[i])) {
                return;
            }
        }
        addFacesListener(listener);
    }

    public RowSelectListener[] getRowSelectListeners() {
        return (RowSelectListener[])getFacesListeners(RowSelectListener.class);
    }

    public void removeRowSelectListener(RowSelectListener listener) {
        removeFacesListener(listener);
    }

    public void addCellSelectListener(CellSelectListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        CellSelectListener[] listeners = getCellSelectListeners();
        for (int i = 0; i < listeners.length; i++) {
            if (listener.equals(listeners[i])) {
                return;
            }
        }
        addFacesListener(listener);
    }

    public CellSelectListener[] getCellSelectListeners() {
        return (CellSelectListener[])getFacesListeners(CellSelectListener.class);
    }

    public void removeCellSelectListener(CellSelectListener listener) {
        removeFacesListener(listener);
    }
    
    @Override
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        // Skip processing if our rendered flag is false
        if (!isRendered()) {
            return;
        }

        // For AJAX asynchronize load request, skip standard decodes process
        if (isAsyncLoadRequest(context)) {
            decode(context);
            return;
        }

        // Process default decodes.
        super.processDecodes(context);

        // Process all children of this component except for
        // UIColumn which is already processed by super class.
        setRowIndex(-1);
        for (UIComponent child : getChildren()) {
            if (!(child instanceof UIColumn) && (child.isRendered())) {
                child.processDecodes(context);
            }
        }
    }

    private boolean isAsyncLoadRequest(FacesContext context) {
        if (!AjaxRenderKitImpl.isAjaxResponse(context)) {
            return false;
        }

        if (getRowIndex() != -1) {
            setRowIndex(-1);
        }

        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String requestId = paramMap.get(REQUEST_DATA_PARAM);
        String clientId = getClientId(context);
        return (requestId != null) && (requestId.equals(clientId));
    }

    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        try {
            updateModel(context);
        } catch (RuntimeException ex) {
            context.renderResponse();
            throw ex;
        }
    }

    public void updateModel(FacesContext context) {
        ValueExpression ve;

        if (selectedRowSet) {
            if ((ve = getValueExpression("selectedRow")) != null) {
                try {
                    ve.setValue(context.getELContext(), selectedRow);
                    selectedRow = -1;
                    selectedRowSet = false;
                } catch (Exception ex) {
                    FacesMessage message = new FacesMessage(ex.getMessage());
                    context.addMessage(getClientId(context), message);
                }
            }

            if ((ve = getValueExpression("selections")) != null) {
                try {
                    ve.setValue(context.getELContext(), selections);
                    selections = null;
                    selectedRowSet = false;
                } catch (Exception ex) {
                    FacesMessage message = new FacesMessage(ex.getMessage());
                    context.addMessage(getClientId(context), message);
                }
            }
        
        }

        if (selectedColumnSet) {
            if ((ve = getValueExpression("selectedColumn")) != null) {
                try {
                    ve.setValue(context.getELContext(), selectedColumn);
                    selectedColumn = -1;
                    selectedColumnSet = false;
                } catch (Exception ex) {
                    FacesMessage message = new FacesMessage(ex.getMessage());
                    context.addMessage(getClientId(context), message);
                }
            }
        }

        Map<String, Object> session = context.getExternalContext().getSessionMap();
        Integer first = (Integer) session.get(this.getClientId(context)+FIRST_ROW_KEY);
        if (first != null) {
            setFirst(first);
        }
        Integer rows = (Integer) session.get(this.getClientId(context)+GRID_ROWS_KEY);
        if (rows != null) {
            setRows(rows);
        }
        
        if (selections != null) {
            if ((ve = getValueExpression("selections")) != null) {
                try {
                    ve.setValue(context.getELContext(), selections);
                    selections = null;
                } catch (Exception ex) {
                    FacesMessage message = new FacesMessage(ex.getMessage());
                    context.addMessage(getClientId(context), message);
                }
            }
        }
        setSelectedRowData(null);
    }

    /**
     * True to fit the height of the grid container to the height of the data.
     */
    private boolean autoHeight;
    private boolean autoHeight_set;

    public boolean isAutoHeight() {
        if (this.autoHeight_set) {
            return this.autoHeight;
        }
        ValueExpression ve = getValueExpression("autoHeight");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public boolean isAutoHeightSet() {
        return autoHeight_set || getValueExpression("autoHeight") != null;
    }

    public void setAutoHeight(boolean autoHeight) {
        this.autoHeight = autoHeight;
        this.autoHeight_set = true;
    }

    /**
     * True to automatically resize the columns to fit their content on iniital render.
     */
    private boolean autoSizeColumns;
    private boolean autoSizeColumns_set;

    public boolean isAutoSizeColumns() {
        if (autoSizeColumns_set) {
            return this.autoSizeColumns;
        }
        ValueExpression ve = getValueExpression("autoSizeColumns");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public boolean isAutoSizeColumnsSet() {
        return autoSizeColumns_set || getValueExpression("autoSizeColumns") != null;
    }

    public void setAutoSizeColumns(boolean autoSizeColumns) {
        this.autoSizeColumns = autoSizeColumns;
        this.autoSizeColumns_set = true;
    }

    private int minColumnWidth = 25;
    private boolean minColumnWidth_set;

    public int getMinColumnWidth() {
        if (minColumnWidth_set) {
            return minColumnWidth;
        }
        ValueExpression ve = getValueExpression("minColumnWidth");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null)
                return (Integer)value;
        }
        return minColumnWidth;
    }

    public boolean isMinColumnWidthSet() {
        return minColumnWidth_set || getValueExpression("minColumnWidth") != null;
    }

    public void setMinColumnWidth(int minColumnWidth) {
        this.minColumnWidth = minColumnWidth;
        this.minColumnWidth_set = true;
    }

    /**
     * True to enable hiding of columns with the header context menu.
     */
    private boolean enableColumnHide;
    private boolean enableColumnHide_set;

    public boolean isEnableColumnHide() {
        if (enableColumnHide_set) {
            return this.enableColumnHide;
        }
        ValueExpression ve = getValueExpression("enableColumnHide");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public boolean isEnableColumnHideSet() {
        return enableColumnHide_set || getValueExpression("enableColumnHide") != null;
    }

    public void setEnableColumnHide(boolean enableColumnHide) {
        this.enableColumnHide = enableColumnHide;
        this.enableColumnHide_set = true;
    }

    /**
     * True to enable drag and drop reorder of columns.
     */
    private boolean enableColumnMove;
    private boolean enableColumnMove_set;

    public boolean isEnableColumnMove() {
        if (enableColumnMove_set) {
            return this.enableColumnMove;
        }
        ValueExpression ve = getValueExpression("enableColumnMove");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public boolean isEnableColumnMoveSet() {
        return enableColumnMove_set || getValueExpression("enableColumnMove") != null;
    }

    public void setEnableColumnMove(boolean enableColumnMove) {
        this.enableColumnMove = enableColumnMove;
        this.enableColumnMove_set = true;
    }

    /**
     * True to enable drag and drop of rows.
     */
    private boolean enableDragDrop;
    private boolean enableDragDrop_set;

    public boolean isEnableDragDrop() {
        if (enableDragDrop_set) {
            return this.enableDragDrop;
        }
        ValueExpression ve = getValueExpression("enableDragDrop");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public boolean isEnableDragDropSet() {
        return enableDragDrop_set || getValueExpression("enableDragDrop") != null;
    }

    public void setEnableDragDrop(boolean enableDragDrop) {
        this.enableDragDrop = enableDragDrop;
        this.enableDragDrop_set = true;
    }

    /**
     * True highlight rows when the mouse is over.
     */
    private boolean trackMouseOver;
    private boolean trackMouseOver_set;

    public boolean isTrackMouseOver() {
        if (trackMouseOver_set) {
            return this.trackMouseOver;
        }
        ValueExpression ve = getValueExpression("trackMouseOver");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public boolean isTrackMouseOverSet() {
        return trackMouseOver_set || getValueExpression("trackMouseOver") != null;
    }

    public void setTrackMouseOver(boolean trackMouseOver) {
        this.trackMouseOver = trackMouseOver;
        this.trackMouseOver_set = true;
    }

    /**
     * True to stripe the rows.
     */
    private boolean stripeRows;
    private boolean stripeRows_set;

    public boolean isStripeRows() {
        if (stripeRows_set) {
            return this.stripeRows;
        }
        ValueExpression ve = getValueExpression("stripeRows");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public boolean isStripeRowsSet() {
        return stripeRows_set || getValueExpression("stripeRows") != null;
    }

    public void setStripeRows(boolean stripeRows) {
        this.stripeRows = stripeRows;
        this.stripeRows_set = true;
    }

    /**
     * True to mask the grid while loading.
     */
    private Boolean loadMask;

    public boolean getLoadMask() {
        if (this.loadMask != null) {
            return this.loadMask;
        }
        ValueExpression ve = getValueExpression("loadMask");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setLoadMask(boolean loadMask) {
        this.loadMask = loadMask;
    }

    /**
     * True to show context menu.
     */
    private Boolean contextMenu;

    public boolean getContextMenu() {
        if (this.contextMenu != null) {
            return this.contextMenu;
        }
        ValueExpression ve = getValueExpression("contextMenu");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setContextMenu(boolean contextMenu) {
        this.contextMenu = contextMenu;
    }

    /**
     * True to show paging toolbar.
     */
    private Boolean paged;

    public boolean isPaged() {
        if (this.paged != null) {
            return this.paged;
        }
        ValueExpression ve = getValueExpression("paged");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    /**
     * Specify the paging toolbar position. May be "top", "bottom", or "both".
     */
    private String toolBarPosition;

    public String getToolBarPosition() {
        if (this.toolBarPosition != null) {
            return this.toolBarPosition;
        }
        ValueExpression ve = getValueExpression("toolBarPosition");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setToolBarPosition(String toolBarPosition) {
        this.toolBarPosition = toolBarPosition;
    }

    /**
     * Set a flag to force reload data.
     */
    private boolean needReload;

    public boolean isNeedReload() {
        return this.needReload;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public void reload() {
        this.needReload = true ;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            selectionModel,
            singleSelect,
            selectedRow,
            selectedRowSet,
            selectedColumn,
            selectedColumnSet,
            autoHeight,
            autoHeight_set,
            autoSizeColumns,
            autoSizeColumns_set,
            minColumnWidth,
            minColumnWidth_set,
            enableColumnHide,
            enableColumnHide_set,
            enableColumnMove,
            enableColumnMove_set,
            enableDragDrop,
            enableDragDrop_set,
            trackMouseOver,
            trackMouseOver_set,
            stripeRows,
            stripeRows_set,
            loadMask,
            contextMenu,
            paged,
            toolBarPosition
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        selectionModel = (SelectionModelType)values[i++];
        singleSelect = (Boolean)values[i++];
        selectedRow = (Integer)values[i++];
        selectedRowSet = (Boolean)values[i++];
        selectedColumn = (Integer)values[i++];
        selectedColumnSet = (Boolean)values[i++];
        autoHeight = (Boolean)values[i++];
        autoHeight_set = (Boolean)values[i++];
        autoSizeColumns = (Boolean)values[i++];
        autoSizeColumns_set = (Boolean)values[i++];
        minColumnWidth = (Integer)values[i++];
        minColumnWidth_set = (Boolean)values[i++];
        enableColumnHide = (Boolean)values[i++];
        enableColumnHide_set = (Boolean)values[i++];
        enableColumnMove = (Boolean)values[i++];
        enableColumnMove_set = (Boolean)values[i++];
        enableDragDrop = (Boolean)values[i++];
        enableDragDrop_set = (Boolean)values[i++];
        trackMouseOver = (Boolean)values[i++];
        trackMouseOver_set = (Boolean)values[i++];
        stripeRows = (Boolean)values[i++];
        stripeRows_set = (Boolean)values[i++];
        loadMask = (Boolean)values[i++];
        contextMenu = (Boolean)values[i++];
        paged = (Boolean)values[i++];
        toolBarPosition = (String)values[i++];
    }

    public Object saveOptimizedState(FacesContext context) {
        return new Object[] {
            selectedRow,
            selectedRowSet,
            selectedColumn,
            selectedColumnSet,
            getValueExpression("first") == null ? getFirst() : null,
            getValueExpression("rows") == null ? getRows() : null
        };
    }

    public void restoreOptimizedState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        this.selectedRow = (Integer)values[0];
        this.selectedRowSet = (Boolean)values[1];
        this.selectedColumn = (Integer)values[2];
        this.selectedColumnSet = (Boolean)values[3];

        Integer first = (Integer)values[4];
        if (first != null) {
            this.setFirst(first);
        }

        Integer rows = (Integer)values[5];
        if (rows != null) {
            this.setRows(rows);
        }
    }
}
