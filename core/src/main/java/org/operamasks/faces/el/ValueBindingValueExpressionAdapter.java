/*
 * $Id: ValueBindingValueExpressionAdapter.java,v 1.4 2007/07/02 07:38:17 jacky Exp $
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

@SuppressWarnings("deprecation")
public class ValueBindingValueExpressionAdapter extends javax.faces.el.ValueBinding
    implements StateHolder, java.io.Serializable
{
    private javax.el.ValueExpression expression;
    private boolean _transient;

    public ValueBindingValueExpressionAdapter() {
        // default constructor for StateHolder
    }

    public ValueBindingValueExpressionAdapter(javax.el.ValueExpression expression) {
        this.expression = expression;
    }

    public javax.el.ValueExpression getValueExpression() {
        return expression;
    }

    public String getExpressionString() {
        return expression.getExpressionString();
    }

    public Object getValue(FacesContext context) {
        try {
            return expression.getValue(context.getELContext());
        } catch (javax.el.PropertyNotFoundException ex) {
            javax.faces.el.PropertyNotFoundException ex2 = new javax.faces.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.el.ELException ex) {
            javax.faces.el.EvaluationException ex2 = new javax.faces.el.EvaluationException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public void setValue(FacesContext context, Object value) {
        try {
            expression.setValue(context.getELContext(), value);
        } catch (javax.el.PropertyNotFoundException ex) {
            javax.faces.el.PropertyNotFoundException ex2 = new javax.faces.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.el.PropertyNotWritableException ex) {
            javax.faces.el.PropertyNotFoundException ex2 = new javax.faces.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.el.ELException ex) {
            javax.faces.el.EvaluationException ex2 = new javax.faces.el.EvaluationException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    public boolean isReadOnly(FacesContext context) {
        try {
            return expression.isReadOnly(context.getELContext());
        } catch (javax.el.PropertyNotFoundException ex) {
            javax.faces.el.PropertyNotFoundException ex2 = new javax.faces.el.PropertyNotFoundException(ex.getCause());
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
            return expression.getType(context.getELContext());
        } catch (javax.el.PropertyNotFoundException ex) {
            javax.faces.el.PropertyNotFoundException ex2 = new javax.faces.el.PropertyNotFoundException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        } catch (javax.el.ELException ex) {
            javax.faces.el.EvaluationException ex2 = new javax.faces.el.EvaluationException(ex.getCause());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
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
            expression = (javax.el.ValueExpression)state;
        }
    }
}
