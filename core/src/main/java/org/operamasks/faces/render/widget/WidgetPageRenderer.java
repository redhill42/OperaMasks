/*
 * $Id: WidgetPageRenderer.java,v 1.9 2007/08/14 05:47:55 jacky Exp $
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
import java.util.Formatter;

import org.operamasks.faces.render.html.HtmlPageRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public class WidgetPageRenderer extends HtmlPageRenderer implements ResourceProvider
{
    @Override
    protected void encodeBodyBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        String skin = SkinManager.getCurrentSkin(context);
        String styleClass = (String)component.getAttributes().get("styleClass");
        String skinClass = "skin-" + skin;

        if (styleClass != null) {
            if (styleClass.indexOf(skinClass) != -1) {
                skinClass = styleClass;
            } else {
                skinClass += " " + styleClass;
            }
        }

        component.getAttributes().put("styleClass", skinClass);
        super.encodeBodyBegin(context, component);
        component.getAttributes().put("styleClass", styleClass);
        Boolean loadMask = (Boolean) component.getAttributes().get("loadMask");
        if(loadMask!=null && loadMask) {
            String clientId = component.getClientId(context);
            ResponseWriter out = context.getResponseWriter();
            out.startElement("div", component);
            out.writeAttribute("id", clientId+"_loadMask", "clientId");
            out.writeAttribute("class", "loading-mask", "class");
            out.endElement("div");

            out.startElement("div", component);
            out.writeAttribute("id", clientId+"_loading", "clientId");
            out.writeAttribute("class", "loading", "class");
            out.startElement("div", component);
            out.writeAttribute("class", "loading-indicator", "class");
            out.write("Loading...");
            out.endElement("div");
            out.endElement("div");
        }
    }

    public void provideResource(ResourceManager manager, UIComponent component) {
        Boolean loadMask = (Boolean) component.getAttributes().get("loadMask");
        if(loadMask!=null && loadMask) {
            YuiExtResource resource = YuiExtResource.register(manager);
            FacesContext context = FacesContext.getCurrentInstance();
            String clientId = component.getClientId(context);
            StringBuilder buf = new StringBuilder();
            Formatter fmt = new Formatter(buf);
            String loadingId = clientId + "_loading";
            String maskId = clientId + "_loadMask";
            String loadingVar = resource.allocVariable(component)+"_loading";
            String maskVar = resource.allocVariable(component)+"_mask";
            fmt.format( "var %s = Ext.get('%s');", loadingVar, loadingId);
            fmt.format( "var %s = Ext.get('%s');", maskVar, maskId);
            fmt.format("%s.setOpacity(.8);",maskVar);
            fmt.format("%s.shift({",maskVar);
            fmt.format("xy:%s.getXY(),",loadingVar);
            fmt.format("width:%s.getWidth(),",loadingVar);
            fmt.format("height:%s.getHeight(),",loadingVar);
            buf.append("remove:true,");
            String duration = (String) component.getAttributes().get("duration");
            if(duration == null) duration = "1";
            fmt.format("duration:%s,",duration);
            buf.append("opacity:.3,");
            buf.append("callback : function(){");
            fmt.format("%s.fadeOut({duration:.2,remove:true});}});",loadingVar);
            resource.addInitScript(fmt.toString());
        }
    }
}
