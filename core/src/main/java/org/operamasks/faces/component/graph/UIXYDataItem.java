/*
 * $Id: UIXYDataItem.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

package org.operamasks.faces.component.graph;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class UIXYDataItem extends UIDataItem
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.XYDataItem";

    public UIXYDataItem() {
        setRendererType(null);
    }

    private Object xValue = null;
    private Object yValue = null;

    public Object getxValue() {
        if (this.xValue != null) {
            return this.xValue;
        }
        ValueExpression ve = getValueExpression("xValue");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setxValue(Object xValue) {
        this.xValue = xValue;
    }

    public Object getyValue() {
        if (this.yValue != null) {
            return this.yValue;
        }
        ValueExpression ve = getValueExpression("yValue");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setyValue(Object yValue) {
        this.yValue = yValue;
    }

    public Object getX() {
        return getxValue();
    }

    public void setX(Object x) {
        setxValue(x);
    }

    public Object getY() {
        return getyValue();
    }

    public void setY(Object y) {
        setyValue(y);
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            xValue,
            yValue,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        xValue = values[1];
        yValue = values[2];
    }
}
