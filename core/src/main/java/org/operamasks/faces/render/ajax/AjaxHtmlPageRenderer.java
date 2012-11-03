/*
 * $Id: AjaxHtmlPageRenderer.java,v 1.15 2008/04/22 14:35:19 jacky Exp $
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

package org.operamasks.faces.render.ajax;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.html.HtmlPage;
import org.operamasks.faces.render.html.HtmlPageRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public class AjaxHtmlPageRenderer extends HtmlPageRenderer implements ResourceProvider
{
    @Override
    public void encodePageBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        super.encodePageBegin(context, component);
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

    @Override
    public void encodePageEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        super.encodePageEnd(context, component);

        ResponseWriter writer = context.getResponseWriter();
        if (writer instanceof AjaxHtmlResponseWriter) {
            // In an Ajax response we write view state only once at end of page.
            // Don't use ViewHandler.writeState(), which is used by form renderer
            // to write a hidden field that send with a form. Instead call
            // AjaxHtmlResponseWriter to write the view state. The view state will
            // send to server by Javascript code.
            ((AjaxHtmlResponseWriter)writer).writeState(context);
        }
    }
    
    public void provideResource(ResourceManager manager, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(manager);
        Boolean loadMask = (Boolean) component.getAttributes().get("loadMask");
        if(loadMask != null && loadMask) {
            if (!((HtmlPage) component).hasParentPage()) {
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

}
