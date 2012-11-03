/*
 * $Id: ManagedBeanContainer.java,v 1.2 2007/10/24 04:40:43 daniel Exp $
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

import org.operamasks.faces.config.ManagedBeanConfig;
import org.operamasks.faces.application.impl.DefaultManagedBeanContainer;
import javax.servlet.ServletContext;
import javax.faces.context.FacesContext;
import java.util.Collection;

/**
 * This class contains centre registry for managed beans.
 */
public abstract class ManagedBeanContainer
{
    public static final String KEY = ManagedBeanContainer.class.getName();

    /**
     * Returns a singleton instance of ManagedBeanContainer.
     */
    public static final ManagedBeanContainer getInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        return assoc.getSingleton(KEY, DefaultManagedBeanContainer.class);
    }

    /**
     * Returns a singleton instance of ManagedBeanContainer.
     */
    public static final ManagedBeanContainer getInstance(ServletContext context) {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance(context);
        return assoc.getSingleton(KEY, DefaultManagedBeanContainer.class);
    }

    /**
     * Add a managed bean namespace.
     *
     * @param fqn the fully qualified managed bean namespace
     */
    public abstract void addNamespace(String fqn);

    /**
     * Find a managed bean namespace.
     *
     * @param name the namespace
     */
    public abstract ManagedBeanNamespace getNamespace(String name);

    /**
     * Adds a new managed bean configuration.
     *
     * @param config the managed bean configuration.
     * @return the managed bean factory
     */
    public abstract ManagedBeanFactory addBeanFactory(ManagedBeanConfig config);

    /**
     * Add a new managed bean factory.
     *
     * @param factory the managed bean factory
     */
    public abstract void addBeanFactory(ManagedBeanFactory factory);

    /**
     * Remove a managed bean.
     *
     * @param name the managed bean name
     */
    public abstract void removeBeanFactory(String name);

    /**
     * Returns the collection of all managed bean factories.
     */
    public abstract Collection<ManagedBeanFactory> getBeanFactories();

    /**
     * Checks whether the given managed bean exist.
     *
     * @param name the managed bean name
     */
    public abstract boolean containsBeanFactory(String name);

    /**
     * Returns the managed bean factory by name.
     *
     * @param name the managed bean name.
     */
    public abstract ManagedBeanFactory getBeanFactory(String name);

    /**
     * Get the managed bean instance from scope, or create the bean
     * and store it in the scope if it doesn't exist.
     *
     * @param context the faces context
     * @param name the managed bean name.
     * @return the managed bean instance
     */
    public abstract Object getBean(FacesContext context, String name);
}
