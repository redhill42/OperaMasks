/*
 * $Id: ScopedAttributeELResolver.java,v 1.6 2007/11/16 20:34:19 daniel Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.beans.FeatureDescriptor;

public class ScopedAttributeELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            ExternalContext extCtx = facesCtx.getExternalContext();
            String key = property.toString();
            Object result;

            result = extCtx.getRequestMap().get(key);
            if (result != null) {
                context.setPropertyResolved(true);
                return result;
            }

            result = extCtx.getSessionMap().get(key);
            if (result != null) {
                context.setPropertyResolved(true);
                return result;
            }

            result = extCtx.getApplicationMap().get(key);
            if (result != null) {
                context.setPropertyResolved(true);
                return result;
            }
        }

        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null) {
            context.setPropertyResolved(true);
            return Object.class;
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            ExternalContext extCtx = facesCtx.getExternalContext();
            String key = property.toString();

            if (extCtx.getRequestMap().get(key) != null) {
                context.setPropertyResolved(true);
                extCtx.getRequestMap().put(key, value);
            } else if (extCtx.getSessionMap().get(key) != null) {
                context.setPropertyResolved(true);
                extCtx.getSessionMap().put(key, value);
            } else if (extCtx.getApplicationMap().get(key) != null) {
                context.setPropertyResolved(true);
                extCtx.getApplicationMap().put(key, value);
            }
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null)
            context.setPropertyResolved(true);
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
        ExternalContext extCtx = facesCtx.getExternalContext();
        List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();

        for (Map.Entry<String,Object> e : extCtx.getRequestMap().entrySet()) {
            String name = e.getKey();
            Object value = e.getValue();
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setName(name);
            feat.setDisplayName(name);
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, value.getClass());
            feats.add(feat);
        }

        for (Map.Entry<String,Object> e: extCtx.getSessionMap().entrySet()) {
            String name = e.getKey();
            Object value = e.getValue();
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setName(name);
            feat.setDisplayName(name);
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, value.getClass());
            feats.add(feat);
        }

        for (Map.Entry<String,Object> e: extCtx.getApplicationMap().entrySet()) {
            String name = e.getKey();
            Object value = e.getValue();
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setName(name);
            feat.setDisplayName(name);
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, value.getClass());
            feats.add(feat);
        }

        return feats.iterator();
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null)
            return Object.class;
        return null;
    }
}
