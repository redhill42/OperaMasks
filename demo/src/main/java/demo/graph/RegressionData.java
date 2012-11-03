/*
 * $Id: RegressionData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class RegressionData extends ArrayDataModel
{
    private static final Point2D[] data = {
        new Point2D.Double(2.0, 56.27),
        new Point2D.Double(3.0, 41.32),
        new Point2D.Double(4.0, 31.45),
        new Point2D.Double(5.0, 18.05),
        new Point2D.Double(6.0, 24.69),
        new Point2D.Double(7.0, 19.78),
        new Point2D.Double(8.0, 20.94),
        new Point2D.Double(9.0, 16.73),
        new Point2D.Double(10.0, 14.21),
        new Point2D.Double(11.0, 12.44),
    };

    public RegressionData() {
        super(data);
    }
}
