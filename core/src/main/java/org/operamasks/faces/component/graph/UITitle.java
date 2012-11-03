/*
 * $Id: UITitle.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import java.awt.Font;
import java.awt.Paint;

import org.operamasks.faces.render.graph.ChartUtils;

public class UITitle extends UIOutput
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.Title";

    private Font font;
    private Paint color;
    private Paint backgroundColor;
    private PositionType position;

    public UITitle() {
        super();
    }

    public Font getFont() {
        if (this.font != null) {
            return this.font;
        }
        ValueExpression ve = getValueExpression("font");
        if (ve != null) {
            return ChartUtils.convertFont(ve);
        }
        return null;
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

    public void setValueExpression(String name, ValueExpression expression) {
        if (name == null) {
            throw new NullPointerException();
        }

        if (expression != null && expression.isLiteralText()) {
            if ("font".equals(name)) {
                setFont(ChartUtils.convertFont(expression));
            } else if ("color".equals(name)) {
                setColor(ChartUtils.convertColor(expression));
            } else if ("backgroundColor".equals(name)) {
                setBackgroundColor(ChartUtils.convertColor(expression));
            } else {
                super.setValueExpression(name, expression);
            }
        } else {
            super.setValueExpression(name, expression);
        }
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

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            font,
            ChartUtils.serialPaintObject(color),
            ChartUtils.serialPaintObject(backgroundColor),
            position
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        font = (Font)values[i++];
        color = (Paint)values[i++];
        backgroundColor = (Paint)values[i++];
        position = (PositionType)values[i++];
    }
}
