/*
 * $Id: DefaultModelBean.java,v 1.3 2007/10/22 11:27:14 daniel Exp $
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.FacesException;
import javax.transaction.UserTransaction;
import javax.transaction.Status;
import javax.transaction.RollbackException;
import javax.el.ELContext;

import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.annotation.Transactional;
import org.operamasks.faces.application.ApplicationAssociate;

public class DefaultModelBean extends ModelBean
{
    private final Object target;

    public DefaultModelBean(Object target) {
        assert target != null;
        this.target = target;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override
    public Object preInvoke(Method method) {
        return this.target;
    }

    @Override
    public Throwable postInvoke(Object target, Throwable exception) {
        return exception;
    }

    /**
     * A simple implementation of transaction demarcation.
     */
    @Override
    public boolean preInvokeTx(Method method)
        throws Exception
    {
        UserTransaction tx = null;
        if (method.isAnnotationPresent(Transactional.class)) {
            tx = ApplicationAssociate.getInstance().getUserTransaction();
        }

        if (tx != null && tx.getStatus() == Status.STATUS_NO_TRANSACTION) {
            tx.begin();
            return true;
        } else {
            return false;
        }
    }

    /**
     * A simple implementation of transaction demarcation.
     */
    @Override
    public Throwable postInvokeTx(boolean transacted, Throwable exception) {
        if (transacted) {
            UserTransaction tx = ApplicationAssociate.getInstance().getUserTransaction();
            assert tx != null;

            if (exception != null) {
                // rollback transaction if error occurred
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    /*ignored*/
                }
            } else {
                // commit transaction if successed
                try {
                    tx.commit();
                } catch (RollbackException ex) {
                    // transaction has marked for rollback-only, so ignore
                } catch (Throwable ex) {
                    exception = ex;
                }
            }
        }

        return exception;
    }

    @Override // for optimization
    public Object getField(Field field) {
        try {
            return field.get(this.target);
        } catch (IllegalAccessException ex) {
            throw new FacesException(ex);
        }
    }

    @Override // for optimization
    public void setField(Field field, Object value) {
        try {
            field.set(this.target, value);
        } catch (IllegalAccessException ex) {
            throw new FacesException(ex);
        }
    }

    @Override // for optimization
    public Object getValue(ELContext context, Object property) {
        return context.getELResolver().getValue(context, this.target, property);
    }

    @Override // for optimization
    public Class<?> getType(ELContext context, Object property) {
        return context.getELResolver().getType(context, this.target, property);
    }

    @Override // for optimization
    public void setValue(ELContext context, Object property, Object value) {
        context.getELResolver().setValue(context, this.target, property, value);
    }

    @Override // for optimization
    public boolean isReadOnly(ELContext context, Object property) {
        return context.getELResolver().isReadOnly(context, this.target, property);
    }

    public boolean equals(Object obj) {
        if (obj instanceof DefaultModelBean) {
            DefaultModelBean other = (DefaultModelBean)obj;
            return this.target == other.target;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.target.hashCode();
    }
}
