/*
 * $Id: HtmlStylesheetRenderer.java,v 1.3 2007/07/02 07:37:47 jacky Exp $
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
import java.io.IOException;

public class HtmlStylesheetRenderer extends HtmlRenderer
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String type = (String)component.getAttributes().get("type");
        String src = (String)component.getAttributes().get("src");

        if (type == null) {
            type = "text/css";
        }

        if (src != null) {
            src = context.getApplication().getViewHandler().getResourceURL(context, src);
            out.startElement("link", component);
            out.writeAttribute("rel", "stylesheet", null);
            out.writeAttribute("type", type, "type");
            out.writeAttribute("href", src, "src");
            out.endElement("link");
            out.write("\n");
        }

        if (component.getChildCount() > 0) {
            out.startElement("style", component);
            out.writeAttribute("type", type, "type");
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component.getChildCount() > 0) {
            ResponseWriter out = context.getResponseWriter();
            out.endElement("style");
            out.write("\n");
        }
    }
}
