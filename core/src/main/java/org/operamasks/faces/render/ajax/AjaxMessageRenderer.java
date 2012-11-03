/*
 * $Id: AjaxMessageRenderer.java,v 1.7 2007/09/17 16:21:48 daniel Exp $
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
import javax.faces.component.UIMessage;
import javax.faces.application.FacesMessage;
import java.io.IOException;
import org.operamasks.faces.render.html.MessageRenderer;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxMessageRenderer extends MessageRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        String clientId = component.getClientId(context);
        FacesMessage message = getFacesMessage(context, component);
        boolean showSummary = false, showDetail = false;
        String summary = null, detail = null;
        String messageClass = "x-error-message";
        String style = null, styleClass = null;

        if (message != null) {
            showSummary = ((UIMessage)component).isShowSummary();
            showDetail = ((UIMessage)component).isShowDetail();
            summary = message.getSummary();
            detail = message.getDetail();
            if (summary == null) summary = "";
            if (detail == null) detail = "";

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
        }

        if (style == null)
            style = (String)component.getAttributes().get("style");
        if (styleClass == null)
            styleClass = (String)component.getAttributes().get("styleClass");
        if (styleClass == null)
            styleClass = messageClass;

        String messageText;
        if (showSummary && showDetail) {
            messageText = summary + " " + detail;
        } else if (showSummary) {
            messageText = summary;
        } else if (showDetail) {
            messageText = detail;
        } else {
            messageText = "";
        }

        String display = (messageText.length() > 0) ? "display:inherit" : "display:none";
        style = (style == null) ? display : (style + ";" + display);

        Object tooltipVal = component.getAttributes().get("tooltip");
        boolean tooltip = false;
        if (tooltipVal instanceof Boolean)
            tooltip = (Boolean)tooltipVal;

        String title = (String)component.getAttributes().get("title");
        if (title == null && tooltip)
            title = summary;
        if (title == null)
            title = "";

        ResponseWriter out = context.getResponseWriter();

        out.startElement("span", component);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("title", title, "title");
        renderPassThruAttributes(out, component, "style,styleClass,title");

        if (out instanceof AjaxResponseWriter) {
            AjaxResponseWriter ajax = (AjaxResponseWriter)out;
            ajax.writeScript("OM.S('" + clientId + "'," + HtmlEncoder.enquote(style) + ");");
            ajax.writeScript("OM.F('" + clientId + "','className','" + styleClass + "');");
            ajax.writeInnerHtmlScript(clientId, messageText);
        } else {
            out.writeAttribute("style", style, "style");
            out.writeAttribute("class", styleClass, "styleClass");
            out.writeText(messageText, null);
        }

        out.endElement("span");
    }
}
