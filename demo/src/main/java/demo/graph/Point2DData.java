/*
 * $Id: Point2DData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

package demo.graph;

import javax.faces.model.ArrayDataModel;
import java.awt.geom.Point2D;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class Point2DData extends ArrayDataModel
{
    public Point2DData() {
        this(4, 40);
    }

    public Point2DData(int numSeries, int numRows) {
        super(createData(numSeries, numRows));
    }

    public void randomize() {
        setWrappedData(createData(4, 40));
    }

    private static Point2D[][] createData(int numSeries, int numRows) {
        Point2D[][] data = new Point2D[numRows][numSeries];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numSeries; j++) {
                double x = (Math.random() - 0.5) * 200.0;
                double y = (Math.random() + 0.5) * 6.0 * x + x;
                data[i][j] = new Point2D.Double(x, y);
            }
        }
        return data;
    }
}
