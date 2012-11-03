/*
 * $Id: PropertyResolverChainWrapper.java,v 1.4 2007/07/02 07:38:17 jacky Exp $
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

import javax.el.ELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.EvaluationException;
import java.util.List;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

import org.operamasks.el.eval.Coercion;

@SuppressWarnings("deprecation")
public class PropertyResolverChainWrapper extends ELResolver
{
    private PropertyResolver legacyResolver;

    public PropertyResolverChainWrapper(PropertyResolver legacyResolver) {
        this.legacyResolver = legacyResolver;
    }

    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null || property == null)
            return null;

        try {
            context.setPropertyResolved(true);
            if (base instanceof List || base.getClass().isArray()) {
                int index = Coercion.coerceToInt(property);
                return legacyResolver.getValue(base, index);
            } else {
                return legacyResolver.getValue(base, property);
            }
        } catch (EvaluationException ex) {
            context.setPropertyResolved(false);
            throw new ELException(ex);
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null || property == null)
            return null;

        try {
            context.setPropertyResolved(true);
            if (base instanceof List || base.getClass().isArray()) {
                int index = Coercion.coerceToInt(property);
                return legacyResolver.getType(base, index);
            } else {
                return legacyResolver.getType(base, property);
            }
        } catch (EvaluationException ex) {
            context.setPropertyResolved(false);
            throw new ELException(ex);
        }
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (base == null || property == null)
            return;

        try {
            context.setPropertyResolved(true);
            if (base instanceof List || base.getClass().isArray()) {
                int index = Coercion.coerceToInt(property);
                legacyResolver.setValue(base, index, value);
            } else {
                legacyResolver.setValue(base, property, value);
            }
        } catch (EvaluationException ex) {
            context.setPropertyResolved(false);
            throw new ELException(ex);
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null || property == null)
            return false;

        try {
            context.setPropertyResolved(true);
            if (base instanceof List || base.getClass().isArray()) {
                int index = Coercion.coerceToInt(property);
                return legacyResolver.isReadOnly(base, index);
            } else {
                return legacyResolver.isReadOnly(base, property);
            }
        } catch (EvaluationException ex) {
            context.setPropertyResolved(false);
            throw new ELException(ex);
        }
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public Class getCommonPropertyType(ELContext context, Object base) {
        if (base == null)
            return Object.class;
        return null;
    }
}
