/*
 * $Id: UIAverageLine.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class UIAverageLine extends UICurve
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.AverageLine";

    public UIAverageLine() {
        super();
    }

    private Double period;
    private Double skip;

    public double getPeriod() {
        if (this.period != null) {
            return this.period;
        }
        ValueExpression ve = getValueExpression("period");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return 10.0;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public double getSkip() {
        if (this.skip != null) {
            return this.skip;
        }
        ValueExpression ve = getValueExpression("skip");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Double)value;
            }
        }
        return 0.0;
    }

    public void setSkip(double skip) {
        this.skip = skip;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            period,
            skip
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        period = (Double)values[1];
        skip = (Double)values[2];
    }
}
