/*
 * $Id: PagingLinkTag.java,v 1.7 2008/03/24 00:49:05 lishaochuan Exp $
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
import org.operamasks.faces.component.widget.UIPager;

/**
 * @jsp.tag name="pagingLink" body-content="JSP"
 */
public class PagingLinkTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UIPager.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return "javax.faces.Link";
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute required="true" type="java.lang.String"
     */
    public void setFor(ValueExpression for_) {
        setValueExpression("for", for_);
    }

    /**
     * @jsp.attribute required="false" type="int"
     */
    public void setStart(ValueExpression start) {
        setValueExpression("start", start);
    }

    /**
     * @jsp.attribute required="false" type="int"
     */
    public void setPageSize(ValueExpression pageSize) {
        setValueExpression("pageSize", pageSize);
    }

    /**
     * @jsp.attribute required="false" type="int"
     */
    public void setShownNumbers(ValueExpression shownNumbers) {
        setValueExpression("shownNumbers", shownNumbers);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setShowFirst(ValueExpression showFirst) {
        setValueExpression("showFirst", showFirst);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setShowLast(ValueExpression showLast) {
        setValueExpression("showLast", showLast);
    }
    
//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyle(ValueExpression style) {
//        setValueExpression("style", style);
//    }

//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyleClass(ValueExpression styleClass) {
//        setValueExpression("styleClass", styleClass);
//    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTheme(ValueExpression theme) {
        setValueExpression("theme", theme);
    }
}
