/*
 * $Id: DataColumnModel.java,v 1.3 2008/04/21 18:10:24 jacky Exp $
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
package org.operamasks.faces.component.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 数据列模型, 每个DataTable将会绑定一个数据列模型
 *
 */
@SuppressWarnings("serial")
public class DataColumnModel implements Serializable
{
    private ArrayList<DataColumn> columns;
    private ColumnModelProvider provider;
    
    public DataColumnModel() {
        this.columns = new ArrayList<DataColumn>();
    }

    public DataColumnModel(ColumnModelProvider provider) {
        this.columns = new ArrayList<DataColumn>();
        this.provider = provider;
    }
    
    /**
     * 获取所有列
     */
    public DataColumn[] getColumns() {
        return columns.toArray(new DataColumn[columns.size()]);
    }

    /**
     * 获取指定列
     */
    public DataColumn getColumn(int colIndex) {
        return columns.get(colIndex);
    }

    /**
     * 获取指定Id的列
     */
    public DataColumn getColumn(String colId) {
        return findColumnById(colId);
    }

    /**
     * 得到列数量
     */
    public int getColumnCount() {
        return columns.size();
    }
    
    /**
     * 添加一列
     */
    public void addColumn(DataColumn column) {
        this.columns.add(column);
        this.columns.trimToSize();
    }

    /**
     * 在指定位置添加一列
     */
    public void addColumn(int colIndex, DataColumn column) {
        this.columns.add(colIndex, column);
        this.columns.trimToSize();
    }
    
    /**
     * 移除一列
     */
    public DataColumn removeColumn(int colIndex) {
        DataColumn column = this.getColumn(colIndex);
        this.columns.remove(column);
        return column;
    }

    public DataColumn removeColumn(String colId) {
        DataColumn column = this.getColumn(colId);
        this.columns.remove(column);
        return column;
    }

    public DataColumn removeColumn(DataColumn column) {
        return this.columns.remove(column) ? column : null;
    }

    public void reset() {
        this.columns.clear();
    }
    
    public DataColumn[] getPrimaryKeyColumns() {
        ArrayList<DataColumn> result = new ArrayList<DataColumn>();
        for (DataColumn column : this.columns) {
            if (column.isPrimaryKey()) {
                result.add(column);
            }
        }
        result.trimToSize();
        return result.toArray(new DataColumn[result.size()]);
    }
    
    private DataColumn findColumnById(String colId) {
        if (colId == null) {
            return null;
        }
        for (DataColumn column : columns) {
            if (colId.equals(column.getId())) {
                return column;
            }
        }
        return null;
    }

}
