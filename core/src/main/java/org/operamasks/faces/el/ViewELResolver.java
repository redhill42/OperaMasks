/*
 * $Id: ViewELResolver.java,v 1.6 2008/03/10 08:35:18 lishaochuan Exp $
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
package org.operamasks.faces.el;

import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.util.Iterator;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.binding.impl.ValueWrapper;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;

public class ViewELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null || !(property instanceof String)) {
            return null;
        }

        String prop = (String)property;

        if (base instanceof UIViewRoot) {
            UIComponent comp = getComponentById((UIViewRoot)base, prop);
            if (comp != null) {
                context.setPropertyResolved(true);
                return comp;
            }
        }

        if (base instanceof UIComponent) {
            UIComponent comp = (UIComponent)base;
            if (comp.getAttributes().containsKey(prop)) {
                context.setPropertyResolved(true);
                return comp.getAttributes().get(prop);
            }
        }

        // fallthrough to BeanELResolver
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null || !(property instanceof String)) {
            return;
        }

        String prop = (String)property;

        if (base instanceof UIViewRoot) {
            UIComponent comp = getComponentById((UIViewRoot)base, prop);
            if (comp != null) {
                throw new PropertyNotWritableException();
            }
        }

        if (base instanceof UIComponent) {
            UIComponent comp = (UIComponent)base;

            Class<?> type = Object.class;
            try {
                // convert to appropriate type if the component attribute has a getter method.
                BeanProperty p = BeanUtils.getProperty(comp.getClass(), prop);
                if (p != null) {
                    type = p.getType();
                    value = Coercion.coerce(value, type);
                }
            } catch (IntrospectionException ex) {}

            // set attribute value through value expression binding for AJAX rendering.
            ValueExpression ve = comp.getValueExpression(prop);
            if (ve != null) {
                ve.setValue(context, value);
                context.setPropertyResolved(true);
            } else {
                ve = new ValueWrapper(value, type);
                comp.setValueExpression(prop, ve);
                context.setPropertyResolved(true);
            }
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base == null || !(property instanceof String)) {
            return null;
        }

        String prop = (String)property;

        if (base instanceof UIViewRoot) {
            UIComponent comp = getComponentById((UIViewRoot)base, prop);
            if (comp != null) {
                context.setPropertyResolved(true);
                return comp.getClass();
            }
        }

        // fallthrough to BeanELResolver
        return null;
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (base == null || property == null) {
            return false;
        }

        String prop = (String)property;

        if (base instanceof UIViewRoot) {
            UIComponent comp = getComponentById((UIViewRoot)base, prop);
            if (comp != null) {
                context.setPropertyResolved(true);
                return true;
            }
        }

        // fallthrough to BeanELResolver
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof UIComponent) {
            return String.class;
        }
        return null;
    }

    private static UIComponent getComponentById(UIViewRoot view, String id) {
        return FacesUtils.getForComponent(id, view);
    }
}
