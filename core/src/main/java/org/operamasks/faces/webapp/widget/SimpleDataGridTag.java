/*
 * $Id: SimpleDataGridTag.java,v 1.2 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.grid.UISimpleDataGrid;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="simpleDataGrid" body-content="JSP"
 */
public class SimpleDataGridTag extends HtmlBasicELTag
{
    private String var;

    public String getComponentType() {
        return UISimpleDataGrid.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UISimpleDataGrid.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }


    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setIndexVar(ValueExpression indexVar) {
        setValueExpression("indexVar", indexVar);
    }

    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }
    
    /**
     * @jsp.attribute type="int[]"
     */
    public void setSelections(ValueExpression selections) {
        setValueExpression("selections", selections);
    }
    
    /**
     * @jsp.attribute type = "java.lang.Boolean"
     */
    public void setShowSelectionColumn(ValueExpression selections) {
        setValueExpression("showSelectionColumn", selections);
    }
    
    /**
     * @jsp.attribute type="int"
     */
    public void setSelectionColumnIndex(ValueExpression selections) {
        setValueExpression("selectionColumnIndex", selections);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UISimpleDataGrid view = (UISimpleDataGrid)component;
        view.setVar(var);
    }

    public void release() {
        super.release();
        var = null;
    }
}
