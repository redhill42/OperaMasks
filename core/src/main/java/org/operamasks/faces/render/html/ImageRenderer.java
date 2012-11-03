/*
 * $Id: ImageRenderer.java,v 1.5 2007/07/02 07:37:47 jacky Exp $
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
import javax.faces.component.UIGraphic;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class ImageRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("img", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeURIAttribute("src", src(context, component), "value");
        renderPassThruAttributes(out, component);
        out.endElement("img");
    }

    private String src(FacesContext context, UIComponent component) {
        Object value = ((UIGraphic)component).getValue();
        if (value == null) {
            return "";
        } else {
            String url = value.toString();
            url = context.getApplication().getViewHandler().getResourceURL(context, url);
            url = context.getExternalContext().encodeResourceURL(url);
            return url;
        }
    }
}
