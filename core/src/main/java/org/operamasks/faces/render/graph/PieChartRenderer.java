/*
 * $Id: PieChartRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.util.TableOrder;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.component.graph.UIChart;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class PieChartRenderer extends ChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;
        PiePlot pieplot = null;

        boolean ring = Coercion.coerceToBoolean(comp.getAttributes().get("ring"));

        if (dataset instanceof CategoryDataset) {
            CategoryDataset catset = (CategoryDataset)dataset;

            if (catset.getRowCount() == 1) {
                PieDataset pieset = new CategoryToPieDataset(catset, TableOrder.BY_ROW, 0);

                if (ring) {
                    chart = ChartFactory.createRingChart(
                                null,
                                pieset,
                                false,
                                false,
                                false);
                } else if (comp.isEffect3D()) {
                    chart = ChartFactory.createPieChart3D(
                                null,
                                pieset,
                                false,
                                false,
                                false);
                } else {
                    chart = ChartFactory.createPieChart(
                                null,
                                pieset,
                                false,
                                false,
                                false);
                }

                pieplot = (PiePlot)chart.getPlot();
            } else {
                if (comp.isEffect3D()) {
                    chart = ChartFactory.createMultiplePieChart3D(
                                null,
                                catset,
                                TableOrder.BY_ROW,
                                false,
                                false,
                                false);
                } else {
                    chart = ChartFactory.createMultiplePieChart(
                                null,
                                catset,
                                TableOrder.BY_ROW,
                                false,
                                false,
                                false);
                }

                pieplot = (PiePlot)((MultiplePiePlot)chart.getPlot()).getPieChart().getPlot();
            }
        }


        if (pieplot != null) {
            if (!comp.isDrawItemLabel()) {
                pieplot.setLabelGenerator(null);
            }

            if (comp.isShowItemTips()) {
                pieplot.setToolTipGenerator(new StandardPieToolTipGenerator());
            }

            Object startAngle = comp.getAttributes().get("startAngle");
            if (startAngle != null) {
                pieplot.setStartAngle(Coercion.coerceToDouble(startAngle));
            }
        }

        return chart;
    }
}
