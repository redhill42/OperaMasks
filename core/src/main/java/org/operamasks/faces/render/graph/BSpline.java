/*
 * $Id: BSpline.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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

import java.awt.geom.Point2D;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;

/**
 * A utility class for calculating B-Spline curve of point data.
 */
public class BSpline
{
    public static XYSeries createBSpline(XYDataset source,
                                         int series, Comparable key,
                                         int samples, int degree)
    {
        if (source == null) {
            throw new NullPointerException();
        }

        XYSeries result = new XYSeries(key);

        if (samples > degree) {
            // Get point data
            int count = source.getItemCount(series);
            Point2D[] points = new Point2D[count];
            for (int i = 0; i < count; i++) {
                double x = source.getXValue(series, i);
                double y = source.getYValue(series, i);
                points[i] = new Point2D.Double(x, y);
            }

            // Compute B-Spline points
            points = bspline(points, samples, degree);
            for (int i = 0; i < samples; i++) {
                result.add(points[i].getX(), points[i].getY());
            }
        }

        return result;
    }

    /**
     * @param controls Control point array made up of point structure.
     * @param samples How many points on the spline are to be calculated.
     * @param t The degree of the polynormial plus 1
     * @return Array in which the calculate spline points
     */
    public static Point2D[] bspline(Point2D[] controls, int samples, int k) {
        int n = controls.length - 1;
        int[] T = knot(n, k);
        double N[][] = new double[n+1][k+1];
        double step = (double)(T[n+k] - T[0])/(samples-1);
        double t = 0.0;

        Point2D[] output = new Point2D[samples];
        for (int i = 0; i < samples-1; i++) {
            output[i] = point(n, k, T, t, N, controls);
            t += step;
        }
        output[samples-1] = (Point2D)controls[n].clone();
        return output;
    }

    private static int[] knot(int n, int k) {
        /*
         * Open Uniform knot vectors are uniform knot vectors which have
         * k-equal knot values at each end:
         *      t(i) = 0, i < k
         *      t(i+1) - t(i) = const, k-1 <= i < n+1
         *      t(i) = t(k+n), i >= n+1
         * e.g. [0,0,0,1,2,3,4,4,4] (k=3,n=5)
         */
        int[] T = new int[n+k+1];
        for (int i = 0; i <= n+k; i++) {
            if (i < k) {
                T[i] = 0;
            } else if (i <= n) {
                T[i] = T[i-1] + 1;
            } else {
                T[i] = T[n] + 1;
            }
        }
        return T;
    }

    private static double blend(int i, int k, int[] T, double t, double N[][]) {
        if (!Double.isNaN(N[i][k])) { // recursive optimization
            return N[i][k];
        }

        /*
         * N(i,k) = N(i,k-1)*(t-T(i))/(T(i+k-1)-T(i)) +
         *          N(i+k,k-1)*(T(i+k)-t)/(T(i+k)-T(i+1))
         * N(i,1) = {1 if T(i) <= t <= T(i+1), 0 otherwise}
         */
        double n;
        if (k == 1) {
            if ((T[i] <= t) && (t < T[i+1])) {
                n = 1.0;
            } else {
                n = 0.0;
            }
        } else {
            n = 0.0;
            if (T[i+k-1] != T[i]) {
                n += blend(i,k-1,T,t,N) * (t-T[i]) / (T[i+k-1]-T[i]);
            }
            if (T[i+k] != T[i+1]) {
                n += blend(i+1,k-1,T,t,N) * (T[i+k]-t) / (T[i+k]-T[i+1]);
            }
        }
        N[i][k] = n;
        return n;
    }

    private static Point2D point(int n, int k, int[] T, double t, double[][] N, Point2D[] P) {
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                N[i][j] = Double.NaN;
            }
        }

        /*
         * P(t) = Sum{i=0,n}(N(i,k)*P(i)), T(k-1) <= t <= T[n+1]
         */
        double x = 0.0, y = 0.0;
        for (int i = 0; i <= n; i++) {
            double m = blend(i, k, T, t, N);
            x += m * P[i].getX();
            y += m * P[i].getY();
        }
        return new Point2D.Double(x, y);
    }
}
