/*
 * $Id: CellTag.java,v 1.3 2007/07/02 07:38:00 jacky Exp $
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

import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.html.HtmlGridCell;

/**
 * @jsp.tag name="cell" body-content="JSP"
 */
public class CellTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return HtmlGridCell.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setRowspan(ValueExpression rowspan) {
        setValueExpression("rowspan", rowspan);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setColspan(ValueExpression colspan) {
        setValueExpression("colspan", colspan);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAlign(ValueExpression align) {
        setValueExpression("align", align);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setValign(ValueExpression valign) {
        setValueExpression("valign", valign);
    }
}
