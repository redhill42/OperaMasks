/*
 * $Id: AjaxTimerRenderer.java,v 1.8 2007/07/02 07:37:50 jacky Exp $
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
import javax.faces.component.UIForm;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.FacesException;
import java.io.IOException;
import java.util.Map;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.component.ajax.AjaxTimer;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxTimerRenderer extends HtmlRenderer
{
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();

        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (paramMap.containsKey(clientId)) {
            component.queueEvent(new ActionEvent(component));
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (!isAjaxHtmlResponse(context))
            return;

        AjaxTimer timer = (AjaxTimer)component;
        long delay = timer.getDelay();
        long period = timer.getPeriod();
        boolean start = timer.getStart();
        String ontimeout = timer.getOntimeout();
        boolean sendForm = timer.getSendForm();

        if (delay <= 0)
            delay = period;
        if (delay <= 0 || period <= 0)
            throw new FacesException("delay or period must > 0");

        String clientId = component.getClientId(context);
        String jsvar = FacesUtils.getJsvar(context, component);
        UIForm form = getParentForm(component);
        String renderId = getRenderId(timer);

        ResponseWriter out = context.getResponseWriter();
        StringBuilder buf = new StringBuilder();

        buf.append("var ").append(jsvar)
           .append("=new OM.ajax.Timer('")
           .append(clientId)
           .append("','")
           .append(getActionURL(context))
           .append("',")
           .append(delay*1000)
           .append(",")
           .append(period*1000)
           .append(");\n");

        if (form != null) {
            if (sendForm) {
                buf.append(jsvar)
                   .append(".form=document.forms['")
                   .append(form.getClientId(context))
                   .append("'];\n");
            } else {
                // must manually include "_postback" hidden field in the post parameter
                // otherwise no timer action be fired.
                buf.append(jsvar)
                   .append(".addParameter('")
                   .append(FormRenderer.getPostbackFieldName(context, form))
                   .append("','');\n");
            }
        }

        if (form == null || !sendForm) {
            if (renderId != null && renderId.length() != 0) {
                // manually include render ID parameter
                buf.append(jsvar)
                   .append(".addParameter('")
                   .append(AjaxUpdater.RENDER_ID_PARAM)
                   .append("','")
                   .append(renderId)
                   .append("');\n");
            }
        }

        // add extra parameters
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                UIParameter param = (UIParameter)kid;
                String name = param.getName();
                Object value = param.getValue();
                if (name != null && value != null) {
                    buf.append(jsvar);
                    buf.append(".addParameter(");
                    buf.append(HtmlEncoder.enquote(name, '\''));
                    buf.append(",");
                    buf.append(HtmlEncoder.enquote(value.toString(), '\''));
                    buf.append(");\n");
                }
            }
        }

        if (ontimeout != null) {
            ontimeout = ontimeout.trim();
            if (ontimeout.length() != 0) {
                buf.append(jsvar);
                buf.append(".ontimeout=function(){");
                buf.append(ontimeout);
                if (!ontimeout.endsWith(";")) {
                    buf.append(";");
                }
                buf.append("};\n");
            }
        }

        if (start) {
            buf.append(jsvar).append(".schedule();\n");
        }

        out.startElement("script", component);
        out.writeAttribute("type", "text/javascript", null);
        out.writeAttribute("language", "Javascript", null);
        out.write("<!--\n");
        out.write(buf.toString());
        out.write("//-->\n");
        out.endElement("script");

        if (form == null) {
            // must write view state if not nested in a form
            context.getApplication().getViewHandler().writeState(context);
        }
    }

    private String getRenderId(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof AjaxUpdater) {
                return ((AjaxUpdater)parent).getRenderId();
            }
            parent = parent.getParent();
        }
        return null;
    }
}
