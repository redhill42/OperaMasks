/*
 * $Id: UIRegressionLine.java,v 1.3 2007/07/02 07:37:54 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

public class UIRegressionLine extends UICurve
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.RegressionLine";

    public UIRegressionLine() {
        super();
    }

    private RegressionType type;
    private int samples = -1;

    public RegressionType getType() {
        if (this.type != null) {
            return this.type;
        }
        ValueExpression ve = getValueExpression("type");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (RegressionType)value;
            }
        }
        return RegressionType.Linear;
    }

    public void setType(RegressionType type) {
        this.type = type;
    }

    public int getSamples() {
        if (this.samples > 1) {
            return this.samples;
        }
        ValueExpression ve = getValueExpression("samples");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null && ((Integer)value) > 1) {
                return (Integer)value;
            }
        }
        return 100;
    }

    public void setSamples(int samples) {
        if (samples < 2) {
            throw new IllegalArgumentException("Samples must > 1");
        }
        this.samples = samples;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            type,
            samples
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        type = (RegressionType)values[1];
        samples = (Integer)values[2];
    }
}
