/*
 * $Id: CompositeMethodAdapter.java,v 1.1 2007/09/25 22:06:35 daniel Exp $
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
import javax.el.MethodInfo;
import javax.el.ELContext;
import javax.faces.event.PhaseId;
import java.util.List;
import java.util.ArrayList;

class CompositeMethodAdapter extends AbstractMethodAdapter
{
    protected List<MethodExpression> chain;
    protected MethodInfo methodInfo;
    protected PhaseId phaseId;

    public CompositeMethodAdapter(MethodInfo methodInfo) {
        this.chain = new ArrayList<MethodExpression>();
        this.methodInfo = methodInfo;
    }

    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addMethodBinding(MethodExpression method) {
        chain.add(method);
    }

    public MethodInfo getMethodInfo(ELContext context) {
        return this.methodInfo;
    }

    public Object invoke(ELContext context, Object[] params) {
        for (MethodExpression method : chain) {
            Object result = method.invoke(context, params);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
