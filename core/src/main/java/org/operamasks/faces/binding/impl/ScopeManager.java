/*
 * $Id: ScopeManager.java,v 1.1 2007/12/18 14:53:15 daniel Exp $
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

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.faces.context.FacesContext;
import org.operamasks.faces.application.ApplicationAssociate;

public class ScopeManager implements Serializable
{
    public static ScopeManager getInstance() {
        return ApplicationAssociate.getInstance().getSingleton(ScopeManager.class);
    }

    private static final String KEY = ScopeManager.class.getName();
    private final ConcurrentScopeMap applicationMap = new ConcurrentScopeMap();

    private static class ScopeMap {
        private Map<String,Map<String,Object>> scopeMap
            = new HashMap<String, Map<String, Object>>();

        public Map<String,Object> get(String key) {
            Map<String,Object> scope = scopeMap.get(key);
            if (scope == null) {
                scope = new HashMap<String, Object>();
                scopeMap.put(key, scope);
            }
            return scope;
        }
    }

    private static class ConcurrentScopeMap {
        private ConcurrentMap<String,ConcurrentMap<String,Object>> scopeMap
            = new ConcurrentHashMap<String, ConcurrentMap<String, Object>>();

        public ConcurrentMap<String,Object> get(String key) {
            ConcurrentMap<String,Object> scope = scopeMap.get(key);
            if (scope == null) {
                scope = new ConcurrentHashMap<String, Object>();
                ConcurrentMap<String,Object> old = scopeMap.putIfAbsent(key, scope);
                if (old != null) scope = old;
            }
            return scope;
        }
    }

    public Map<String,Object> getApplicationMap(FacesContext context, String viewId) {
        return applicationMap.get(viewId);
    }

    public Map<String,Object> getSessionMap(FacesContext context, String viewId) {
        Map<String,Object> sessionMap = context.getExternalContext().getSessionMap();
        ConcurrentScopeMap scopeMap = (ConcurrentScopeMap)sessionMap.get(KEY);
        if (scopeMap == null) {
            scopeMap = new ConcurrentScopeMap();
            sessionMap.put(KEY, scopeMap);
        }
        return scopeMap.get(viewId);
    }

    public Map<String,Object> getRequestMap(FacesContext context, String viewId) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        ScopeMap scopeMap = (ScopeMap)requestMap.get(KEY);
        if (scopeMap == null) {
            scopeMap = new ScopeMap();
            requestMap.put(KEY, scopeMap);
        }
        return scopeMap.get(viewId);
    }

    private ScopeManager() {}
}
