/*
 * $Id: FacesResourceBundleELResolver.java,v 1.5 2007/09/11 12:50:48 daniel Exp $
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
import javax.faces.application.Application;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Iterator;
import java.beans.FeatureDescriptor;
import org.operamasks.faces.application.ApplicationAssociate;

public class FacesResourceBundleELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            Application app = facesCtx.getApplication();
            ResourceBundle result = app.getResourceBundle(facesCtx, property.toString());
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
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            Application app = facesCtx.getApplication();
            if (app.getResourceBundle(facesCtx, property.toString()) != null) {
                context.setPropertyResolved(true);
                return ResourceBundle.class;
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            Application app = facesCtx.getApplication();
            if (app.getResourceBundle(facesCtx, property.toString()) != null) {
                throw new PropertyNotWritableException();
            }
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        if (base == null && property != null) {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            Application app = facesCtx.getApplication();
            if (app.getResourceBundle(facesCtx, property.toString()) != null) {
                context.setPropertyResolved(true);
                return true;
            }
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base != null)
            return null;

        FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
        ApplicationAssociate associate = ApplicationAssociate.getInstance(facesCtx);
        ArrayList<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();

        for (String var : associate.getResourceBundles()) {
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setName(var);
            feat.setDisplayName(associate.getResourceBundleDisplayName(var));
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, ResourceBundle.class);
            feats.add(feat);
        }
        return feats.iterator();
    }

    public Class getCommonPropertyType(ELContext context, Object base) {
        if (base == null)
            return String.class;
        return null;
    }
}
