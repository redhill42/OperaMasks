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
package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;

import org.operamasks.faces.component.widget.grid.UIEditDataGrid;

/**
 * @jsp.tag name="editDataGrid" body-content="JSP"
 */
public class EditDataGridTag extends DataGridTag {
    public String getComponentType() {
        return UIEditDataGrid.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIEditDataGrid.RENDERER_TYPE;
    }
    
    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setBindBean(ValueExpression bindBean) {
        setValueExpression("bindBean", bindBean);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setAddedData(ValueExpression changedData) {
        setValueExpression("addedData", changedData);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setModifiedData(ValueExpression changedData) {
        setValueExpression("modifiedData", changedData);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setRemovedData(ValueExpression changedData) {
        setValueExpression("removedData", changedData);
    }
}
