/*
 * $Id: MethodBindingMethodExpressionAdapter.java,v 1.4 2007/07/02 07:38:16 jacky Exp $
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

package org.operamasks.faces.el;

import javax.faces.context.FacesContext;
import javax.faces.component.StateHolder;
import javax.el.MethodExpression;
import javax.el.MethodInfo;

@SuppressWarnings("deprecation")
public class MethodBindingMethodExpressionAdapter extends javax.faces.el.MethodBinding
    implements StateHolder, java.io.Serializable
{
    private MethodExpression expression;
    private boolean _transient;

    public MethodBindingMethodExpressionAdapter() {
        // default constructor for StateHolder
    }
    
    public MethodBindingMethodExpressionAdapter(MethodExpression expression) {
        this.expression = expression;
    }

    public MethodExpression getMethodExpression() {
        return expression;
    }

    public String getExpressionString() {
        return expression.getExpressionString();
    }

    public Object invoke(FacesContext context, Object[] args) {
        try {
            return expression.invoke(context.getELContext(), args);
        } catch (javax.el.MethodNotFoundException ex) {
            javax.faces.el.MethodNotFoundException ex2 = new javax.faces.el.MethodNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.el.ELException ex) {
            javax.faces.el.EvaluationException ex2 = new javax.faces.el.EvaluationException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public Class getType(FacesContext context) {
        try {
            MethodInfo mi = expression.getMethodInfo(context.getELContext());
            return mi.getReturnType();
        } catch (javax.el.MethodNotFoundException ex) {
            throw new javax.faces.el.MethodNotFoundException(ex);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.MethodNotFoundException(ex);
        }
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean _transient) {
        this._transient = _transient;
    }

    public Object saveState(FacesContext context) {
        if (_transient)
            return null;
        return expression;
    }

    public void restoreState(FacesContext context, Object state) {
        if (state != null) {
            expression = (MethodExpression)state;
        }
    }
}
