/*
 * $Id: OutputColumnTag.java,v 1.8 2007/12/11 04:20:12 jacky Exp $
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
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.widget.grid.UIOutputColumn;

/**
 * @jsp.tag name="outputColumn" body-content="JSP"
 */
public class OutputColumnTag extends HtmlBasicELTag
{
    private MethodExpression formatter;

    public String getComponentType() {
        return UIOutputColumn.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="javax.faces.convert.Converter"
     */
    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute method-signature="java.lang.String format(javax.faces.component.UIColumn, java.lang.Object)"
     */
    public void setFormatter(MethodExpression formatter) {
        this.formatter = formatter;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setClientFormatter(ValueExpression clientFormatter) {
        setValueExpression("clientFormatter", clientFormatter);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setHeader(ValueExpression header) {
        setValueExpression("columnHeader", header);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTooltip(ValueExpression tooltip) {
        setValueExpression("tooltip", tooltip);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAlign(ValueExpression align) {
        setValueExpression("align", align);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setLocked(ValueExpression locked) {
        setValueExpression("locked", locked);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setFixed(ValueExpression fixed) {
        setValueExpression("fixed", fixed);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setSortable(ValueExpression sortable) {
        setValueExpression("sortable", sortable);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setHidden(ValueExpression hidden) {
        setValueExpression("hidden", hidden);
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
     * @jsp.attribute type="java.lang.String"
     */
    public void setEditor(ValueExpression editor) {
        setValueExpression("editor", editor);
    }

    public void setProperties(UIComponent component) {
        super.setProperties(component);

        if (formatter != null) {
            ((UIOutputColumn)component).setFormatter(formatter);
        }
    }

    public void release() {
        super.release();
        formatter = null;
    }
}
