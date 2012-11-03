/*
 * $Id: RadarChartRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.category.CategoryDataset;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.component.graph.UIChart;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class RadarChartRenderer extends ChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;

        if (dataset instanceof CategoryDataset) {
            SpiderWebPlot plot = new SpiderWebPlot((CategoryDataset)dataset);
            plot.setWebFilled(false);

            Object startAngle = comp.getAttributes().get("startAngle");
            if (startAngle != null) {
                plot.setStartAngle(Coercion.coerceToDouble(startAngle));
            }

            if (comp.isShowItemTips()) {
                plot.setToolTipGenerator(new StandardCategoryToolTipGenerator());
            }

            chart = new JFreeChart(null, null, plot, false);
        }

        return chart;
    }
}
