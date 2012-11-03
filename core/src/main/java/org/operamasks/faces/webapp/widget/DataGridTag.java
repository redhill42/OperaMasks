/*
 * $Id: DataGridTag.java,v 1.11 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.webapp.widget;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.event.MethodExpressionRowSelectListener;
import org.operamasks.faces.event.MethodExpressionCellSelectListener;

/**
 * @jsp.tag name="dataGrid" body-content="JSP"
 */
public class DataGridTag extends HtmlBasicELTag
{
    private String var;
    private MethodExpression rowSelectListener;
    private MethodExpression cellSelectListener;

    public String getComponentType() {
        return UIDataGrid.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIDataGrid.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }
    
    /**
     * @jsp.attribute type="int"
     */
    public void setFirst(ValueExpression first) {
        setValueExpression("first", first);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setRows(ValueExpression rows) {
        setValueExpression("rows", rows);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setIndexVar(ValueExpression indexVar) {
        setValueExpression("indexVar", indexVar);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.widget.grid.UIDataGrid.SelectionModelType"
     */
    public void setSelectionModel(ValueExpression selectionModel) {
        setValueExpression("selectionModel", selectionModel);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setSingleSelect(ValueExpression singleSelect) {
        setValueExpression("singleSelect", singleSelect);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setSelectedRow(ValueExpression selectedRow) {
        setValueExpression("selectedRow", selectedRow);
    }

    /**
     * @jsp.attribute type="int[]"
     */
    public void setSelections(ValueExpression selections) {
        setValueExpression("selections", selections);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setSelectedColumn(ValueExpression selectedColumn) {
        setValueExpression("selectedColumn", selectedColumn);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setSelectedRowData(ValueExpression selectedRowData) {
        setValueExpression("selectedRowData", selectedRowData);
    }
    
    /**
     * @jsp.attribute method-signature="void select(org.operamasks.faces.event.RowSelectEvent)"
     */
    public void setRowSelectListener(MethodExpression rowSelectListener) {
        this.rowSelectListener = rowSelectListener;
    }

    /**
     * @jsp.attribute method-signature="void select(org.operamasks.faces.event.CellSelectEvent)"
     */
    public void setCellSelectListener(MethodExpression cellSelectListener) {
        this.cellSelectListener = cellSelectListener;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnrowselect(ValueExpression onrowselect) {
        setValueExpression("onrowselect", onrowselect);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOncellselect(ValueExpression oncellselect) {
        setValueExpression("oncellselect", oncellselect);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOndataready(ValueExpression ondataready) {
        setValueExpression("ondataready", ondataready);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setAutoHeight(ValueExpression autoHeight) {
        setValueExpression("autoHeight", autoHeight);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setAutoSizeColumns(ValueExpression autoSizeColumns) {
        setValueExpression("autoSizeColumns", autoSizeColumns);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setMinColumnWidth(ValueExpression minColumnWidth) {
        setValueExpression("minColumnWidth", minColumnWidth);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableColumnHide(ValueExpression enableColumnHide) {
        setValueExpression("enableColumnHide", enableColumnHide);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableColumnMove(ValueExpression enableColumnMove) {
        setValueExpression("enableColumnMove", enableColumnMove);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableDragDrop(ValueExpression enableDragDrop) {
        setValueExpression("enableDragDrop", enableDragDrop);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setTrackMouseOver(ValueExpression trackMouseOver) {
        setValueExpression("trackMouseOver", trackMouseOver);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setStripeRows(ValueExpression stripeRows) {
        setValueExpression("stripeRows", stripeRows);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setLoadMask(ValueExpression loadMask) {
        setValueExpression("loadMask", loadMask);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setContextMenu(ValueExpression contextMenu) {
        setValueExpression("contextMenu", contextMenu);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setPaged(ValueExpression paged) {
        setValueExpression("paged", paged);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setToolBarPosition(ValueExpression toolBarPosition) {
        setValueExpression("toolBarPosition", toolBarPosition);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIDataGrid grid = (UIDataGrid)component;
        grid.setVar(var);
        if (rowSelectListener != null) {
            grid.addRowSelectListener(new MethodExpressionRowSelectListener(rowSelectListener));
        }
        if (cellSelectListener != null) {
            grid.addCellSelectListener(new MethodExpressionCellSelectListener(cellSelectListener));
        }
    }

    public void release() {
        super.release();
        var = null;
        rowSelectListener = null;
        cellSelectListener = null;
    }
}
