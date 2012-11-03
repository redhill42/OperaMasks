/*
 * $$Id: DebugInterceptor.java,v 1.9 2008/02/18 14:02:02 jacky Exp $$ 
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

package org.operamasks.faces.debug;

import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.render.Renderer;

import org.operamasks.faces.interceptor.AbstractInterceptor;
import org.operamasks.faces.interceptor.InvokeContext;

public class DebugInterceptor extends AbstractInterceptor {
	private static final String KEY_AFTER_METHOD_CALL = "AFTER_METHOD_CALL";
	private static final String KEY_BEFORE_METHOD_CALL = "BEFORE_METHOD_CALL";
    private long startTime;

	public DebugInterceptor() {}
	
	private void beforeMethodCall(Object obj, Method method, UIComponent component, String resourceKey) {
		Debug.getLogger().log(Level.INFO, resourceKey,
				new Object[] {getTargetClassName(obj), method.getName(),
						component.getId()});
	}
	
	private void afterMethodCall(Object obj, Method method, UIComponent component,
			long processTime, String resourceKey) {
		Debug.getLogger().log(Level.INFO, resourceKey,
				new Object[] {getTargetClassName(obj), method.getName(),
						component.getId(), processTime});
	}

	private String getTargetClassName(Object proxy) {
		String className = proxy.getClass().getName();
		if (className.startsWith("$"))
			className = className.substring(1);
		
		int enhanceFlagIndex = className.indexOf("$$");
		if (enhanceFlagIndex != -1) {
			className = className.substring(0, enhanceFlagIndex);
		}
		
		return className;
	}
	
	public void beforeInvoke(InvokeContext context) throws Throwable {
        startTime = System.currentTimeMillis();
		Object obj = context.getTarget();
		Method method = context.getMethod();
		Object[] args = context.getArgs();
		
		if (obj instanceof UIComponent) {
			beforeMethodCall(obj, method, (UIComponent)obj, KEY_BEFORE_METHOD_CALL);
		}
		
		if (obj instanceof Renderer) {
			beforeMethodCall(obj, method, (UIComponent)args[1], KEY_BEFORE_METHOD_CALL);
		}
	}
	
	@Override
	public boolean isMatch(InvokeContext context) {
		Object obj = context.getTarget();
		Method method = context.getMethod();
		Object[] args = context.getArgs();
		
		if (obj instanceof UIComponent)
			return Debug.isDebugComponentMethod(method, (UIComponent)obj);
		
		if (obj instanceof Renderer) {
			return Debug.isDebugRendererMethod(method, args);
		}

		return super.isMatch(context);
	}
	
	@Override
	public void onException(InvokeContext context, Throwable t)
			throws Throwable {
		if (Debug.isEnabled(DebugMode.EXCEPTION))
			Debug.getLogger().log(Level.INFO, context.getTarget().getClass().getName() +
					"." + context.getMethod().getName() + " throws a exception", t);
		
		throw t;
	}

	@Override
	public void afterInvoke(InvokeContext context) throws Throwable {
        long processTime = System.currentTimeMillis() - startTime;
        Object obj = context.getTarget();
        Method method = context.getMethod();
        Object[] args = context.getArgs();
        
        if (obj instanceof UIComponent) {
            afterMethodCall(obj, method, (UIComponent)obj, processTime, KEY_AFTER_METHOD_CALL);
        }
        
        if (obj instanceof Renderer) {
            afterMethodCall(obj, method, (UIComponent)args[1], processTime, KEY_AFTER_METHOD_CALL);
        }
	}
}