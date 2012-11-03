/*
 * $Id: TitleTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.graph.UITitle;

/**
 * @jsp.tag name="title" body-content="JSP"
 */
public class TitleTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UITitle.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setFont(ValueExpression font) {
        setValueExpression("font", font);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setColor(ValueExpression color) {
        setValueExpression("color", color);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setBackgroundColor(ValueExpression backgroundColor) {
        setValueExpression("backgroundColor", backgroundColor);
    }

    /**
     * @jsp.attribute type="org.operamasks.faces.component.graph.PositionType"
     */
    public void setPosition(ValueExpression position) {
        setValueExpression("position", position);
    }
}
