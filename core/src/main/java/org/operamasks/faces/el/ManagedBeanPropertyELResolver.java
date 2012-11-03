/*
 * $Id: ManagedBeanPropertyELResolver.java,v 1.10 2008/01/03 10:18:56 daniel Exp $
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import javax.el.ELException;
import org.operamasks.el.resolver.BeanPropertyELResolver;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.annotation.Accessible;

public class ManagedBeanPropertyELResolver extends BeanPropertyELResolver
{
    @Override
    protected boolean fieldAccessible(Class baseClass, Field f) {
        if (Modifier.isPublic(f.getModifiers())) {
            return true;
        }

        if (getBeanProperty(baseClass, f.getName()) != null) {
            return false;
        }

        Accessible acc = f.getAnnotation(Accessible.class);
        if (acc != null) {
            return acc.value();
        }

        for (Annotation a : f.getAnnotations()) {
            Class<? extends Annotation> at = a.annotationType();
            if (at.isAnnotationPresent(Accessible.class)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Object getFieldValue(Field field, Object base) {
        return ModelBean.wrap(base).getField(field);
    }

    @Override
    protected void setFieldValue(Field field, Object base, Object value) {
        ModelBean.wrap(base).setField(field, value);
    }

    @Override
    protected Object getPropertyValue(Method method, Object base) {
        try {
            return method.invoke(base);
        } catch (ELException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ELException(ex);
        }
    }

    protected void setPropertyValue(Method method, Object base, Object value) {
        try {
            method.invoke(base, value);
        } catch (ELException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ELException(ex);
        }
    }
}
