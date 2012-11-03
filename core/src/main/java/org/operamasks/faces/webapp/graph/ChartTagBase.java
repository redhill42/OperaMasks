/*
 * $Id: ChartTagBase.java,v 1.4 2007/07/02 07:37:56 jacky Exp $
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
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.graph.UIChart;

public abstract class ChartTagBase extends HtmlBasicELTag
{
    private MethodExpression init;

    public String getComponentType() {
        return UIChart.COMPONENT_TYPE;
    }

    public abstract String getRendererType();

    /**
     * @jsp.attribute method-signature="void init(java.lang.Object)"
     */
    public void setInit(MethodExpression init) {
        this.init = init;
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setHeight(ValueExpression height) {
        setValueExpression("height", height);
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
     * @jsp.attribute type="org.operamasks.faces.component.graph.OrientationType"
     */
    public void setOrientation(ValueExpression orientation) {
        setValueExpression("orientation", orientation);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setShowLegend(ValueExpression showLegend) {
        setValueExpression("showLegend", showLegend);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEffect3D(ValueExpression effect3D) {
        setValueExpression("effect3D", effect3D);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setxAxisLabel(ValueExpression xAxisLabel) {
        setValueExpression("xAxisLabel", xAxisLabel);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setyAxisLabel(ValueExpression yAxisLabel) {
        setValueExpression("yAxisLabel", yAxisLabel);
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
    public void setBackgroundImage(ValueExpression backgroundImage) {
        setValueExpression("backgroundImage", backgroundImage);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.PositionType"
     */
    public void setBackgroundImagePosition(ValueExpression backgroundImagePosition) {
        setValueExpression("backgroundImagePosition", backgroundImagePosition);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setBackgroundImageAlpha(ValueExpression backgroundImageAlpha) {
        setValueExpression("backgroundImageAlpha", backgroundImageAlpha);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setPlotColor(ValueExpression plotColor) {
        setValueExpression("plotColor", plotColor);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setPlotImage(ValueExpression plotImage) {
        setValueExpression("plotImage", plotImage);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.PositionType"
     */
    public void setPlotImagePosition(ValueExpression plotImagePosition) {
        setValueExpression("plotImagePosition", plotImagePosition);
    }
    
    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setPlotImageAlpha(ValueExpression plotImageAlpha) {
        setValueExpression("plotImageAlpha", plotImageAlpha);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setBackgroundAlpha(ValueExpression backgroundAlpha) {
        setValueExpression("backgroundAlpha", backgroundAlpha);
    }

    /**
     * @jsp.attribute type="java.lang.Float"
     */
    public void setForegroundAlpha(ValueExpression foregroundAlpha) {
        setValueExpression("foregroundAlpha", foregroundAlpha);
    }

    /**
     * @jsp.attribute type="java.awt.Paint[]"
     */
    public void setColorPalette(ValueExpression colorPalette) {
        setValueExpression("colorPalette", colorPalette);
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
    public void setShowItemTips(ValueExpression showItemTips) {
        setValueExpression("showItemTips", showItemTips);
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

    public void setProperties(UIComponent component) {
        super.setProperties(component);

        if (this.init != null) {
            ((UIChart)component).setInit(this.init);
        }
    }

    public void release() {
        super.release();
        init = null;
    }
}
