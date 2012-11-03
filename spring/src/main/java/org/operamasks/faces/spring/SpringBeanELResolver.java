/*
 * $Id: SpringBeanELResolver.java,v 1.5 2007/12/04 12:45:34 daniel Exp $
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

package org.operamasks.faces.spring;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELResolver;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.jsf.FacesContextUtils;
import org.operamasks.faces.application.ManagedBeanContainer;

class SpringBeanELResolver extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null || property == null) {
            return null;
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        BeanFactory bf = FacesContextUtils.getWebApplicationContext(fc);

        if (bf != null) {
            String key = property.toString();

            if (key.equals("webApplicationContext")) {
                context.setPropertyResolved(true);
                return bf;
            }

            // Resolve bean that only managed by spring container. The beans
            // that managed by spring and JSF are resolved by JSF container.
            ManagedBeanContainer bc = ManagedBeanContainer.getInstance();
            if (bf.containsBean(key) && !bc.containsBeanFactory(key)) {
                context.setPropertyResolved(true);
                return bf.getBean(key);
            }
        }

        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        // do nothing
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }
}
