/*
 * $Id: HistogramDataItemTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.faces.webapp.UIComponentClassicTagBase;

import org.operamasks.faces.component.graph.UIHistogramDataItem;

/**
 * @jsp.tag name="histogramDataItem" body-content="JSP"
 */
public class HistogramDataItemTag extends DataItemTag
{
    public String getComponentType() {
        return UIHistogramDataItem.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setBins(ValueExpression bins) {
        setValueExpression("bins", bins);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setMinimumValue(ValueExpression minimumValue) {
        setValueExpression("minimumValue", minimumValue);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setMaximumValue(ValueExpression maximumValue) {
        setValueExpression("maximumValue", maximumValue);
    }

    public int doStartTag() throws JspException {
        UIComponentClassicTagBase tag = getParentUIComponentClassicTagBase(pageContext);
        if (!(tag instanceof HistogramDataSeriesTag)) {
            throw new JspTagException("The histogramDataItem tag can only be nested " +
                                       "in a histogramDataSeries tag.");
        }
        return super.doStartTag();
    }
}
