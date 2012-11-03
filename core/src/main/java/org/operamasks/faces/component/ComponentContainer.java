/*
 * $Id 
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
package org.operamasks.faces.component;

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.servlet.ServletContext;

import org.operamasks.faces.application.ApplicationAssociate;

public abstract class ComponentContainer
{

    public static final String KEY = ComponentContainer.class.getName();

    /**
     * Returns a singleton instance of ComponentContainer.
     */
    public static final ComponentContainer getInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        return assoc.getSingleton(KEY, DefaultComponentContainer.class);
    }

    /**
     * Returns a singleton instance of ComponentContainer.
     */
    public static final ComponentContainer getInstance(ServletContext context) {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance(context);
        return assoc.getSingleton(KEY, DefaultComponentContainer.class);
    }

//    /**
//     * Adds a new component configuration.
//     *
//     * @param config the component configuration.
//     * @return the component factory
//     */
//    public abstract ComponentFactory addComponentFactory(ComponentConfig config);

    /**
     * Add a new component factory.
     *
     * @param factory the managed bean factory
     */
    public abstract void addComponentFactory(ComponentFactory factory);

    /**
     * Remove a component factory.
     *
     * @param name the component factory name
     */
    public abstract void removeComponentFactory(String componentType);

    /**
     * Returns the collection of all component types.
     */
    public abstract Collection<String> getComponentTypes();

    /**
     * Returns the collection of all component factories.
     */
    public abstract Collection<ComponentFactory> getComponentFactories();

    /**
     * Checks whether the given component exist.
     *
     * @param componentType the component type
     */
    public abstract boolean containsComponentFactory(String componentType);

    /**
     * Returns the component factory by name.
     *
     * @param componentType the component type
     */
    public abstract ComponentFactory getComponentFactory(String componentType);

    public abstract ComponentFactory getComponentFactoryByName(String compClassName);

    public abstract ComponentFactory getComponentFactoryByClass(Class<? extends UIComponent> compClass);

}
