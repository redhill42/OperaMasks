/*
 * $Id: LineChartRenderer.java,v 1.5 2007/07/02 07:37:44 jacky Exp $
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
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;

import org.operamasks.faces.component.graph.UIChart;
import org.operamasks.faces.component.graph.UIDataItem;
import org.operamasks.faces.component.graph.LineStyleType;
import static org.operamasks.faces.render.graph.ChartRendererHelper.*;

public class LineChartRenderer extends ChartRenderer
{
    protected JFreeChart createChart(UIChart comp) {
        Dataset dataset = createDataset(comp);
        JFreeChart chart = null;

        if (dataset instanceof CategoryDataset) {
            if (comp.isEffect3D()) {
                chart = ChartFactory.createLineChart3D(
                            null,
                            null,
                            null,
                            (CategoryDataset)dataset,
                            getChartOrientation(comp),
                            false,
                            false,
                            false);
            } else {
                chart = ChartFactory.createLineChart(
                            null,
                            null,
                            null,
                            (CategoryDataset)dataset,
                            getChartOrientation(comp),
                            false,
                            false,
                            false);
            }
        } else if (dataset instanceof TimeSeriesCollection) {
            chart = ChartFactory.createTimeSeriesChart(
                            null,
                            null,
                            null,
                            (XYDataset)dataset,
                            false,
                            false,
                            false);
            ((XYPlot)chart.getPlot()).setOrientation(getChartOrientation(comp));
        } else if (dataset instanceof XYDataset) {
            chart = ChartFactory.createXYLineChart(
                            null,
                            null,
                            null,
                            (XYDataset)dataset,
                            getChartOrientation(comp),
                            false,
                            false,
                            false);
        }

        return chart;
    }

    protected void setSeriesStyles(JFreeChart chart, UIChart comp) {
        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            CategoryItemRenderer r = ((CategoryPlot)plot).getRenderer();
            if (r instanceof LineAndShapeRenderer) {
                setLineStyles((LineAndShapeRenderer)r, comp);
            }
        } else if (plot instanceof XYPlot) {
            XYItemRenderer r = ((XYPlot)plot).getRenderer();
            if (r instanceof XYLineAndShapeRenderer) {
                setLineStyles((XYLineAndShapeRenderer)r, comp);
            }
        }

        super.setSeriesStyles(chart, comp);
    }

    protected void setSeriesStyles(AbstractRenderer renderer, UIChart comp, int index, UIDataItem item) {
        super.setSeriesStyles(renderer, comp, index, item);
        
        if (renderer instanceof LineAndShapeRenderer) {
            setSeriesLineStyles((LineAndShapeRenderer)renderer, comp, index, item);
        } else if (renderer instanceof XYLineAndShapeRenderer) {
            setSeriesLineStyles((XYLineAndShapeRenderer)renderer, comp, index, item);
        }
    }

    private void setLineStyles(LineAndShapeRenderer renderer, UIChart comp) {
        Boolean drawLines = comp.getDrawLines();
        if (drawLines != null) {
            renderer.setBaseLinesVisible(drawLines);
        }

        Boolean drawMarkers = comp.getDrawMarkers();
        if (drawMarkers != null) {
            renderer.setBaseShapesVisible(drawMarkers);
        }

        Boolean fillMarkers = comp.getFillMarkers();
        if (fillMarkers != null) {
            renderer.setBaseShapesFilled(fillMarkers);
        }
        renderer.setUseFillPaint(true);

        Boolean drawOutline = comp.getDrawOutline();
        if (drawOutline != null) {
            renderer.setDrawOutlines(drawOutline);
        } else if (renderer instanceof LineRenderer3D) {
            renderer.setDrawOutlines(false);
        }
        renderer.setUseOutlinePaint(true);
    }

    private void setSeriesLineStyles(LineAndShapeRenderer renderer, UIChart comp, int index, UIDataItem item) {
        Boolean drawLines = item.getDrawLines();
        if (drawLines != null) {
            renderer.setSeriesLinesVisible(index, drawLines);
        }

        Float lineWidth = item.getLineWidth();
        LineStyleType lineStyle = item.getLineStyle();
        if (lineWidth == null)
            lineWidth = comp.getLineWidth();
        if (lineStyle == null)
            lineStyle = comp.getLineStyle();
        if (lineWidth != null || lineStyle != null) {
            if (lineWidth == null)
                lineWidth = 1.0f;
            renderer.setSeriesStroke(index, createLineStroke(lineWidth, lineStyle));
        }

        Boolean drawMarkers = item.getDrawMarkers();
        if (drawMarkers != null) {
            renderer.setSeriesShapesVisible(index, drawMarkers);
        }

        Boolean fillMarkers = item.getFillMarkers();
        if (fillMarkers != null) {
            renderer.setSeriesShapesFilled(index, fillMarkers);
        }

        Paint markerFillColor = item.getMarkerFillColor();
        if (markerFillColor == null) {
            markerFillColor = comp.getMarkerFillColor();
            if (markerFillColor == null)
                markerFillColor = renderer.getSeriesPaint(index);
        }
        renderer.setSeriesFillPaint(index, markerFillColor);
    }

    private void setLineStyles(XYLineAndShapeRenderer renderer, UIChart comp) {
        Boolean drawLines = comp.getDrawLines();
        if (drawLines != null) {
            renderer.setBaseLinesVisible(drawLines);
        }

        Boolean drawMarkers = comp.getDrawMarkers();
        if (drawMarkers != null) {
            renderer.setBaseShapesVisible(drawMarkers);
        }

        Boolean fillMarkers = comp.getFillMarkers();
        if (fillMarkers != null) {
            renderer.setBaseShapesFilled(fillMarkers);
        }
        renderer.setUseFillPaint(true);

        Boolean drawOutline = comp.getDrawOutline();
        if (drawOutline != null) {
            renderer.setDrawOutlines(drawOutline);
        }
        renderer.setUseOutlinePaint(true);
    }

    private void setSeriesLineStyles(XYLineAndShapeRenderer renderer, UIChart comp, int index, UIDataItem item) {
        Boolean drawLines = item.getDrawLines();
        if (drawLines != null) {
            renderer.setSeriesLinesVisible(index, drawLines);
        }

        Float lineWidth = item.getLineWidth();
        LineStyleType lineStyle = item.getLineStyle();
        if (lineWidth == null)
            lineWidth = comp.getLineWidth();
        if (lineStyle == null)
            lineStyle = comp.getLineStyle();
        if (lineWidth != null || lineStyle != null) {
            if (lineWidth == null)
                lineWidth = 1.0f;
            renderer.setSeriesStroke(index, createLineStroke(lineWidth, lineStyle));
        }

        Boolean drawMarkers = item.getDrawMarkers();
        if (drawMarkers != null) {
            renderer.setSeriesShapesVisible(index, drawMarkers);
        }

        Boolean fillMarkers = item.getFillMarkers();
        if (fillMarkers != null) {
            renderer.setSeriesShapesFilled(index, fillMarkers);
        }

        Paint markerFillColor = item.getMarkerFillColor();
        if (markerFillColor == null) {
            markerFillColor = comp.getMarkerFillColor();
            if (markerFillColor == null)
                markerFillColor = renderer.getSeriesPaint(index);
        }
        renderer.setSeriesFillPaint(index, markerFillColor);
    }

    private Stroke createLineStroke(float width, LineStyleType style) {
        if (style == null || style == LineStyleType.Solid) {
            return new BasicStroke(width);
        }

        float[] dash = null;
        if (style == LineStyleType.Dot) {
            dash = new float[] { width*2 };
        } else if (style == LineStyleType.Dash) {
            dash = new float[] { width*8, width*2 };
        } else if (style == LineStyleType.DashDot) {
            dash = new float[] { width*8, width*2, width*2, width*2 };
        } else if (style == LineStyleType.DashDotDot) {
            dash = new float[] { width*8, width*2, width*2, width*2, width*2, width*2 };
        }

        return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, dash, 0f);
    }
}
