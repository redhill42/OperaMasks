/*
 * $Id: FunctionItemTag.java,v 1.3 2007/07/02 07:37:56 jacky Exp $
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.operamasks.faces.component.graph.UIFunctionItem;

/**
 * @jsp.tag name="functionItem" body-content="JSP"
 */
public class FunctionItemTag extends DataItemTag
{
    private MethodExpression expression;

    public String getComponentType() {
        return UIFunctionItem.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setStart(ValueExpression start) {
        setValueExpression("start", start);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setEnd(ValueExpression end) {
        setValueExpression("end", end);
    }

    /**
     * @jsp.attribute type="java.lang.Double"
     */
    public void setStep(ValueExpression step) {
        setValueExpression("step", step);
    }

    /**
     * @jsp.attribute method-signature="double f(double)"
     */
    public void setExpression(MethodExpression expression) {
        this.expression = expression;
    }

    public void setProperties(UIComponent component) {
        super.setProperties(component);
        ((UIFunctionItem)component).setExpression(expression);
    }

    public void release() {
        super.release();
        expression = null;
    }

    public int doStartTag() throws JspException {
        UIComponentClassicTagBase tag = getParentUIComponentClassicTagBase(pageContext);
        if (!(tag instanceof FunctionSeriesTag)) {
            throw new JspTagException("The functionItem tag can only be nested in a functionSeries tag.");
        }
        return super.doStartTag();
    }
}
