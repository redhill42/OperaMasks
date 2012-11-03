/*
 * $Id: CommandButtonRenderer.java,v 1.6 2007/09/19 08:02:51 daniel Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import java.util.Map;
import java.io.IOException;

import org.operamasks.faces.util.FacesUtils;

public class CommandButtonRenderer extends HtmlRenderer
{
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (!paramMap.containsKey(clientId) &&
            !paramMap.containsKey(clientId+".x") &&
            !paramMap.containsKey(clientId+".y"))
            return;

        String type = (String)component.getAttributes().get("type");
        if (type != null && type.equalsIgnoreCase("reset"))
            return;

        ActionEvent actionEvent = new ActionEvent(component);
        component.queueEvent(actionEvent);
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        String clientId = component.getClientId(context);
        String type = (String)component.getAttributes().get("type");
        if (type == null) type = "submit";

        String label = getLabel(context, component);
        String imageSrc = (String)component.getAttributes().get("image");

        ResponseWriter out = context.getResponseWriter();
        out.startElement("input", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (imageSrc != null) {
            out.writeAttribute("type", "image", "type");
            out.writeAttribute("name", clientId, "clientId");
            out.writeURIAttribute("src", src(context, imageSrc), "image");
        } else {
            out.writeAttribute("type", type.toLowerCase(), "type");
            out.writeAttribute("name", clientId, "clientId");
            out.writeAttribute("value", label, "value");
        }
        String onclick = getOnclickScript(context, component);
        if (onclick != null)
            out.writeAttribute("onclick", onclick, "onclick");
        renderPassThruAttributes(out, component, "onclick");
        out.endElement("input");
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        // children encoded as button label
    }

    private String getLabel(FacesContext context, UIComponent component) {
        Object value = ((UICommand)component).getValue();
        if (value != null) {
            return value.toString();
        } else {
            return FacesUtils.encodeComponentChildren(context, component);
        }
    }

    private String src(FacesContext context, String value) {
        if (value == null) {
            return "";
        } else {
            value = context.getApplication().getViewHandler().getResourceURL(context, value);
            return context.getExternalContext().encodeResourceURL(value);
        }
    }

    protected String getOnclickScript(FacesContext context, UIComponent component) {
        return (String)component.getAttributes().get("onclick");
    }
}
