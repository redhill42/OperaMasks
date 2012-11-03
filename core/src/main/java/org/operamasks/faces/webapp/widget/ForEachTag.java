/*
 * $Id: ForEachTag.java,v 1.1 2007/07/16 04:28:38 jacky Exp $
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
 * 
 */
package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import org.operamasks.faces.component.widget.invisible.ForEach;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="forEach" body-content="JSP"
 */

public class ForEachTag extends HtmlBasicELTag
{
    private String var;

    @Override
    public String getComponentType() {
        return ForEach.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return ForEach.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setFirst(ValueExpression first) {
        setValueExpression("first", first);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setRows(ValueExpression rows) {
        setValueExpression("rows", rows);
    }
    
    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setStep(ValueExpression step) {
        setValueExpression("step", step);
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
    public void setIndexVar(ValueExpression indexVar) {
        setValueExpression("indexVar", indexVar);
    }

    /**
     * @jsp.attribute
     */
    public void setVar(String var) {
        this.var = var;
    }
    

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIData data = (UIData)component;
        data.setVar(var);
    }

    public void release() {
        super.release();
        var = null;
    }
}
