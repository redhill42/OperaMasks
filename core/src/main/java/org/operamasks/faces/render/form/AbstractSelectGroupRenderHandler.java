/*
 * $Id: AbstractSelectGroupRenderHandler.java,v 1.2 2008/04/29 07:40:25 lishaochuan Exp $
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

package org.operamasks.faces.render.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.ext.AbstractRenderHandler;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

@DependPackages("Ext.form")
public abstract class AbstractSelectGroupRenderHandler extends AbstractRenderHandler {
	private final static String CONTAINER = "_container";
	protected List<String> jsvarNames = new ArrayList<String>();

	@EncodeHtmlBegin
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        String clientId = component.getClientId(context);
        String containerId = clientId + CONTAINER;
        out.startElement("div", null);
        out.writeAttribute("id", containerId, null);
        out.write(buildHtmls(context,component));
        out.endElement("div");
    }
	
	@EncodeResourceBegin
    public void resourceBegin(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        resource.allocVariable(component);
        resource.addInitScript(buildScripts(context, component));
    }
	
	@EncodeAjaxBegin
	public void ajaxResponse(FacesContext context, UIComponent component) throws IOException {
		String clientId = component.getClientId(context);
        String containerId = clientId + CONTAINER;
		Formatter fmt = new Formatter(new StringBuffer());
		fmt.format("OM.T(\"%s\",\"%s\");\n", containerId, buildHtmls(context, component));
		fmt.format(buildScripts(context,component));
		
		AjaxResponseWriter out = (AjaxResponseWriter) context.getResponseWriter();
		out.writeScript(fmt.toString());
	}
	
	
	abstract protected String buildHtmls(FacesContext context, UIComponent component);
	abstract protected String buildScripts(FacesContext context, UIComponent component);
	
	protected String generateSelectBoxId(String prefix, int index){
		return prefix + "_" + index;
	}
	
	@OperationListener("show")
	public void show(UIComponent component) throws IOException{
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String containerId = clientId + CONTAINER;
        addOperationScript("document.getElementById('" + containerId + "').style.display='block';");
    }
	
	@OperationListener("hide")
	public void hide(UIComponent component) throws IOException{
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String containerId = clientId + CONTAINER;
        addOperationScript("document.getElementById('" + containerId + "').style.display='none';");
    }
	
	@OperationListener("enable")
	public void enable(UIComponent component) throws IOException{
        StringBuffer sb = new StringBuffer();
		for(String jsvarName : jsvarNames){
			sb.append(jsvarName).append(".enable();\n");
		}
        addOperationScript(sb.toString());
    }
	
	@OperationListener("disable")
	public void disable(UIComponent component) throws IOException{
        StringBuffer sb = new StringBuffer();
		for(String jsvarName : jsvarNames){
			sb.append(jsvarName).append(".disable();\n");
		}
        addOperationScript(sb.toString());
    }
	
	@OperationListener("focus")
	public void focus(UIComponent component) throws IOException{
		if(jsvarNames.size() > 0){
			StringBuffer sb = new StringBuffer();
			sb.append(jsvarNames.get(0)).append(".focus();\n");
	        addOperationScript(sb.toString());
		}
    }
}
