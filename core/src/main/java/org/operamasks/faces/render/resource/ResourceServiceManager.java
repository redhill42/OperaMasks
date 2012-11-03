/*
 * $Id: ResourceServiceManager.java,v 1.4 2007/09/25 22:06:35 daniel Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.operamasks.faces.application.ApplicationAssociate;

public class ResourceServiceManager
{
    private static final String SERVICES_FILE_NAME = "META-INF/faces.resource.services";

    public static ResourceServiceManager getInstance(FacesContext context) {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance(context);
        return assoc.getSingleton(ResourceServiceManager.class);
    }

    private List<ResourceService> services;
    private GlobalResourceService global;

    private ResourceServiceManager() {
        services = new CopyOnWriteArrayList<ResourceService>();
        global = new GlobalResourceService();
        init();
    }

    public void registerResourceService(ResourceService service) {
        services.add(service);
    }

    public void unregisterResourceService(ResourceService service) {
        services.remove(service);
    }

    public void service(FacesContext context)
        throws IOException
    {
        // iterate through registered resource services
        for (ResourceService service : services) {
            service.service(context);
            if (context.getResponseComplete()) {
                return;
            }
        }

        // invoke global system resource service at last
        global.service(context);
    }

    private void init() {
        // load services from config file
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Logger logger = Logger.getLogger(getClass().getName());

        try {
            Enumeration<URL> urls =
                (cl == null) ? ClassLoader.getSystemResources(SERVICES_FILE_NAME)
                             : cl.getResources(SERVICES_FILE_NAME);
            Set<Class> classes = new HashSet<Class>();

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                InputStream stream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String name;
                while ((name = reader.readLine()) != null) {
                    if ((name = name.trim()).length() != 0) {
                        try {
                            Class clazz = (cl == null) ? Class.forName(name) : cl.loadClass(name);
                            if (!classes.contains(clazz)) {
                                ResourceService service = (ResourceService)clazz.newInstance();
                                registerResourceService(service);
                                classes.add(clazz);
                            }
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, "Could not load resource service: " + name, ex);
                        }
                    }
                }
                stream.close();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Load resource services failed.", ex);
        }
    }
}
