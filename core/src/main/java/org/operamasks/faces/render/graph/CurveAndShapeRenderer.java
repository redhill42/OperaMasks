/*
 * $Id: CurveAndShapeRenderer.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

public class CurveAndShapeRenderer extends LineAndShapeRenderer
{
    /** A flag indicating whether or not Area are drawn at each XY point. */
    private boolean drawArea;

    /**
     * Create a renderer with both curve lines and shapes visible by default.
     */
    public CurveAndShapeRenderer() {
        super();
    }

    /**
     * Creates a new renderer with lines and/or shapes visible.
     */
    public CurveAndShapeRenderer(boolean lines, boolean shapes) {
        super(lines, shapes);
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
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row, int column,
                         int pass)
    {
        if (pass == 0) {
            if (row == 0 && column == 0) {
                int seriesCount = dataset.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    drawSeriesCurve(g2, dataArea, plot, domainAxis, rangeAxis, dataset, series);
                }
            }
        } else {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);
        }
    }

    private void drawSeriesCurve(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 CategoryAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 CategoryDataset dataset,
                                 int series)
    {
        // do nothing if item is not visible
        if (!(getItemVisible(series, 0) && (getItemLineVisible(series, 0) || drawArea))) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        PlotOrientation orientation = plot.getOrientation();

        int itemCount = dataset.getColumnCount();
        double[][] points = new double[itemCount][2];
        int count = 0;

        // get data points
        for (int i = 0; i < itemCount; i++) {
            Number value = dataset.getValue(series, i);
            if (value != null) {
                points[count][0] = domainAxis.getCategoryMiddle(i, itemCount, dataArea, xAxisLocation);
                points[count][1] = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, yAxisLocation);
                count++;
            }
        }

        if (count < 2) {
            return;
        }

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

        Paint paint = getSeriesPaint(series);
        Stroke stroke = getSeriesStroke(series);

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

        if (getItemLineVisible(series, 0)) {
            g2.setPaint(paint);
            g2.setStroke(stroke);
            g2.draw(path);
        }
    }
}
