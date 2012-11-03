/*
 * $Id: PropertyResolverAdapter.java,v 1.5 2007/07/02 07:38:16 jacky Exp $
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

import javax.faces.el.PropertyResolver;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.context.FacesContext;
import javax.el.ELResolver;
import java.lang.reflect.Array;
import org.operamasks.el.eval.Coercion;

@SuppressWarnings("deprecation")
public class PropertyResolverAdapter extends PropertyResolver
{
    private ELResolver elResolver;

    public PropertyResolverAdapter(ELResolver elResolver) {
        this.elResolver = elResolver;
    }

    public Object getValue(Object base, Object property)
        throws EvaluationException
    {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            return elResolver.getValue(context.getELContext(), base, property);
        } catch (javax.el.PropertyNotFoundException ex) {
            throw new javax.faces.el.PropertyNotFoundException(ex);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.EvaluationException(ex);
        }
    }

    public Object getValue(Object base, int index)
        throws EvaluationException
    {
        if (base == null)
            return null;

        if (base.getClass().isArray()) {
            try {
                return Array.get(base, index);
            } catch (ArrayIndexOutOfBoundsException ex) {
                return null;
            }
        } else if (base instanceof java.util.List) {
            try {
                return ((java.util.List)base).get(index);
            } catch (ArrayIndexOutOfBoundsException ex) {
                return null;
            }
        } else {
            throw new javax.faces.el.PropertyNotFoundException();
        }
    }

    public void setValue(Object base, Object property, Object value)
        throws EvaluationException
    {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            elResolver.setValue(context.getELContext(), base, property, value);
        } catch (javax.el.PropertyNotFoundException ex) {
            throw new javax.faces.el.PropertyNotFoundException(ex);
        } catch (javax.el.PropertyNotWritableException ex) {
            throw new javax.faces.el.PropertyNotFoundException(ex);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.EvaluationException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object base, int index, Object value)
        throws EvaluationException
    {
        if (base == null)
            throw new javax.faces.el.PropertyNotFoundException();

        Class type = base.getClass();
        if (type.isArray()) {
            try {
                Array.set(base, index, Coercion.coerce(value, type.getComponentType()));
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new javax.faces.el.PropertyNotFoundException(ex);
            }
        } else if (base instanceof java.util.List) {
            try {
                ((java.util.List)base).set(index, value);
            } catch (IndexOutOfBoundsException ex) {
                throw new javax.faces.el.PropertyNotFoundException(ex);
            }
        } else {
            throw new javax.faces.el.PropertyNotFoundException();
        }
    }

    public boolean isReadOnly(Object base, Object property)
        throws EvaluationException
    {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            return elResolver.isReadOnly(context.getELContext(), base, property);
        } catch (javax.el.PropertyNotFoundException ex) {
            throw new javax.faces.el.PropertyNotFoundException(ex);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.EvaluationException(ex);
        }
    }

    public boolean isReadOnly(Object base, int index)
        throws EvaluationException
    {
        if (base == null)
            throw new javax.faces.el.PropertyNotFoundException();

        if ((base instanceof java.util.List) || base.getClass().isArray()) {
            return false;
        } else {
            throw new javax.faces.el.PropertyNotFoundException();
        }
    }

    @SuppressWarnings("unchecked")
    public Class getType(Object base, Object property)
        throws EvaluationException
    {
        if (base == null) {
            throw new PropertyNotFoundException();
        }

        try {
            FacesContext context = FacesContext.getCurrentInstance();
            return elResolver.getType(context.getELContext(), base, property);
        } catch (javax.el.PropertyNotFoundException ex) {
            throw new javax.faces.el.PropertyNotFoundException(ex);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.EvaluationException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public Class getType(Object base, int index)
        throws EvaluationException
    {
        if (base == null)
            throw new PropertyNotFoundException();

        if (base.getClass().isArray()) {
            int length = Array.getLength(base);
            if (index < 0 || index >= length)
                throw new PropertyNotFoundException();
            return base.getClass().getComponentType();
        } else if (base instanceof java.util.List) {
            java.util.List list = (java.util.List)base;
            if (index < 0 || index >= list.size())
                throw new PropertyNotFoundException();
            Object value = list.get(index);
            return (value != null) ? value.getClass() : Object.class;
        } else {
            throw new javax.faces.el.PropertyNotFoundException();
        }
    }
}
