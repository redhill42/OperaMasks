/*
 * $Id: MessagesRenderer.java,v 1.5 2007/09/17 16:21:48 daniel Exp $
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
import javax.faces.component.UIMessages;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.application.FacesMessage;
import java.io.IOException;
import java.util.Iterator;

public class MessagesRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        // If globalOnly attribute is true, only global messages (that is,
        // messages not associated with any client identifier) are to be
        // displayed.  Otherwise, all messages are to be displayed.
        Iterator<FacesMessage> messageIter;
        if (((UIMessages)component).isGlobalOnly())
            messageIter = context.getMessages(null);
        else
            messageIter = context.getMessages();
        if (!messageIter.hasNext())
            return;
        
        boolean showSummary = ((UIMessages)component).isShowSummary();
        boolean showDetail = ((UIMessages)component).isShowDetail();

        Object tooltipVal = component.getAttributes().get("tooltip");
        boolean tooltip = false;
        if (tooltipVal instanceof Boolean)
            tooltip = (Boolean)tooltipVal;

        String layout = (String)component.getAttributes().get("layout");
        boolean tableLayout = "table".equals(layout);

        ResponseWriter out = context.getResponseWriter();

        out.startElement(tableLayout ? "table" : "ul", component);
        writeIdAttributeIfNecessary(context, out, component);
        renderPassThruAttributes(out, component);

        while (messageIter.hasNext()) {
            FacesMessage message = messageIter.next();
            String summary = message.getSummary();
            String detail = message.getDetail();
            if (summary == null) summary = "";
            if (detail == null) detail = "";

            String title = null;
            if (showSummary && showDetail && tooltip)
                title = summary;

            String messageClass = "x-error-message";
            String style = null, styleClass = null;
            if (message.getSeverity() == FacesMessage.SEVERITY_INFO) {
                style = (String)component.getAttributes().get("infoStyle");
                styleClass = (String)component.getAttributes().get("infoClass");
                messageClass = "x-info-message";
            } else if (message.getSeverity() == FacesMessage.SEVERITY_WARN) {
                style = (String)component.getAttributes().get("warnStyle");
                styleClass = (String)component.getAttributes().get("warnClass");
                messageClass = "x-warn-message";
            } else if (message.getSeverity() == FacesMessage.SEVERITY_ERROR) {
                style = (String)component.getAttributes().get("errorStyle");
                styleClass = (String)component.getAttributes().get("errorClass");
                messageClass = "x-error-message";
            } else if (message.getSeverity() == FacesMessage.SEVERITY_FATAL) {
                style = (String)component.getAttributes().get("fatalStyle");
                styleClass = (String)component.getAttributes().get("fatalClass");
                messageClass = "x-fatal-message";
            }
            if (styleClass == null) {
                styleClass = messageClass;
            }

            if (tableLayout) {
                out.startElement("tr", component);
                out.startElement("td", component);
            } else {
                out.startElement("li", component);
            }
            if (style != null)
                out.writeAttribute("style", style, "style");
            if (styleClass != null)
                out.writeAttribute("class", styleClass, "styleClass");
            if (title != null)
                out.writeAttribute("title", title, "title");
            if (showSummary) {
                out.writeText(summary, null);
                if (showDetail)
                    out.writeText(" ", null);
            }
            if (showDetail) {
                out.writeText(detail, null);
            }
            if (tableLayout) {
                out.endElement("td");
                out.endElement("tr");
            } else {
                out.endElement("li");
            }
        }

        out.endElement(tableLayout ? "table" : "ul");
    }
}
