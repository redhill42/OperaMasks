/*
 * $Id: DataTableImpl.java,v 1.1 2008/04/21 13:06:55 jacky Exp $
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
package org.operamasks.faces.component.data.impl;

import java.util.ArrayList;

import org.operamasks.faces.component.data.DataColumn;
import org.operamasks.faces.component.data.DataColumnModel;
import org.operamasks.faces.component.data.DataPK;
import org.operamasks.faces.component.data.DataRow;
import org.operamasks.faces.component.data.DataTable;
import org.operamasks.faces.component.data.DataTableEvent;
import org.operamasks.faces.component.data.DataTableEventProcessor;
import org.operamasks.faces.component.data.DataTableEventType;
import org.operamasks.faces.component.data.DataTableListener;
import org.operamasks.faces.component.data.DataTableRowEvent;

/**
 * DataTable的实现
 * @see DataTable
 *
 */
@SuppressWarnings({"serial","unchecked"})
public class DataTableImpl implements DataTable
{
    private ArrayList<DataRow> rows;
    private DataColumnModel columnModel;
    private DataTableEventProcessor eventProcessor;

    
    public DataTableImpl(DataColumnModel columnModel) {
        this.columnModel = columnModel;
        this.rows = new ArrayList<DataRow>();
        this.eventProcessor = new DataTableEventProcessor();
    }

    
    public void addRow(DataRow row) {
        eventProcessor.broadcast(DataTableEventType.BEFORE_ROW_ADD, new DataTableRowEvent(this,row));
        this.rows.add(row);
        this.rows.trimToSize();
        eventProcessor.broadcast(DataTableEventType.AFTER_ROW_ADD, new DataTableRowEvent(this,row));
    }

    public void addRow(int rowIndex, DataRow row) {
        eventProcessor.broadcast(DataTableEventType.BEFORE_ROW_ADD, new DataTableRowEvent(this,row));
        this.rows.add(rowIndex, row);
        this.rows.trimToSize();
        eventProcessor.broadcast(DataTableEventType.AFTER_ROW_ADD, new DataTableRowEvent(this,row));
    }

    public DataColumnModel getColumnModel() {
        return this.columnModel;
    }

    public DataRow getRow(int rowIndex) {
        return this.rows.get(rowIndex);
    }

    public DataRow getRow(DataPK pk) {
        return findRowByPK(pk);
    }

    public int getRowCount() {
        return this.rows.size();
    }

    public DataRow[] getRows() {
        eventProcessor.broadcast(DataTableEventType.BEFORE_FETCH_ROW, new DataTableEvent(this));
        return this.rows.toArray(new DataRow[this.rows.size()]);
    }

    public DataRow removeRow(int rowIndex) {
        DataRow row = this.getRow(rowIndex);
        eventProcessor.broadcast(DataTableEventType.BEFORE_ROW_DELETE, new DataTableRowEvent(this,row));
        this.rows.remove(row);
        eventProcessor.broadcast(DataTableEventType.AFTER_ROW_DELETE, new DataTableRowEvent(this,row));
        return row == null ? null : row;
    }

    public DataRow removeRow(DataPK pk) {
        DataRow row = this.findRowByPK(pk);
        eventProcessor.broadcast(DataTableEventType.BEFORE_ROW_DELETE, new DataTableRowEvent(this,row));
        this.rows.remove(row);
        eventProcessor.broadcast(DataTableEventType.AFTER_ROW_DELETE, new DataTableRowEvent(this,row));
        return row == null ? null : row;
    }

    public DataRow removeRow(DataRow row) {
        eventProcessor.broadcast(DataTableEventType.BEFORE_ROW_DELETE, new DataTableRowEvent(this,row));
        boolean removed = this.rows.remove(row);
        eventProcessor.broadcast(DataTableEventType.AFTER_ROW_DELETE, new DataTableRowEvent(this,row));
        return removed ? row : null;
    }

    private DataRow findRowByPK(DataPK pk) {
        DataColumn[] pkColumns = this.columnModel.getPrimaryKeyColumns();
        if (pkColumns.length == 0 || pk.getValues() == null || pkColumns.length != pk.getValues().length) {
            throw new IllegalArgumentException("primary key column is not match DataPK's length");
        }
        
        for (DataRow row : this.rows) {
            boolean isMatch = true;
            Object[] values = pk.getValues();
            for (int i = 0 ; i < values.length ; i++) {
                Object pkData = values[i];
                if (pkData == null || !pkData.equals(row.getData(i))) {
                    isMatch = false;
                }
            }
            if (isMatch) {
                return row;
            }
        }
        return null;
    }

    public void addDataTableListener(DataTableEventType eventType,
            DataTableListener listener) {
        eventProcessor.addDataTableListener(eventType, listener);
    }


    public void removeDataTableListener(DataTableEventType eventType,
            DataTableListener listener) {
        eventProcessor.removeDataTableListener(eventType, listener);
    }
    
}
