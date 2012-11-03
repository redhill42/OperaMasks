/*
 * $Id: AjaxStatusRenderer.java,v 1.5 2007/07/02 07:37:53 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxStatusRenderer extends HtmlRenderer
{
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (isAjaxResponse(context))
            return; // not rerendered

        ResponseWriter out = context.getResponseWriter();

        UIComponent startFacet = getFacet(component, "start");
        UIComponent stopFacet = getFacet(component, "stop");

        String clientId = component.getClientId(context);
        String startId = clientId + ".start";
        String stopId = clientId + ".stop";

        String layout = (String)component.getAttributes().get("layout");
        String tag = (layout != null && layout.equals("block")) ? "div" : "span";

        String style = (String)component.getAttributes().get("style");
        String styleClass =(String)component.getAttributes().get("styleClass");

        // render start panel with hidden
        if (startFacet != null) {
            String startStyle = (String)component.getAttributes().get("startStyle");
            if (startStyle == null)
                startStyle = style;
            if (startStyle == null) {
                startStyle = "display: none";
            } else {
                startStyle += "; display: none";
            }

            String startStyleClass = (String)component.getAttributes().get("startStyleClass");
            if (startStyleClass == null)
                startStyleClass = styleClass;

            out.startElement(tag, component);
            out.writeAttribute("id", startId, "clientId");
            out.writeAttribute("style", startStyle, "style");
            if (startStyleClass != null)
                out.writeAttribute("class", startStyleClass, "startStyleClass");
            startFacet.encodeAll(context);
            out.endElement(tag);
        }

        // render stop panel
        if (stopFacet != null) {
            String stopStyle = (String)component.getAttributes().get("stopStyle");
            if (stopStyle == null)
                stopStyle = style;
            String stopStyleClass = (String)component.getAttributes().get("stopStyleClass");
            if (stopStyleClass == null)
                stopStyleClass = styleClass;

            out.startElement(tag, component);
            out.writeAttribute("id", stopId, "clientId");
            if (stopStyle != null)
                out.writeAttribute("style", stopStyle, "style");
            if (stopStyleClass != null)
                out.writeAttribute("class", stopStyleClass, "stopStyleClass");
            stopFacet.encodeAll(context);
            out.endElement(tag);
        }

        // create support script to handle status events
        String renderId = getRenderId(component);
        String onstart = (String)component.getAttributes().get("onstart");
        String onstop = (String)component.getAttributes().get("onstop");

        StringBuilder buf = new StringBuilder();
        buf.append("OM.ajax.addStatusTarget(");
        if (renderId != null) {
            buf.append(HtmlEncoder.enquote(renderId, '\''));
        } else {
            buf.append("null");
        }

        buf.append(",{");
        buf.append("onstart:function(){");
        if (startFacet != null) {
            buf.append("document.getElementById('");
            buf.append(startId).append("').style.display='';");
        }
        if (stopFacet != null) {
            buf.append("document.getElementById('");
            buf.append(stopId).append("').style.display='none';");
        }
        if (onstart != null) {
            buf.append(onstart);
            if (!onstart.trim().endsWith(";"))
                buf.append(';');
        }
        buf.append("},");  // XXX comma

        buf.append("onstop:function(){");
        if (startFacet != null) {
            buf.append("document.getElementById('");
            buf.append(startId).append("').style.display='none';");
        }
        if (stopFacet != null) {
            buf.append("document.getElementById('");
            buf.append(stopId).append("').style.display='';");
        }
        if (onstop != null) {
            buf.append(onstop);
            if (!onstop.trim().endsWith(";"))
                buf.append(';');
        }
        buf.append("}");

        buf.append("});");

        UIForm form = getParentForm(component);
        if (form != null) {
            FormRenderer.addSupportScript(form, buf.toString());
        } else {
            out.startElement("script", component);
            out.writeAttribute("type", "text/javascript", null);
            out.write(buf.toString());
            out.endElement("script");
        }
    }

    private UIComponent getFacet(UIComponent component, String name) {
        UIComponent facet = component.getFacet(name);
        return (facet != null && facet.isRendered()) ? facet : null;
    }

    private String getRenderId(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof AjaxUpdater) {
                return ((AjaxUpdater)parent).getRenderId();
            }
            parent = parent.getParent();
        }
        return null;
    }
}
