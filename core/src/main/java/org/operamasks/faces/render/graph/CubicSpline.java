/*
 * $Id: CubicSpline.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;

public class CubicSpline
{
    public static XYSeries createCubicSpline(XYDataset source,
                                             int series, Comparable key,
                                             int samples)
    {
        if (source == null) {
            throw new NullPointerException();
        }

        int n = source.getItemCount(series);
        double[][] data = new double[n][2];
        for (int i = 0; i < n; i++) {
            data[i][0] = source.getXValue(series, i);
            data[i][1] = source.getYValue(series, i);
        }

        XYSeries result = new XYSeries(key);
        CubicSplineFunction2D f = new CubicSplineFunction2D(data);
        double step = (data[n-1][0] - data[0][0])/samples;
        double t = data[0][0];
        for (int i = 0; i < samples; i++, t += step) {
            result.add(t, f.getValue(t));
        }
        return result;
    }
}
