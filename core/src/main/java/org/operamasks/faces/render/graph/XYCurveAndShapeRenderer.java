/*
 * $Id: XYCurveAndShapeRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Arrays;
import java.util.Comparator;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

public class XYCurveAndShapeRenderer extends XYLineAndShapeRenderer
{
    /** A flag indicating whether or not Area are drawn at each XY point. */
    private boolean drawArea;

    /**
     * Create a renderer with both curve lines and shapes visible by default.
     */
    public XYCurveAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Creates a new renderer with lines and/or shapes visible.
     */
    public XYCurveAndShapeRenderer(boolean lines, boolean shapes) {
        super(lines, shapes);
        this.setDrawSeriesLineAsPath(true);
    }

    /**
     * Returns true if Area is being plotted by the renderer.
     *
     * @return <code>true</code> if Area is being plotted by the renderer.
     */
    public boolean getDrawArea() {
        return this.drawArea;
    }

    /**
     * Set a flag indicating whether or not Area are drawn at each XY point.
     *
     * @return <code>true</code> if Area is being plotted by the renderer.
     */
    public void setDrawArea(boolean drawArea) {
        this.drawArea = drawArea;
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items. Instead of drawing separate lines,
     * a GeneralPath is constructed and drawn at the end of
     * the series painting.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataArea  the area within which the data is being drawn.
     */
    protected void drawPrimaryLineAsPath(XYItemRendererState state,
                                         Graphics2D g2, XYPlot plot,
                                         XYDataset dataset,
                                         int pass,
                                         int series,
                                         int item,
                                         ValueAxis domainAxis,
                                         ValueAxis rangeAxis,
                                         Rectangle2D dataArea) {

        if (item != 0) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        PlotOrientation orientation = plot.getOrientation();

        int itemCount = dataset.getItemCount(series);
        double[][] points = new double[itemCount][2];
        int count = 0;

        for (int i = 0; i < itemCount; i++) {
            double x = dataset.getXValue(series, i);
            double y = dataset.getYValue(series, i);
            double transX = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
            if (!Double.isNaN(transX) && !Double.isNaN(transY)) {
                points[count][0] = transX;
                points[count][1] = transY;
                count++;
            }
        }

        if (count < 2) {
            return;
        }

        // sort points according to x axis
        Arrays.sort(points, new Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return a[0] > b[0] ? 1 : a[0] < b[0] ? -1 : 0;
            }
        });

        // draw curve
        CubicSplineFunction2D f = new CubicSplineFunction2D(points, count);
        GeneralPath path = new GeneralPath();

        double startX = points[0][0];
        double startY = points[0][1];
        double endX = points[count-1][0];
        double endY = points[count-1][1];
        double yz = rangeAxis.valueToJava2D(0.0, dataArea, yAxisLocation);

        if (orientation == PlotOrientation.HORIZONTAL) {
            if (drawArea) {
                path.moveTo((float)yz, (float)startX);
                path.lineTo((float)startY, (float)startX);
                for (double x = Math.floor(startX) + 1.0; x < endX; x += 1.0) {
                    path.lineTo((float)f.getValue(x), (float)x);
                }
                path.lineTo((float)endY, (float)endX);
                path.lineTo((float)yz, (float)endX);
                path.closePath();
            } else {
                path.moveTo((float)startY, (float)startX);
                for (double x = Math.floor(startX) + 1.0; x < endX; x += 1.0) {
                    path.lineTo((float)f.getValue(x), (float)x);
                }
                path.lineTo((float)endY, (float)endX);
            }
        } else {
            if (drawArea) {
                path.moveTo((float)startX, (float)yz);
                path.lineTo((float)startX, (float)startY);
                for (double x = Math.floor(startX) + 1.0; x < endX; x += 1.0) {
                    path.lineTo((float)x, (float)f.getValue(x));
                }
                path.lineTo((float)endX, (float)endY);
                path.lineTo((float)endX, (float)yz);
                path.closePath();
            } else {
                path.moveTo((float)startX, (float)startY);
                for (double x = Math.floor(startX) + 1.0; x < endX; x += 1.0) {
                    path.lineTo((float)x, (float)f.getValue(x));
                }
                path.lineTo((float)endX, (float)endY);
            }
        }

        Paint paint = getItemPaint(series, item);
        Stroke stroke = getItemStroke(series, item);

        if (drawArea) {
            g2.setPaint(paint);
            g2.fill(path);

            // create paint for outline
            if (paint instanceof Color) {
                paint = ((Color)paint).darker();
            } else if (paint instanceof GradientPaint) {
                paint = ((GradientPaint)paint).getColor1().darker();
            }
        }

        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(path);
    }
}
