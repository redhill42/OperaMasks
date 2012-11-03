/*
 * $Id: AjaxLoggerRenderer.java,v 1.5 2007/07/02 07:37:53 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;

import java.io.IOException;

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;

public class AjaxLoggerRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) {}

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (isAjaxResponse(context))
            return;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), null);
        renderPassThruAttributes(out, component);
        out.endElement("div");

        String level = (String)component.getAttributes().get("level");

        StringBuilder buf = new StringBuilder();
        buf.append("<script type=\"text/javascript\">");
        buf.append("LOG.consoleDivId='");
        buf.append(component.getClientId(context));
        buf.append("';");
        if (level != null) {
            buf.append("LOG.LEVEL=LOG.");
            buf.append(level.toUpperCase());
            buf.append("||LOG.INFO;");
        }
        buf.append("</script>\n");
        out.write(buf.toString());
    }

    public void provideResource(ResourceManager manager, UIComponent component) {
        manager.registerScriptResource("logging.js");
    }
}
