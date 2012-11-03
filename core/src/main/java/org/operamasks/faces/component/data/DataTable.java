/*
 * $Id: DataTable.java,v 1.4 2008/04/21 13:06:55 jacky Exp $
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

/**
 * 离线数据对象，以二维表形式组织，并包含列头信息等元数据，用于取代UIData类型的组件
 *
 */
@SuppressWarnings("serial")
public interface DataTable extends Serializable
{
    /**
     * 获取所有行数据
     */
    public DataRow[] getRows();

    /**
     * 获取指定行数据
     */
    public DataRow getRow(int rowIndex);

    /**
     * 根据主键获取行数据
     */
    public DataRow getRow(DataPK pk);
    
    /**
     * 得到总行数
     */
    public int getRowCount();
    
    /**
     * 添加一行数据
     */
    public void addRow(DataRow row);
    
    public void addRow(int rowIndex, DataRow row);

    /**
     * 移除一行数据
     */
    public DataRow removeRow(int rowIndex);

    /**
     * 移除一行数据
     */
    public DataRow removeRow(DataPK pk);

    /**
     * 移除一行数据
     */
    public DataRow removeRow(DataRow row);

    /**
     * 获取列模型
     */
    public DataColumnModel getColumnModel();
    
    /**
     * 为DataTable处理或移除Listener
     */
    public void addDataTableListener(DataTableEventType eventType, DataTableListener listener);
    public void removeDataTableListener(DataTableEventType eventType, DataTableListener listener);
}
