/*
 * $Id: DefaultManagedBeanContainer.java,v 1.5 2008/03/10 07:36:06 jacky Exp $
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

package org.operamasks.faces.application.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.ManagedBeanNamespace;
import org.operamasks.faces.application.ManagedBeanFactory;
import org.operamasks.faces.config.ManagedBeanConfig;
import org.operamasks.faces.config.ManagedBeanConfig.FactoryMethod;
import org.operamasks.faces.config.ManagedBeanConfig.EventListener;
import org.operamasks.faces.event.EventBroadcaster;
import org.operamasks.faces.event.ModelEventListener;
import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.annotation.ManagedBeanScope;

public class DefaultManagedBeanContainer extends ManagedBeanContainer
{
    // maps the managed bean name to managed bean factory
    private Map<String,ManagedBeanFactory> registry;

    // the managed bean namespace declarations
    private Map<String,ManagedBeanNamespace> namespaces;

    protected DefaultManagedBeanContainer() {
        this.registry = new HashMap<String,ManagedBeanFactory>();
        this.namespaces = new HashMap<String,ManagedBeanNamespace>();
    }

    public void addNamespace(String fqn) {
        // add intermindate namespaces
        int pos = 0;
        while ((pos = fqn.indexOf('.', pos)) != -1) {
            String n = fqn.substring(0, pos);
            if (!namespaces.containsKey(n)) {
                namespaces.put(n, new ManagedBeanNamespace(n));
            }
            pos = pos+1;
        }

        // add full namespace
        if (!namespaces.containsKey(fqn)) {
            namespaces.put(fqn, new ManagedBeanNamespace(fqn));
        }
    }

    public ManagedBeanNamespace getNamespace(String name) {
        return this.namespaces.get(name);
    }

    public ManagedBeanFactory addBeanFactory(ManagedBeanConfig config) {
        // create appropriate managed bean factory instance.
        ManagedBeanFactory factory;
        if (config instanceof FactoryMethod) {
            factory = new FactoryMethodManagedBeanFactory((FactoryMethod)config);
        } else {
            factory = new DefaultManagedBeanFactory(config);
        }

        addBeanFactory(factory);
        return factory;
    }

    public void addBeanFactory(ManagedBeanFactory factory) {
        // Add managed bean namespaces.
        String beanName = factory.getBeanName();
        int dot = beanName.lastIndexOf('.');
        if (dot != -1) {
            addNamespace(beanName.substring(0, dot));
        }

        // Add a mapping of managed bean name and managed bean factory instance.
        this.registry.put(beanName, factory);

        // add additional configuration.
        ManagedBeanConfig config = factory.getConfig();
        if (config != null) {
            // add factory methods
            for (FactoryMethod factoryMethod : config.getFactoryMethods()) {
                addBeanFactory(factoryMethod);
            }

            // add model event listeners.
            EventBroadcaster broadcaster = EventBroadcaster.getInstance();
            for (EventListener listenerConfig : config.getEventListeners()) {
                ModelEventListener listener = new ModelEventListenerMethodAdapter
                    (beanName, listenerConfig.getListenerMethod());
                for (String eventType : listenerConfig.getEventTypes()) {
                    broadcaster.addEventListener(eventType, listener);
                }
            }
        }
    }

    public void removeBeanFactory(String name) {
        this.registry.remove(name);
    }

    public boolean containsBeanFactory(String name) {
        return this.registry.containsKey(name);
    }

    public ManagedBeanFactory getBeanFactory(String name) {
        return this.registry.get(name);
    }

    public Collection<ManagedBeanFactory> getBeanFactories() {
        return Collections.unmodifiableCollection(this.registry.values());
    }

    public Object getBean(FacesContext context, String beanName) {
        // first lookup from scope
        ExternalContext ext = context.getExternalContext();
        Object bean = ext.getRequestMap().get(beanName);
        if (bean == null) {
            bean = ext.getSessionMap().get(beanName);
            if (bean == null) {
                bean = ext.getApplicationMap().get(beanName);
            }
        }

        // create new managed bean if not found in scope
        if (bean == null) {
            bean = createAndStoreBean(context, beanName);
        }

        return bean;
    }

    protected Object createAndStoreBean(FacesContext context, String beanName) {
        ManagedBeanFactory factory = this.registry.get(beanName);
        if (factory == null) {
            return null;
        }

        // create the managed bean instance
        ManagedBeanScope scope = factory.getScope();
        Object bean = factory.createBean(context);
        if (bean == null) {
            return null;
        }

        // add bean to appropriate scope
        ExternalContext ext = context.getExternalContext();
        if (scope == ManagedBeanScope.REQUEST) {
            ext.getRequestMap().put(beanName, bean);
        } else if (scope == ManagedBeanScope.SESSION) {
            ext.getSessionMap().put(beanName, bean);
        } else if (scope == ManagedBeanScope.APPLICATION) {
            ext.getApplicationMap().put(beanName, bean);
        }

        // broadcast managed bean created event
        EventBroadcaster events = EventBroadcaster.getInstance();
        events.broadcast(this, EventTypes.MANAGED_BEAN_CREATED, beanName, scope.toString(), bean);

        return bean;
    }
}
