/*
 * $$Id: AbstractInterceptor.java,v 1.2 2008/02/20 04:16:05 jacky Exp $$ 
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

public abstract class AbstractInterceptor implements Interceptor
{
	public void invoke(InvokeContext context) throws Throwable {
		boolean match = isMatch(context);
		try {
			if (match)
				beforeInvoke(context);
	        if (!context.isComplete()) {
	            doInvoke(context);
	        }
			if (match)
				afterInvoke(context);
		} catch (Throwable t) {
			onException(context, t);
		} finally {
		    doFinally(context);
		}
	}
	
	public abstract void beforeInvoke(InvokeContext context) throws Throwable;
	public abstract void afterInvoke(InvokeContext context) throws Throwable;
	
    public void doInvoke(InvokeContext context) throws Throwable {
        Object result = context.getProxy().invokeSuper(context.getTarget(), context.getArgs());
        context.setReturnValue(result);
    }
    
    public void onException(InvokeContext context, Throwable t) throws Throwable {
        throw t;
    }
    
    public void doFinally(InvokeContext context) throws Throwable {
        // do nothing, sub-class implements it if needed.
    }

	public boolean isMatch(InvokeContext context) {
		return true;
	}
}