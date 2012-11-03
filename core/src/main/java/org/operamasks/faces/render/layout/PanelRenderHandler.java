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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.layout.impl.UIPanel;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.html.HtmlResponseWriter;

@ExtClass("Ext.Panel")
public class PanelRenderHandler extends LayoutRenderHandler
{
    @Override
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
        super.htmlBegin(context, component);
        if(!isContainer(component.getParent())){
            HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
            out.startElement("div", component);
            out.writeAttribute("id", getSinglePanelContentId(context, component), null);
            out.writeAttribute("style", FULL_WIDTH_HEIGHT, null); 
        }
    }
    
    protected String getSinglePanelContentId(FacesContext context, UIComponent component){
        return component.getClientId(context) + "_content";
    }
    
    @Override
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
        if(!isContainer(component.getParent())){
            HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
            out.endElement("div");
        }
        super.htmlEnd(context, component);
    }
    
    @Override
    protected void processExtConfig(FacesContext context,
            UIComponent component, ExtConfig config) {
        UIPanel panel = (UIPanel)component;
        String title = panel.getTitle();
        if(title == null){
            title = " ";
        }
        config.set("layout", "fit");
        config.set("title", title);
        if(!isContainer(component.getParent())){
            config.set("contentEl", getSinglePanelContentId(context, component));
        }else{
            config.set("contentEl", component.getClientId(context));
        }
        
        //inner borderLayout
        if("east".equals(panel.getRegion()) || "west".equals(panel.getRegion())){
            if(panel.getWidth() == null){
                config.set("width", 100);
            }
            if(panel.getHeight() == null){
                config.set("height", 100);
            }
        }
    }
}
