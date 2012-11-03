/*
 * $Id: ImplicitObjectELResolverForJsp.java,v 1.4 2007/07/02 07:38:17 jacky Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.beans.FeatureDescriptor;

public class ImplicitObjectELResolverForJsp extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            if ("facesContext".equals(property)) {
                context.setPropertyResolved(true);
                return facesCtx;
            }
            if ("view".equals(property)) {
                context.setPropertyResolved(true);
                return facesCtx.getViewRoot();
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null) {
            if ("facesContext".equals(property) || "view".equals(property)) {
                throw new PropertyNotWritableException();
            }
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null) {
            if ("facesContext".equals(property) || "view".equals(property)) {
                context.setPropertyResolved(true);
            }
        }
        return null;
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null && property != null) {
            String var = property.toString();
            if (var.equals("facesContext") || var.equals("view")) {
                context.setPropertyResolved(true);
                return true;
            }
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>(2);

        FeatureDescriptor feat = new FeatureDescriptor();
        feat.setName("facesContext");
        feat.setDisplayName("facesContext");
        feat.setExpert(false);
        feat.setHidden(false);
        feat.setPreferred(true);
        feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
        feat.setValue(TYPE, FacesContext.class);
        feats.add(feat);

        feat = new FeatureDescriptor();
        feat.setName("view");
        feat.setDisplayName("view");
        feat.setExpert(false);
        feat.setHidden(false);
        feat.setPreferred(true);
        feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
        feat.setValue(TYPE, UIViewRoot.class);
        feats.add(feat);

        return feats.iterator();
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }
}
