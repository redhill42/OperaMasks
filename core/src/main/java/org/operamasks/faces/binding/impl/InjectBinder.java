/*
 * $Id: InjectBinder.java,v 1.3 2007/10/31 17:07:49 daniel Exp $
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
import javax.faces.context.FacesContext;
import org.operamasks.faces.binding.ModelBean;
import static org.operamasks.faces.util.FacesUtils.*;

class InjectBinder extends DependencyBinder
{
    private String expr;
    private boolean renew;

    public InjectBinder(String expr, boolean renew) {
        if (!isValueExpression(expr)) {
            expr = "#{" + expr + "}";
        }

        this.expr = expr;
        this.renew = renew;
    }

    public String getExpression() {
        return this.expr;
    }

    public boolean isRenew() {
        return this.renew;
    }

    @Override
    public void inject(FacesContext ctx, PropertyBinding binding, ModelBean bean) {
        if (this.renew || binding.getModelValue(bean) == null) {
            Class<?> type = binding.getType();
            Object value;

            if (type == ValueExpression.class) {
                value = createValueExpression(bean, this.expr, Object.class);
            } else {
                value = bean.evaluateExpression(this.expr, type);
            }
            if (value != null) {
                binding.setModelValue(bean, value);
            }
        }
    }
}
