/*
 * $Id: BarChartRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.time.TimeSeriesCollection;

import org.operamasks.faces.component.graph.UIChart;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class BarChartRenderer extends ChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;

        if (dataset instanceof CategoryDataset) {
            if (comp.isStacked()) {
                if (comp.isEffect3D()) {
                    chart = ChartFactory.createStackedBarChart3D(
                                null,
                                null,
                                null,
                                (CategoryDataset)dataset,
                                getChartOrientation(comp),
                                false,
                                false,
                                false);
                } else {
                    chart = ChartFactory.createStackedBarChart(
                                null,
                                null,
                                null,
                                (CategoryDataset)dataset,
                                getChartOrientation(comp),
                                false,
                                false,
                                false);
                }
            } else {
                if (comp.isEffect3D()) {
                    chart = ChartFactory.createBarChart3D(
                                null,
                                null,
                                null,
                                (CategoryDataset)dataset,
                                getChartOrientation(comp),
                                false,
                                false,
                                false);
                } else {
                    chart = ChartFactory.createBarChart(
                                null,
                                null,
                                null,
                                (CategoryDataset)dataset,
                                getChartOrientation(comp),
                                false,
                                false,
                                false);
                }
            }
        } else if (dataset instanceof IntervalXYDataset) {
            chart = ChartFactory.createXYBarChart(
                                null,
                                null,
                                (dataset instanceof TimeSeriesCollection),
                                null,
                                (IntervalXYDataset)dataset,
                                getChartOrientation(comp),
                                false,
                                false,
                                false);
        }

        return chart;
    }

    protected void setChartStyles(JFreeChart chart, UIChart comp) {
        super.setChartStyles(chart, comp);

        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            setBarStyles((CategoryPlot)plot, comp);
        } else if (plot instanceof XYPlot) {
            setBarStyles((XYPlot)plot, comp);
        }
    }

    private void setBarStyles(CategoryPlot plot, UIChart comp) {
        CategoryItemRenderer r = plot.getRenderer();
        if (r instanceof BarRenderer) {
            BarRenderer renderer = (BarRenderer)r;

            Boolean drawOutline = comp.getDrawOutline();
            if (drawOutline != null) {
                renderer.setDrawBarOutline(drawOutline);
            } else if (renderer instanceof BarRenderer3D) {
                renderer.setDrawBarOutline(false);
            }
        }
    }

    private void setBarStyles(XYPlot plot, UIChart comp) {
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYBarRenderer) {
            XYBarRenderer renderer = (XYBarRenderer)r;

            Boolean drawOutline = comp.getDrawOutline();
            if (drawOutline != null) {
                renderer.setDrawBarOutline(drawOutline);
            }
        }
    }
}
