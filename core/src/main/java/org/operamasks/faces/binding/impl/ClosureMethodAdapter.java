/*
 * $Id: ClosureMethodAdapter.java,v 1.5 2008/01/31 04:12:24 daniel Exp $
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

import javax.el.MethodInfo;
import javax.el.ELContext;
import elite.lang.Closure;

class ClosureMethodAdapter extends AbstractMethodAdapter
{
    private Closure closure;

    ClosureMethodAdapter(Closure closure) {
        this.closure = closure;
    }

    public MethodInfo getMethodInfo(ELContext context) {
        return closure.getMethodInfo(context);
    }

    public Object invoke(ELContext context, Object[] args) {
        return closure.call(context, args);
    }
}
