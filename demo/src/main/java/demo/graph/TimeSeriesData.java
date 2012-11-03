/*
 * $Id: TimeSeriesData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

package demo.graph;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;

import org.operamasks.faces.component.graph.TimeDataItem;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class TimeSeriesData extends ArrayList<TimeDataItem>
{
    public TimeSeriesData() {
        randomize();
    }

    public void randomize() {
        clear();

        GregorianCalendar cal = new GregorianCalendar(2007, 0, 1);
        double value = 100.0;
        for (int i = 0; i < 365; i++) {
            value = (value + Math.random()*10) - 5;
            add(new TimeDataItem(cal.getTime(), value));
            cal.add(Calendar.DATE, 1);
        }
    }
}
