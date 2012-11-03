/*
 * $Id: DataRow.java,v 1.5 2008/04/21 18:10:24 jacky Exp $
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

import org.operamasks.util.DataMap;

/**
 * 行数据对象，每个DataTable包含了n行的DataRow。<br/>
 * 出于性能考虑DataRow对象并不持有ColumnModel对象，因此其自身并不具备按ColumnModel校验的能力，
 * 并不保证加入的数据是完全符合ColumnModel要求的数据。<br/>
 * DataRow对象存储的是columnId与data的键值对。
 */
@SuppressWarnings("serial")
public class DataRow implements Serializable
{
    private DataMap<String, Object> data;
    
    public DataRow() {
        this.data = new DataMap<String, Object>();
    }
    
    public void setData(String columnId, Object data) {
        this.data.put(columnId, data);
        this.data.trimToSize();
    }

    public Object getData(String columnId) {
        return this.data.get(columnId);
    }
    
    public Object removeData(String columnId) {
        return this.data.remove(columnId);
    }

    public Object getData(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.data.size()) {
            throw new IllegalArgumentException("out of bound, rowIndex : " + rowIndex);
        }
        return this.toArray()[rowIndex];
    }
    

    public void reset() {
        this.data.clear();
    }
    
    @SuppressWarnings("unchecked")
    public String[] getColumnKeys() {
        return (String[]) this.data.keySet().toArray(new String[this.data.keySet().size()]);
    }
    
    public Object[] toArray() {
        return this.data.values().toArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return (T[]) this.data.values().toArray(a);
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }
}
