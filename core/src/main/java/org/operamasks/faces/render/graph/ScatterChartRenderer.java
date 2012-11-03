/*
 * $Id: ScatterChartRenderer.java,v 1.6 2007/07/02 07:37:45 jacky Exp $
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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.CategoryPlot;

import org.operamasks.faces.component.graph.UIChart;

public class ScatterChartRenderer extends LineChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        JFreeChart chart = super.createChart(comp);

        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof CategoryPlot) {
                LineAndShapeRenderer r = (LineAndShapeRenderer)((CategoryPlot)plot).getRenderer();
                r.setBaseLinesVisible(false);
                r.setBaseShapesVisible(true);
                r.setDrawOutlines(false);
            } else if (plot instanceof XYPlot) {
                XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)((XYPlot)plot).getRenderer();
                r.setBaseLinesVisible(false);
                r.setBaseShapesVisible(true);
                r.setDrawOutlines(false);
            }
        }

        return chart;
    }
}
