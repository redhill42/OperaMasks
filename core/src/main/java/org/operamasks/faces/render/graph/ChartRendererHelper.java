/*
 * $Id: ChartRendererHelper.java,v 1.7 2007/07/02 07:37:44 jacky Exp $
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

package org.operamasks.faces.render.graph;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.el.MethodExpression;
import javax.el.ELContext;
import java.util.Date;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Year;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Hour;
import org.jfree.data.statistics.HistogramDataset;

import org.operamasks.faces.component.graph.UIChart;
import org.operamasks.faces.component.graph.UIDataSeries;
import org.operamasks.faces.component.graph.UIDataLabel;
import org.operamasks.faces.component.graph.UIDataItem;
import org.operamasks.faces.component.graph.UIXYDataItem;
import org.operamasks.faces.component.graph.UIXYDataSeries;
import org.operamasks.faces.component.graph.UITimeSeries;
import org.operamasks.faces.component.graph.UITimeDataItem;
import org.operamasks.faces.component.graph.TimePeriodType;
import org.operamasks.faces.component.graph.UIFunctionSeries;
import org.operamasks.faces.component.graph.UIFunctionItem;
import org.operamasks.faces.component.graph.UIHistogramDataSeries;
import org.operamasks.faces.component.graph.UIHistogramDataItem;
import static org.operamasks.el.eval.Coercion.coerceToDouble;
import static org.operamasks.resources.Resources.*;

final class ChartRendererHelper
{
    private ChartRendererHelper() {}

    public static class Key implements Comparable<Key> {
        private int id;
        private String label;

        public Key(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public String toString() {
            return label;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                return id == ((Key)obj).id;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return id;
        }

        public int compareTo(Key other) {
            return id - other.id;
        }
    }

    public static Dataset createDataset(UIChart component) {
        UIDataSeries data = component.getDataSeries();
        if (data == null) {
            throw new FacesException(_T(UI_CHART_NO_DATA_SERIES_ASSOCIATED));
        }

        data.resetDataModel(); // re-evaluate data model

        if (data instanceof UIXYDataSeries) {
            return createXYDataset((UIXYDataSeries)data);
        } else if (data instanceof UITimeSeries) {
            return createTimeSeriesDataset((UITimeSeries)data);
        } else if (data instanceof UIHistogramDataSeries) {
            return createHistogramDataset((UIHistogramDataSeries)data);
        } else if (data instanceof UIFunctionSeries) {
            return createFunctionSeries((UIFunctionSeries)data);
        } else {
            return createCategoryDataset(data);
        }
    }

    public static CategoryDataset createCategoryDataset(UIDataSeries data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        UIDataLabel label = data.getLabel();
        UIDataItem[] items = data.getItems();
        int numSeries = items.length;
        if (numSeries == 0) {
            return dataset; // return empty dataset
        }

        Key[] seriesKeys = new Key[numSeries];
        for (int i = 0; i < numSeries; i++) {
            seriesKeys[i] = new Key(i, items[i].getLegend());
        }

        int rowIndex = data.getFirst();
        int rows = data.getRows();
        
        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            String labelText = (label == null) ? "" : label.getText();
            Key rowKey = new Key(rowIndex, labelText);

            for (int i = 0; i < numSeries; i++) {
                double value = coerceToDouble(items[i].getValue());
                dataset.addValue(value, seriesKeys[i], rowKey);
            }
        }

        data.setRowIndex(-1);
        return dataset;
    }

    public static Key getCategoryKey(UIDataSeries data, Object dataValue) {
        UIDataLabel dataLabel = data.getLabel();
        if (dataLabel == null) {
            return null;
        }

        int rowIndex = data.getFirst();
        int rows = data.getRows();
        Key rowKey = null;

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            Object labelValue = dataLabel.getValue();
            if ((dataValue == null && labelValue == null) ||
                (dataValue != null && dataValue.equals(labelValue))) {
                rowKey = new Key(rowIndex, dataLabel.getText());
                break;
            }
        }
        
        data.setRowIndex(-1);
        return rowKey;
    }

    public static XYDataset createXYDataset(UIXYDataSeries data) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        UIXYDataItem[] items = data.getItems();
        int numSeries = items.length;
        if (numSeries == 0) {
            return dataset; // return empty dataset
        }

        XYSeries[] series = new XYSeries[numSeries];
        for (int i = 0; i < numSeries; i++) {
            series[i] = new XYSeries(new Key(i, items[i].getLegend()));
            dataset.addSeries(series[i]);
        }

        int rowIndex = data.getFirst();
        int rows = data.getRows();

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            for (int i = 0; i < numSeries; i++) {
                double x = coerceToDouble(items[i].getX());
                double y = coerceToDouble(items[i].getY());
                series[i].add(x, y);
            }
        }

        data.setRowIndex(-1);
        return dataset;
    }

    public static XYDataset createTimeSeriesDataset(UITimeSeries data) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        UITimeDataItem[] items = data.getItems();
        int numSeries = items.length;
        if (numSeries == 0) {
            return dataset; // return empty dataset
        }

        Class timePeriodClass = getTimePeriodClass(data.getTimePeriod());

        TimeSeries[] series = new TimeSeries[numSeries];
        for (int i = 0; i < numSeries; i++) {
            String name = items[i].getLegend();
            if (name == null)
                name = "";
            series[i] = new TimeSeries(name, timePeriodClass);
            dataset.addSeries(series[i]);
        }

        int rowIndex = data.getFirst();
        int rows = data.getRows();

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            for (int i = 0; i < numSeries; i++) {
                Date time = items[i].getTime();
                double value = coerceToDouble(items[i].getValue());
                RegularTimePeriod rtp = RegularTimePeriod.createInstance
                    (timePeriodClass, time, RegularTimePeriod.DEFAULT_TIME_ZONE);
                series[i].add(rtp, value);
            }
        }

        data.setRowIndex(-1);
        return dataset;
    }

    public static Class getTimePeriodClass(TimePeriodType period) {
        switch (period) {
        case Year:          return Year.class;
        case Quarter:       return Quarter.class;
        case Month:         return Month.class;
        case Week:          return Week.class;
        case Day:           return Day.class;
        case Hour:          return Hour.class;
        case Minute:        return Minute.class;
        case Second:        return Second.class;
        case Millisecond:   return Millisecond.class;
        default:            throw new AssertionError();
        }
    }

    public static long getTimePeriodValue(UIDataSeries data, Object value) {
        if (!(data instanceof UITimeSeries)) {
            throw new IllegalArgumentException();
        }

        Date time = ChartUtils.convertDate(value);
        Class timePeriodClass = getTimePeriodClass(((UITimeSeries)data).getTimePeriod());
        RegularTimePeriod timePeriod = RegularTimePeriod.createInstance
            (timePeriodClass, time, RegularTimePeriod.DEFAULT_TIME_ZONE);
        return timePeriod.getFirstMillisecond();
    }

    private static class DoubleList {
        private double[] values;
        private int size;

        public DoubleList(int initialCapacity) {
            this.values = new double[initialCapacity];
        }

        private void ensureCapacity(int minCapacity) {
            int oldCapacity = values.length;
            if (minCapacity > oldCapacity) {
                double[] oldValues = values;
                int newCapacity = (oldCapacity * 3)/2 + 1;
                if (newCapacity < minCapacity)
                    newCapacity = minCapacity;
                values = new double[newCapacity];
                System.arraycopy(oldValues, 0, values, 0, size);
            }
        }

        public void add(double value) {
            ensureCapacity(size + 1);
            values[size++] = value;
        }

        public double[] toArray() {
            if (size < values.length) {
                double[] oldValues = values;
                values = new double[size];
                System.arraycopy(oldValues, 0, values, 0, size);
            }
            return values;
        }
    }

    @SuppressWarnings("unchecked")
    public static XYDataset createHistogramDataset(UIHistogramDataSeries data) {
        HistogramDataset dataset = new HistogramDataset();

        UIHistogramDataItem[] items = data.getItems();
        int numSeries = items.length;
        if (numSeries == 0) {
            return dataset; // return empty dataset
        }

        int rowIndex = data.getFirst();
        int rows = data.getRows();

        int rowCount = data.getRowCount() - rowIndex;
        if (rowCount < 0) rowCount = 10;
        DoubleList[] values = new DoubleList[numSeries];
        for (int i = 0; i < numSeries; i++) {
            values[i] = new DoubleList(rowCount);
        }

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }
            for (int i = 0; i < numSeries; i++) {
                values[i].add(coerceToDouble(items[i].getValue()));
            }
        }
        data.setRowIndex(-1);

        for (int i = 0; i < numSeries; i++) {
            Key seriesKey = new Key(i, items[i].getLegend());
            double[] seriesValues = values[i].toArray();
            int bins = items[i].getBins();
            Double minimum = items[i].getMinimumValue();
            Double maximum = items[i].getMaximumValue();
            if (minimum != null && maximum != null) {
                dataset.addSeries(seriesKey, seriesValues, bins, minimum, maximum);
            } else {
                dataset.addSeries(seriesKey, seriesValues, bins);
            }
        }

        return dataset;
    }
    
    public static XYDataset createFunctionSeries(UIFunctionSeries data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        UIFunctionItem[] items = data.getItems();
        for (int i = 0; i < items.length; i++) {
            Key key = new Key(i, items[i].getLegend());
            dataset.addSeries(createFunctionSeries(key, items[i]));
        }
        return dataset;
    }

    private static XYSeries createFunctionSeries(Key key, UIFunctionItem item) {
        double start = item.getStart();
        double end = item.getEnd();
        double step = item.getStep();
        MethodExpression expr = item.getExpression();

        if (expr == null) {
            throw new FacesException("Function expression is required.");
        }
        if (start >= end) {
            throw new FacesException("Start must less than end.");
        }
        if (step <= 0) {
            throw new FacesException("Step must be positive value.");
        }

        XYSeries series = new XYSeries(key);
        ELContext elc = FacesContext.getCurrentInstance().getELContext();
        Object[] args = new Object[1];

        int samples = (int)((end - start)/step);
        for (int i = 0; i <= samples; i++) {
            double x; Double y;
            args[0] = x = start + (step * i);
            y = (Double)expr.invoke(elc, args);
            if (y != null && (Double.isNaN(y) || Double.isInfinite(y))) {
                y = null;
            }
            series.add(x, y);
        }

        return series;
    }
}
