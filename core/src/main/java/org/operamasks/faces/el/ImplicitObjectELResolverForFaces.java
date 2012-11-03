/*
 * $Id: ImplicitObjectELResolverForFaces.java,v 1.4 2007/07/02 07:38:17 jacky Exp $
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
import javax.faces.context.ExternalContext;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;

public class ImplicitObjectELResolverForFaces extends ELResolver
{
    private static final String[] IMPLICIT_OBJECTS = {
        "application", "applicationScope", "cookie", "facesContext", "header", "headerValues",
        "initParam", "param", "paramValues", "request", "requestScope", "session", "sessionScope",
        "view"
    };

    private static final Class<?>[] IMPLICIT_OBJECT_TYPES = {
        java.lang.Object.class,  // application
        java.util.Map.class,     // applicationScope
        java.util.Map.class,     // cookie
        javax.faces.context.FacesContext.class, // facesContext
        java.util.Map.class,     // header
        java.util.Map.class,     // headerValues
        java.util.Map.class,     // initParam
        java.util.Map.class,     // param
        java.util.Map.class,     // paramValues
        java.lang.Object.class,  // request
        java.util.Map.class,     // requestScope
        java.lang.Object.class,  // session
        java.util.Map.class,     // sessionScope
        javax.faces.component.UIViewRoot.class, // view
    };

    private static final int
        APPLICATION = 0,
        APPLICATION_SCOPE = 1,
        COOKIE = 2,
        FACES_CONTEXT = 3,
        HEADER = 4,
        HEADER_VALUES = 5,
        INIT_PARAM  = 6,
        PARAM = 7,
        PARAM_VALUES = 8,
        REQUEST = 9,
        REQUEST_SCOPE = 10,
        SESSION = 11,
        SESSION_SCOPE = 12,
        VIEW = 13;

    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null && property != null) {
            int idx = Arrays.binarySearch(IMPLICIT_OBJECTS, property.toString());
            if (idx >= 0) {
                FacesContext facesContext = (FacesContext)context.getContext(FacesContext.class);
                ExternalContext extCtx = facesContext.getExternalContext();
                context.setPropertyResolved(true);

                switch (idx) {
                case APPLICATION:
                    return extCtx.getContext();
                case APPLICATION_SCOPE:
                    return extCtx.getApplicationMap();
                case COOKIE:
                    return extCtx.getRequestCookieMap();
                case FACES_CONTEXT:
                    return facesContext;
                case HEADER:
                    return extCtx.getRequestHeaderMap();
                case HEADER_VALUES:
                    return extCtx.getRequestHeaderValuesMap();
                case INIT_PARAM:
                    return extCtx.getInitParameterMap();
                case PARAM:
                    return extCtx.getRequestParameterMap();
                case PARAM_VALUES:
                    return extCtx.getRequestParameterValuesMap();
                case REQUEST:
                    return extCtx.getRequest();
                case REQUEST_SCOPE:
                    return extCtx.getRequestMap();
                case SESSION:
                    return extCtx.getSession(true);
                case SESSION_SCOPE:
                    return extCtx.getSessionMap();
                case VIEW:
                    return facesContext.getViewRoot();
                }
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null && property != null) {
            int idx = Arrays.binarySearch(IMPLICIT_OBJECTS, property.toString());
            if (idx >= 0) {
                throw new PropertyNotWritableException();
            }
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property)
        throws ELException
    {
        if (context == null)
            throw new NullPointerException();

        if (base == null && property != null) {
            int idx = Arrays.binarySearch(IMPLICIT_OBJECTS, property.toString());
            if (idx >= 0) {
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
            int idx = Arrays.binarySearch(IMPLICIT_OBJECTS, property.toString());
            if (idx >= 0) {
                context.setPropertyResolved(true);
                return true;
            }
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>(IMPLICIT_OBJECTS.length);
        for (int i = 0; i < IMPLICIT_OBJECTS.length; i++) {
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setDisplayName(IMPLICIT_OBJECTS[i]);
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setName(IMPLICIT_OBJECTS[i]);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, IMPLICIT_OBJECT_TYPES[i]);
            feats.add(feat);
        }
        return feats.iterator();
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }
}
