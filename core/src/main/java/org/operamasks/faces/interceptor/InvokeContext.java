/*
 * $$Id: InvokeContext.java,v 1.2 2008/02/18 14:02:02 jacky Exp $$ 
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

import org.operamasks.cglib.proxy.MethodProxy;

/**
 * 代理调用上下文
 *
 */
public class InvokeContext {
    private Object returnValue;
    private boolean isComplete;
    private Object target;
    private Method method;
    private Object[] args;
    private boolean hasReturnValue;
    private MethodProxy proxy;
    private Throwable exception;
    
    public InvokeContext(Object target, Method method, Object[] args, MethodProxy proxy) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.proxy = proxy;
    }
    
    /**
     * handler链的返回值
     * @return
     */
    public Object getReturnValue() {
        return returnValue;
    }
    public void setReturnValue(Object returnValue) {
        this.hasReturnValue = true;
        this.returnValue = returnValue;
    }
    /**
     * 是否不执行后续的handler
     * @return
     */
    public boolean isComplete() {
        return isComplete;
    }
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
    
    public boolean hasReturnValue() {
        return hasReturnValue;
    }
    
    public Object getTarget() {
        return target;
    }
    public Method getMethod() {
        return method;
    }
    public Object[] getArgs() {
        return args;
    }

    public MethodProxy getProxy() {
        return proxy;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
