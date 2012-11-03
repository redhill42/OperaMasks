/*
 * $Id 
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

public class UIEditDataGrid extends UIDataGrid{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.EditDataGrid";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.EditGrid";
    
    public UIEditDataGrid() {
        setRendererType(RENDERER_TYPE);
        pos = -1;
    }
    
    private Object addedData;
    public Object getAddedData() {
        return this.addedData;
    }

    public void setAddedData(Object addedData) {
        this.addedData = addedData;
        ValueExpression ve = getValueExpression("addedData");
        if (ve != null) {
            ve.setValue(getFacesContext().getELContext(), addedData);
        }
    }

    private Object transData;
    public Object getTransData() {
        return this.transData;
    }

    public void setTransData(Object transData) {
        this.transData = transData;
        ValueExpression ve = getValueExpression("transData");
        if (ve != null) {
            ve.setValue(getFacesContext().getELContext(), transData);
        }
    }

    private Object modifiedData;
    public Object getModifiedData() {
        return this.modifiedData;
    }

    public void setModifiedData(Object modifiedData) {
        this.modifiedData = modifiedData;
        ValueExpression ve = getValueExpression("modifiedData");
        if (ve != null) {
            ve.setValue(getFacesContext().getELContext(), modifiedData);
        }
    }

    private Object removedData;
    public Object getRemovedData() {
        return this.removedData;
    }

    public void setRemovedData(Object removedData) {
        this.removedData = removedData;
        ValueExpression ve = getValueExpression("removedData");
        if (ve != null) {
            ve.setValue(getFacesContext().getELContext(), removedData);
        }
    }
    
    
    private Object bindBean;

    public Object getBindBean() {
        if (this.bindBean != null) {
            return this.bindBean;
        }
        ValueExpression ve = getValueExpression("bindBean");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBindBean(Object bindBean) {
        this.bindBean = bindBean;
    }
    
    private int pos;

    public void insertRow(int pos) {
        this.pos = pos;
    }

    public int getInsertRow() {
        return this.pos;
    }

    private Object insertRowData;

    public void insertRow(int pos, Object rowData) {
        this.pos = pos;
        this.insertRowData = rowData;
    }

    public Object getInsertRowData() {
        return this.insertRowData;
    }

    private boolean commit;
    
    public void commit() {
        this.commit = true;
    }
    public boolean isCommit() {
        return this.commit;
    }
    
    public void setCommit(boolean commit) {
        this.commit = commit;
    }
    
    public void remove() {
        this.remove(-1);
    }
    
    public void remove(int row) {
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("row", new Integer(row));
        cm.invoke(getFacesContext(), "remove", this);
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            bindBean,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        bindBean = values[i++];
    }
}
