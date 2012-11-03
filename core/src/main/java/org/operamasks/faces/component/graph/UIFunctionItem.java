/*
 * $Id: UIFunctionItem.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class UIFunctionItem extends UIDataItem
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.FunctionItem";

    public UIFunctionItem() {
        super();
    }

    private Double start;
    private Double end;
    private Double step;
    private MethodExpression expression;

    public double getStart() {
        if (this.start != null) {
            return this.start;
        }
        ValueExpression ve = getValueExpression("start");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return 0.0;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        if (this.end != null) {
            return this.end;
        }
        ValueExpression ve = getValueExpression("end");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return 1.0;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getStep() {
        if (this.step != null) {
            return this.step;
        }
        ValueExpression ve = getValueExpression("step");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return 0.1;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public MethodExpression getExpression() {
        return expression;
    }

    public void setExpression(MethodExpression expression) {
        this.expression = expression;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            start,
            end,
            step,
            saveAttachedState(context, expression)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        start = (Double)values[1];
        end = (Double)values[2];
        step = (Double)values[3];
        expression = (MethodExpression)restoreAttachedState(context, values[4]);
    }
}
