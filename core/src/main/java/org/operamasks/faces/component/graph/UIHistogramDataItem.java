/*
 * $Id: UIHistogramDataItem.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

public class UIHistogramDataItem extends UIDataItem
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.HistogramDataItem";

    public UIHistogramDataItem() {
        super();
    }

    private Integer bins;
    private Double minimumValue;
    private Double maximumValue;

    public int getBins() {
        if (this.bins != null) {
            return this.bins;
        }
        ValueExpression ve = getValueExpression("bins");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return 5;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public Double getMinimumValue() {
        if (this.minimumValue != null) {
            return this.minimumValue;
        }
        ValueExpression ve = getValueExpression("minimumValue");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Double getMaximumValue() {
        if (this.maximumValue != null) {
            return this.maximumValue;
        }
        ValueExpression ve = getValueExpression("maximumValue");
        if (ve != null) {
            return (Double)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            bins,
            minimumValue,
            maximumValue
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        bins = (Integer)values[1];
        minimumValue = (Double)values[2];
        maximumValue = (Double)values[3];
    }
}
