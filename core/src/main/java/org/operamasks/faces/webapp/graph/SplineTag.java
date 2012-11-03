/*
 * $Id: SplineTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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
import org.operamasks.faces.component.graph.UISpline;

/**
 * @jsp.tag name="spline" body-content="JSP"
 */
public class SplineTag extends CurveTag
{
    public String getComponentType() {
        return UISpline.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.SplineType"
     */
    public void setType(ValueExpression type) {
        setValueExpression("type", type);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setSamples(ValueExpression samples) {
        setValueExpression("samples", samples);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setDegree(ValueExpression degree) {
        setValueExpression("degree", degree);
    }
}
