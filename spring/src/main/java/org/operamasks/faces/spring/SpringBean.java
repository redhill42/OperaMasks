/*
 * $Id: SpringBean.java,v 1.9 2008/01/31 04:12:24 daniel Exp $
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

package org.operamasks.faces.spring;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.el.eval.ELEngine;
import org.operamasks.faces.binding.ModelBean;
import org.springframework.aop.framework.Advised;

class SpringBean extends ModelBean
{
    private final Advised proxy;

    public SpringBean(Advised advised) {
        this.proxy = advised;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.proxy.getTargetClass();
    }

    @Override
    public Object preInvoke(Method method)
        throws Exception
    {
        return this.proxy.getTargetSource().getTarget();
    }

    @Override
    public Throwable postInvoke(Object target, Throwable exception) {
        try {
            this.proxy.getTargetSource().releaseTarget(target);
        } catch (Throwable ex) {
            exception = ex;
        }

        return exception;
    }

    @Override
    protected Object invokeMethod(Object target, Method method, Object[] args)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        // Invoke the proxy method if the method is declared in the interface
        // of target model bean.
        Method proxyMethod = getProxyMethod(method);
        if (proxyMethod != null) {
            return proxyMethod.invoke(this.proxy, args);
        }

        // Invoke the underlying target model bean method.
        return method.invoke(target, args);
    }

    @Override
    protected Object invokeDynamicMethod(ELContext ctx, Object target, Method method, Closure[] args) {
        // Invoke the proxy method if the method is declared in the interface
        // of target model bean.
        Method proxyMethod = getProxyMethod(method);
        if (proxyMethod != null) {
            return ELEngine.invokeMethod(ctx, this.proxy, proxyMethod, args);
        }

        // Invoke the underlying target model bean method.
        return ELEngine.invokeMethod(ctx, target, method, args);
    }

    private Method getProxyMethod(Method method) {
        // The interface method must be a proxy method
        Class c = method.getDeclaringClass();
        if (c.isInterface() && c.isInstance(this.proxy)) {
            return method;
        }

        // The private or final method must not be a proxy method
        int mod = method.getModifiers();
        if ((mod & (Modifier.PRIVATE|Modifier.FINAL)) != 0) {
            return null;
        }

        // Find the proxy method from proxy implementation class
        try {
            return this.proxy.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof SpringBean) {
            SpringBean other = (SpringBean)obj;
            return this.proxy.equals(other.proxy);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.proxy.hashCode();
    }
}
