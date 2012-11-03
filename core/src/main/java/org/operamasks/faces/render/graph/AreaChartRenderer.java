/*
 * $Id: AreaChartRenderer.java,v 1.5 2007/07/02 07:37:44 jacky Exp $
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeriesCollection;

import org.operamasks.faces.component.graph.UIChart;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class AreaChartRenderer extends ChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;

        if (dataset instanceof CategoryDataset) {
            if (comp.isStacked()) {
                chart = ChartFactory.createStackedAreaChart(
                            null,
                            null,
                            null,
                            (CategoryDataset)dataset,
                            getChartOrientation(comp),
                            false,
                            false,
                            false);
            } else {
                chart = ChartFactory.createAreaChart(
                            null,
                            null,
                            null,
                            (CategoryDataset)dataset,
                            getChartOrientation(comp),
                            false,
                            false,
                            false);
            }
        } else if (dataset instanceof XYDataset) {
            chart = ChartFactory.createXYAreaChart(
                        null,
                        null,
                        null,
                        (XYDataset)dataset,
                        getChartOrientation(comp),
                        false,
                        false,
                        false);

            if (dataset instanceof TimeSeriesCollection) {
                DateAxis xAxis = new DateAxis(null);
                xAxis.setLowerMargin(0.02);
                xAxis.setUpperMargin(0.02);
                ((XYPlot)chart.getPlot()).setDomainAxis(xAxis);
            }
        }

        return chart;
    }
}
