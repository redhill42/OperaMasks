/*
 * $Id: BindingUtils.java,v 1.14 2008/03/05 12:50:40 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Iterator;

import org.operamasks.el.eval.Coercion;
import static org.operamasks.faces.util.FacesUtils.*;

public final class BindingUtils
{
    public static final Method getInterfaceMethod(Method method) {
        Class declClass = method.getDeclaringClass();
        if (declClass.isInterface()) {
            return method;
        }

        for (Class c : declClass.getInterfaces()) {
            try {
                return c.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException ex) {}
        }

        return method;
    }

    public static final Method getReadMethod(Class targetClass, String name, Class type) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        if (type == Boolean.TYPE) {
            try {
                return targetClass.getMethod("is" + name);
            } catch (NoSuchMethodException ex) {}
        }

        try {
            return targetClass.getMethod("get" + name);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static final Method getWriteMethod(Class targetClass, String name, Class type) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        try {
            return targetClass.getMethod("set" + name, type);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static boolean isBindingPresent(Class targetClass, Method method, Class<? extends Annotation> at) {
        // check for @Bind annotation for the given method
        if (method.isAnnotationPresent(at)) {
            return true;
        }

        // check for super classes
        for (Class c = targetClass.getSuperclass(); c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
                if (m.isAnnotationPresent(at)) {
                    return true;
                }
            } catch (Exception ex) {}
        }

        return false;
    }

    public static UIComponent findLabelComponent(FacesContext ctx, UIComponent from) {
        UIComponent parent = from;
        while (parent != null) {
            if (parent instanceof NamingContainer) {
                break;
            }
            parent = parent.getParent();
        }
        if (parent == null) {
            parent = ctx.getViewRoot();
        }

        Iterator<UIComponent> kids = createChildrenIterator(parent, false);
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if (isLabelFor(ctx, kid, from)) {
                return kid;
            }
        }
        return null;
    }

    public static UIComponent findLabelFacet(FacesContext ctx, UIComponent from, boolean create) {
        UIComponent parent = from;
        while (parent != null) {
            if (parent instanceof UIColumn) {
                break;
            }
            parent = parent.getParent();
        }
        if (parent == null) {
            return null;
        }

        if (parent.getFacetCount() > 0) {
            for (UIComponent facet : parent.getFacets().values()) {
                if (isLabelFor(ctx, facet, from)) {
                    return facet;
                }
                if (facet.getChildCount() > 0) {
                    Iterator<UIComponent> kids = createChildrenIterator(facet, false);
                    while (kids.hasNext()) {
                        UIComponent kid = kids.next();
                        if (isLabelFor(ctx, kid, from)) {
                            return kid;
                        }
                    }
                }
            }
        }

        if (create && parent.getFacets().get("header") == null) {
            UIComponent header = ctx.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
            header.setTransient(true);
            parent.getFacets().put("header", header);
            return header;
        }

        return null;
    }

    private static boolean isLabelFor(FacesContext ctx, UIComponent label, UIComponent comp) {
        if ("javax.faces.Output".equals(label.getFamily())) {
            String forId = (String)label.getAttributes().get("for");
            if ((forId != null) && (forId.equals(comp.getId()) || forId.equals(comp.getClientId(ctx)))) {
                return true;
            }
        }
        return false;
    }

    public static ValueExpression createValueWrapper(Object scope, String expression, Class<?> expectedType) {
        Object value;
        if (isValueExpression(expression)) {
            value = createValueExpression(scope, expression, expectedType);
        } else if (expectedType == String.class || expectedType == Object.class) {
            value = expression;
        } else {
            value = Coercion.coerce(expression, expectedType);
        }
        return new ValueWrapper(value, expectedType);
    }

    private BindingUtils() {}
}
