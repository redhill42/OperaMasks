/*
 * $Id: UILegend.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

public class UILegend extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.Legend";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.Legend";

    private PositionType position;
    private Double   leftMargin;
    private Double   rightMargin;
    private Double   topMargin;
    private Double   bottomMargin;
    private Paint    backgroundColor;
    private Paint    borderColor;
    private Float    borderWidth;
    private Font     itemFont;
    private Paint    itemColor;

    public UILegend() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public PositionType getPosition() {
        if (this.position != null) {
            return this.position;
        }
        ValueExpression ve = getValueExpression("position");
        if (ve != null) {
            return (PositionType)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPosition(PositionType position) {
        this.position = position;
    }

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

    public void setBackgroundColor(Paint color) {
        this.backgroundColor = color;
    }

    public Paint getBorderColor() {
        if (this.borderColor != null) {
            return this.borderColor;
        }
        ValueExpression ve = getValueExpression("borderColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setBorderColor(Paint borderColor) {
        this.borderColor = borderColor;
    }

    public Float getBorderWidth() {
        if (this.borderWidth != null) {
            return this.borderWidth;
        }
        ValueExpression ve = getValueExpression("borderWidth");
        if (ve != null) {
            return (Float)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBorderWidth(Float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Font getItemFont() {
        if (this.itemFont != null) {
            return this.itemFont;
        }
        ValueExpression ve = getValueExpression("itemFont");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        }
        return null;
    }

    public void setItemFont(Font itemFont) {
        this.itemFont = itemFont;
    }

    public Paint getItemColor() {
        if (this.itemColor != null) {
            return this.itemColor;
        }
        ValueExpression ve = getValueExpression("itemColor");
        if (ve != null) {
            return ChartUtils.convertColor(ve);
        } else {
            return null;
        }
    }

    public void setItemColor(Paint itemColor) {
        this.itemColor = itemColor;
    }

    public void setValueExpression(String name, ValueExpression expression) {
        if (name == null) {
            throw new NullPointerException();
        }

        if (expression != null && expression.isLiteralText()) {
            if ("backgroundColor".equals(name)) {
                setBackgroundColor(ChartUtils.convertColor(expression));
            } else if ("borderColor".equals(name)) {
                setBorderColor(ChartUtils.convertColor(expression));
            } else if ("itemFont".equals(name)) {
                setItemFont(ChartUtils.convertFont(expression));
            } else if ("itemColor".equals(name)) {
                setItemColor(ChartUtils.convertColor(expression));
            } else {
                super.setValueExpression(name, expression);
            }
        } else {
            super.setValueExpression(name, expression);
        }
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            position,
            leftMargin,
            rightMargin,
            topMargin,
            bottomMargin,
            ChartUtils.serialPaintObject(backgroundColor),
            ChartUtils.serialPaintObject(borderColor),
            borderWidth,
            itemFont,
            ChartUtils.serialPaintObject(itemColor),
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        position = (PositionType)values[i++];
        leftMargin = (Double)values[i++];
        rightMargin = (Double)values[i++];
        topMargin = (Double)values[i++];
        bottomMargin = (Double)values[i++];
        backgroundColor = (Paint)values[i++];
        borderColor = (Paint)values[i++];
        borderWidth = (Float)values[i++];
        itemFont = (Font)values[i++];
        itemColor = (Paint)values[i++];
    }
}
