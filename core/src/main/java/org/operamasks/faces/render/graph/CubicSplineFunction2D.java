/*
 * $Id: CubicSplineFunction2D.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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

import org.jfree.data.function.Function2D;

public class CubicSplineFunction2D implements Function2D
{
    /*
     * Algorithm source: http://i-math.sysu.edu.cn/na/ch02_8.pdf
     */

    private int count;
    private double[] x, y;
    private double[] s, dy;

    public CubicSplineFunction2D(double[][] data) {
        this(data, data.length);
    }

    public CubicSplineFunction2D(double[][] data, int count) {
        this.count = count;
        this.x = new double[count];
        this.y = new double[count];
        for (int i = 0; i < count; i++) {
            this.x[i] = data[i][0];
            this.y[i] = data[i][1];
        }

        this.s = new double[count];
        this.dy = new double[count];
        init(x, y, s, dy);
    }

    private void init(double[] x, double[] y, double[] s, double[] dy) {
        double h0, h1, alpha, beta;
        int n = this.count;
        int j;

        s[0] = dy[0] = 0.0;
        h0 = x[1] - x[0];
        for (j = 1; j < n-2; j++) {
            h1 = x[j+1] - x[j];
            alpha = h0 / (h0+h1);
            beta = (1.0 - alpha) * (y[j] - y[j-1]) / h0;
            beta = 3.0 * (beta + alpha * (y[j+1] - y[j]) / h1);
            dy[j] = -alpha / (2.0 + (1.0 - alpha) * dy[j-1]);
            s[j] = (beta - (1.0 - alpha) * s[j-1]);
            s[j] = s[j] / (2.0 + (1.0 - alpha) * dy[j-1]);
            h0 = h1;
        }

        for (j = n-2; j >= 0; j--)
            dy[j] = dy[j]*dy[j+1] + s[j];
        for (j = 0; j <= n-2; j++)
            s[j] = x[j+1] - x[j];
    }

    public double getValue(double t) {
        double[] x = this.x, y = this.y;
        double[] s = this.s, dy = this.dy;
        double h0, h1, z;
        int n = this.count;
        int i;

        if (t >= x[n-1]) {
            i = n-2;
        } else {
            i = 0;
            while (t > x[i+1]) {
                i = i+1;
            }
        }

        h1 = (x[i+1] - t) / s[i];
        h0 = h1*h1;
        z = (3.0*h0 - 2.0*h0*h1)*y[i] + s[i]*(h0 - h0*h1)*dy[i];

        h1 = (t - x[i]) / s[i];
        h0 = h1*h1;
        z = z + (3.0*h0 - 2.0*h0*h1)*y[i+1] - s[i]*(h0 - h0*h1)*dy[i+1];

        return z;
    }
}
