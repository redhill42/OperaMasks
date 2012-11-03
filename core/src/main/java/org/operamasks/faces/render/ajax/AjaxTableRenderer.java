/*
 * $Id: AjaxTableRenderer.java,v 1.5 2007/07/02 07:37:51 jacky Exp $
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
import javax.faces.component.NamingContainer;
import java.io.IOException;
import java.io.StringWriter;
import org.operamasks.faces.render.html.TableRenderer;

public class AjaxTableRenderer extends TableRenderer
{
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext context, UIComponent component) {
        // already rendered
    }

    public void encodeChildren(FacesContext context, UIComponent component)
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
            super.encodeBegin(context, component);
            super.encodeChildren(context, component);
            super.encodeEnd(context, component);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) {
        // already rendered
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        // render outer div start
        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", null);
        out.writeAttribute("id", getOuterClientId(context, component), null);
        out.write("");

        // render inner HTML
        super.encodeBegin(context, component);
        super.encodeChildren(context, component);
        super.encodeEnd(context, component);

        // render out div end
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
        super.encodeBegin(context, component);
        super.encodeChildren(context, component);
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
