/*
 * $Id: GroupRenderer.java,v 1.4 2007/07/02 07:37:47 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class GroupRenderer extends HtmlRenderer
{
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String layout = (String)component.getAttributes().get("layout");

        if (layout != null && layout.equals("block"))
            out.startElement("div", component);
        else
            out.startElement("span", component);
        writeIdAttributeIfNecessary(context, out, component);
        renderPassThruAttributes(out, component);
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        super.encodeChildren(context, component);
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String layout = (String)component.getAttributes().get("layout");

        if (layout != null && layout.equals("block"))
            out.endElement("div");
        else
            out.endElement("span");
    }
}
