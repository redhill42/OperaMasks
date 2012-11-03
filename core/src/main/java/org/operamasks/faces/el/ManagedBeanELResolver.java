/*
 * $Id: ManagedBeanELResolver.java,v 1.16 2007/12/26 21:13:21 daniel Exp $
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
import javax.faces.context.FacesContext;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;

import org.operamasks.faces.application.ManagedBeanFactory;
import org.operamasks.faces.application.ManagedBeanNamespace;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.config.ManagedBeanConfig;

public class ManagedBeanELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property) {
        if (property == null) {
            return null;
        }
        
        String key = property.toString();
        if (base != null) {
            if (base instanceof ManagedBeanNamespace) {
                key = ((ManagedBeanNamespace)base).getFQN() + "." + key;
            } else {
                return null;
            }
        }

        FacesContext fctx = (FacesContext)context.getContext(FacesContext.class);
        ManagedBeanContainer mbcon = ManagedBeanContainer.getInstance();

        Object bean = mbcon.getBean(fctx, key);
        if (bean != null) {
            context.setPropertyResolved(true);
            return bean;
        }

        ManagedBeanNamespace namespace = mbcon.getNamespace(key);
        if (namespace != null) {
            context.setPropertyResolved(true);
            return namespace;
        }

        return null;
    }

    public Class getType(ELContext context, Object base, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        // fallthrough to ScopedAttributeELResolver
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base != null) {
            return null;
        }

        List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();
        for (ManagedBeanFactory mbean : ManagedBeanContainer.getInstance().getBeanFactories()) {
            FeatureDescriptor feat = new FeatureDescriptor();
            ManagedBeanConfig config = mbean.getConfig();
            if (config != null) {
                feat.setName(config.getManagedBeanName());
                feat.setDisplayName(config.getDisplayName());
                feat.setShortDescription(config.getDescription());
            } else {
                feat.setName(mbean.getBeanName());
                feat.setDisplayName(mbean.getBeanName());
            }
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setPreferred(true);
            feat.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
            feat.setValue(TYPE, findClass(mbean.getBeanClassName()));
            feats.add(feat);
        }
        return feats.iterator();
    }

    private Class<?> findClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (Exception ex) {
            return java.lang.Object.class; // FIXME
        }
    }

    public Class getCommonPropertyType(ELContext context, Object base) {
        if (base == null)
            return Object.class;
        return null;
    }
}
