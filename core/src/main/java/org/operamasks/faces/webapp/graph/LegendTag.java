/*
 * $Id: LegendTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.graph.UILegend;

/**
 * @jsp.tag name="legend" body-content="JSP"
 */
public class LegendTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UILegend.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    protected String getFacetName() {
        return "legend";
    }
    
    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.PositionType"
     */
    public void setPosition(ValueExpression position) {
        setValueExpression("position", position);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setLeftMargin(ValueExpression leftMargin) {
        setValueExpression("leftMargin", leftMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setRightMargin(ValueExpression rightMargin) {
        setValueExpression("rightMargin", rightMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setTopMargin(ValueExpression topMargin) {
        setValueExpression("topMargin", topMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setBottomMargin(ValueExpression bottomMargin) {
        setValueExpression("bottomMargin", bottomMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setBackgroundColor(ValueExpression backgroundColor) {
        setValueExpression("backgroundColor", backgroundColor);
    }
    
    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setBorderColor(ValueExpression borderColor) {
        setValueExpression("borderColor", borderColor);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setBorderWidth(ValueExpression borderWidth) {
        setValueExpression("borderWidth", borderWidth);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setItemFont(ValueExpression itemFont) {
        setValueExpression("itemFont", itemFont);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setItemColor(ValueExpression itemColor) {
        setValueExpression("itemColor", itemColor);
    }
}
