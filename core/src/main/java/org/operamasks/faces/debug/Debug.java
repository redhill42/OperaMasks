/*
 * $$Id: Debug.java,v 1.6 2008/01/30 07:58:15 yangdong Exp $$
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

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class Debug {
	private static final String KEY_DEBUG_MODE = "org.operamasks.faces.DEBUG_MODE";
	private static final String NAME_DEBUG_LOGGER = "org.operamasks.faces.Debug";
	private static final String NAME_DEBUG_RESOURCES = "org.operamasks.faces.debug.Debug_Messages";
	
	private static String[] COMPONENT_LIFECYCLE_METHODS = new String[] {
		"encodeAll",
		"encodeBegin",
		"encodeChildren",
		"encodeEnd",
		"processDecodes",
		"processValidators",
		"processUpdates",
		"broadcast"
	};
	
	private static String[] RENDERER_LIFECYCLE_METHODS = new String[] {
		"encodeBegin",
		"encodeChildren",
		"encodeEnd",
		"decode"
	};

	static {
	    LogManager.getLogManager().addLogger(new DebugLogger(NAME_DEBUG_LOGGER, NAME_DEBUG_RESOURCES));
	}
	
	public static Map<DebugMode, Object> createDefaultDebugMode() {
	    Map<DebugMode, Object> debugMode = new HashMap<DebugMode, Object>();
		debugMode.put(DebugMode.RESTORE_VIEW, false);
		debugMode.put(DebugMode.APPLY_REQUEST_VALUES, false);
		debugMode.put(DebugMode.PROCESS_VALIDATIONS, false);
		debugMode.put(DebugMode.INVOKE_APPLICATION, false);
		debugMode.put(DebugMode.RENDER_RESPONSE, false);
		
		debugMode.put(DebugMode.INCLUDE_COMPONENT_IDS, new String[0]);
		debugMode.put(DebugMode.EXCLUDE_COMPONENT_IDS, new String[0]);
		debugMode.put(DebugMode.INCLUDE_COMPONENT_CLASSES, new String[0]);
		debugMode.put(DebugMode.EXCLUDE_COMPONENT_CLASSES, new String[0]);
		debugMode.put(DebugMode.INCLUDE_COMPONENT_METHODS, new String[0]);
		debugMode.put(DebugMode.EXCLUDE_COMPONENT_METHODS, new String[0]);
		
		debugMode.put(DebugMode.INCLUDE_RENDERER_TYPES, new String[0]);
		debugMode.put(DebugMode.EXCLUDE_RENDERER_TYPES, new String[0]);
		debugMode.put(DebugMode.INCLUDE_RENDERER_METHODS, new String[0]);
		debugMode.put(DebugMode.EXCLUDE_RENDERER_METHODS, new String[0]);

		debugMode.put(DebugMode.EXCEPTION, false);
		debugMode.put(DebugMode.UNCOMPRESSED_JS, false);
		debugMode.put(DebugMode.MISC, false);
		
		return debugMode;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isEnabled(FacesContext context, DebugMode mode) {
		Object obj = getValue(mode);
		
		if (obj == null)
			return false;
		
		if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue();
		
		return true;
	}
	
	public static Object getValue(FacesContext context, DebugMode mode) {
		return getDebugMode(context).get(mode);
	}

	@SuppressWarnings("unchecked")
	private static Map<DebugMode, Object> getDebugMode(FacesContext context) {
		Map<DebugMode, Object> debugMode = (Map<DebugMode, Object>)context.getExternalContext(
				).getApplicationMap().get(KEY_DEBUG_MODE);
		
		if (debugMode == null) {
			debugMode = createDefaultDebugMode();
			context.getExternalContext().getApplicationMap().put(KEY_DEBUG_MODE, debugMode);
		}
		
		return debugMode;
	}
	
	public static boolean isEnabled(DebugMode mode) {
		return isEnabled(FacesContext.getCurrentInstance(), mode);
	}
	
	public static Object getValue(DebugMode mode) {
		return getValue(FacesContext.getCurrentInstance(), mode);
	}
	
	public static void setValue(DebugMode mode, Object value) {
		getDebugMode(FacesContext.getCurrentInstance()).put(mode, value);
	}
	
	public static void setValue(FacesContext context, DebugMode mode, Object value) {
		getDebugMode(context).put(mode, value);
	}
	
	public static Logger getLogger() {
		return Logger.getLogger(NAME_DEBUG_LOGGER);
	}
	
	private static class DebugLogger extends Logger {
		protected DebugLogger(String name, String resourceBundleName) {
			super(name, resourceBundleName);
			addHandler(new DebugLogHandler());
		}
		
		@Override
		public void log(LogRecord record) {
			record.setSourceClassName(NAME_DEBUG_LOGGER);
			record.setSourceMethodName(null);
			
			super.log(record);
		}
	}
	
	private static final String KEY_DEBUGLOGHANDLER_OUTPUTSTREAM = "org.operamasks.faces.DebugLogHander.outputStream";
	private static class DebugLogHandler extends StreamHandler {
		@Override
		public synchronized void publish(LogRecord record) {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context == null)
				return;
			
			OutputStream out = (OutputStream)context.getExternalContext(
					).getRequestMap().get(KEY_DEBUGLOGHANDLER_OUTPUTSTREAM);
			if (out == null) {
				out = new ByteArrayOutputStream(); 
				context.getExternalContext().getRequestMap().put(
						KEY_DEBUGLOGHANDLER_OUTPUTSTREAM, out);
			}
			setOutputStream(out);
			
			super.publish(record);
		}
	}
	
	public static String getServerLog(FacesContext context) {
		OutputStream out = (OutputStream)context.getExternalContext(
			).getRequestMap().get(KEY_DEBUGLOGHANDLER_OUTPUTSTREAM);
		
		if (out != null)
			return out.toString();
		
		return null;
	}
	
	public static String getServerLog() {
		return getServerLog(FacesContext.getCurrentInstance());
	}

	public static final DebugMode[] DEBUG_MODE_LIFECYCLE_PHASES = new DebugMode[] {
			DebugMode.RESTORE_VIEW,
			DebugMode.APPLY_REQUEST_VALUES,
			DebugMode.PROCESS_VALIDATIONS,
			DebugMode.UPDATE_MODE_VALUES,
			DebugMode.INVOKE_APPLICATION,
			DebugMode.RENDER_RESPONSE
	};

	@SuppressWarnings("unchecked")
	public static boolean isDebugComponent(Class componentClass) {
		if (!UIComponent.class.isAssignableFrom(componentClass))
			return false;
		
		String[] includeComponentClasses = (String[])getValue(DebugMode.INCLUDE_COMPONENT_CLASSES);
		String[] excludeComponentClasses = (String[])getValue(DebugMode.EXCLUDE_COMPONENT_CLASSES);
		String componentClassName = componentClass.getName();
		
		if (isMatch(excludeComponentClasses, componentClassName))
			return false;
		
		if (isMatch(includeComponentClasses, componentClassName))
			return true;
		
		return false;
	}

	private static boolean isMatch(String[] regexes, String value) {
		for (String regex : regexes) {
			if (Pattern.matches(regex, value))
				return true;
		}
		
		return false;
	}

	public static boolean isDebugRenderer(String rendererType) {
		String[] includeRendererTypes = (String[])getValue(DebugMode.INCLUDE_RENDERER_TYPES);
		String[] excludeRendererTypes = (String[])getValue(DebugMode.EXCLUDE_RENDERER_TYPES);
		
		if (isMatch(excludeRendererTypes, rendererType))
			return false;
		
		if (isMatch(includeRendererTypes, rendererType))
			return true;
		
		return false;
	}
	
	public static boolean isDebugComponentMethod(Method method, UIComponent component) {
		String[] includeComponentIds = (String[])getValue(DebugMode.INCLUDE_COMPONENT_IDS);
		String[] excludeComponentIds = (String[])getValue(DebugMode.EXCLUDE_COMPONENT_IDS);
		String[] includeComponentMethods = (String[])getValue(DebugMode.INCLUDE_COMPONENT_METHODS);
		String[] excludeComponentMethods = (String[])getValue(DebugMode.EXCLUDE_COMPONENT_METHODS);

		String methodName = method.getName();
		if (!isBelongTo(COMPONENT_LIFECYCLE_METHODS, methodName))
			return false;
		
		if (isMatch(excludeComponentMethods, methodName))
			return false;
		
		String componentId = getComponentId(component);
		
		if (isMatch(excludeComponentIds, componentId))
			return false;
		
		if (isMatch(includeComponentMethods, methodName) &&
				isMatch(includeComponentIds, componentId))
			return true;
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private static String getComponentId(UIComponent component) {
		String componentId = "???";
		if (component instanceof UIComponentBase) {
			Class componentBaseClass = component.getClass();
			while (!componentBaseClass.equals(UIComponentBase.class) &&
					!componentBaseClass.equals(Object.class)) {
				componentBaseClass = componentBaseClass.getSuperclass();
			}
			
			if (componentBaseClass.equals(UIComponentBase.class)) {
				try {
					Field idField = componentBaseClass.getDeclaredField("id");
					idField.setAccessible(true);
					componentId = (String)idField.get(component);
					idField.setAccessible(false);
				} catch (Exception e) {
					// Can't determine component id
				}
			}
		}
		
		if (componentId == null)
			componentId = "???";
		
		return componentId;
	}
	
	private static boolean isBelongTo(String[] valueSet, String value) {
		for (String aValue : valueSet) {
			if (aValue.equals(value))
				return true;
		}
		
		return false;
	}

	public static boolean isDebugRendererMethod(Method method, Object[] args) {
		String[] includeComponentIds = (String[])getValue(DebugMode.INCLUDE_COMPONENT_IDS);
		String[] excludeComponentIds = (String[])getValue(DebugMode.EXCLUDE_COMPONENT_IDS);
		String[] includeRendererMethods = (String[])getValue(DebugMode.INCLUDE_RENDERER_METHODS);
		String[] excludeRendererMethods = (String[])getValue(DebugMode.EXCLUDE_RENDERER_METHODS);

		String methodName = method.getName();
		if (!isBelongTo(RENDERER_LIFECYCLE_METHODS, methodName))
			return false;
		
		if (isMatch(excludeRendererMethods, methodName))
			return false;
		
		UIComponent component = (UIComponent)args[1];
		String componentId = getComponentId(component);
		
		if (isMatch(excludeComponentIds, componentId))
			return false;
		
		if (isMatch(includeRendererMethods, methodName) &&
				isMatch(includeComponentIds, componentId))
			return true;
		
		return false;
	}
}