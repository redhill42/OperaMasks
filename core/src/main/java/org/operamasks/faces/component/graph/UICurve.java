/*
 * $Id: UICurve.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

import org.operamasks.faces.render.graph.ChartUtils;

public abstract class UICurve extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.graph.Curve";

    public UICurve() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String legend;
    private Paint color;

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

    @Override
    public void setValueExpression(String name, ValueExpression ve) {
        if (ve != null && ve.isLiteralText() && "color".equals(name)) {
            setColor(ChartUtils.convertColor(ve));
        } else {
            super.setValueExpression(name, ve);
        }
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            legend,
            ChartUtils.serialPaintObject(color)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        legend = (String)values[1];
        color = (Paint)values[2];
    }
}
