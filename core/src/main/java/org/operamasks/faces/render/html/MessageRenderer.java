/*
 * $Id: MessageRenderer.java,v 1.6 2007/09/17 16:21:48 daniel Exp $
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
import javax.faces.component.UIMessage;
import javax.faces.application.FacesMessage;
import java.io.IOException;
import java.util.Iterator;

import org.operamasks.faces.util.FacesUtils;

public class MessageRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        FacesMessage message = getFacesMessage(context, component);
        if (message == null)
            return;

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

        if (style == null)
            style = (String)component.getAttributes().get("style");
        if (styleClass == null)
            styleClass = (String)component.getAttributes().get("styleClass");
        if (styleClass == null)
            styleClass = messageClass;

        boolean showSummary = ((UIMessage)component).isShowSummary();
        boolean showDetail = ((UIMessage)component).isShowDetail();
        String summary = message.getSummary();
        String detail = message.getDetail();
        if (summary == null) summary = "";
        if (detail == null) detail = "";

        Object tooltipVal = component.getAttributes().get("tooltip");
        boolean tooltip = false;
        if (tooltipVal instanceof Boolean)
            tooltip = (Boolean)tooltipVal;

        String title = (String)component.getAttributes().get("title");
        if (title == null && tooltip)
            title = summary;

        ResponseWriter out = context.getResponseWriter();

        out.startElement("span", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (style != null)
            out.writeAttribute("style", style, "style");
        if (styleClass != null)
            out.writeAttribute("class", styleClass, "styleClass");
        if (title != null)
            out.writeAttribute("title", title, "title");
        renderPassThruAttributes(out, component, "style,styleClass,title");

        if (showSummary && showDetail) {
            out.writeText(summary + " " + detail, null);
        } else if (showSummary) {
            out.writeText(summary, null);
        } else if (showDetail) {
            out.writeText(detail, null);
        }

        out.endElement("span");
    }

    protected FacesMessage getFacesMessage(FacesContext context, UIComponent component) {
        String id = ((UIMessage)component).getFor();
        if (id == null || id.length() == 0)
            return null;

        UIComponent forComponent = FacesUtils.getForComponent(context, id, component);
        if (forComponent == null)
            return null;

        String clientId = forComponent.getClientId(context);
        Iterator<FacesMessage> messageIter = context.getMessages(clientId);
        if (!messageIter.hasNext())
            return null;
        return messageIter.next();
    }
}
