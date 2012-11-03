/*
 * $Id: DynamicAccordionLayout.java,v 1.4 2008/03/11 03:21:00 lishaochuan Exp $
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
 * 
 */
package org.operamasks.faces.component.layout;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.servlet.jsp.jstl.sql.Result;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class DynamicAccordionLayout extends AccordionLayout
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.DynamicAccordionLayout";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.LayoutManager";
    public static final String RENDERER_TYPE = "org.operamasks.faces.layout.DynamicAccordionLayout";

    public DynamicAccordionLayout() {
        setRendererType(RENDERER_TYPE);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Object value;
    private String var;
    private String indexVar;
    private DataModel model;

    public Object getValue() {
        if (this.value != null) {
            return this.value;
        }
        ValueExpression ve = getValueExpression("value");
        if (ve != null) {
            return (Object)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setValue(Object value) {
        setDataModel(null);
        this.value = value;
    }

    public String getVar() {
        if (this.var != null) {
            return this.var;
        }
        ValueExpression ve = getValueExpression("var");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setVar(String var) {
        this.var = var;
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

    public DataModel getDataModel() {
        if(model != null) {
            return model;
        }
        Object current = getValue();
        if (current == null) {
            setDataModel(new ListDataModel(Collections.EMPTY_LIST));
        } else if (current instanceof DataModel) {
            setDataModel((DataModel) current);
        } else if (current instanceof List) {
            setDataModel(new ListDataModel((List) current));
        } else if (Object[].class.isAssignableFrom(current.getClass())) {
            setDataModel(new ArrayDataModel((Object[]) current));
        } else if (current instanceof ResultSet) {
            setDataModel(new ResultSetDataModel((ResultSet) current));
        } else if (current instanceof Result) {
            setDataModel(new ResultDataModel((Result) current));
        } else {
            setDataModel(new ScalarDataModel(current));
        }
        return (model);
    }
    
    public void setRowIndex(int rowIndex) {
        DataModel localModel = getDataModel();
        localModel.setRowIndex(rowIndex);

        if (var != null) {
            Map<String, Object> requestMap =
                 getFacesContext().getExternalContext().getRequestMap();
            if (localModel.isRowAvailable()) {
                requestMap.put(var, localModel.getRowData());
            } else {
                requestMap.remove(var);
            }
        }
        
        if (indexVar != null) {
            Map<String, Object> requestMap =
                 getFacesContext().getExternalContext().getRequestMap();
            if (localModel.isRowAvailable()) {
                requestMap.put(indexVar, rowIndex);
            } else {
                requestMap.remove(indexVar);
            }
        }

    }

    protected void setDataModel(DataModel model) {
        this.model = model;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            var         ,
            indexVar    ,
            value       ,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        var         = (String) values[i++];
        indexVar    = (String) values[i++];
        value       = (Object) values[i++];
    }
}
