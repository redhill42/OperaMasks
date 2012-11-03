/*
 * $Id: CurveAreaChartRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeriesCollection;

import org.operamasks.faces.component.graph.UIChart;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class CurveAreaChartRenderer extends CurveChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;

        if (dataset instanceof CategoryDataset) {
            chart = createCurveAreaChart((CategoryDataset)dataset, getChartOrientation(comp));
        } else if (dataset instanceof XYDataset) {
            chart = createXYCurveAreaChart((XYDataset)dataset, getChartOrientation(comp));
        }

        return chart;
    }

    private JFreeChart createCurveAreaChart(CategoryDataset dataset, PlotOrientation orientation) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setCategoryMargin(0.0);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRangeIncludesZero(false);
        
        CurveAndShapeRenderer renderer = new CurveAndShapeRenderer(true, false);
        renderer.setDrawArea(true);

        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (dataset.getRowCount() > 1) {
            plot.setForegroundAlpha(0.75f);
        }

        return new JFreeChart(null, null, plot, false);
    }

    private JFreeChart createXYCurveAreaChart(XYDataset dataset, PlotOrientation orientation) {
        ValueAxis xAxis;
        if (dataset instanceof TimeSeriesCollection) {
            xAxis = new DateAxis();
        } else {
            xAxis = new NumberAxis();
            ((NumberAxis)xAxis).setAutoRangeIncludesZero(false);
        }
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRangeIncludesZero(false);

        XYCurveAndShapeRenderer renderer = new XYCurveAndShapeRenderer(true, false);
        renderer.setDrawArea(true);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (dataset.getSeriesCount() > 1) {
            plot.setForegroundAlpha(0.75f);
        }

        return new JFreeChart(null, null, plot, false);
    }
}
