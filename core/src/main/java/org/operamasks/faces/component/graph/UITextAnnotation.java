/*
 * $Id: UITextAnnotation.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

public class UITextAnnotation extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.TextAnnotatoin";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.TextAnnotation";

    public UITextAnnotation() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Object xValue;
    private Object yValue;
    private String text;
    private Font font;
    private Paint color;
    private PositionType anchor;
    private Double rotationAngle;
    private Boolean drawArrow;
    private Double arrowAngle;
    private Double arrowLength;
    private Paint arrowColor;

    public Object getxValue() {
        if (this.xValue != null) {
            return this.xValue;
        }
        ValueExpression ve = getValueExpression("xValue");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setxValue(Object xValue) {
        this.xValue = xValue;
    }

    public Object getyValue() {
        if (this.yValue != null) {
            return this.yValue;
        }
        ValueExpression ve = getValueExpression("yValue");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setyValue(Object yValue) {
        this.yValue = yValue;
    }

    public String getText() {
        if (this.text != null) {
            return this.text;
        }
        ValueExpression ve = getValueExpression("text");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        if (this.font != null) {
            return this.font;
        }
        ValueExpression ve = getValueExpression("font");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        } else {
            return null;
        }
    }

    public void setFont(Font font) {
        this.font = font;
    }

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

    public PositionType getAnchor() {
        if (this.anchor != null) {
            return this.anchor;
        }
        ValueExpression ve = getValueExpression("anchor");
        if (ve != null) {
            return (PositionType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAnchor(PositionType anchor) {
        this.anchor = anchor;
    }

    public Double getRotationAngle() {
        if (this.rotationAngle != null) {
            return this.rotationAngle;
        }
        ValueExpression ve = getValueExpression("rotationAngle");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setRotationAngle(Double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public boolean isDrawArrow() {
        if (this.drawArrow != null) {
            return this.drawArrow;
        }
        ValueExpression ve = getValueExpression("drawArrow");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setDrawArrow(boolean drawArrow) {
        this.drawArrow = drawArrow;
    }

    public Double getArrowAngle() {
        if (this.arrowAngle != null) {
            return this.arrowAngle;
        }
        ValueExpression ve = getValueExpression("arrowAngle");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setArrowAngle(Double arrowAngle) {
        this.arrowAngle = arrowAngle;
    }

    public Double getArrowLength() {
        if (this.arrowLength != null) {
            return this.arrowLength;
        }
        ValueExpression ve = getValueExpression("arrowLength");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return null;
    }

    public void setArrowLength(Double arrowLength) {
        this.arrowLength = arrowLength;
    }

    public Paint getArrowColor() {
        if (this.arrowColor != null) {
            return this.arrowColor;
        }
        ValueExpression ve = getValueExpression("arrowColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setArrowColor(Paint arrowColor) {
        this.arrowColor = arrowColor;
    }

    public void setValueExpression(String name, ValueExpression ve) {
        if (ve != null && ve.isLiteralText()) {
            if ("font".equals(name)) {
                setFont(ChartUtils.convertFont(ve));
            } else if ("color".equals(name)) {
                setColor(ChartUtils.convertColor(ve));
            } else if ("arrowColor".equals(name)) {
                setArrowColor(ChartUtils.convertColor(ve));
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
            xValue,
            yValue,
            text,
            font,
            ChartUtils.serialPaintObject(color),
            anchor,
            rotationAngle,
            drawArrow,
            arrowAngle,
            arrowLength,
            ChartUtils.serialPaintObject(arrowColor)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        xValue = values[i++];
        yValue = values[i++];
        text = (String)values[i++];
        font = (Font)values[i++];
        color = (Paint)values[i++];
        anchor = (PositionType)values[i++];
        rotationAngle = (Double)values[i++];
        drawArrow = (Boolean)values[i++];
        arrowAngle = (Double)values[i++];
        arrowLength = (Double)values[i++];
        arrowColor = (Paint)values[i++];
    }
}
