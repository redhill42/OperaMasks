/*
 * $Id: InjectionManagerFactory.java,v 1.2 2007/10/24 04:40:43 daniel Exp $
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

import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.faces.context.FacesContext;

import org.operamasks.util.Utils;

/**
 * Responsible to create InjectionManager instance.
 */
public abstract class InjectionManagerFactory
{
    private static final String FACTORY_KEY = InjectionManagerFactory.class.getName();
    private static final String FACTORY_SERVICE = "META-INF/services/" + FACTORY_KEY;

    /**
     * Create new instance of InjectionManager, or null if the injection
     * functionality is not compatible with current environment.
     *
     * @param context the faces context used to initialize injection manager
     * @return new instance of InjectionManager or null.
     */
    protected abstract InjectionManager newInjectionManager(FacesContext context);

    /**
     * Get the instance of InjectionManagerFactory that is available
     * to current runtime environment.
     */
    public static InjectionManager createInstance(FacesContext context) {
        for (String classname : getFactoryClasses(context)) {
            try {
                // try to instantiate the InjectionManager
                Class<?> clazz = Utils.findClass(classname);
                InjectionManagerFactory factory = (InjectionManagerFactory)clazz.newInstance();
                InjectionManager manager = factory.newInjectionManager(context);
                if (manager != null) {
                    return manager;
                }
            } catch (Throwable ex) {
                // skip this provider if it can't be used for current environemnt.
            }
        }

        // If no provider found then return Noop injection manager.
        return new NoopInjectionManager();
    }

    private static String[] getFactoryClasses(FacesContext context) {
        String provider;

        // Does a factory class is defined in context parameter?
        provider = context.getExternalContext().getInitParameter(FACTORY_KEY);
        if (provider != null) {
            return new String[] { provider };
        }

        // Does a factory class is defined in system property?
        provider = System.getProperty(FACTORY_KEY);
        if (provider != null) {
            return new String[] { provider };
        }

        // Search for factory classes in the service configuration file.
        List<String> list = new ArrayList<String>();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = loader.getResources(FACTORY_SERVICE);
            while (resources.hasMoreElements()) {
                InputStream input = resources.nextElement().openStream();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        list.add(line.trim());
                    }
                } finally {
                    input.close();
                }
            }
        } catch (Exception ex) {
            // TODO: logging
        }

        return list.toArray(new String[list.size()]);
    }

    private static class NoopInjectionManager implements InjectionManager {
        public void inject(Object bean) {
            // noop
        }
        public void invokePostConstruct(Object bean) {
            // noop
        }
        public void invokePreDestroy(Object bean) {
            // noop
        }
    }
}
