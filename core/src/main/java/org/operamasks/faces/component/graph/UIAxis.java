/*
 * $Id: UIAxis.java,v 1.5 2007/07/02 07:37:54 jacky Exp $
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
import java.awt.Font;
import java.awt.Paint;

import org.operamasks.faces.render.graph.ChartUtils;

public class UIAxis extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.Axis";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.Axis";

    public UIAxis() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Boolean     visible;
    private String      mapTo;
    private Boolean     logarithmic;
    private String      label;
    private Font        labelFont;
    private Paint       labelColor;
    private Double      labelAngle;
    private Boolean     drawLine;
    private Paint       lineColor;
    private Boolean     drawGridLine;
    private Paint       gridLineColor;
    private Boolean     drawBaseLine;
    private Paint       baseLineColor;
    private Boolean     drawTickLabels;
    private Font        tickLabelFont;
    private Paint       tickLabelColor;
    private String      tickLabelFormat;
    private String      itemTipFormat;
    private Boolean     drawTickMarks;
    private Float       tickMarkInsideLength;
    private Float       tickMarkOutsideLength;
    private Paint       tickMarkColor;
    private Boolean     inverted;
    private Object      lowerBound;
    private Object      upperBound;
    private Double      lowerMargin;
    private Double      upperMargin;
    private Double      tickStep;
    private TimePeriodType tickUnit;

    public boolean isVisible() {
        if (this.visible != null) {
            return this.visible;
        }
        ValueExpression ve = getValueExpression("visible");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return true;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns a UIChart component id that this axis will map itself
     * to another chart axis. This is only used by a Composite Chart.
     */
    public String getMapTo() {
        if (this.mapTo != null) {
            return this.mapTo;
        }
        ValueExpression ve = getValueExpression("mapTo");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMapTo(String mapTo) {
        this.mapTo = mapTo;
    }

    public boolean isLogarithmic() {
        if (this.logarithmic != null) {
            return this.logarithmic;
        }
        ValueExpression ve = getValueExpression("logarithmic");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setLogarithmic(boolean logarithmic) {
        this.logarithmic = logarithmic;
    }

    public String getLabel() {
        if (this.label != null) {
            return this.label;
        }
        ValueExpression ve = getValueExpression("label");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Font getLabelFont() {
        if (this.labelFont != null) {
            return this.labelFont;
        }
        ValueExpression ve = getValueExpression("labelFont");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        } else {
            return null;
        }
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }

    public Paint getLabelColor() {
        if (this.labelColor != null) {
            return this.labelColor;
        }
        ValueExpression ve = getValueExpression("labelColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setLabelColor(Paint labelColor) {
        this.labelColor = labelColor;
    }

    public Double getLabelAngle() {
        if (this.labelAngle != null) {
            return this.labelAngle;
        }
        ValueExpression ve = getValueExpression("labelAngle");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabelAngle(Double labelAngle) {
        this.labelAngle = labelAngle;
    }

    public boolean isDrawLine() {
        if (this.drawLine != null) {
            return this.drawLine;
        }
        ValueExpression ve = getValueExpression("drawLine");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return true;
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    public Paint getLineColor() {
        if (this.lineColor != null) {
            return this.lineColor;
        }
        ValueExpression ve = getValueExpression("lineColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setLineColor(Paint lineColor) {
        this.lineColor = lineColor;
    }

    public Boolean getDrawGridLine() {
        if (this.drawGridLine != null) {
            return this.drawGridLine;
        }
        ValueExpression ve = getValueExpression("drawGridLine");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawGridLine(Boolean drawGridLine) {
        this.drawGridLine = drawGridLine;
    }

    public Paint getGridLineColor() {
        if (this.gridLineColor != null) {
            return this.gridLineColor;
        }
        ValueExpression ve = getValueExpression("gridLineColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setGridLineColor(Paint gridLineColor) {
        this.gridLineColor = gridLineColor;
    }

    public Boolean getDrawBaseLine() {
        if (this.drawBaseLine != null) {
            return this.drawBaseLine;
        }
        ValueExpression ve = getValueExpression("drawBaseLine");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDrawBaseLine(Boolean drawBaseLine) {
        this.drawBaseLine = drawBaseLine;
    }

    public Paint getBaseLineColor() {
        if (this.baseLineColor != null) {
            return this.baseLineColor;
        }
        ValueExpression ve = getValueExpression("baseLineColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setBaseLineColor(Paint baseLineColor) {
        this.baseLineColor = baseLineColor;
    }

    public boolean isDrawTickLabels() {
        if (this.drawTickLabels != null) {
            return this.drawTickLabels;
        }
        ValueExpression ve = getValueExpression("drawTickLabels");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return true;
    }

    public void setDrawTickLabels(boolean drawTickLabels) {
        this.drawTickLabels = drawTickLabels;
    }

    public Font getTickLabelFont() {
        if (this.tickLabelFont != null) {
            return this.tickLabelFont;
        }
        ValueExpression ve = getValueExpression("tickLabelFont");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        } else {
            return null;
        }
    }

    public void setTickLabelFont(Font tickLabelFont) {
        this.tickLabelFont = tickLabelFont;
    }

    public Paint getTickLabelColor() {
        if (this.tickLabelColor != null) {
            return this.tickLabelColor;
        }
        ValueExpression ve = getValueExpression("tickLabelColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setTickLabelColor(Paint tickLabelColor) {
        this.tickLabelColor = tickLabelColor;
    }

    public String getTickLabelFormat() {
        if (this.tickLabelFormat != null) {
            return this.tickLabelFormat;
        }
        ValueExpression ve = getValueExpression("tickLabelFormat");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTickLabelFormat(String tickLabelFormat) {
        this.tickLabelFormat = tickLabelFormat;
    }

    public String getItemTipFormat() {
        if (this.itemTipFormat != null) {
            return this.itemTipFormat;
        }
        ValueExpression ve = getValueExpression("itemTipFormat");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setItemTipFormat(String itemTipFormat) {
        this.itemTipFormat = itemTipFormat;
    }

    public boolean isDrawTickMarks() {
        if (this.drawTickMarks != null) {
            return this.drawTickMarks;
        }
        ValueExpression ve = getValueExpression("drawTickMarks");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return true;
    }

    public void setDrawTickMarks(boolean drawTickMarks) {
        this.drawTickMarks = drawTickMarks;
    }

    public float getTickMarkInsideLength() {
        if (this.tickMarkInsideLength != null) {
            return this.tickMarkInsideLength;
        }
        ValueExpression ve = getValueExpression("tickMarkInsideLength");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Float)value;
            }
        }
        return 0f;
    }

    public void setTickMarkInsideLength(float tickMarkInsideLength) {
        this.tickMarkInsideLength = tickMarkInsideLength;
    }

    public float getTickMarkOutsideLength() {
        if (this.tickMarkOutsideLength != null) {
            return this.tickMarkOutsideLength;
        }
        ValueExpression ve = getValueExpression("tickMarkOutsideLength");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Float)value;
            }
        }
        return 2.0f;
    }

    public void setTickMarkOutsideLength(float tickMarkOutsideLength) {
        this.tickMarkOutsideLength = tickMarkOutsideLength;
    }

    public Paint getTickMarkColor() {
        if (this.tickMarkColor != null) {
            return this.tickMarkColor;
        }
        ValueExpression ve = getValueExpression("tickMarkColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setTickMarkColor(Paint tickMarkColor) {
        this.tickMarkColor = tickMarkColor;
    }

    public boolean isInverted() {
        if (this.inverted != null) {
            return this.inverted;
        }
        ValueExpression ve = getValueExpression("inverted");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public Object getLowerBound() {
        if (this.lowerBound != null) {
            return this.lowerBound;
        }
        ValueExpression ve = getValueExpression("lowerBound");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLowerBound(Object lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Object getUpperBound() {
        if (this.upperBound != null) {
            return this.upperBound;
        }
        ValueExpression ve = getValueExpression("upperBound");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setUpperBound(Object upperBound) {
        this.upperBound = upperBound;
    }

    public Double getLowerMargin() {
        if (this.lowerMargin != null) {
            return this.lowerMargin;
        }
        ValueExpression ve = getValueExpression("lowerMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLowerMargin(Double lowerMargin) {
        this.lowerMargin = lowerMargin;
    }

    public Double getUpperMargin() {
        if (this.upperMargin != null) {
            return this.upperMargin;
        }
        ValueExpression ve = getValueExpression("upperMargin");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setUpperMargin(Double upperMargin) {
        this.upperMargin = upperMargin;
    }

    public Double getTickStep() {
        if (this.tickStep != null) {
            return this.tickStep;
        }
        ValueExpression ve = getValueExpression("tickStep");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTickStep(Double tickStep) {
        this.tickStep = tickStep;
    }

    public TimePeriodType getTickUnit() {
        if (this.tickUnit != null) {
            return this.tickUnit;
        }
        ValueExpression ve = getValueExpression("tickUnit");
        if (ve != null) {
            return (TimePeriodType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTickUnit(TimePeriodType tickUnit) {
        this.tickUnit = tickUnit;
    }

    public void setValueExpression(String name, ValueExpression ve) {
        if (ve != null && ve.isLiteralText()) {
            if ("labelFont".equals(name)) {
                setLabelFont(ChartUtils.convertFont(ve));
            } else if ("labelColor".equals(name)) {
                setLabelColor(ChartUtils.convertColor(ve));
            } else if ("lineColor".equals(name)) {
                setLineColor(ChartUtils.convertColor(ve));
            } else if ("gridLineColor".equals(name)) {
                setGridLineColor(ChartUtils.convertColor(ve));
            } else if ("baseLineColor".equals(name)) {
                setBaseLineColor(ChartUtils.convertColor(ve));
            } else if ("tickLabelFont".equals(name)) {
                setTickLabelFont(ChartUtils.convertFont(ve));
            } else if ("tickLabelColor".equals(name)) {
                setTickLabelColor(ChartUtils.convertColor(ve));
            } else if ("tickMarkColor".equals(name)) {
                setTickMarkColor(ChartUtils.convertColor(ve));
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
            visible,
            mapTo,
            logarithmic,
            label,
            labelFont,
            ChartUtils.serialPaintObject(labelColor),
            labelAngle,
            drawLine,
            ChartUtils.serialPaintObject(lineColor),
            drawGridLine,
            ChartUtils.serialPaintObject(gridLineColor),
            drawBaseLine,
            ChartUtils.serialPaintObject(baseLineColor),
            drawTickLabels,
            tickLabelFont,
            ChartUtils.serialPaintObject(tickLabelColor),
            tickLabelFormat,
            itemTipFormat,
            drawTickMarks,
            tickMarkInsideLength,
            tickMarkOutsideLength,
            ChartUtils.serialPaintObject(tickMarkColor),
            inverted,
            lowerBound,
            upperBound,
            lowerMargin,
            upperMargin,
            tickStep,
            tickUnit,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        visible = (Boolean)values[i++];
        mapTo = (String)values[i++];
        logarithmic = (Boolean)values[i++];
        label = (String)values[i++];
        labelFont = (Font)values[i++];
        labelColor = (Paint)values[i++];
        labelAngle = (Double)values[i++];
        drawLine = (Boolean)values[i++];
        lineColor = (Paint)values[i++];
        drawGridLine = (Boolean)values[i++];
        gridLineColor = (Paint)values[i++];
        drawBaseLine = (Boolean)values[i++];
        baseLineColor = (Paint)values[i++];
        drawTickLabels = (Boolean)values[i++];
        tickLabelFont = (Font)values[i++];
        tickLabelColor = (Paint)values[i++];
        tickLabelFormat = (String)values[i++];
        itemTipFormat = (String)values[i++];
        drawTickMarks = (Boolean)values[i++];
        tickMarkInsideLength = (Float)values[i++];
        tickMarkOutsideLength = (Float)values[i++];
        tickMarkColor = (Paint)values[i++];
        inverted = (Boolean)values[i++];
        lowerBound = values[i++];
        upperBound = values[i++];
        lowerMargin = (Double)values[i++];
        upperMargin = (Double)values[i++];
        tickStep = (Double)values[i++];
        tickUnit = (TimePeriodType)values[i++];
    }
}
