/*
 * $Id: AjaxFormatRenderer.java,v 1.4 2007/07/02 07:37:53 jacky Exp $
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
import javax.faces.context.FacesContext;
import org.operamasks.faces.render.html.FormatRenderer;
import org.operamasks.faces.util.HtmlEncoder;
import java.io.IOException;

public class AjaxFormatRenderer extends FormatRenderer
{
    @Override
    protected void renderCurrentValue(FacesContext context, UIComponent component, String currentValue)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            String clientId = component.getClientId(context);

            // render dynamic pass through attributes
            if (hasPassThruAttributes(component)) {
                out.startElement("span", component);
                out.writeAttribute("id", clientId, "clientId");
                renderPassThruAttributes(out, component);
                out.endElement("span");
            }

            // render format content, it's dynamic in most cases
            String message = formatMessage(component, currentValue);
            if (needsEscape(component))
                message = HtmlEncoder.encode(message);
            out.writeInnerHtmlScript(clientId, message);
        } else {
            super.renderCurrentValue(context, component, currentValue);
        }
    }
}
