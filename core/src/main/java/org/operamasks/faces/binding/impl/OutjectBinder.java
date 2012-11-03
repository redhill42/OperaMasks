/*
 * $Id: OutjectBinder.java,v 1.3 2007/10/16 04:26:45 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.el.ValueExpression;

import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.util.FacesUtils;

class OutjectBinder extends DependencyBinder
{
    private String expr;
    private ManagedBeanScope scope;

    public OutjectBinder(String expr, ManagedBeanScope scope) {
        this.expr = expr;
        this.scope = scope;
    }

    public String getExpression() {
        return this.expr;
    }

    public ManagedBeanScope getScope() {
        return this.scope;
    }

    @Override
    public void outject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
        Object value = binding.getModelValue(bean);
        if (value == null) {
            return;
        }

        if (FacesUtils.isValueExpression(this.expr)) {
            ValueExpression ve = FacesUtils.createValueExpression(bean, this.expr, binding.getType());
            ve.setValue(ctx.getELContext(), value);
        } else {
            ExternalContext ext = ctx.getExternalContext();
            if (this.scope == ManagedBeanScope.REQUEST) {
                ext.getRequestMap().put(this.expr, value);
            } else if (this.scope == ManagedBeanScope.SESSION) {
                ext.getSessionMap().put(this.expr, value);
            } else if (this.scope == ManagedBeanScope.APPLICATION) {
                ext.getApplicationMap().put(this.expr, value);
            }
        }
    }
}
