/*
 * $Id: DataViewTag.java,v 1.6 2007/08/22 05:30:59 jacky Exp $
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

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.UIDataView;

/**
 * @jsp.tag name="dataView" body-content="JSP"
 */
public class DataViewTag extends HtmlBasicELTag
{
    private String var;

    public String getComponentType() {
        return UIDataView.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIDataView.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
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

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTemplate(ValueExpression template) {
        setValueExpression("template", template);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setContainer(ValueExpression container) {
        setValueExpression("container", container);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setIndexVar(ValueExpression indexVar) {
        setValueExpression("indexVar", indexVar);
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setAsync(ValueExpression  async) {
        setValueExpression("async", async);
    }
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOndataready(ValueExpression ondataready) {
        setValueExpression("ondataready", ondataready);
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

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIDataView view = (UIDataView)component;
        view.setVar(var);
    }

    public void release() {
        super.release();
        var = null;
    }
}
