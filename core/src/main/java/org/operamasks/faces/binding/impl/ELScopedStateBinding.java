/*
 * $Id: ELScopedStateBinding.java,v 1.5 2008/01/31 04:12:24 daniel Exp $
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

import java.util.Map;
import javax.faces.context.FacesContext;
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

class ELScopedStateBinding extends Binding implements Injector
{
    protected String  name;
    protected String  scope;
    protected Closure closure;
    protected Closure init;

    ELScopedStateBinding(String name, String scope, Closure closure, Closure init) {
        super(null);
        this.name = name;
        this.scope = scope;
        this.closure = closure;
        this.init = init;
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        Map<String,Object> scopeMap = getScopeMap(ctx, (ELiteBean)mbc.getModelBean());
        ELContext elctx = ctx.getELContext();

        if (scopeMap != null) {
            Object value;
            if ((value = scopeMap.get(name)) != null) {
                closure.setValue(elctx, value);
            } else {
                if (init != null) {
                    value = init.call(elctx);
                    closure.setValue(elctx, value);
                } else {
                    value = closure.getValue(elctx);
                }
                if (value != null) {
                    scopeMap.put(name, value);
                }
            }
        }
    }

    public void inject(FacesContext ctx, ModelBean bean) {
        // injection is not allowed otherwise request value will be lost
    }

    public void outject(FacesContext ctx, ModelBean bean) {
        Map<String,Object> scopeMap = getScopeMap(ctx, (ELiteBean)bean);
        ELContext elctx = ctx.getELContext();

        if (scopeMap != null) {
            Object value = closure.getValue(elctx);
            if (value != null) {
                scopeMap.put(name, value);
            } else {
                scopeMap.remove(name);
            }
        }
    }

    private Map<String,Object> getScopeMap(FacesContext ctx, ELiteBean bean) {
        String path = bean.getPath();
        if (path == null) {
            path = ctx.getViewRoot().getViewId();
        }

        ScopeManager scopeMgr = ScopeManager.getInstance();
        if ("session".equals(scope)) {
            return scopeMgr.getSessionMap(ctx, path);
        } else if ("application".equals(scope)) {
            return scopeMgr.getApplicationMap(ctx, path);
        } else if ("request".equals(scope)) {
            return scopeMgr.getRequestMap(ctx, path);
        } else {
            return null;
        }
    }
}
