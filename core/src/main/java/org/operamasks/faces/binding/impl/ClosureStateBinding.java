/*
 * $Id: ClosureStateBinding.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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

import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;

class ClosureStateBinding extends StateBinding
{
    private Closure closure;
    private Closure init;

    ClosureStateBinding(String key, Class<?> type, boolean inServer, Closure closure, Closure init) {
        super(null, key, type, inServer);
        this.closure = closure;
        this.init = init;
    }

    protected Object getStateValue(FacesContext ctx, ModelBindingContext mbc) {
        ELContext elctx = ctx.getELContext();
        Object value = closure.getValue(elctx);
        if (value == null && init != null) {
            value = init.call(elctx);
            if (value != null) {
                closure.setValue(elctx, value);
            }
        }
        return value;
    }

    protected void setStateValue(FacesContext ctx, ModelBindingContext mbc, Object value) {
        closure.setValue(ctx.getELContext(), value);
    }

    protected ValueExpression createValueAdapter(FacesContext ctx, ModelBindingContext mbc) {
        return new ClosureValueAdapter(closure, init);
    }
}
