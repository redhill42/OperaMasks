/*
 * $Id: MethodExpressionModelEventListener.java,v 1.1 2007/09/25 13:36:19 daniel Exp $
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
package org.operamasks.faces.event;

import javax.el.MethodExpression;
import javax.el.ELContext;
import javax.el.MethodInfo;
import javax.faces.context.FacesContext;
import org.operamasks.util.Utils;

/**
 * MethodExpressionModelEventListener is an {@link ModelEventListener}
 * that wraps a <code>MethodExpression</code>. When it receives a
 * {@link ModelEvent}, it executes a method on an object idenfied by
 * the <code>MethodExpression</code>.
 */
public class MethodExpressionModelEventListener implements ModelEventListener
{
    private final MethodExpression methodExpression;

    public MethodExpressionModelEventListener(MethodExpression expression) {
        this.methodExpression = expression;
    }

    public void processModelEvent(ModelEvent event) {
        FacesContext context     = FacesContext.getCurrentInstance();
        ELContext    elContext   = context.getELContext();
        MethodInfo   methodInfo  = methodExpression.getMethodInfo(elContext);
        Class[]      paramTypes  = methodInfo.getParamTypes();

        Object[] paramValues;
        if (paramTypes.length == 1 && paramTypes[0] == ModelEvent.class) {
            paramValues = new Object[] { event };
        } else {
            paramValues = Utils.buildParameterList(paramTypes, event.getParameters(), false);
        }

        methodExpression.invoke(elContext, paramValues);
    }

    public boolean equals(Object obj) {
        if (obj instanceof MethodExpressionModelEventListener) {
            MethodExpressionModelEventListener other = (MethodExpressionModelEventListener)obj;
            return this.methodExpression.equals(other.methodExpression);
        }
        return false;
    }

    public int hashCode() {
        return this.methodExpression.hashCode();
    }
}
