/*
 * $Id: HtmlBasicELTag.java,v 1.7 2008/04/21 02:36:14 lishaochuan Exp $
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

package org.operamasks.faces.webapp.html;

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @jsp.attribute name="id" required = "false" rtexprvalue = "true"
 * description="The component identifier for this component.  This value must be
 *       unique within the closest parent component that is a naming
 *       container."
 * description_zh_CN="这个组件的组件标识符。这个值在最近的命名容器类型的父组件范围内，必须是唯一的。"
 *   
 * @jsp.attribute name="rendered" required = "false" type = "boolean"
 * description="TFlag indicating whether or not this component should be rendered
 *       (during Render Response Phase), or processed on any subsequent
 *       form submit.  The default value for this property is true."
 * description_zh_CN="一个标志，指出该组件是否要在任何随后的form提交过程中被渲染或处理。
 *       这个属性的缺省值是true。"
 * 
 * @jsp.attribute name="binding" required = "false" type = "javax.faces.component.UIComponent"
 * description="The ValueExpression linking this component to a property in a backing bean"
 * description_zh_CN="一个值表达式，用于把该组件链接到一个ManagedBean的某个属性。"
 * 
 * @jsp.attribute name="style" required = "false" type = "java.lang.String"
 * description="The css style of component."
 * description_zh_CN="组件的css样式。"
 */
public abstract class HtmlBasicELTag extends UIComponentELTag
{
    private Map<String,ValueExpression> values;

    /**
     * Associate a value expression with a String key.
     *
     * @param key The key String.
     * @param value The value expression to associate.
     */
    public void setValueExpression(String key, ValueExpression value) {
        assert key != null;
        if (value == null) {
            removeValueExpression(key);
        } else {
            if (values == null)
                values = new HashMap<String,ValueExpression>();
            values.put(key, value);
        }
    }

    /**
     * Get the value expression associated with a key.
     *
     * @param key The string key.
     * @return The value expression associated with the key, or null.
     */
    public ValueExpression getValueExpression(String key) {
        assert key != null;
        if (values == null)
            return null;
        return values.get(key);
    }

    /**
     * Remove a value expression associated with a key.
     *
     * @param key the string key.
     */
    public void removeValueExpression(String key) {
        assert key != null;
        if (values != null) {
            values.remove(key);
        }
    }

    /**
     * Returns the map for the value expression kept by this tag handler.
     */
    public Map<String,ValueExpression> getValueExpressions() {
        if (values == null)
            values = new HashMap<String,ValueExpression>();
        return values;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (values != null) {
            for (Map.Entry<String,ValueExpression> e : values.entrySet()) {
                String name = e.getKey();
                ValueExpression value = e.getValue();
                if (value.getExpectedType() == Converter.class) {
                    setConverter(component, name, value);
                } else {
                    component.setValueExpression(name, value);
                }
            }
        }
    }

    protected void setConverter(UIComponent component, String name, ValueExpression converter) {
        if (converter.isLiteralText()) {
            String converterId = converter.getExpressionString();
            FacesContext context = FacesContext.getCurrentInstance();
            Converter conv = context.getApplication().createConverter(converterId);
            component.getAttributes().put(name, conv);
        } else {
            component.setValueExpression(name, converter);
        }
    }

    /**
     * Release state.
     */
    public void release() {
        super.release();
        if (values != null)
            values.clear();
        values = null;
    }
}
