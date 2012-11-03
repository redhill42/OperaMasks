/*
 * $Id: Binders.java,v 1.6 2007/12/11 11:32:10 yangdong Exp $
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
package org.operamasks.faces.binding.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.event.EventBroadcaster;

final class Binders
{
    static final class LoggerBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            Logger logger = (Logger)binding.getModelValue(bean);
            if (logger == null) {
                String name = binding.getDeclaringClass().getName();
                logger = new LoggerWrapper(bean, Logger.getLogger(name), name);
                binding.setModelValue(bean, logger);
            } else if (!(logger instanceof LoggerWrapper)) {
                logger = new LoggerWrapper(bean, logger, logger.getName());
                binding.setModelValue(bean, logger);
            }
        }
    }

    static final class FacesContextBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            binding.setModelValue(bean, ctx);
        }
    }

    static final class ExternalContextBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            binding.setModelValue(bean, ctx.getExternalContext());
        }
    }

    static final class ApplicationBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            if (binding.getModelValue(bean) == null) {
                binding.setModelValue(bean, ctx.getApplication());
            }
        }
    }

    static final class ExpressionFactoryBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            if (binding.getModelValue(bean) == null) {
                binding.setModelValue(bean, ctx.getApplication().getExpressionFactory());
            }
        }
    }

    static final class NavigationHandlerBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            if (binding.getModelValue(bean) == null) {
                binding.setModelValue(bean, ctx.getApplication().getNavigationHandler());
            }
        }
    }

    static final class EventBroadcasterBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            if (binding.getModelValue(bean) == null) {
                binding.setModelValue(bean, EventBroadcaster.getInstance());
            }
        }
    }

    static final class EntityManagerBinder extends DependencyBinder {
        public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
            if (binding.isReadable() && binding.isWriteable()) {
                EntityManager value = (EntityManager)binding.getModelValue(bean);
                if ((value != null) && !(value instanceof EntityManagerWrapper)) {
                    value = new EntityManagerWrapper(bean, value);
                    binding.setModelValue(bean, value);
                }
            }
        }
    }

    public static DependencyBinder getDependencyBinder(Class<?> type) {
        Binders binders = ApplicationAssociate.getInstance().getSingleton(Binders.class);
        return binders.getBinder(type);
    }

    //------------- private ------------

    private Map<Class,DependencyBinder> binders;

    private static final String CONFIG_FILE = "/org/operamasks/faces/binding/impl/binders.properties";

    private Binders() throws IOException {
        this.binders = new HashMap<Class,DependencyBinder>();

        Properties config = new Properties();
        config.load(this.getClass().getResourceAsStream(CONFIG_FILE));

        ClassLoader loader = ApplicationAssociate.getInstance().getClassLoader();

        for (Object key : config.keySet()) {
            try {
                String binderClassName = config.getProperty((String)key);
                if (binderClassName.indexOf('.') == -1) {
                    binderClassName = Binders.class.getName() + "$" + binderClassName;
                }

                Class targetClass = loader.loadClass((String)key);
                Class binderClass = loader.loadClass(binderClassName);

                DependencyBinder binder = (DependencyBinder)binderClass.newInstance();
                this.binders.put(targetClass, binder);

            } catch (Throwable ex) {/*ignored*/}
        }
    }

    DependencyBinder getBinder(Class<?> type) {
        return this.binders.get(type);
    }
}
