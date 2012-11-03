/*
 * $Id: UITimeSeries.java,v 1.4 2007/07/02 07:37:54 jacky Exp $
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

public class UITimeSeries extends UIDataSeries
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.TimeSeries";

    public UITimeSeries() {
        setRendererType(null);
    }

    @Override
    public UITimeDataItem[] getItems() {
        return getItems(UITimeDataItem.class);
    }

    private TimePeriodType timePeriod;

    public TimePeriodType getTimePeriod() {
        if (this.timePeriod != null) {
            return this.timePeriod;
        }
        ValueExpression ve = getValueExpression("timePeriod");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (TimePeriodType)value;
            }
        }
        return TimePeriodType.Day;
    }

    public void setTimePeriod(TimePeriodType timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            timePeriod
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        timePeriod = (TimePeriodType)values[1];
    }
}
