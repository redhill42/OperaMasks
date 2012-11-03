/*
 * $Id: MovingAverageTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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

package org.operamasks.faces.webapp.graph;

import javax.el.ValueExpression;
import org.operamasks.faces.component.graph.UIAverageLine;

/**
 * @jsp.tag name="movingAverage" body-content="JSP"
 */
public class MovingAverageTag extends CurveTag
{
    public String getComponentType() {
        return UIAverageLine.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setPeriod(ValueExpression period) {
        setValueExpression("period", period);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setSkip(ValueExpression skip) {
        setValueExpression("skip", skip);
    }
}
