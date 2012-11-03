/*
 * $Id: DelegatingModelBean.java,v 1.6 2008/01/31 04:12:24 daniel Exp $
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
import javax.el.ELContext;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import org.operamasks.faces.binding.ModelBean;
import elite.lang.Closure;

public abstract class DelegatingModelBean extends ModelBean
{
    protected abstract ModelBean getDelegate();

    @Override
    public Class<?> getTargetClass() {
        return getDelegate().getTargetClass();
    }

    @Override
    public Object preInvoke(Method method)
        throws Exception
    {
        return getDelegate().preInvoke(method);
    }

    @Override
    public Throwable postInvoke(Object target, Throwable exception) {
        return getDelegate().postInvoke(target, exception);
    }

    @Override
    public boolean preInvokeTx(Method method)
        throws Exception
    {
        return getDelegate().preInvokeTx(method);
    }

    @Override
    public Throwable postInvokeTx(boolean transacted, Throwable exception) {
        return getDelegate().postInvokeTx(transacted, exception);
    }

    @Override
    public Object getField(Field field) {
        return getDelegate().getField(field);
    }

    @Override
    public void setField(Field field, Object value) {
        getDelegate().setField(field, value);
    }

    @Override
    public Object invoke(Method method, Object... args) throws Exception {
        return getDelegate().invoke(method, args);
    }

    @Override
    public Object invokeAction(Method method, Object... args) throws Exception {
        return getDelegate().invokeAction(method, args);
    }

    @Override
    public Object getValue(ELContext context, Object property) {
        return getDelegate().getValue(context, property);
    }

    @Override
    public Class<?> getType(ELContext context, Object property) {
        return getDelegate().getType(context, property);
    }

    @Override
    public void setValue(ELContext context, Object property, Object value) {
        getDelegate().setValue(context, property, value);
    }

    @Override
    public boolean isReadOnly(ELContext context, Object property) {
        return getDelegate().isReadOnly(context, property);
    }

    @Override
    public MethodInfo getMethodInfo(ELContext context, String name)
        throws MethodNotFoundException
    {
        return getDelegate().getMethodInfo(context, name);
    }

    @Override
    public Object invoke(ELContext context, String name, Closure[] args)
        throws MethodNotFoundException
    {
        return getDelegate().invoke(context, name, args);
    }
}
