/*
 * $Id: ClosureValueAdapter.java,v 1.5 2008/01/31 04:12:24 daniel Exp $
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

import javax.el.ELContext;
import elite.lang.Closure;

class ClosureValueAdapter extends AbstractValueAdapter
{
    private final Closure closure;
    private final Closure init;

    ClosureValueAdapter(Closure closure, Closure init) {
        this.closure = closure;
        this.init = init;
    }

    public Object getValue(ELContext context) {
        Object value = closure.getValue(context);
        if (value == null && init != null) {
            value = init.call(context);
            if (value != null) {
                closure.setValue(context, value);
            }
        }
        return value;
    }

    public void setValue(ELContext context, Object value) {
        closure.setValue(context, value);
    }

    public boolean isReadOnly(ELContext context) {
        return closure.isReadOnly(context);
    }

    public Class<?> getType(ELContext context) {
        return closure.getType(context);
    }

    public Class<?> getExpectedType() {
        return closure.getExpectedType();
    }
}
