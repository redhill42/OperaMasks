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

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.layout.impl.UIPanel;
import org.operamasks.faces.component.layout.impl.UIWindow;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

@ExtClass("Ext.Window")
@DependPackages("Ext.Window")
public class WindowRenderHandler extends LayoutRenderHandler
{
    @Override
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        out.writeAttribute("class", "x-hidden", null);
        
        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        String defaultStyle = "";
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
        
        
        UIWindow window = (UIWindow)component; 
        String title = window.getTitle();
        out.startElement("div", component);
        out.writeAttribute("class", "x-window-header", null);
        out.write(title == null ? "" :title);
        out.endElement("div");
        
        // window body div
        String bodyStyle = window.getBodyStyle();
        out.startElement("div", component);
        String styleExtra = "position:relative;";
        if (bodyStyle != null) {
            styleExtra += bodyStyle;
        }
        out.writeAttribute("style", styleExtra, null);
        out.writeAttribute("class", "x-window-body", null);
    }
    
    @Override
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.endElement("div");
        out.endElement("div");
        out.write("\n");
    }
    
    @Override
    protected void processExtConfig(FacesContext context,
            UIComponent component, ExtConfig config) {
        String jsvar = FacesUtils.getJsvar(context, component);
        config.set("layout", "fit");
        config.set("closeAction", "hide");
        config.set("keys",String.format("[{key: 27, fn: function(){%s.hide();}}]",jsvar),true);
    }
    
    @OperationListener("setModal")
    public void setTitle(UIWindow window, Boolean modal) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(context, window));
        cm.getAttributes().put("modal", modal);
        cm.invoke(context, "setModal", window);
    }
}
