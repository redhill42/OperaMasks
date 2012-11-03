/*
 * $Id: BootstrapExternalContext.java,v 1.1 2007/10/24 04:40:43 daniel Exp $
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

package org.operamasks.faces.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.Principal;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

@SuppressWarnings("unchecked")
class BootstrapExternalContext extends ExternalContext
{
    private ServletContext context;
    private Map<String,Object> applicationMap;
    private Map<String,String> initParameterMap;

    BootstrapExternalContext(ServletContext context) {
        this.context = context;
    }

    public Object getContext() {
        return this.context;
    }

    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    public Map<String,String> getInitParameterMap() {
        if (this.initParameterMap == null) {
            final ServletContext context = this.context;
            this.initParameterMap = new ScopeMap() {
                protected Enumeration getAttributeNames() {
                    return context.getInitParameterNames();
                }
                protected Object getAttribute(String name) {
                    return context.getInitParameter(name);
                }
            };
        }

        return this.initParameterMap;
    }

    public Map<String,Object> getApplicationMap() {
        if (this.applicationMap == null) {
            final ServletContext context = this.context;
            this.applicationMap = new ScopeMap() {
                protected Enumeration getAttributeNames() {
                    return context.getAttributeNames();
                }
                protected Object getAttribute(String name) {
                    return context.getAttribute(name);
                }
                protected void setAttribute(String name, Object value) {
                    context.setAttribute(name, value);
                }
                protected void removeAttribute(String name) {
                    context.removeAttribute(name);
                }
            };
        }

        return this.applicationMap;
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public Set<String> getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public Object getRequest() {
        return null;
    }

    public String getAuthType() {
        return null;
    }

    public String getRemoteUser() {
        return null;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public boolean isUserInRole(String role) {
        return false;
    }

    public String getRequestContextPath() {
        return null;
    }

    public Map<String,Object> getRequestCookieMap() {
        return Collections.emptyMap();
    }

    public Map<String,String> getRequestHeaderMap() {
        return Collections.emptyMap();
    }

    public Map<String,String[]> getRequestHeaderValuesMap() {
        return Collections.emptyMap();
    }

    public Locale getRequestLocale() {
        return null;
    }

    public Iterator<Locale> getRequestLocales() {
        return Collections.EMPTY_SET.iterator();
    }

    public Map<String,Object> getRequestMap() {
        return Collections.emptyMap();
    }

    public Map<String,String> getRequestParameterMap() {
        return Collections.emptyMap();
    }

    public Iterator<String> getRequestParameterNames() {
        return Collections.EMPTY_SET.iterator();
    }

    public Map<String,String[]> getRequestParameterValuesMap() {
        return Collections.emptyMap();
    }

    public String getRequestPathInfo() {
        return null;
    }

    public String getRequestServletPath() {
        return null;
    }

    public Object getResponse() {
        return null;
    }

    public void dispatch(String path) throws IOException {
        throw new IllegalStateException();
    }

    public void redirect(String url) throws IOException {
        throw new IllegalStateException();
    }

    public String encodeActionURL(String url) {
        return null;
    }

    public String encodeNamespace(String name) {
        return name;
    }

    public String encodeResourceURL(String url) {
        return null;
    }

    public Object getSession(boolean create) {
        return null;
    }

    public Map<String,Object> getSessionMap() {
        return Collections.emptyMap();
    }

    public void log(String message) {
        context.log(message);
    }

    public void log(String message, Throwable exception) {
        context.log(message, exception);
    }

    private static abstract class ScopeMap extends AbstractMap {
        protected abstract Enumeration getAttributeNames();
        protected abstract Object getAttribute(String name);
        protected void removeAttribute(String name) {
            throw new UnsupportedOperationException();
        }
        protected void setAttribute(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        public final Set entrySet() {
            Enumeration e = getAttributeNames();
            Set result = new HashSet();
            if (e != null) {
                while (e.hasMoreElements()) {
                    result.add(new ScopeEntry((String)e.nextElement()));
                }
            }
            return result;
        }

        private class ScopeEntry implements Map.Entry {
            private final String key;
            public ScopeEntry(String key) {
                this.key = key;
            }
            public Object getKey() {
                return key;
            }
            public Object getValue() {
                return getAttribute(key);
            }
            public Object setValue(Object value) {
                if (value == null) {
                    removeAttribute(key);
                } else {
                    setAttribute(key, value);
                }
                return null;
            }
            public boolean equals(Object obj) {
                return (obj != null && this.hashCode() == obj.hashCode());
            }
            public int hashCode() {
                return key.hashCode();
            }
        }

        public final Object get(Object key) {
            if (key != null)
                return getAttribute(key.toString());
            return null;
        }

        public final boolean contains(Object key) {
            return get(key) != null;
        }

        public final Object put(Object key, Object value) {
            if (key == null) {
                throw new NullPointerException();
            }
            if (value == null) {
                removeAttribute(key.toString());
            } else {
                setAttribute(key.toString(), value);
            }
            return null;
        }

        public final Object remove(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            removeAttribute(key.toString());
            return null;
        }
    }
}
