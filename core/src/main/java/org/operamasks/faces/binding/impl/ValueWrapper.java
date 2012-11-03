/*
 * $Id: ValueWrapper.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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

public class ValueWrapper extends AbstractValueAdapter
{
    private Object value;
    private Class type;

    public ValueWrapper(Object value, Class type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue(ELContext context) {
        if (this.value instanceof ValueExpression) {
            return ((ValueExpression)this.value).getValue(context);
        } else {
            return this.value;
        }
    }

    public void setValue(ELContext context, Object value) {
        if (this.value instanceof ValueExpression) {
            ((ValueExpression)this.value).setValue(context, value);
        } else {
            this.value = value;
        }
    }

    public boolean isReadOnly(ELContext context) {
        if (this.value instanceof ValueExpression) {
            return ((ValueExpression)this.value).isReadOnly(context);
        } else {
            return false;
        }
    }

    public Class<?> getType(ELContext context) {
        if (this.value instanceof ValueExpression) {
            return ((ValueExpression)this.value).getType(context);
        } else {
            return this.type;
        }
    }

    public Class<?> getExpectedType() {
        return this.type;
    }

    public String getExpressionString() {
        if (this.value instanceof ValueExpression) {
            return ((ValueExpression)value).getExpressionString();
        } else {
            return null;
        }
    }
}
