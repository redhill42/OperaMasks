/*
 * $Id: BeanArrayAdapter.java,v 1.1 2008/04/21 18:10:24 jacky Exp $
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;

import org.operamasks.faces.component.data.DataColumn;
import org.operamasks.faces.component.data.DataColumnModel;
import org.operamasks.faces.component.data.DataLoadException;
import org.operamasks.faces.component.data.DataLoadAdapter;
import org.operamasks.faces.component.data.DataRow;
import org.operamasks.faces.component.data.DataTable;

/**
 * 数组形式的数据提供者
 * @param <T>
 *
 */
public class BeanArrayAdapter<T> implements DataLoadAdapter<T>
{

	public DataTable provideData(T data) throws DataLoadException{
	    if (data == null || !data.getClass().isArray()) {
			throw new IllegalArgumentException("data must be not null and an array object");
		}
	    Class<?> clazz = data.getClass();
	    Class<?> itemType = clazz.getComponentType();

	    PropertyDescriptor[] props = null;
	    try {
	        props = Introspector.getBeanInfo(itemType).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("the item type of array has not properties", e);
        }
        
        
        
        DataColumnModel model = new DataColumnModel();
        for (PropertyDescriptor prop : props) {
            if (prop.getReadMethod() != null && !"class".equals(prop.getName())) {
                DataColumn column = new DataColumn(prop.getName(), prop.getPropertyType());
                column.setCaption(prop.getName());
                model.addColumn(column);
            }
        }
        
        if (model.getColumnCount() == 0) {
            throw new IllegalArgumentException("could not find any readMethod from item type of the array");
        }
        
        DataTable table = new DataTableImpl(model);
        
        int length = Array.getLength(data);
        for (int i = 0; i < length ; i++) {
            DataRow row = new DataRow();
            Object rowObj = Array.get(data, i);
            for (PropertyDescriptor prop : props) {
                if (prop.getReadMethod() != null && !"class".equals(prop.getName())) {
                    Object propValue;
                    try {
                        propValue = prop.getReadMethod().invoke(rowObj);
                    } catch (Exception e) {
                        throw new DataLoadException("error happended while read value of property["+ itemType.getName() +"." + prop.getName() + "]", e);
                    }
                    row.setData(prop.getName(), propValue);
                }
            }
            table.addRow(row);
        }
		return table;
	}

}
