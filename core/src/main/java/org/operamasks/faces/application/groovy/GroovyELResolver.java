/*
 * $Id: GroovyELResolver.java,v 1.2 2007/10/23 08:20:30 daniel Exp $
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

package org.operamasks.faces.application.groovy;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import javax.el.ELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;

import groovy.lang.GroovyObject;
import groovy.lang.MissingPropertyException;
import groovy.lang.MetaProperty;

class GroovyELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (!(base instanceof GroovyObject) || (property == null)) {
            return null;
        }

        try {
            GroovyObject target = (GroovyObject)base;
            Object value = target.getProperty(property.toString());
            context.setPropertyResolved(true);
            return value;
        } catch (MissingPropertyException ex) {
            throw new PropertyNotFoundException(ex);
        }
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (!(base instanceof GroovyObject) || (property == null)) {
            return;
        }

        try {
            GroovyObject target = (GroovyObject)base;
            target.setProperty(property.toString(), value);
            context.setPropertyResolved(true);
        } catch (MissingPropertyException ex) {
            throw new PropertyNotFoundException(ex);
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (!(base instanceof GroovyObject) || (property == null)) {
            return null;
        }

        GroovyObject target = (GroovyObject)base;
        MetaProperty mp = target.getMetaClass().getMetaProperty(property.toString());
        if (mp != null) {
            context.setPropertyResolved(true);
            return mp.getType();
        }

        try {
            Object value = target.getProperty(property.toString());
            context.setPropertyResolved(true);
            return (value == null) ? null : value.getClass();
        } catch (MissingPropertyException ex) {
            throw new PropertyNotFoundException(ex);
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (!(base instanceof GroovyObject)) {
            return null;
        }

        List properties = ((GroovyObject)base).getMetaClass().getProperties();
        List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(properties.size());
        for (Iterator it = properties.iterator(); it.hasNext(); ) {
            MetaProperty mp = (MetaProperty)it.next();
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setName(mp.getName());
            feat.setDisplayName(mp.getName());
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, mp.getType());
            list.add(feat);
        }
        return list.iterator();
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null)
            return null;
        return Object.class;
    }
}
