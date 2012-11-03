/*
 * $Id: DataSeriesTag.java,v 1.4 2007/07/02 07:37:56 jacky Exp $
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

package org.operamasks.faces.webapp.graph;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.graph.UIDataSeries;

/**
 * @jsp.tag name="dataSeries" body-content="JSP"
 */
public class DataSeriesTag extends HtmlBasicELTag
{
    private String var;

    public String getComponentType() {
        return UIDataSeries.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    protected String getFacetName() {
        return "dataSeries";
    }
    
    /**
     * @jsp.attribute type="int"
     */
    public void setFirst(ValueExpression first) {
        setValueExpression("first", first);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setRows(ValueExpression rows) {
        setValueExpression("rows", rows);
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

    public void setProperties(UIComponent component) {
        super.setProperties(component);
        ((UIData)component).setVar(var);
    }

    public void release() {
        super.release();
        var = null;
    }
}
