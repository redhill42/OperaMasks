/*
 * $Id: UIDataItem.java,v 1.4 2007/07/02 07:37:55 jacky Exp $
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

package org.operamasks.faces.component.graph;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import java.awt.Paint;
import java.awt.Font;

import org.operamasks.faces.render.graph.ChartUtils;

public class UIDataItem extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.DataItem";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.DataItem";

    public UIDataItem() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Object value;

    public Object getValue() {
        if (this.value != null) {
            return this.value;
        }
        ValueExpression ve = getValueExpression("value");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setValue(Object value) {
        this.value = value;
    }

    private String legend;

    public String getLegend() {
        if (this.legend != null) {
            return this.legend;
        }
        ValueExpression ve = getValueExpression("legend");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    private Boolean showLegend;

    public boolean isShowLegend() {
        if (this.showLegend != null) {
            return this.showLegend;
        }
        ValueExpression ve = getValueExpression("showLegend");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return true;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    private Paint color;

    public Paint getColor() {
        if (this.color != null) {
            return this.color;
        }
        ValueExpression ve = getValueExpression("color");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    private Boolean drawOutline;

    public Boolean getDrawOutline() {
        if (this.drawOutline != null) {
            return this.drawOutline;
        }
        ValueExpression ve = getValueExpression("drawOutline");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawOutline(Boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    private Paint outlineColor;

    public Paint getOutlineColor() {
        if (this.outlineColor != null) {
            return this.outlineColor;
        }
        ValueExpression ve = getValueExpression("outlineColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setOutlineColor(Paint outlineColor) {
        this.outlineColor = outlineColor;
    }

    private Boolean drawItemLabel;

    public Boolean getDrawItemLabel() {
        if (this.drawItemLabel != null) {
            return this.drawItemLabel;
        }
        ValueExpression ve = getValueExpression("drawItemLabel");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawItemLabel(Boolean drawItemLabel) {
        this.drawItemLabel = drawItemLabel;
    }

    private Paint itemLabelColor;

    public Paint getItemLabelColor() {
        if (this.itemLabelColor != null) {
            return this.itemLabelColor;
        }
        ValueExpression ve = getValueExpression("itemLabelColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setItemLabelColor(Paint itemLabelColor) {
        this.itemLabelColor = itemLabelColor;
    }

    private Font itemLabelFont;

    public Font getItemLabelFont() {
        if (this.itemLabelFont != null) {
            return this.itemLabelFont;
        }
        ValueExpression ve = getValueExpression("itemLabelFont");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        } else {
            return null;
        }
    }

    public void setItemLabelFont(Font itemLabelFont) {
        this.itemLabelFont = itemLabelFont;
    }

    private Boolean drawLines;

    public Boolean getDrawLines() {
        if (this.drawLines != null) {
            return this.drawLines;
        }
        ValueExpression ve = getValueExpression("drawLines");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawLines(Boolean drawLines) {
        this.drawLines = drawLines;
    }

    private Float lineWidth;

    public Float getLineWidth() {
        if (this.lineWidth != null) {
            return this.lineWidth;
        }
        ValueExpression ve = getValueExpression("lineWidth");
        if (ve != null) {
            return (Float)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLineWidth(Float lineWidth) {
        this.lineWidth = lineWidth;
    }

    private LineStyleType lineStyle;

    public LineStyleType getLineStyle() {
        if (this.lineStyle != null) {
            return this.lineStyle;
        }
        ValueExpression ve = getValueExpression("lineStyle");
        if (ve != null) {
            return (LineStyleType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLineStyle(LineStyleType lineStyle) {
        this.lineStyle = lineStyle;
    }

    private Boolean drawMarkers;

    public Boolean getDrawMarkers() {
        if (this.drawMarkers != null) {
            return this.drawMarkers;
        }
        ValueExpression ve = getValueExpression("drawMarkers");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawMarkers(Boolean drawMarkers) {
        this.drawMarkers = drawMarkers;
    }

    private Boolean fillMarkers;

    public Boolean getFillMarkers() {
        if (this.fillMarkers != null) {
            return this.fillMarkers;
        }
        ValueExpression ve = getValueExpression("fillMarkers");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFillMarkers(Boolean fillMarkers) {
        this.fillMarkers = fillMarkers;
    }

    private Paint markerFillColor;

    public Paint getMarkerFillColor() {
        if (this.markerFillColor != null) {
            return markerFillColor;
        }
        ValueExpression ve = getValueExpression("markerFillColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setMarkerFillColor(Paint markerFillColor) {
        this.markerFillColor = markerFillColor;
    }

    @Override
    public void setValueExpression(String name, ValueExpression ve) {
        if (ve != null && ve.isLiteralText()) {
            if ("color".equals(name)) {
                setColor(ChartUtils.convertColor(ve));
            } else if ("outlineColor".equals(name)) {
                setOutlineColor(ChartUtils.convertColor(ve));
            } else if ("itemLabelColor".equals(name)) {
                setItemLabelColor(ChartUtils.convertColor(ve));
            } else if ("itemLabelFont".equals(name)) {
                setItemLabelFont(ChartUtils.convertFont(ve));
            } else if ("markerFillColor".equals(name)) {
                setMarkerFillColor(ChartUtils.convertColor(ve));
            } else {
                super.setValueExpression(name, ve);
            }
        } else {
            super.setValueExpression(name, ve);
        }
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            value,
            legend,
            showLegend,
            ChartUtils.serialPaintObject(color),
            drawOutline,
            ChartUtils.serialPaintObject(outlineColor),
            drawItemLabel,
            ChartUtils.serialPaintObject(itemLabelColor),
            itemLabelFont,
            drawLines,
            lineWidth,
            lineStyle,
            drawMarkers,
            fillMarkers,
            ChartUtils.serialPaintObject(markerFillColor)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        value = values[i++];
        legend = (String)values[i++];
        showLegend = (Boolean)values[i++];
        color = (Paint)values[i++];
        drawOutline = (Boolean)values[i++];
        outlineColor = (Paint)values[i++];
        drawItemLabel = (Boolean)values[i++];
        itemLabelColor = (Paint)values[i++];
        itemLabelFont = (Font)values[i++];
        drawLines = (Boolean)values[i++];
        lineWidth = (Float)values[i++];
        lineStyle = (LineStyleType)values[i++];
        drawMarkers = (Boolean)values[i++];
        fillMarkers = (Boolean)values[i++];
        markerFillColor = (Paint)values[i++];
    }
}
