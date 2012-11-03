/*
 * $Id: AxisTag.java,v 1.5 2007/07/02 07:37:56 jacky Exp $
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
import org.operamasks.faces.component.graph.UIAxis;

/**
 * @jsp.tag name="axis" body-content="JSP"
 */
public class AxisTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UIAxis.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setVisible(ValueExpression visible) {
        setValueExpression("visible", visible);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setMapTo(ValueExpression mapTo) {
        setValueExpression("mapTo", mapTo);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setLogarithmic(ValueExpression logarithmic) {
        setValueExpression("logarithmic", logarithmic);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLabel(ValueExpression label) {
        setValueExpression("label", label);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setLabelFont(ValueExpression labelFont) {
        setValueExpression("labelFont", labelFont);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setLabelColor(ValueExpression labelColor) {
        setValueExpression("labelColor", labelColor);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setLabelAngle(ValueExpression labelAngle) {
        setValueExpression("labelAngle", labelAngle);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawLine(ValueExpression drawLine) {
        setValueExpression("drawLine", drawLine);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setLineColor(ValueExpression lineColor) {
        setValueExpression("lineColor", lineColor);
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setDrawGridLine(ValueExpression drawGridLine) {
        setValueExpression("drawGridLine", drawGridLine);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setGridLineColor(ValueExpression gridLineColor) {
        setValueExpression("gridLineColor", gridLineColor);
    }

    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setDrawBaseLine(ValueExpression drawBaseLine) {
        setValueExpression("drawBaseLine", drawBaseLine);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setBaseLineColor(ValueExpression baseLineColor) {
        setValueExpression("baseLineColor", baseLineColor);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawTickLabels(ValueExpression drawTickLabels) {
        setValueExpression("drawTickLabels", drawTickLabels);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setTickLabelFont(ValueExpression tickLabelFont) {
        setValueExpression("tickLabelFont", tickLabelFont);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setTickLabelColor(ValueExpression tickLabelColor) {
        setValueExpression("tickLabelColor", tickLabelColor);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTickLabelFormat(ValueExpression tickLabelFormat) {
        setValueExpression("tickLabelFormat", tickLabelFormat);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setItemTipFormat(ValueExpression itemTipFormat) {
        setValueExpression("itemTipFormat", itemTipFormat);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setDrawTickMarks(ValueExpression drawTickMarks) {
        setValueExpression("drawTickMarks", drawTickMarks);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setTickMarkColor(ValueExpression tickMarkColor) {
        setValueExpression("tickMarkColor", tickMarkColor);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setTickMarkInsideLength(ValueExpression tickMarkInsideLength) {
        setValueExpression("tickMarkInsideLength", tickMarkInsideLength);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setTickMarkOutsideLength(ValueExpression tickMarkOutsideLength) {
        setValueExpression("tickMarkOutsideLength", tickMarkOutsideLength);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setInverted(ValueExpression inverted) {
        setValueExpression("inverted", inverted);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setLowerBound(ValueExpression lowerBound) {
        setValueExpression("lowerBound", lowerBound);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setUpperBound(ValueExpression upperBound) {
        setValueExpression("upperBound", upperBound);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setLowerMargin(ValueExpression lowerMargin) {
        setValueExpression("lowerMargin", lowerMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setUpperMargin(ValueExpression upperMargin) {
        setValueExpression("upperMargin", upperMargin);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setTickStep(ValueExpression tickStep) {
        setValueExpression("tickStep", tickStep);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.TimePeriodType"
     */
    public void setTickUnit(ValueExpression tickUnit) {
        setValueExpression("tickUnit", tickUnit);
    }
}
