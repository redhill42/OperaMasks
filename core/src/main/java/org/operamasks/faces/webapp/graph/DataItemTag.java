/*
 * $Id: DataItemTag.java,v 1.4 2007/07/02 07:37:56 jacky Exp $
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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.faces.webapp.UIComponentClassicTagBase;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.graph.UIDataItem;

/**
 * @jsp.tag name="dataItem" body-content="JSP"
 */
public class DataItemTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UIDataItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLegend(ValueExpression legend) {
        setValueExpression("legend", legend);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setShowLegend(ValueExpression showLegend) {
        setValueExpression("showLegend", showLegend);
    }
    
    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setColor(ValueExpression color) {
        setValueExpression("color", color);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawOutline(ValueExpression drawOutline) {
        setValueExpression("drawOutline", drawOutline);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setOutlineColor(ValueExpression outlineColor) {
        setValueExpression("outlineColor", outlineColor);
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setDrawItemLabel(ValueExpression drawItemLabel) {
        setValueExpression("drawItemLabel", drawItemLabel);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setItemLabelColor(ValueExpression itemLabelColor) {
        setValueExpression("itemLabelColor", itemLabelColor);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setItemLabelFont(ValueExpression itemLabelFont) {
        setValueExpression("itemLabelFont", itemLabelFont);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawLines(ValueExpression drawLines) {
        setValueExpression("drawLines", drawLines);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setLineWidth(ValueExpression lineWidth) {
        setValueExpression("lineWidth", lineWidth);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.LineStyleType"
     */
    public void setLineStyle(ValueExpression lineStyle) {
        setValueExpression("lineStyle", lineStyle);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawMarkers(ValueExpression drawMarkers) {
        setValueExpression("drawMarkers", drawMarkers);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setFillMarkers(ValueExpression fillMarkers) {
        setValueExpression("fillMarkers", fillMarkers);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setMarkerFillColor(ValueExpression markerFillColor) {
        setValueExpression("markerFillColor", markerFillColor);
    }

    public int doStartTag() throws JspException {
        if (this.getClass() == DataItemTag.class) {
            UIComponentClassicTagBase tag = getParentUIComponentClassicTagBase(pageContext);
            if ((tag == null) || (tag.getClass() != DataSeriesTag.class)) {
                throw new JspTagException("The dataItem tag can only be nested in a dataSeries tag.");
            }
        }
        return super.doStartTag();
    }
}
