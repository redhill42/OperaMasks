/*
 * $Id: ManagedBeanFactory.java,v 1.16 2007/12/10 06:55:33 jacky Exp $
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

package org.operamasks.faces.application;

import javax.faces.context.FacesContext;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.config.ManagedBeanConfig;

public interface ManagedBeanFactory
{
    /**
     * Get the managed bean configuration information, or null for
     * non-standard managed bean.
     */
    public ManagedBeanConfig getConfig();

    /**
     * Get the managed bean name.
     */
    public String getBeanName();

    /**
     * Get the managed bean class name.
     */
    public String getBeanClassName();

    /**
     * Get the managed bean scope.
     */
    public ManagedBeanScope getScope();

    /**
     * Attempt to instantiate the managed bean and set its properties.
     */
    public Object createBean(FacesContext context);

    /**
     * Attempt to destroy the managed bean
     */
    public void destroyBean(Object bean);

    /**
     * Determines whether the given bean is an instance that created by this factory.
     */
    public boolean isInstance(Object bean);
    
    /**
     * Get the class loader of the factory inused.
     */
    public ClassLoader getClassLoader();
}
