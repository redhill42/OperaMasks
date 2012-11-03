/*
 * $Id: ExtJsUtils.java,v 1.4 2008/01/07 09:41:56 yangdong Exp $
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
 *
 */
package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;

public class ExtJsUtils {
    public static final String DIV_CONTAINER_POSTFIX = "_div";

    public static void encodeContainerForComponent(FacesContext context, UIComponent component,
            String style, String styleClass) throws IOException {
    	encodeContainerForComponentBegin(context, component, style, styleClass);
        encodeContainerForComponentEnd(context, component, style, styleClass);
    }
    
	public static void encodeContainerForComponentBegin(FacesContext context, UIComponent component, String style, String styleClass) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
    
        ResponseWriter out = context.getResponseWriter();
        
		out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        
        if (style != null && style.length() > 0) {
            out.writeAttribute("style", style, "style");
        }
        
        if (styleClass != null && styleClass.length() > 0) {
            out.writeAttribute("class", styleClass, "class");
        }
	}

	public static void encodeContainerForComponentEnd(FacesContext context, UIComponent component,
            String style, String styleClass) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
    
        ResponseWriter out = context.getResponseWriter();
        
		out.endElement("div");
	}
    
	@SuppressWarnings("unchecked")
	public static String createJsArray(Map<String, Object> config) {
        if (config.size() == 0)
            return "{}";
        
        StringBuilder buf = new StringBuilder();
        buf.append("{\n");
        
        for (String key : config.keySet()) {
            Object value = config.get(key);
            if (value == null)
                continue;
            
            buf.append(key);
            buf.append(" : ");
            
            if (value instanceof Boolean || value instanceof JsObject) {
                buf.append(value.toString());
            } else if (value instanceof Object[][]) {
            	Object[][] array = (Object[][])value;
            	Map<String, Object> nestedConfig = new HashMap<String, Object>();
            	
            	for (Object[] data : array) {
            		if (data.length < 2)
            			continue;
            		
            		nestedConfig.put((String)data[0], data[1]);
            	}
            	
            	buf.append(createJsArray(nestedConfig));
            } else if (value instanceof Map){
            	buf.append(createJsArray((Map<String, Object>)value));
            } else {
                buf.append("\"").append(value.toString()).append("\"");
            }
            
            buf.append(",\n");
        }
        
        // remove last ',\n'
        if (buf.length() != 2)
            buf.delete(buf.length() - 2, buf.length());
        
        buf.append("\n}");
        
        return buf.toString();
    }
    
    public static class JsObject {
        private String value;
        public JsObject(String value) {
            this.value = value;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value;
        }
    }
    
	public static String getComponentSource(FacesContext context, UIComponent component) {
		return "Ext.get('" + component.getClientId(context) + "').dom";
	}
	
	public static String createJsArray(UIComponent component, String[] propertyNames) {
		return createJsArray(component, propertyNames, null);
	}
	
	public static String createJsArray(UIComponent component, String[] propertyNames, String[] jsObjectNames) {
		Map<String, Object> properties = new HashMap<String, Object>();
		
		for (String name : propertyNames) {
			Object value = component.getAttributes().get(name);
			
			if ((value instanceof String) && isJsObject(name, jsObjectNames)) {
				value = new ExtJsUtils.JsObject((String)value);
			}
			
			properties.put(name, value);
		}				

		return createJsArray(properties);
	}

	private static boolean isJsObject(String name, String[] jsObjectNames) {
		if (jsObjectNames == null || jsObjectNames.length == 0)
			return false;
		
		for (String jsObjectName : jsObjectNames) {
			if (name.equals(jsObjectName))
				return true;
		}
		
		return false;
	}

	public static void applyToContainer(StringBuilder buf, FacesContext context, String jsvar,
					UIComponent component) {
		applyToContainer(buf, context, jsvar, component, null);
	}
	
	public static void applyToContainer(Formatter fmt, FacesContext context, String jsvar,
			UIComponent component) {
		applyToContainer(fmt, context, jsvar, component, null);
	}
	
	public static void applyToContainer(Formatter fmt, FacesContext context, String jsvar,
				UIComponent component, String applyToClientId) {
		if (component.getParent() instanceof UIToolBar ||
				component.getParent() instanceof UIPagingToolbar)
			return;
		
		if (applyToClientId == null) {
			applyToClientId = component.getClientId(context);
		}
		
	    fmt.format("if(Ext.get('%1$s')) {\n" +
	    		"%2$s.applyToMarkup('%1$s')\n" +
	    		"}\n",
	    		applyToClientId, jsvar 
	    );
	}
	
	public static void applyToContainer(StringBuilder buf, FacesContext context, String jsvar,
			UIComponent component, String applyToClientId) {
		applyToContainer(new Formatter(buf), context, jsvar, component, applyToClientId);
	}

	public static Lifecycle getLifecycle() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory)
				FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		String lifecycleId = getFacesContext().getExternalContext(
				).getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
	        if (lifecycleId == null) {
	            lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
	        }
	        
	    return lifecycleFactory.getLifecycle(lifecycleId);
	}
	
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
}