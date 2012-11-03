/*
 * $$Id: CompositeInterceptor.java,v 1.2 2008/02/20 04:16:05 jacky Exp $$ 
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
package org.operamasks.faces.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;
import java.util.List;

import org.operamasks.cglib.proxy.MethodInterceptor;
import org.operamasks.cglib.proxy.MethodProxy;

public class CompositeInterceptor implements MethodInterceptor 
{
    private List<Interceptor> interceptors = new LinkedList<Interceptor>();
    
    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public Object intercept(Object target, Method method, Object[] args,
            MethodProxy proxy) throws Throwable {
        InvokeContext context = new InvokeContext(target, method, args, proxy);
        return invoke(context);
    }

    public Object invoke(InvokeContext context) throws Throwable {
        for (Interceptor interceptor : interceptors) {
            try {
                interceptor.invoke(context);
            } catch(UndeclaredThrowableException ue) {
                throw ue.getUndeclaredThrowable();
            } catch (Exception e) {
                throw e;
            }
        }
        
        if (context.hasReturnValue()) {
            return context.getReturnValue();
        }
        
        try {
            return invokeSuper(context);
        } catch(UndeclaredThrowableException ue) {
            throw ue.getUndeclaredThrowable();
        } catch (Exception e) {
            throw e;
        }
    }
    
	private Object invokeSuper(InvokeContext context) throws Throwable {
	    return context.getProxy().invokeSuper(context.getTarget(), context.getArgs());
	}

}
