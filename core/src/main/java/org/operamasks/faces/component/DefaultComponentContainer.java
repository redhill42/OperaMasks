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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;

public class DefaultComponentContainer extends ComponentContainer
{
    private Map<String,ComponentFactory> registry;
    
    public DefaultComponentContainer() {
        this.registry = new HashMap<String,ComponentFactory>();
    }
    
//    @Override
//    public ComponentFactory addComponentFactory(ComponentConfig config) {
//        return null;
//    }
//
    @Override
    public void addComponentFactory(ComponentFactory factory) {
        String componentType = factory.getConfig().getComponentType();
        this.registry.put(componentType, factory);
    }

    @Override
    public boolean containsComponentFactory(String componentType) {
        return this.registry.containsKey(componentType);
    }

    @Override
    public Collection<String> getComponentTypes() {
        return Collections.unmodifiableCollection(this.registry.keySet());
    }

    @Override
    public Collection<ComponentFactory> getComponentFactories() {
        return Collections.unmodifiableCollection(this.registry.values());
    }

    @Override
    public ComponentFactory getComponentFactory(String componentType) {
        return this.registry.get(componentType);
    }

    @Override
    public ComponentFactory getComponentFactoryByClass(Class<? extends UIComponent> compClass) {
        for (String type : registry.keySet()) {
            ComponentFactory fac = registry.get(type);
            Class<? extends UIComponent> clz = fac.getComponentClass();
            if (compClass.equals(clz)) {
                return fac;
            }
        }
        return null;
    }

    @Override
    public void removeComponentFactory(String componentType) {
        this.registry.remove(componentType);
    }

    @Override
    public ComponentFactory getComponentFactoryByName(String compClassName) {
        for (String type : registry.keySet()) {
            ComponentFactory fac = registry.get(type);
            String className = fac.getConfig().getComponentClass();
            if (compClassName.equals(className)) {
                return fac;
            }
        }
        return null;
    }
}
