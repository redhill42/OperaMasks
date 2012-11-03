/*
 * $Id: MethodExpressionMethodBindingAdapter.java,v 1.4 2007/07/02 07:38:17 jacky Exp $
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

import javax.faces.component.StateHolder;
import javax.faces.el.MethodBinding;
import javax.faces.context.FacesContext;
import javax.el.ELContext;
import javax.el.MethodInfo;
import javax.el.MethodExpression;
import org.operamasks.util.Utils;

@SuppressWarnings("deprecation")
public class MethodExpressionMethodBindingAdapter extends javax.el.MethodExpression
    implements StateHolder, java.io.Serializable
{
    private MethodBinding binding;
    private boolean _transient;

    public MethodExpressionMethodBindingAdapter() {
        // default constructor for StateHolder
    }

    public MethodExpressionMethodBindingAdapter(MethodBinding binding) {
        this.binding = binding;
    }

    public MethodBinding getMethodBinding() {
        return binding;
    }

    public String getExpressionString() {
        return binding.getExpressionString();
    }

    public Object invoke(ELContext context, Object[] args) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            return binding.invoke(facesCtx, args);
        } catch (javax.faces.el.MethodNotFoundException ex) {
            throw new javax.el.MethodNotFoundException(ex);
        } catch (javax.faces.el.EvaluationException ex) {
            throw new javax.el.ELException(ex);
        }
    }

    public MethodInfo getMethodInfo(ELContext context) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            return new MethodInfo(null, binding.getType(facesCtx), new Class[0]);
        } catch (javax.faces.el.EvaluationException ex) {
            throw new javax.el.ELException(ex);
        }
    }

    public boolean isLiteralText() {
        String expr = binding.getExpressionString();
        return !(expr.startsWith("#{") && expr.endsWith("}"));
    }

    public int hashCode() {
        return binding.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MethodExpression) {
            MethodExpression other = (MethodExpression)obj;
            return this.getExpressionString().equals(other.getExpressionString());
        }
        return false;
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean _transient) {
        this._transient = _transient;
    }

    public Object saveState(FacesContext context) {
        if (_transient) {
            return null;
        }

        if (binding instanceof StateHolder) {
            Object[] state = new Object[2];
            state[0] = ((StateHolder)binding).saveState(context);
            state[1] = binding.getClass().getName();
            return state;
        } else {
            return binding;
        }
    }

    public void restoreState(FacesContext context, Object state) {
        if (state == null) {
            return;
        }

        if (state instanceof MethodBinding) {
            this.binding = (MethodBinding)state;
        } else {
            Object[] values = (Object[])state;
            Object bindingState = values[0];
            String className = (String)values[1];

            try {
                Class bindingClass = Utils.findClass(className);
                binding = (MethodBinding)bindingClass.newInstance();
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException(ex);
            } catch (InstantiationException ex) {
                throw new IllegalStateException(ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
            ((StateHolder)binding).restoreState(context, bindingState);
        }
    }
}
