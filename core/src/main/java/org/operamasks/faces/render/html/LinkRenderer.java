/*
 * $Id: LinkRenderer.java,v 1.4 2007/07/02 07:37:46 jacky Exp $
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
import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import java.io.IOException;

public class LinkRenderer extends UIOutputRenderer
{
    protected Object getValue(UIComponent component) {
        return ((UIOutput)component).getValue();
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isDisabled(component)) {
            renderDisabledLink(context, component);
        } else {
            renderActiveLink(context, component);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        if (isDisabled(component)) {
            out.endElement("span");
        } else {
            out.endElement("a");
        }
    }

    private void renderActiveLink(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        out.startElement("a", component);

        String id = component.getId();
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            // render ID and name if explicitly specified
            id = component.getClientId(context);
            out.writeAttribute("id", id, "clientId");
            out.writeAttribute("name", id, "clientId");
        }

        String url = getUrl(context, component);
        String type = (String)component.getAttributes().get("type");
        if (url != null)
            out.writeURIAttribute("href", url, "value");
        if (type != null)
            out.writeAttribute("type", type, "type");
        renderPassThruAttributes(out, component);
    }

    private String getUrl(FacesContext context, UIComponent component) {
        StringBuffer buf = new StringBuffer();

        String value = getCurrentValue(context, component);
        if (value != null) {
            buf.append(value);
        }

        boolean q = (value == null) || (value.indexOf('?') == -1);
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                UIParameter param = (UIParameter)kid;
                buf.append(q ? '?' : '&');
                buf.append(param.getName());
                buf.append('=');
                buf.append(param.getValue());
                q = false;
            }
        }

        if (buf.length() == 0) {
            return null;
        } else {
            String url = context.getApplication().getViewHandler()
                                .getResourceURL(context, buf.toString());
            return context.getExternalContext().encodeResourceURL(url);
        }
    }

    private void renderDisabledLink(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        out.startElement("span", component);
        writeIdAttributeIfNecessary(context, out, component);
        renderPassThruAttributes(out, component, "disabled");
    }
}
