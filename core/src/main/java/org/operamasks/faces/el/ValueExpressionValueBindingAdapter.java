/*
 * $Id: ValueExpressionValueBindingAdapter.java,v 1.4 2007/07/02 07:38:16 jacky Exp $
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

import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.faces.component.StateHolder;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import org.operamasks.util.Utils;

@SuppressWarnings("deprecation")
public class ValueExpressionValueBindingAdapter extends ValueExpression
    implements StateHolder, java.io.Serializable
{
    private ValueBinding binding;
    private boolean _transient;

    public ValueExpressionValueBindingAdapter() {
        // default constructor for StateHolder
    }

    public ValueExpressionValueBindingAdapter(ValueBinding binding) {
        this.binding = binding;
    }

    public ValueBinding getValueBinding() {
        return binding;
    }

    public String getExpressionString() {
        return binding.getExpressionString();
    }

    public Object getValue(ELContext context) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            return binding.getValue(facesCtx);
        } catch (javax.faces.el.PropertyNotFoundException ex) {
            javax.el.PropertyNotFoundException ex2 = new javax.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.faces.el.EvaluationException ex) {
            javax.el.ELException ex2 = new javax.el.ELException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public void setValue(ELContext context, Object value) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            binding.setValue(facesCtx, value);
        } catch (javax.faces.el.PropertyNotFoundException ex) {
            javax.el.PropertyNotFoundException ex2 = new javax.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.faces.el.EvaluationException ex) {
            javax.el.ELException ex2 = new javax.el.ELException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public boolean isReadOnly(ELContext context) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            return binding.isReadOnly(facesCtx);
        } catch (javax.faces.el.PropertyNotFoundException ex) {
            javax.el.PropertyNotFoundException ex2 = new javax.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.faces.el.EvaluationException ex) {
            javax.el.ELException ex2 = new javax.el.ELException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public Class getType(ELContext context) {
        try {
            FacesContext facesCtx = (FacesContext)context.getContext(FacesContext.class);
            return binding.getType(facesCtx);
        } catch (javax.faces.el.PropertyNotFoundException ex) {
            javax.el.PropertyNotFoundException ex2 = new javax.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.faces.el.EvaluationException ex) {
            javax.el.ELException ex2 = new javax.el.ELException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public boolean isLiteralText() {
        return false;
    }

    public Class getExpectedType() {
        return Object.class;
    }

    public int hashCode() {
        return binding.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ValueExpression) {
            ValueExpression other = (ValueExpression)obj;
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

        if (state instanceof ValueBinding) {
            this.binding = (ValueBinding)state;
        } else {
            Object[] values = (Object[])state;
            Object bindingState = values[0];
            String className = (String)values[1];

            try {
                Class bindingClass = Utils.findClass(className);
                binding = (ValueBinding)bindingClass.newInstance();
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
