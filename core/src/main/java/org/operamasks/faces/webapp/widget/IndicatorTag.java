/*
 * $Id: IndicatorTag.java,v 1.3 2007/07/02 07:37:59 jacky Exp $
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

package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;

import org.operamasks.faces.component.ajax.AjaxStatus;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="indicator" body-content="empty"
 */
public class IndicatorTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return AjaxStatus.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return "org.operamasks.faces.widget.AjaxIndicator";
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStartImage(ValueExpression startImage) {
        setValueExpression("startImage", startImage);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStopImage(ValueExpression stopImage) {
        setValueExpression("stopImage", stopImage);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRenderId(ValueExpression renderId) {
        setValueExpression("renderId", renderId);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setDelay(ValueExpression delay) {
        setValueExpression("delay", delay);
    }
}
