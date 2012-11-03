/*
 * $Id: ServletExternalContext.java,v 1.7 2008/01/12 01:46:26 lishaochuan Exp $
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

package org.operamasks.faces.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.Principal;

public class ServletExternalContext extends ExternalContext
{
    private ServletContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ScopeManager scope;

    ServletExternalContext(ServletContext context,
                           HttpServletRequest request,
                           HttpServletResponse response)
    {
        this.context = context;
        this.request = request;
        this.response = response;
        this.scope = new ScopeManager();
    }

    public void dispatch(String path)
        throws IOException
    {
        try {
            RequestDispatcher dispatcher = context.getRequestDispatcher(path);
            dispatcher.forward(request, response);
        } catch (ServletException ex) {
            throw new FacesException(ex);
        }
    }

    public void redirect(String url) throws IOException {
        response.sendRedirect(url);
        FacesContext.getCurrentInstance().responseComplete();
    }

    public String encodeActionURL(String url) {
        return response.encodeURL(url);
    }

    public String encodeNamespace(String name) {
        return name;
    }

    public String encodeResourceURL(String url) {
        return response.encodeURL(url);
    }

    public Map<String,Object> getApplicationMap() {
        return scope.getApplicationMap(context);
    }

    public String getAuthType() {
        return request.getAuthType();
    }

    public Object getContext() {
        return context;
    }

    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    public Map<String,String> getInitParameterMap() {
        return scope.getInitParameterMap(context);
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        if (request == null)
            throw new NullPointerException();
        if (request instanceof HttpServletRequest){
            this.request = (HttpServletRequest)request;
        }
    }

    public void setRequestCharacterEncoding(String encoding)
        throws UnsupportedEncodingException
    {
        request.setCharacterEncoding(encoding);
    }

    public String getRequestContextPath() {
        return request.getContextPath();
    }

    public Map<String,Object> getRequestCookieMap() {
        return scope.getCookieMap(request);
    }

    public Map<String,String> getRequestHeaderMap() {
        return scope.getHeaderMap(request);
    }

    public Map<String,String[]> getRequestHeaderValuesMap() {
        return scope.getHeaderValuesMap(request);
    }

    public Locale getRequestLocale() {
        return request.getLocale();
    }

    public Iterator<Locale> getRequestLocales() {
        final Enumeration enu = request.getLocales();
        return new Iterator<Locale>() {
            public boolean hasNext() { return enu.hasMoreElements(); }
            public Locale next()     { return (Locale)enu.nextElement(); }
            public void remove()     { throw new UnsupportedOperationException(); }
        };
    }

    public Map<String,Object> getRequestMap() {
        return scope.getRequestMap(request);
    }

    public Map<String,String> getRequestParameterMap() {
        return scope.getParameterMap(request);
    }

    public Iterator<String> getRequestParameterNames() {
        final Enumeration enu = request.getParameterNames();
        return new Iterator<String>() {
            public boolean hasNext() { return enu.hasMoreElements(); }
            public String next()     { return (String)enu.nextElement(); }
            public void remove()     { throw new UnsupportedOperationException(); }
        };
    }

    public Map<String,String[]> getRequestParameterValuesMap() {
        return scope.getParameterValuesMap(request);
    }

    public String getRequestPathInfo() {
        return request.getPathInfo();
    }

    public String getRequestServletPath() {
        return request.getServletPath();
    }

    public String getRequestCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public String getRequestContentType() {
        return request.getContentType();
    }

    public String getResponseCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    public String getResponseContentType() {
        return response.getContentType();
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        if (response == null)
            throw new NullPointerException();
        this.response = (HttpServletResponse)response;
    }

    public void setResponseCharacterEncoding(String encoding) {
        response.setCharacterEncoding(encoding);
    }

    public Object getSession(boolean create) {
        return request.getSession(create);
    }

    public Map<String,Object> getSessionMap() {
        return scope.getSessionMap(request);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public void log(String message) {
        context.log(message);
    }

    public void log(String message, Throwable exception) {
        context.log(message, exception);
    }

    @SuppressWarnings("unchecked")
    private static class ScopeManager {
        private Map<String,Object>   applicationMap;
        private Map<String,String>   initParamMap;
        private Map<String,Object>   cookieMap;
        private Map<String,String>   headerMap;
        private Map<String,String[]> headerValuesMap;
        private Map<String,String>   paramMap;
        private Map<String,String[]> paramValuesMap;
        private Map<String,Object>   requestMap;
        private Map<String,Object>   sessionMap;

        public Map<String,Object> getApplicationMap(final ServletContext context) {
            if (applicationMap == null) {
                applicationMap = new ScopeMap() {
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
            return applicationMap;
        }

        public Map<String,Object> getCookieMap(final HttpServletRequest request) {
            if (cookieMap == null) {
                cookieMap = new HashMap<String,Object>();
                if (request.getCookies() != null) {
                    for (Cookie c : request.getCookies()) {
                        if (!cookieMap.containsKey(c.getName())) {
                            cookieMap.put(c.getName(), c);
                        }
                    }
                }
                cookieMap = Collections.unmodifiableMap(cookieMap);
            }
            return cookieMap;
        }

        public Map<String,String> getInitParameterMap(final ServletContext context) {
            if (initParamMap == null) {
                initParamMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return context.getInitParameterNames();
                    }
                    protected Object getAttribute(String name) {
                        return context.getInitParameter(name);
                    }
                };
            }
            return initParamMap;
        }

        public Map<String,String> getHeaderMap(final HttpServletRequest request) {
            if (headerMap == null) {
                headerMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return request.getHeaderNames();
                    }
                    protected Object getAttribute(String name) {
                        return request.getHeader(name);
                    }
                };
            }
            return headerMap;
        }

        public Map<String,String[]> getHeaderValuesMap(final HttpServletRequest request) {
            if (headerValuesMap == null) {
                headerValuesMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return request.getHeaderNames();
                    }
                    protected Object getAttribute(String name) {
                        Enumeration e = request.getHeaders(name);
                        if (e != null) {
                            List list = new ArrayList();
                            while (e.hasMoreElements()) {
                                list.add(e.nextElement());
                            }
                            return list.toArray(new String[list.size()]);
                        }
                        return null;
                    }
                };
            }
            return headerValuesMap;
        }

        public Map<String,String> getParameterMap(final HttpServletRequest request) {
            if (paramMap == null) {
                paramMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return request.getParameterNames();
                    }
                    protected Object getAttribute(String name) {
                        return request.getParameter(name);
                    }
                };
            }
            return paramMap;
        }

        public Map<String,String[]> getParameterValuesMap(final HttpServletRequest request) {
            if (paramValuesMap == null) {
                paramValuesMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return request.getParameterNames();
                    }
                    protected Object getAttribute(String name) {
                        return request.getParameterValues(name);
                    }
                };
            }
            return paramValuesMap;
        }

        public Map<String,Object> getRequestMap(final HttpServletRequest request) {
            if (requestMap == null) {
                requestMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        return request.getAttributeNames();
                    }
                    protected Object getAttribute(String name) {
                        return request.getAttribute(name);
                    }
                    protected void setAttribute(String name, Object value) {
                        request.setAttribute(name, value);
                    }
                    protected void removeAttribute(String name) {
                        request.removeAttribute(name);
                    }
                };
            }
            return requestMap;
        }

        public Map<String,Object> getSessionMap(final HttpServletRequest request) {
            if (sessionMap == null) {
                sessionMap = new ScopeMap() {
                    protected Enumeration getAttributeNames() {
                        HttpSession session = request.getSession(false);
                        if (session != null)
                            return session.getAttributeNames();
                        return null;
                    }
                    protected Object getAttribute(String name) {
                        HttpSession session = request.getSession(false);
                        if (session != null)
                            return session.getAttribute(name);
                        return null;
                    }
                    protected void setAttribute(String name, Object value) {
                        request.getSession().setAttribute(name, value);
                    }
                    protected void removeAttribute(String name) {
                        HttpSession session = request.getSession(false);
                        if (session != null)
                            session.removeAttribute(name);
                    }
                };
            }
            return sessionMap;
        }
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

        @SuppressWarnings("unchecked")
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

        public final boolean containsKey(Object key) {
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
