/*
 * $Id 
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
package org.operamasks.faces.render.layout;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.EncodeInitScript;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.EncodeResourceEnd;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.annotation.component.PropertyListener;
import org.operamasks.faces.component.AjaxActionEventHanlder;
import org.operamasks.faces.component.layout.impl.UILayout;
import org.operamasks.faces.component.layout.impl.UIPanel;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.ext.AbstractRenderHandler;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;

@DependPackages("Ext.Layout")
public abstract class LayoutRenderHandler extends AbstractRenderHandler {
    protected static final String FULL_WIDTH_HEIGHT = "width:100%;height:100%;";
    
    @EncodeHtmlBegin
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        
        out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), null);
        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        String defaultStyle = "";
        if(!isContainer(component.getParent()) || isPanel(component)){
            defaultStyle = FULL_WIDTH_HEIGHT;
        }
        if (style != null && style.length() > 0) {
            style = defaultStyle + style;
        } else {
            style = defaultStyle;
        }
        out.writeAttribute("style", style, "style");
        if (styleClass != null && styleClass.length() > 0) {
            out.writeAttribute("class", styleClass, "class");
        }
        out.write("\n");
    }
    
    private boolean isPanel(UIComponent component) {
        return component instanceof UIPanel && !(component instanceof UILayout);
    }

    @EncodeHtmlEnd
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.endElement("div");
        out.write("\n");
    }

    @EncodeResourceBegin
    public void resourceBegin(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        String jsvar = resource.allocVariable(component);
        Formatter fmt = new Formatter(new StringBuffer());
        fmt.format("%s = new %s({", jsvar, getExtClass(component));
        ExtConfig config = new ExtConfig(component);
        if (!isContainer(component.getParent())) {
            config.set("applyTo", component.getClientId(context));
        }
        processExtConfig(context, component, config);
        String configStr = config.toScript();
        fmt.format(configStr);
        if (hasItemChildren(component)) {
            fmt.format(",items:[\n");
        }
        resource.addInitScript(fmt.toString());
    }

    private boolean hasItemChildren(UIComponent component) {
        for (UIComponent c : component.getChildren()) {
            if (isContainerItem(c)) {
                return true;
            }
            if(hasItemChildren(c)){
                return true;
            }
        }
        return false;
    }

    @EncodeResourceEnd
    public void resourceEnd(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        String script = "";
        if (hasItemChildren(component)) {
            script += "]\n";
        }
        script += "})\n";
        UIComponent parent = component.getParent();
        if (isContainer(parent) && !component.equals(parent.getChildren().get(parent.getChildCount() - 1))) {
            script += ",";
        }
        resource.addInitScript(script);
    }
    
    @EncodeInitScript
    public void initScript(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        AjaxActionEventHanlder handler = new AjaxActionEventHanlder(component);
        resource.addInitScript(handler.toScript());
    }
    
    protected void processExtConfig(FacesContext context, UIComponent component, ExtConfig config) {
        // do nothing, sub class can overwrite it
    }

    @PropertyListener
    public void propertyChanged(PropertyChangeEvent event) {
//        System.out.println("property name==>" + event.getPropertyName());
//        System.out.println("oldValue==>" + event.getOldValue());
//        System.out.println("newValue==>" + event.getNewValue());
    }

    @OperationListener("setTitle")
    public void setTitle(UIPanel panel, String title) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(context, panel));
        cm.getAttributes().put("title", title);
        cm.invoke(context, "setTitle", panel);
    }
}
