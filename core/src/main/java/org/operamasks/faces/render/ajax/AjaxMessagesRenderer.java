/*
 * $Id: AjaxMessagesRenderer.java,v 1.5 2007/07/02 07:37:53 jacky Exp $
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
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.operamasks.faces.render.html.MessagesRenderer;

public class AjaxMessagesRenderer extends MessagesRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component);
        } else if (isAjaxHtmlResponse(context)) {
            renderAjaxHtmlResponse(context, component);
        } else {
            super.encodeEnd(context, component);
        }
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        // render outer div start
        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", null);
        out.writeAttribute("id", getOuterClientId(context, component), null);
        out.writeText("", null);

        // render inner HTML
        super.encodeEnd(context, component);

        // render outer div end
        out.endElement("div");
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        String clientId = getOuterClientId(context, component);

        // create a temporary ResponseWriter to get inner HTML
        StringWriter buf = new StringWriter();
        ResponseWriter inner = out.cloneWithHtmlWriter(buf);

        // encode inner HTML
        context.setResponseWriter(inner);
        super.encodeEnd(context, component);
        context.setResponseWriter(out);

        // output javascript to set inner HTML
        out.writeInnerHtmlScript(clientId, buf.toString());
    }

    private String getOuterClientId(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        return clientId + NamingContainer.SEPARATOR_CHAR + "_outer";
    }
}
