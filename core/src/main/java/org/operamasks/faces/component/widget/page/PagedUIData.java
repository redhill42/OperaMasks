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

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ScalarDataModel;

public class PagedUIData extends UIData
{
    private DataModel model;
    private String indexVar;
    
    @Override
    protected void setDataModel(DataModel dataModel) {
        if (dataModel instanceof ScalarDataModel) {
            Object scalar = dataModel.getWrappedData();
            if (scalar instanceof PagedDataProvider) {
                dataModel = new PagedDataModel((PagedDataProvider)scalar);
            }
        }

        if (dataModel instanceof PagedDataModel) {
            PagedDataModel paged = (PagedDataModel)dataModel;
            paged.setStart(super.getFirst());
            paged.setLimit(super.getRows());
        }

        super.setDataModel(dataModel);
        this.model = dataModel;
    }

    @Override
    public void setFirst(int first) {
        super.setFirst(first);
        if (this.model instanceof PagedDataModel) {
            ((PagedDataModel)this.model).setStart(first);
        }
    }
    
    @Override
    public void setRows(int rows) {
        super.setRows(rows);
        if (this.model instanceof PagedDataModel) {
            ((PagedDataModel)this.model).setLimit(rows);
        }
    }
    
    @Override
    public void setRowIndex(int rowIndex) {
        super.setRowIndex(rowIndex);
        if (indexVar != null) {
            Map<String, Object> requestMap =
                 getFacesContext().getExternalContext().getRequestMap();
            if (model.isRowAvailable()) {
                requestMap.put(indexVar, rowIndex);
            } else {
                requestMap.remove(indexVar);
            }
        }
    }
    
    public String getIndexVar() {
        if (this.indexVar != null) {
            return this.indexVar;
        }
        ValueExpression ve = getValueExpression("indexVar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setIndexVar(String indexVar) {
        this.indexVar = indexVar;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            indexVar
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        indexVar = (String)values[i++];
    }
}
