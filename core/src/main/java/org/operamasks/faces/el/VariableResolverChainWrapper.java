/*
 * $Id: VariableResolverChainWrapper.java,v 1.5 2007/08/08 23:48:38 jacky Exp $
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
import javax.el.PropertyNotWritableException;
import javax.faces.el.VariableResolver;
import javax.faces.el.EvaluationException;
import javax.faces.context.FacesContext;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

@SuppressWarnings("deprecation")
public class VariableResolverChainWrapper extends ELResolver
{
    private VariableResolver legacyResolver;

    public VariableResolverChainWrapper(VariableResolver legacyResolver) {
        this.legacyResolver = legacyResolver;
    }

    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null) {
            try {
                FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
                Object value = legacyResolver.resolveVariable(facesCtx, property.toString());
                context.setPropertyResolved(value!=null);
                return value;
            } catch (Throwable ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }
        }
        return null;
    }

    public Class getType(ELContext context, Object base, Object property)
        throws ELException
    {
        Object result = getValue(context, base, property);
        if (result != null)
            return result.getClass();
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (base == null && property != null) {
            context.setPropertyResolved(true);
            throw new PropertyNotWritableException();
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;
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
