/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.component.widget.page;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.servlet.jsp.jstl.sql.Result;

public class PagedDataModel extends DataModel{

    private PagedDataProvider provider;
    private DataModel pagedModel;
    private int index;
    private int start = -1;
    private int limit = -1;
    private int rowCount = -1;
    private int[] currRange = new int[2];

    public PagedDataModel(PagedDataProvider provider) {
        setWrappedData(provider);
    }
    
    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Object getRowData() {
        if (provider == null) {
            return (null);
        } else if (!isRowAvailable()) {
            throw new IllegalArgumentException();
        }
        
        if(start == -1) start = 0;
        if(limit == -1) limit = rowCount;
        
        if(pagedModel == null) {
            createPagedDataModel(start, limit);
        }
        
        if(index < currRange[0] || index > currRange[1]) {
            start = (index/limit)*limit;
            createPagedDataModel(start, limit);
        }
        pagedModel.setRowIndex(index-start);
        if(pagedModel.isRowAvailable()) {
            return pagedModel.getRowData();
        }
        return null;
    }

    @Override
    public int getRowIndex() {
        return index;
    }

    @Override
    public Object getWrappedData() {
        return this.provider;
    }

    @Override
    public boolean isRowAvailable() {
        return (provider != null) && (index >= 0 && index < rowCount);
    }

    @Override
    public void setRowIndex(int rowIndex) {

        if (rowIndex < -1) {
            throw new IllegalArgumentException();
        }
        int old = index;
        index = rowIndex;
        if (provider == null) {
            return;
        }
        
        DataModelListener [] listeners = getDataModelListeners();
        int n = listeners.length;
        if ((old != index) && (listeners != null) && n > 0) {
            Object rowData = null;
            if (isRowAvailable()) {
                rowData = getRowData();
            }
            DataModelEvent event =
                new DataModelEvent(this, index, rowData);
            for (int i = 0; i < n; i++) {
                if (null != listeners[i]) {
                    listeners[i].rowSelected(event);
                }
            }
        }

    }

    @Override
    public void setWrappedData(Object data) {
        if (data == null) {
            setRowIndex(-1);
            throw new IllegalArgumentException("data must be an implemention of org.operamasks.faces.component.widget.page.PagedDataProvider");
        } else {
            this.provider = (PagedDataProvider) data;
            index = -1;
            rowCount = provider.getTotalCount();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createPagedDataModel(int start, int limit) {
        Object current = this.provider.getData(start, limit);
        if (current == null) {
            pagedModel = new ListDataModel(Collections.EMPTY_LIST);
        } else if (current instanceof DataModel) {
            pagedModel = (DataModel) current;
        } else if (current instanceof List) {
            pagedModel = new ListDataModel((List) current);
        } else if (Object[].class.isAssignableFrom(current.getClass())) {
            pagedModel = new ArrayDataModel((Object[]) current);
        } else if (current instanceof ResultSet) {
            pagedModel = new ResultSetDataModel((ResultSet) current);
        } else if (current instanceof Result) {
            pagedModel = new ResultDataModel((Result) current);
        } else {
            pagedModel = new ScalarDataModel(current);
        }
        currRange[0] = start;
        currRange[1] = start + limit - 1;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
