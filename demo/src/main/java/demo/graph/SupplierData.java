/*
 * $Id: SupplierData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import javax.faces.model.ArrayDataModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.component.graph.TimeDataItem;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class SupplierData extends ArrayDataModel
{
    private static List<TimeDataItem> createSupplier1Bids() {
        List<TimeDataItem> bids = new ArrayList<TimeDataItem>();
        GregorianCalendar cal = new GregorianCalendar(2007, 4, 22, 1, 0, 0);

        cal.set(Calendar.MINUTE, 13);
        bids.add(new TimeDataItem(cal.getTime(), 200));
        cal.set(Calendar.MINUTE, 14);
        bids.add(new TimeDataItem(cal.getTime(), 195));
        cal.set(Calendar.MINUTE, 45);
        bids.add(new TimeDataItem(cal.getTime(), 190));
        cal.set(Calendar.MINUTE, 46);
        bids.add(new TimeDataItem(cal.getTime(), 188));
        cal.set(Calendar.MINUTE, 47);
        bids.add(new TimeDataItem(cal.getTime(), 185));
        cal.set(Calendar.MINUTE, 52);
        bids.add(new TimeDataItem(cal.getTime(), 180));

        return bids;
    }

    private static List<TimeDataItem> createSupplier2Bids() {
        List<TimeDataItem> bids = new ArrayList<TimeDataItem>();
        GregorianCalendar cal = new GregorianCalendar(2007, 4, 22, 1, 0, 0);

        cal.set(Calendar.MINUTE, 25);
        bids.add(new TimeDataItem(cal.getTime(), 185));
        cal.add(Calendar.HOUR, 1);
        cal.set(Calendar.MINUTE, 0);
        bids.add(new TimeDataItem(cal.getTime(), 175));
        cal.set(Calendar.MINUTE, 5);
        bids.add(new TimeDataItem(cal.getTime(), 170));
        cal.set(Calendar.MINUTE, 6);
        bids.add(new TimeDataItem(cal.getTime(), 168));
        cal.set(Calendar.MINUTE, 9);
        bids.add(new TimeDataItem(cal.getTime(), 165));
        cal.set(Calendar.MINUTE, 10);
        bids.add(new TimeDataItem(cal.getTime(), 163));

        return bids;
    }

    private static TimeDataItem[][] createData() {
        List<TimeDataItem> series1 = createSupplier1Bids();
        List<TimeDataItem> series2 = createSupplier2Bids();
        int rows = series1.size();

        TimeDataItem[][] data = new TimeDataItem[rows][2];
        for (int i = 0; i < rows; i++) {
            data[i][0] = series1.get(i);
            data[i][1] = series2.get(i);
        }
        return data;
    }

    public SupplierData() {
        super(createData());
    }
}
