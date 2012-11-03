/*
 * $Id: AbstractMethodAdapter.java,v 1.1 2007/09/25 22:06:35 daniel Exp $
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

import javax.el.MethodExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

abstract class AbstractMethodAdapter extends MethodExpression implements StateHolder
{
    public String getExpressionString() {
        return null;
    }

    public boolean isLiteralText() {
        return false;
    }

    public Object saveState(FacesContext context) {
        // no need to save state, restored by injector
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        // no need to save state, restored by injector
    }

    public boolean isTransient() {
        return true; // no state saving at all
    }

    public void setTransient(boolean newTransientValue) {
        // noop
    }

    protected Object writeReplace() {
        return null;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }
}
