/*
 * $Id: LocalStringAttributeBinding.java,v 1.4 2008/03/10 08:35:18 lishaochuan Exp $
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

package org.operamasks.faces.binding.impl;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;
import java.beans.IntrospectionException;

class LocalStringAttributeBinding extends Binding
{
    private Class<?> declaringClass;
    private String id;
    private String attribute;

    LocalStringAttributeBinding(String viewId) {
        super(viewId);
    }

    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    public void setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        UIComponent comp = mbc.getComponent(id);
        if (comp == null || comp.getValueExpression(attribute) != null) {
            return;
        }

        // Add local string attribute binding only if the component
        // has the property defined for the attribute.
        try {
            BeanProperty p = BeanUtils.getProperty(comp.getClass(), attribute);
            if (p != null) {
                String key = id + "." + attribute;
                String text = FacesUtils.getLocalString(declaringClass, key);
                if (text != null) {
                    ValueExpression ve = BindingUtils.createValueWrapper(
                        mbc.getModelBean(), text, p.getType());
                    comp.setValueExpression(attribute, ve);
                }
            }
        } catch (IntrospectionException ex) {}
    }
}
