/*
 * $Id: CompositeChartRenderer.java,v 1.5 2007/07/02 07:37:44 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

import org.operamasks.faces.component.graph.UIChart;
import org.operamasks.faces.component.graph.UIAxis;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.resources.Resources.*;

public class CompositeChartRenderer extends ChartRenderer
{
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        // Create subcharts
        UIChart c = (UIChart)component;
        List<UIChart> kids = getSubchartComponents(c);
        List<JFreeChart> subcharts = createSubcharts(context, kids);

        // Create composite chart
        JFreeChart compositeChart;
        if (subcharts.size() == 0) {
            return;
        } else if (subcharts.size() == 1) {
            compositeChart = subcharts.get(0);
        } else {
            Class type = getComonDatasetType(subcharts);
            if (XYDataset.class.isAssignableFrom(type)) {
                compositeChart = createXYCompositeChart(subcharts, c);
            } else {
                compositeChart = createCategoryCompositeChart(subcharts, c);
            }
        }

        // Initialize composite chart
        initChart(compositeChart, c);

        // Initialize individual subcharts
        for (int i = 0; i < subcharts.size(); i++) {
            UIChart kid = kids.get(i);
            JFreeChart subchart = subcharts.get(i);
            ChartRenderer cr = (ChartRenderer)FacesUtils.getRenderer(context, kid);
            cr.initChart(subchart, kid);
        }

        // Encode composite chart image
        encodeChartImage(context, c, compositeChart);
    }

    protected JFreeChart createChart(UIChart comp) {
        throw new AssertionError();
    }

    private List<UIChart> getSubchartComponents(UIChart comp) {
        List<UIChart> result = new ArrayList<UIChart>();
        for (UIComponent kid : comp.getChildren()) {
            if (kid.isRendered() && (kid instanceof UIChart)) {
                result.add((UIChart)kid);
            }
        }
        return result;
    }

    private List<JFreeChart> createSubcharts(FacesContext context, List<UIChart> kids) {
        List<JFreeChart> result = new ArrayList<JFreeChart>(kids.size());
        for (UIChart kid : kids) {
            ChartRenderer cr = (ChartRenderer)FacesUtils.getRenderer(context, kid);
            result.add(cr.createChart(kid));
        }
        return result;
    }

    private Class getComonDatasetType(List<JFreeChart> subcharts) {
        Class commonType = null;

        for (JFreeChart c : subcharts) {
            Plot plot = c.getPlot();
            Class type = null;

            if (plot instanceof CategoryPlot) {
                Dataset ds = ((CategoryPlot)plot).getDataset();
                if (ds instanceof CategoryDataset) {
                    type = CategoryDataset.class;
                }
            } else if (plot instanceof XYPlot) {
                Dataset ds = ((XYPlot)plot).getDataset();
                if (ds instanceof TimeSeriesCollection) {
                    type = TimeSeriesCollection.class;
                } else if (ds instanceof XYDataset) {
                    type = XYDataset.class;
                }
            }

            if (type != null && commonType == null) {
                commonType = type;
            } else if (type == null || type != commonType) {
                throw new FacesException(_T(UI_CHART_INCOMPATIBLE_COMMON_DATA_SERIES));
            }
        }

        return commonType;
    }

    private JFreeChart createCategoryCompositeChart(List<JFreeChart> subcharts, UIChart comp) {
        CategoryPlot compositePlot = new CategoryPlot();
        compositePlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        compositePlot.setOrientation(getChartOrientation(comp));

        for (int i = 0; i < subcharts.size(); i++) {
            CategoryPlot subplot = (CategoryPlot)subcharts.get(i).getPlot();
            compositePlot.setDataset(i, subplot.getDataset());
            compositePlot.setRenderer(i, subplot.getRenderer());

            if (i == 0) {
                // Axis zero is always available.
                compositePlot.setDomainAxis(0, subplot.getDomainAxis());
                compositePlot.setRangeAxis(0, subplot.getRangeAxis());
            } else {
                int yAxisMap = getRangeAxisMap(comp, i);
                ValueAxis yAxis = null;
                if (yAxisMap == -1) {
                    yAxisMap = 0; // map to axis zero by default
                } else if (yAxisMap == i) {
                    yAxis = subplot.getRangeAxis(); // add subplot axis to composite plot
                }
                compositePlot.setRangeAxis(i, yAxis);
                compositePlot.mapDatasetToRangeAxis(i, yAxisMap);
            }
        }

        return new JFreeChart(null, null, compositePlot, false);
    }

    private JFreeChart createXYCompositeChart(List<JFreeChart> subcharts, UIChart comp) {
        XYPlot compositePlot = new XYPlot();
        compositePlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        compositePlot.setOrientation(getChartOrientation(comp));

        for (int i = 0; i < subcharts.size(); i++) {
            XYPlot subplot = (XYPlot)subcharts.get(i).getPlot();
            compositePlot.setDataset(i, subplot.getDataset());
            compositePlot.setRenderer(i, subplot.getRenderer());

            if (i == 0) {
                compositePlot.setDomainAxis(0, subplot.getDomainAxis());
                compositePlot.setRangeAxis(0, subplot.getRangeAxis());
            } else {
                int yAxisMap = getRangeAxisMap(comp, i);
                ValueAxis yAxis = null;
                if (yAxisMap == -1) {
                    yAxisMap = 0; // map to axis zero by default
                } else if (yAxisMap == i) {
                    yAxis = subplot.getRangeAxis(); // add subplot axis to composite plot
                }
                compositePlot.setRangeAxis(i, yAxis);
                compositePlot.mapDatasetToRangeAxis(i, yAxisMap);
            }
        }

        return new JFreeChart(null, null, compositePlot, false);
    }

    private int getRangeAxisMap(UIChart comp, int index) {
        UIChart subcomp = getSubchartComponent(comp, index);
        int yAxisMap = -1;

        if (subcomp != null) {
            // Does subchart's y axis is mapped?
            UIAxis yAxis = subcomp.getyAxis();
            if (yAxis != null) {
                String mapId = (String)yAxis.getAttributes().get("mapTo");
                if (mapId != null) {
                    yAxisMap = getSubchartIndexById(comp, mapId);
                }
            }

            // If the subhcart has it's own y axis the map it.
            if (yAxisMap == -1) {
                if (yAxis != null || subcomp.getyAxisLabel() != null) {
                    yAxisMap = index;
                }
            }
        }

        return yAxisMap;
    }

    private UIChart getSubchartComponent(UIChart parent, int index) {
        for (UIComponent kid : parent.getChildren()) {
            if (kid.isRendered() && (kid instanceof UIChart)) {
                if (index == 0) {
                    return ((UIChart)kid);
                }
                index--;
            }
        }
        return null;
    }

    private int getSubchartIndexById(UIChart parent, String id) {
        int index = 0;
        for (UIComponent kid : parent.getChildren()) {
            if (kid.isRendered() && (kid instanceof UIChart)) {
                if (kid.getId().equals(id)) {
                    return index;
                }
                index++;
            }
        }
        return index;
    }
}
