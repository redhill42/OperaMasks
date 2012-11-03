/*
 * $Id: WindowRenderer.java,v 1.7 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import java.io.IOException;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;

public class WindowRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();

        String style = (String)component.getAttributes().get("style");
        if (style == null) style = "";
        style = "visibility:hidden;" + style;

        out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        out.writeAttribute("style", style, "style");
        renderPassThruAttributes(out, component, "width,height,style");
        out.write("\n");

        String icon = (String)component.getAttributes().get("icon");
        String label = (String)component.getAttributes().get("label");
        String labelStyle = (String)component.getAttributes().get("labelStyle");
        String labelStyleClass = (String)component.getAttributes().get("labelStyleClass");

        if (icon != null) {
            icon = context.getApplication().getViewHandler().getResourceURL(context, icon);
            out.startElement("img", component);
            out.writeURIAttribute("src", icon, "icon");
            out.writeAttribute("alt", "", null);
            out.endElement("img");
        }

        out.startElement("label", component);
        if (labelStyle != null)
            out.writeAttribute("style", labelStyle, "labelStyle");
        if (labelStyleClass != null)
            out.writeAttribute("class", labelStyleClass, "labelStyleClass");
        out.writeText(label, "label");
        out.endElement("label");
        out.write("\n");

        String contentPaneStyle = (String)component.getAttributes().get("contentPaneStyle");
        String contentPaneStyleClass = (String)component.getAttributes().get("contentPaneStyleClass");

        out.startElement("div", component);
        if (contentPaneStyle != null)
            out.writeAttribute("style", contentPaneStyle, "contentPaneStyle");
        if (contentPaneStyleClass != null)
            out.writeAttribute("class", contentPaneStyleClass, "contentPaneStyleClass");
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.endElement("div");
        out.endElement("div");
        out.write("\n");

        writeScript(context, component);
    }

    protected void writeScript(FacesContext context, UIComponent component)
        throws IOException
    {
        String jsvar = FacesUtils.getJsvar(context, component);
        StringBuilder buf = new StringBuilder();
        createScript(context, component, jsvar, buf);
        
        ResponseWriter out = context.getResponseWriter();
        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.writeAttribute("language", "Javascript", null);
        out.write(buf.toString());
        out.endElement("script");
    }
    
    protected void createScript(FacesContext context, UIComponent component,
                                String jsvar, StringBuilder buf)
    {
        buf.append("var ").append(jsvar)
           .append("= new ")
           .append(getClassName())
           .append("('")
           .append(component.getClientId(context))
           .append("');\n");

        buf.append(jsvar)
           .append(".setShowIcon(")
           .append(component.getAttributes().get("showIcon"))
           .append(").setCanMove(")
           .append(component.getAttributes().get("canMove"))
           .append(").setCanResize(")
           .append(component.getAttributes().get("canResize"))
           .append(").setCanMinimize(")
           .append(component.getAttributes().get("canMinimize"))
           .append(").setCanMaximize(")
           .append(component.getAttributes().get("canMaximize"))
           .append(").setCanClose(")
           .append(component.getAttributes().get("canClose"));

        Integer left = (Integer)component.getAttributes().get("left");
        Integer top = (Integer)component.getAttributes().get("top");
        Integer width = (Integer)component.getAttributes().get("width");
        Integer height = (Integer)component.getAttributes().get("height");

        if (left != null && left != Integer.MIN_VALUE) {
            buf.append(").setLeft(").append(left);
        }
        if (top != null && top != Integer.MIN_VALUE) {
            buf.append(").setTop(").append(top);
        }
        if (width != null && width != Integer.MIN_VALUE) {
            buf.append(").setWidth(").append(width);
        }
        if (height != null && height != Integer.MIN_VALUE) {
            buf.append(").setHeight(").append(height);
        }
        buf.append(");\n");

        String onbeforeclose = (String)component.getAttributes().get("onbeforeclose");
        if (onbeforeclose != null && onbeforeclose.length() != 0) {
            buf.append(jsvar);
            buf.append(".onbeforeclose=function(){");
            buf.append(onbeforeclose);
            if (!onbeforeclose.endsWith(";"))
                buf.append(';');
            buf.append("};");
        }

        String onclose = (String)component.getAttributes().get("onclose");
        if (onclose != null && onclose.length() != 0) {
            buf.append(jsvar);
            buf.append(".onclose=function(){");
            buf.append(onclose);
            if (!onclose.endsWith(";"))
                buf.append(';');
            buf.append("};");
        }

        Boolean show = (Boolean)component.getAttributes().get("show");
        if (show != null && show) {
            buf.append(jsvar);
            buf.append(".show();");
        }
    }

    protected String getClassName() {
        return "UIWindow";
    }

    public void provideResource(ResourceManager manager, UIComponent component) {
        WidgetResource.register(manager);
    }
}
