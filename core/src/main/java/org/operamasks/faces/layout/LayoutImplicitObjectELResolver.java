/*
 * $Id: LayoutImplicitObjectELResolver.java,v 1.5 2007/12/04 12:45:56 daniel Exp $
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

package org.operamasks.faces.layout;

import javax.el.ELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class LayoutImplicitObjectELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null && property != null) {
            if ("layout".equals(property)) {
                FacesContext fc = FacesContext.getCurrentInstance();
                if (fc == null) {
                    return null;
                }

                LayoutContext lc = LayoutContext.getCurrentInstance(fc);
                if (lc == null) {
                    return null;
                }

                context.setPropertyResolved(true);
                return lc;
            }
        }

        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null) {
            if ("layout".equals(property)) {
                throw new PropertyNotWritableException();
            }
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null) {
            if ("layout".equals(property)) {
                context.setPropertyResolved(true);
            }
        }

        return null;
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base == null && property != null) {
            if ("layout".equals(property)) {
                context.setPropertyResolved(true);
                return true;
            }
        }

        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>(1);

        FeatureDescriptor feat = new FeatureDescriptor();
        feat.setName("layout");
        feat.setDisplayName("Layout Context");
        feat.setExpert(false);
        feat.setHidden(false);
        feat.setPreferred(true);
        feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
        feat.setValue(TYPE, LayoutContext.class);
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
