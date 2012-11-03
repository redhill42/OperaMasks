/*
 * $Id: ApplicationAssociate.java,v 1.49 2007/10/24 04:40:43 daniel Exp $
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
import javax.faces.context.ExternalContext;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;
import javax.naming.InitialContext;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ApplicationAssociate
{
    // --------------- Web Application Integration --------------

    public static final String ASSOCIATE_KEY = ApplicationAssociate.class.getName();

    private static final String APPLICATION_LISTENER_KEY = ApplicationListener.class.getName();
    private static final String APPLICATION_LISTENER_FILE_NAME = "META-INF/services/" + APPLICATION_LISTENER_KEY;

    // Map of application level attributes
    private ConcurrentMap<String,Object> attributes;

    // List of ApplicationListeners
    private ApplicationListener[] applicationListeners;

    // Manages resource dependency injections.
    private InjectionManager injectionManager;

    public static ApplicationAssociate getInstance(ServletContext context) {
        return (ApplicationAssociate)context.getAttribute(ASSOCIATE_KEY);
    }

    public static ApplicationAssociate getInstance(FacesContext context) {
        Map applicationMap = context.getExternalContext().getApplicationMap();
        return (ApplicationAssociate)applicationMap.get(ASSOCIATE_KEY);
    }

    public static ApplicationAssociate getInstance() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (context == null) ? null : getInstance(context);
    }

    public ApplicationAssociate() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            throw new IllegalStateException("Cannot get Web application context " +
                                            "during JSF Engine initialized.");
        }

        Map<String,Object> appMap = context.getExternalContext().getApplicationMap();
        if (appMap.containsKey(ASSOCIATE_KEY)) {
            throw new IllegalStateException("JSF Application associate already initialized.");
        }

        appMap.put(ASSOCIATE_KEY, this);

        this.attributes = new ConcurrentHashMap<String,Object>();
        this.injectionManager = InjectionManagerFactory.createInstance(context);
    }

    public ConcurrentMap<String,Object> getAttributes() {
        return this.attributes;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T)this.attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getSingleton(String key, Class<T> c) {
        T singleton = (T)this.attributes.get(key);
        if (singleton == null) {
            try {
                Constructor<T> cons = c.getDeclaredConstructor();
                cons.setAccessible(true);
                singleton = cons.newInstance();
            } catch (NoSuchMethodException ex) {
                throw new AssertionError("The class " + c.getName() + " must have a no-args constructor.");
            } catch (InvocationTargetException ex) {
                throw new FacesException(ex.getTargetException());
            } catch (Exception ex) {
                throw new FacesException(ex);
            }

            T oldValue = (T)this.attributes.putIfAbsent(key, singleton);
            if (oldValue != null) {
                singleton = oldValue;
            }
        }
        return singleton;
    }

    public <T> T getSingleton(Class<T> c) {
        return getSingleton(c.getName(), c);
    }
    
    /**
     * Get the injection manager.
     */
    public InjectionManager getInjectionManager() {
        return injectionManager;
    }

    /**
     * Returns the context class loader.
     */
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public synchronized ApplicationListener[] getApplicationListeners() {
        if (this.applicationListeners == null) {
            this.applicationListeners = loadApplicationListeners();
        }
        return this.applicationListeners;
    }

    private ApplicationListener[] loadApplicationListeners() {
        ClassLoader cl = this.getClassLoader();
        List<ApplicationListener> listeners = new ArrayList<ApplicationListener>();
        Logger logger = Logger.getLogger(getClass().getName());

        try {
            Enumeration<URL> urls =
                (cl == null) ? ClassLoader.getSystemResources(APPLICATION_LISTENER_FILE_NAME)
                             : cl.getResources(APPLICATION_LISTENER_FILE_NAME);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                InputStream stream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String name;
                while ((name = reader.readLine()) != null) {
                    if ((name = name.trim()).length() != 0) {
                        try {
                            Class clazz = (cl == null) ? Class.forName(name) : cl.loadClass(name);
                            ApplicationListener listener = (ApplicationListener)clazz.newInstance();
                            listeners.add(listener);
                        } catch (Throwable ex) {
                            logger.log(Level.FINEST, "Could not load application listener: " + name, ex);
                        }
                    }
                }
                stream.close();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Load application listeners failed.", ex);
        }

        return listeners.toArray(new ApplicationListener[listeners.size()]);
    }

    // Flag to indicate at least one request has been processed by the
    // Lifecycle instance for this application.
    private boolean requestMade;

    public void requestMade() {
        requestMade = true;
    }

    public boolean isRequestMade() {
        return requestMade;
    }
    
    private boolean responseRendered;
    
    public void responseRendered() {
        responseRendered = true;
    }
    
    public boolean isResponseRendered() {
        return responseRendered;
    }

    // -------------------- Faces Mapping -------------------

    // List of FacesServlet URL mappings
    private String[] facesMappings = new String[0];

    // The ResourceServlet mapping
    private String resourceMapping;

    public String[] getFacesMappings() {
        return facesMappings.clone();
    }

    public void setFacesMappings(String[] facesMappings) {
        assert facesMappings != null;
        this.facesMappings = facesMappings;
    }

    public String getResourceMapping() {
        return resourceMapping;
    }

    public void setResourceMapping(String mapping) {
        if (mapping != null) {
            // only prefix mapping is allowed
            if (mapping.startsWith("/") && mapping.endsWith("/*")) {
                mapping = mapping.substring(0, mapping.length()-2);
            } else {
                return;
            }
        }
        this.resourceMapping = mapping;
    }

    public String getViewId(FacesContext context) {
        ExternalContext ectx = context.getExternalContext();
        Map<String,Object> requestMap = ectx.getRequestMap();
        String path, pathInfo;

        path = (String)requestMap.get("javax.servlet.include.servlet_path");
        if (path != null) {
            pathInfo = (String)requestMap.get("javax.servlet.include.path_info");
        } else {
            path = ectx.getRequestServletPath();
            pathInfo = ectx.getRequestPathInfo();
        }

        if (path == null || path.length() == 0) {
            return null;
        }

        for (String mapping : facesMappings) {
            char c = mapping.charAt(0);
            if (c == '/') {
                // for prefix mapping, return the pathInfo as the view ID
                if (mapping.equals(path)) {
                    if (pathInfo == null) {
                        return path;
                    } else {
                        // prune extra prefixes so /faces/faces/foo.jsp converted
                        // to /foo.jsp where /faces/* is mapped to FacesServlet
                        int length = mapping.length();
                        String prefix = mapping.concat("/");
                        while (pathInfo.startsWith(prefix)) {
                            pathInfo = pathInfo.substring(length);
                        }
                        return pathInfo;
                    }
                }
            } else if (c == '.') {
                // for suffix mapping, return the servlet path as the view ID
                if (path.endsWith(mapping)) {
                    return path;
                }
            }
        }

        // no mappings found, fallback to default behavior
        return (pathInfo != null) ? pathInfo : path;
    }

    public String getFacesMapping(FacesContext context) {
        ExternalContext ectx = context.getExternalContext();
        Map<String,Object> requestMap = ectx.getRequestMap();

        String path = (String)requestMap.get("javax.servlet.include.servlet_path");
        if (path == null) {
            path = ectx.getRequestServletPath();
        }

        if (path == null || path.length() == 0) {
            return null;
        }

        for (String mapping : facesMappings) {
            char c = mapping.charAt(0);
            if (c == '/') {
                // for prefix mapping
                if (mapping.equals(path)) {
                    return mapping;
                }
            } else if (c == '.') {
                // for suffix mapping
                if (path.endsWith(mapping)) {
                    return mapping;
                }
            }
        }

        return null;
    }

    // ---------------------- Transaction Management ---------------------

    private UserTransaction userTransaction;
    private boolean transactionInited = false;

    public UserTransaction getUserTransaction() {
        if (!this.transactionInited) {
            try {
                InitialContext initctx = new InitialContext();
                this.userTransaction = (UserTransaction)initctx.lookup("java:comp/UserTransaction");
            } catch (Exception ex) {
                this.userTransaction = null;
            } finally {
                this.transactionInited = true;
            }
        }

        return this.userTransaction;
    }

    // ---------------- Resource Bundle Mapping --------------

    // maps of variable name to resource bundle name
    private Map<String,String[]> resourceBundles = new HashMap<String, String[]>();

    /**
     * Regisrer a resource bundle.
     */
    public void addResourceBundle(String var, String baseName, String displayName) {
        String[] rb = new String[] { baseName, displayName };
        resourceBundles.put(var, rb);
    }

    /**
     * Find the resource base name bundle by given variable name.
     */
    public String getResourceBundleBaseName(String var) {
        String[] rb = resourceBundles.get(var);
        return (rb != null) ? rb[0] : null;
    }

    /**
     * Find the resource bundle display name by given variable name.
     */
    public String getResourceBundleDisplayName(String var) {
        String[] rb = resourceBundles.get(var);
        return (rb != null) ? rb[1] : null;
    }

    /**
     * Get all the resource bundle variable names.
     */
    public Collection<String> getResourceBundles() {
        return resourceBundles.keySet();
    }
}
