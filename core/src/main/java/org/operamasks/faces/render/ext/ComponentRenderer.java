/*
 * $Id: ComponentRenderer.java,v 1.11 2008/02/19 07:51:56 lishaochuan Exp $
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

package org.operamasks.faces.render.ext;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.Container;
import org.operamasks.faces.render.ComponentBaseRenderer;
import org.operamasks.faces.render.HtmlBasicRenderer;
import org.operamasks.faces.render.ResourceRenderer;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;

/**
 * Ext相关组件的Renderer基类
 *
 */
public abstract class ComponentRenderer extends ComponentBaseRenderer implements HtmlBasicRenderer, ResourceRenderer {
    public void encodeBegin(FacesContext context, UIComponent component,
            	HtmlResponseWriter out) throws IOException {
    	if (!encodeMarkup())
    		return;
    	
       	out.startElement("div", component);
    	out.writeAttribute("id", component.getClientId(context), "clientId");
    	
    	String style = (String)component.getAttributes().get("style");
    	String styleClass = (String)component.getAttributes().get("styleClass");
    	if (style != null && style.length() > 0) {
            out.writeAttribute("style", style, "style");
        }
        if (styleClass != null && styleClass.length() > 0) {
            out.writeAttribute("class", styleClass, "class");
        }
    }
    
    public void encodeResourceBegin(ResourceManager rm,
            UIComponent component) {
    	YuiExtResource resource = YuiExtResource.register(rm, getPackageDependencies());
    	
        String beginScript = getBeginScript(resource, component);
        if (beginScript != null && !beginScript.equals(""))
        	resource.addInitScript(beginScript);
        
        defineComponent(component, resource);
        
        String initScript = getInitScript(component,
        		FacesUtils.getJsvar(getContext(), component));
        
        if (initScript != null && !initScript.equals(""))
        	resource.addInitScript(initScript);
        
        
    }
    
    protected String getInitScript(UIComponent component, String jsvar) {
		return null;
	}

	protected YuiExtResource getResourceInstance(ResourceManager rm) {
    	return YuiExtResource.register(rm, getPackageDependencies());
    }

	protected void defineComponent(UIComponent component, YuiExtResource resource) {
		String extClass = getExtClass(component);
		if (extClass == null || extClass.equals(""))
			return;
		
		String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
		resource.addVariable(jsvar);
		
		Formatter fmt = createFormatter();
		ConfigOptions configOptions = getConfigOptions(FacesContext.getCurrentInstance(), component);
		fmt.format("%s = new %s(%s);\n", jsvar, extClass, configOptions);
      
		resource.addInitScript(fmt.toString());
	}
    
    public void encodeResourceEnd(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = (YuiExtResource)rm.getRegisteredResource(YuiExtResource.RESOURCE_ID);
        
        String endScript = getEndScript(resource, component);
        if (endScript != null && !endScript.equals(""))
        	resource.addInitScript(endScript);
        
        String extClass = getExtClass(component);
        if (extClass != null && !extClass.equals("")) {
        	releaseComponentJsvar(component, resource);
        }
    }

	protected void releaseComponentJsvar(UIComponent component,
			YuiExtResource resource) {
		String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
		resource.releaseVariable(jsvar);
	}
    
    protected String getBeginScript(YuiExtResource resource, UIComponent component) {
    	return null;
    }
    
    protected Formatter createFormatter() {
    	return new Formatter(new StringBuilder());
	}
    
    protected Formatter createFormatter(StringBuffer buf) {
    	return new Formatter(buf);
	}

	protected String getEndScript(YuiExtResource resource, UIComponent component) {
	    UIComponent parent = component.getParent();
	    if(parent.getClass().getAnnotation(Container.class) != null){
	        Formatter fmt = createFormatter();
	        String parentJsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), parent);
	        String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
	        fmt.format("%s.add(%s);\n", parentJsvar, jsvar);
	        return fmt.toString();
	    }
	    return null;
    }

    /**
     * 得到Ext对应的类名称
     */
    protected abstract String getExtClass(UIComponent component);

    /**
     * 获取Ext的组件配置
     * @param component 
     * @param facesContext 
     */
    protected ConfigOptions getConfigOptions(FacesContext context, UIComponent component){
        return null;
    }
    
    protected ConfigOptions createConfigOptions() {
    	return createConfigOptions(null);
    }
    
    protected ConfigOptions createConfigOptions(UIComponent component) {
    	return Utils.createConfigOptions(component);
    }

	protected String[] getPackageDependencies() {
        return new String[0];
    }
    
    /**
     * 是否需要渲染一个markup，用做组件的容器
     */
    protected boolean encodeMarkup() {
        return true;
    }
    
    public void encodeChildren(FacesContext context, UIComponent component,
    		HtmlResponseWriter out) throws IOException {
    }
    
    public void encodeEnd(FacesContext context, UIComponent component,
    		HtmlResponseWriter out) throws IOException {
        if (!encodeMarkup())
            return;
        
        out.endElement("div");
        out.write("\n");
    }
    
    public void encodeResourceChildren(ResourceManager manager,
    		UIComponent component) {
    }
    
    public boolean getEncodeResourceChildren() {
    	return false;
    }
}
