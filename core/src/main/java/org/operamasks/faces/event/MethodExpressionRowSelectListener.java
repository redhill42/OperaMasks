/*
 * $Id: MethodExpressionRowSelectListener.java,v 1.3 2007/07/02 07:38:18 jacky Exp $
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

import javax.faces.component.StateHolder;
import javax.faces.event.AbortProcessingException;
import javax.faces.context.FacesContext;
import javax.el.MethodExpression;
import javax.el.ELContext;
import javax.el.ELException;

public class MethodExpressionRowSelectListener implements RowSelectListener, StateHolder
{
    private MethodExpression methodExpression;
    private boolean isTransient;

    public MethodExpressionRowSelectListener() {
        // no-arg constructor for StateHolder
    }

    public MethodExpressionRowSelectListener(MethodExpression methodExpression) {
        this.methodExpression = methodExpression;
    }

    public void processRowSelect(RowSelectEvent event)
        throws AbortProcessingException
    {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ELContext elContext = context.getELContext();
            methodExpression.invoke(elContext, new Object[] {event});
        } catch (ELException ex) {
            throw new AbortProcessingException(ex.getMessage(), ex.getCause());
        }
    }

    public Object saveState(FacesContext context) {
        return methodExpression;
    }

    public void restoreState(FacesContext context, Object state) {
        methodExpression = (MethodExpression)state;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean newTransientValue) {
        isTransient = newTransientValue;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MethodExpressionRowSelectListener) {
            MethodExpressionRowSelectListener other = (MethodExpressionRowSelectListener)obj;
            return methodExpression.equals(other.methodExpression);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return methodExpression.hashCode();
    }
}
