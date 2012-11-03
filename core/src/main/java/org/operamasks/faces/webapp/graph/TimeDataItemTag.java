/*
 * $Id: TimeDataItemTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.operamasks.faces.component.graph.UITimeDataItem;

/**
 * @jsp.tag name="timeDataItem" body-content="JSP"
 */
public class TimeDataItemTag extends DataItemTag
{
    public String getComponentType() {
        return UITimeDataItem.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.util.Date"
     */
    public void setTime(ValueExpression time) {
        setValueExpression("time", time);
    }

    public int doStartTag() throws JspException {
        UIComponentClassicTagBase tag = getParentUIComponentClassicTagBase(pageContext);
        if (!(tag instanceof TimeSeriesTag)) {
            throw new JspTagException("The timeDataItem tag can only be nested in a timeSeries tag.");
        }
        return super.doStartTag();
    }
}
