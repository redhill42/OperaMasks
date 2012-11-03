/*
 * $Id: AjaxIndicatorRenderer.java,v 1.3 2007/07/02 07:38:06 jacky Exp $
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
import javax.faces.application.ViewHandler;
import java.io.IOException;

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.component.ajax.AjaxUpdater;

public class AjaxIndicatorRenderer extends HtmlRenderer
{
    private static final String DEFAULT_START_IMAGE = "/widget/image/loading.gif";
    private static final String DEFAULT_STOP_IMAGE = "/widget/image/done.gif";

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (isAjaxResponse(context))
            return; // not rerendered

        String clientId = component.getClientId(context);
        String startId = clientId + ".start";
        String stopId = clientId + ".stop";
        String startImage, stopImage;

        ResourceManager rm = ResourceManager.getInstance(context);
        ViewHandler vh = context.getApplication().getViewHandler();

        startImage = (String)component.getAttributes().get("startImage");
        if (startImage != null) {
            startImage = vh.getResourceURL(context, startImage);
        } else {
            startImage = rm.getSkinResourceURL(DEFAULT_START_IMAGE);
        }

        stopImage = (String)component.getAttributes().get("stopImage");
        if (stopImage != null) {
            stopImage = vh.getResourceURL(context, stopImage);
        } else {
            stopImage = rm.getSkinResourceURL(DEFAULT_STOP_IMAGE);
        }

        ResponseWriter out = context.getResponseWriter();

        // render start span
        out.startElement("img", null);
        out.writeAttribute("id", startId, null);
        out.writeAttribute("style", "display:none", null);
        out.writeAttribute("src", startImage, null);
        out.endElement("img");

        // render stop span
        out.startElement("img", null);
        out.writeAttribute("id", stopId, null);
        out.writeAttribute("src", stopImage, null);
        out.endElement("img");

        // add support script
        String renderId = getRenderId(component);
        Integer delay = (Integer)component.getAttributes().get("delay");

        String script =
            "OM.ajax.addStatusTarget(" +
                ((renderId != null) ? HtmlEncoder.enquote(renderId, '\'') : "null") +
            ",{" +
            "c:0," +
            "update:function(){" +
                "var a=document.getElementById('" + startId + "');" +
                "var b=document.getElementById('" + stopId + "');" +
                "if(this.c>0){" +
                    "a.style.display='';" +
                    "b.style.display='none';" +
                "}else{" +
                    "a.style.display='none';" +
                    "b.style.display='';" +
                "}" +
            "},";

        if (delay == null || delay <= 0) {
            script +=
                "onstart:function(){" +
                    "if(this.c++==0){" +
                        "this.update();" +
                    "}" +
                "},";
        } else {
            script +=
                "onstart:function(){" +
                    "if(this.c++==0){" +
                        "var _self=this;" +
                        "window.setTimeout(function(){_self.update();}," + delay + ");" +
                    "}" +
                "},";
        }

        script +=
            "onstop:function(){" +
                "if(--this.c==0){" +
                    "this.update();" +
                "}" +
            "}" +
            "});";

        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.write(script);
        out.endElement("script");
    }

    private String getRenderId(UIComponent component) {
        String renderId = (String)component.getAttributes().get("renderId");
        if (renderId != null) {
            return renderId;
        }

        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof AjaxUpdater) {
                renderId = ((AjaxUpdater)parent).getRenderId();
                break;
            }
            parent = parent.getParent();
        }
        return renderId;
    }
}
