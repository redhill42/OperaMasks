/*
 * $Id: HtmlScriptRenderer.java,v 1.4 2007/07/02 07:37:47 jacky Exp $
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
import java.io.StringWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.operamasks.faces.util.FacesUtils;

public class HtmlScriptRenderer extends HtmlRenderer
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
        String language = (String)component.getAttributes().get("language");
        String src = (String)component.getAttributes().get("src");

        if (type == null) {
            type = "text/javascript";
        }

        if (src != null) {
            src = context.getApplication().getViewHandler().getResourceURL(context, src);
            out.startElement("script", component);
            out.writeAttribute("type", type, "type");
            if (language != null)
                out.writeAttribute("language", language, "language");
            out.writeAttribute("src", src, "src");
            out.endElement("script");
            out.write("\n");
        }

        if (component.getChildCount() > 0) {
            out.startElement("script", component);
            out.writeAttribute("type", type, "type");
            if (language != null)
                out.writeAttribute("language", language, "language");
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
            out.endElement("script");
            out.write("\n");
        }
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (component.getChildCount() == 0)
            return;

        ResponseWriter out = context.getResponseWriter();
        StringWriter buf = new StringWriter();
        ResponseWriter bufOut = out.cloneWithWriter(buf);

        context.setResponseWriter(bufOut);
        super.encodeChildren(context, component);
        context.setResponseWriter(out);

        StringBuffer output = new StringBuffer();

        // replace element ids
        Pattern p = Pattern.compile("@\\{([a-zA-Z0-9_:-]+)\\}");
        Matcher m = p.matcher(buf.getBuffer());
        while (m.find()) {
            String marker = m.group(1);
            UIComponent comp = FacesUtils.getForComponent(context, marker, component);
            String id = (comp != null) ? comp.getClientId(context) : marker;
            m.appendReplacement(output, "document.getElementById('" + id + "')");
        }
        m.appendTail(output);

        out.write(output.toString());
    }

    public boolean getRendersChildren() {
        return true;
    }
}
