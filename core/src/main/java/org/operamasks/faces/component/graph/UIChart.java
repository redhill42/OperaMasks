/*
 * $Id: UIChart.java,v 1.8 2007/08/13 13:25:13 daniel Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.el.MethodExpression;

import java.awt.Paint;
import java.awt.Font;

import org.operamasks.faces.render.graph.ChartUtils;

public class UIChart extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.Chart";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.Chart";

    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 370;

    public UIChart() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public UIDataSeries getDataSeries() {
        UIComponent facet = getFacet("dataSeries");
        if (facet instanceof UIDataSeries) {
            return (UIDataSeries)facet;
        }
        for (UIComponent child : getChildren()) {
            if (child instanceof UIDataSeries) {
                return (UIDataSeries)child;
            }
        }
        return null;
    }

    public void setDataSeries(UIDataSeries series) {
        getFacets().put("dataSeries", series);
    }

    public UIAxis getxAxis() {
        return (UIAxis)getFacet("xAxis");
    }

    public void setxAxis(UIAxis xAxis) {
        getFacets().put("xAxis", xAxis);
    }

    public UIAxis getyAxis() {
        return (UIAxis)getFacet("yAxis");
    }

    public void setyAxis(UIAxis yAxis) {
        getFacets().put("yAxis", yAxis);
    }

    public UILegend getLegend() {
        UIComponent facet = getFacet("legend");
        if (facet instanceof UILegend) {
            return (UILegend)facet;
        }
        for (UIComponent child : getChildren()) {
            if (child instanceof UILegend) {
                return (UILegend)child;
            }
        }
        return null;
    }

    public void setLegend(UILegend legend) {
        getFacets().put("legend", legend);
    }

    private ChartType chartType;

    public ChartType getChartType() {
        if (this.chartType != null) {
            return this.chartType;
        }
        ValueExpression ve = getValueExpression("chartType");
        if (ve != null) {
            return (ChartType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getRendererType() {
        String rendererType = super.getRendererType();
        if (rendererType == null) {
            ChartType chartType = getChartType();
            if (chartType != null) {
                rendererType = chartType.getRendererType();
            }
        }
        return rendererType;
    }
    
    private MethodExpression init;

    public MethodExpression getInit() {
        return init;
    }

    public void setInit(MethodExpression init) {
        this.init = init;
    }

    private int width;
    private boolean width_set;

    public int getWidth() {
        if (this.width_set) {
            return this.width;
        }
        ValueExpression ve = getValueExpression("width");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return DEFAULT_WIDTH;
    }

    public void setWidth(int width) {
        this.width = width;
        this.width_set = true;
    }

    private int height;
    private boolean height_set;

    public int getHeight() {
        if (this.height_set) {
            return this.height;
        }
        ValueExpression ve = getValueExpression("height");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return DEFAULT_HEIGHT;
    }

    public void setHeight(int height) {
        this.height = height;
        this.height_set = true;
    }

    private Double leftMargin;
    private Double rightMargin;
    private Double topMargin;
    private Double bottomMargin;

    public Double getLeftMargin() {
        if (this.leftMargin != null) {
            return this.leftMargin;
        }
        ValueExpression ve = getValueExpression("leftMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setLeftMargin(Double leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Double getRightMargin() {
        if (this.rightMargin != null) {
            return this.rightMargin;
        }
        ValueExpression ve = getValueExpression("rightMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setRightMargin(Double rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Double getTopMargin() {
        if (this.topMargin != null) {
            return this.topMargin;
        }
        ValueExpression ve = getValueExpression("topMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setTopMargin(Double topMargin) {
        this.topMargin = topMargin;
    }

    public Double getBottomMargin() {
        if (this.bottomMargin != null) {
            return this.bottomMargin;
        }
        ValueExpression ve = getValueExpression("bottomMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setBottomMargin(Double bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    private OrientationType orientation;

    public OrientationType getOrientation() {
        if (this.orientation != null) {
            return this.orientation;
        }
        ValueExpression ve = getValueExpression("orientation");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (OrientationType)value;
            }
        }
        return OrientationType.Vertical;
    }

    public void setOrientation(OrientationType orientation) {
        this.orientation = orientation;
    }

    private Boolean effect3D;

    public boolean isEffect3D() {
        if (this.effect3D != null) {
            return this.effect3D;
        }
        ValueExpression ve = getValueExpression("effect3D");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setEffect3D(boolean effect3D) {
        this.effect3D = effect3D;
    }

    private Boolean stacked;

    public boolean isStacked() {
        if (this.stacked != null) {
            return this.stacked;
        }
        ValueExpression ve = getValueExpression("stacked");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    private Boolean showLegend;

    public Boolean getShowLegend() {
        if (this.showLegend != null) {
            return this.showLegend;
        }
        ValueExpression ve = getValueExpression("showLegend");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        }
        return null;
    }

    public void setShowLegend(Boolean showLegend) {
        this.showLegend = showLegend;
    }

    private String title;

    public String getTitle() {
        if (this.title != null) {
            return this.title;
        }
        ValueExpression ve = getValueExpression("title");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String xAxisLabel;
    private String yAxisLabel;

    public String getxAxisLabel() {
        if (this.xAxisLabel != null) {
            return this.xAxisLabel;
        }
        ValueExpression ve = getValueExpression("xAxisLabel");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getyAxisLabel() {
        if (this.yAxisLabel != null) {
            return this.yAxisLabel;
        }
        ValueExpression ve = getValueExpression("yAxisLabel");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    private Paint backgroundColor;

    public Paint getBackgroundColor() {
        if (this.backgroundColor != null) {
            return this.backgroundColor;
        }
        ValueExpression ve = getValueExpression("backgroundColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setBackgroundColor(Paint backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private Object backgroundImage;

    public Object getBackgroundImage() {
        if (this.backgroundImage != null) {
            return this.backgroundImage;
        }
        ValueExpression ve = getValueExpression("backgroundImage");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBackgroundImage(Object backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    private PositionType backgroundImagePosition;

    public PositionType getBackgroundImagePosition() {
        if (this.backgroundImagePosition != null) {
            return this.backgroundImagePosition;
        }
        ValueExpression ve = getValueExpression("backgroundImagePosition");
        if (ve != null) {
            return (PositionType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBackgroundImagePosition(PositionType edge) {
        this.backgroundImagePosition = edge;
    }

    private Float backgroundImageAlpha;

    public float getBackgroundImageAlpha() {
        if (this.backgroundImageAlpha != null) {
            return this.backgroundImageAlpha;
        }
        ValueExpression ve = getValueExpression("backgroundImageAlpha");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Float)value;
            }
        }
        return 1.0F;
    }

    public void setBackgroundImageAlpha(float backgroundImageAlpha) {
        this.backgroundImageAlpha = backgroundImageAlpha;
    }

    private Paint plotColor;

    public Paint getPlotColor() {
        if (this.plotColor != null) {
            return this.plotColor;
        }
        ValueExpression ve = getValueExpression("plotColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setPlotColor(Paint plotColor) {
        this.plotColor = plotColor;
    }

    private Object plotImage;

    public Object getPlotImage() {
        if (this.plotImage != null) {
            return this.plotImage;
        }
        ValueExpression ve = getValueExpression("plotImage");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPlotImage(Object plotImage) {
        this.plotImage = plotImage;
    }

    private PositionType plotImagePosition;

    public PositionType getPlotImagePosition() {
        if (this.plotImagePosition != null) {
            return this.plotImagePosition;
        }
        ValueExpression ve = getValueExpression("plotImagePosition");
        if (ve != null) {
            return (PositionType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPlotImagePosition(PositionType plotImagePosition) {
        this.plotImagePosition = plotImagePosition;
    }

    private Float plotImageAlpha;

    public float getPlotImageAlpha() {
        if (this.plotImageAlpha != null) {
            return this.plotImageAlpha;
        }
        ValueExpression ve = getValueExpression("plotImageAlpha");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Float)value;
            }
        }
        return 1.0F;
    }

    public void setPlotImageAlpha(float plotImageAlpha) {
        this.plotImageAlpha = plotImageAlpha;
    }

    private Float backgroundAlpha;

    public Float getBackgroundAlpha() {
        if (this.backgroundAlpha != null) {
            return this.backgroundAlpha;
        }
        ValueExpression ve = getValueExpression("backgroundAlpha");
        if (ve != null) {
            return (Float)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBackgroundAlpha(Float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    private Float foregroundAlpha;

    public Float getForegroundAlpha() {
        if (this.foregroundAlpha != null) {
            return this.foregroundAlpha;
        }
        ValueExpression ve = getValueExpression("foregroundAlpha");
        if (ve != null) {
            return (Float)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setForegroundAlpha(Float foregroundAlpha) {
        this.foregroundAlpha = foregroundAlpha;
    }

    public Paint[] getColorPalette() {
        ValueExpression ve = getValueExpression("colorPalette");
        if (ve != null) {
            return (Paint[])ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
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

    public boolean isDrawItemLabel() {
        if (this.drawItemLabel != null) {
            return this.drawItemLabel;
        }
        ValueExpression ve = getValueExpression("drawItemLabel");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setDrawItemLabel(boolean drawItemLabel) {
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

    private Boolean showItemTips;

    public boolean isShowItemTips() {
        if (this.showItemTips != null) {
            return this.showItemTips;
        }
        ValueExpression ve = getValueExpression("showItemTips");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setShowItemTips(boolean showItemTips) {
        this.showItemTips = showItemTips;
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
            if ("backgroundColor".equals(name)) {
                setBackgroundColor(ChartUtils.convertColor(ve));
            } else if ("plotColor".equals(name)) {
                setPlotColor(ChartUtils.convertColor(ve));
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
            chartType,
            saveAttachedState(context, init),
            width,
            width_set,
            height,
            height_set,
            leftMargin,
            rightMargin,
            topMargin,
            bottomMargin,
            orientation,
            effect3D,
            stacked,
            showLegend,
            title,
            xAxisLabel,
            yAxisLabel,
            ChartUtils.serialPaintObject(backgroundColor),
            backgroundImage,
            backgroundImagePosition,
            backgroundImageAlpha,
            ChartUtils.serialPaintObject(plotColor),
            plotImage,
            plotImagePosition,
            plotImageAlpha,
            backgroundAlpha,
            foregroundAlpha,
            drawOutline,
            ChartUtils.serialPaintObject(outlineColor),
            drawItemLabel,
            ChartUtils.serialPaintObject(itemLabelColor),
            itemLabelFont,
            showItemTips,
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
        chartType = (ChartType)values[i++];
        init = (MethodExpression)restoreAttachedState(context, values[i++]);
        width = (Integer)values[i++];
        width_set = (Boolean)values[i++];
        height = (Integer)values[i++];
        height_set = (Boolean)values[i++];
        leftMargin = (Double)values[i++];
        rightMargin = (Double)values[i++];
        topMargin = (Double)values[i++];
        bottomMargin = (Double)values[i++];
        orientation = (OrientationType)values[i++];
        effect3D = (Boolean)values[i++];
        stacked = (Boolean)values[i++];
        showLegend = (Boolean)values[i++];
        title = (String)values[i++];
        xAxisLabel = (String)values[i++];
        yAxisLabel = (String)values[i++];
        backgroundColor = (Paint)values[i++];
        backgroundImage = values[i++];
        backgroundImagePosition = (PositionType)values[i++];
        backgroundImageAlpha = (Float)values[i++];
        plotColor = (Paint)values[i++];
        plotImage = values[i++];
        plotImagePosition = (PositionType)values[i++];
        plotImageAlpha = (Float)values[i++];
        backgroundAlpha = (Float)values[i++];
        foregroundAlpha = (Float)values[i++];
        drawOutline = (Boolean)values[i++];
        outlineColor = (Paint)values[i++];
        drawItemLabel = (Boolean)values[i++];
        itemLabelColor = (Paint)values[i++];
        itemLabelFont = (Font)values[i++];
        showItemTips = (Boolean)values[i++];
        drawLines = (Boolean)values[i++];
        lineWidth = (Float)values[i++];
        lineStyle = (LineStyleType)values[i++];
        drawMarkers = (Boolean)values[i++];
        fillMarkers = (Boolean)values[i++];
        markerFillColor = (Paint)values[i++];
    }
}
