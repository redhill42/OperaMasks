/*
 * $Id: ProgressBarTag.java,v 1.5 2007/07/02 07:38:00 jacky Exp $
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
import org.operamasks.faces.component.widget.UIProgressBar;

/**
 * @jsp.tag name="progressBar" body-content="JSP"
 */
public class ProgressBarTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.widget.ProgressBar";
    }

    public String getComponentType() {
        return UIProgressBar.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="javax.faces.convert.Converter"
     */
    public void setConverter(ValueExpression converter) {
        setValueExpression("converter", converter);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setMinimum(ValueExpression minimum) {
        setValueExpression("minimum", minimum);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setMaximum(ValueExpression maximum) {
        setValueExpression("maximum", maximum);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
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
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }
}
