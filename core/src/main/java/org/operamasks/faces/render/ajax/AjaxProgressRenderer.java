/*
 * $Id: AjaxProgressRenderer.java,v 1.8 2007/07/02 07:37:53 jacky Exp $
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
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;
import javax.el.MethodExpression;
import javax.el.ELException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.text.NumberFormat;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.component.ajax.AjaxProgress;
import org.operamasks.faces.component.ajax.ProgressStatus;
import org.operamasks.faces.component.ajax.ProgressAction;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.widget.UIProgressBar;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxProgressRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxHtmlResponse(context)) {
            renderAjaxHtmlResponse(context, component);
        } else if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component);
        }
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxProgress progress = (AjaxProgress)component;
        String clientId = component.getClientId(context);
        String renderId = getRenderId(component);
        String jsvar = FacesUtils.getJsvar(context, component);

        StringBuilder buf = new StringBuilder();

        // var progress = new OM.ajax.Progress('id', 'url', 'renderId', interval);
        buf.append("var ")
           .append(jsvar)
           .append("=new OM.ajax.Progress('")
           .append(clientId)
           .append("','")
           .append(getActionURL(context))
           .append("',")
           .append(progress.getInterval() * 1000)
           .append(");\n");

        if (renderId != null && renderId.length() != 0) {
            buf.append(jsvar)
               .append(".addParameter('")
               .append(AjaxUpdater.RENDER_ID_PARAM)
               .append("','")
               .append(renderId)
               .append("');\n");
        }

        // update related components when progress state changed
        List<UIComponent> forComponents = getForComponents(context, component);
        String onstatechange = progress.getOnstatechange();

        if (onstatechange != null || forComponents.size() != 0) {
            buf.append(jsvar);
            buf.append(".onstatechange=function(){");
            for (UIComponent forComponent : forComponents) {
                if (forComponent instanceof UIProgressBar) {
                    buf.append(FacesUtils.getJsvar(context, forComponent));
                    buf.append(".setValue(this.percentage);");
                    buf.append(FacesUtils.getJsvar(context, forComponent));
                    buf.append(".setState(this.state);");
                } else if (forComponent instanceof HtmlOutputText) {
                    buf.append("document.getElementById('");
                    buf.append(forComponent.getClientId(context));
                    buf.append("').innerHTML=this.message;");
                }
            }
            if (onstatechange != null) {
                buf.append(onstatechange);
                if (!onstatechange.endsWith(";"))
                    buf.append(';');
            }
            buf.append("};");
        }

        if (progress.getStart()) {
            buf.append(jsvar);
            buf.append(".setAutoStart();");
        }

        ResponseWriter out = context.getResponseWriter();
        out.startElement("script", component);
        out.writeAttribute("type", "text/javascript", null);
        out.write(buf.toString());
        out.endElement("script");
    }

    private List<UIComponent> getForComponents(FacesContext context, UIComponent component) {
        List<UIComponent> result = new ArrayList<UIComponent>();

        String forIds = (String)component.getAttributes().get("for");
        if (forIds != null) {
            for (String id : forIds.split(" ")) {
                UIComponent forComponent = FacesUtils.getForComponent(context, id, component);
                if (forComponent != null) {
                    result.add(forComponent);
                }
            }
        }

        UIComponent parent = component.getParent();
        while (parent != null) {
            if ((parent instanceof UIProgressBar) || (parent instanceof HtmlOutputText)) {
                result.add(parent);
                break;
            }
            parent = parent.getParent();
        }

        return result;
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxProgress progress = (AjaxProgress)component;
        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String action = paramMap.get(clientId);
        if (action == null || action.length() == 0) {
            return;
        }

        MethodExpression actionExpression = progress.getAction();
        if (actionExpression == null) {
            return;
        }

        ProgressStatus status = new ProgressStatus(ProgressAction.valueOf(action));
        try {
            actionExpression.invoke(context.getELContext(), new Object[] {status});
        } catch (ELException ex) {
            throw new FacesException(ex);
        }

        if (status.getState() == null) {
            // action doesn't performed
            return;
        }

        String message = status.getMessage();
        if (message == null) {
            // set default message
            NumberFormat format = NumberFormat.getPercentInstance(context.getViewRoot().getLocale());
            message = format.format((double)status.getPercentage()/100);
        }

        StringBuilder buf = new StringBuilder();
        buf.append(FacesUtils.getJsvar(context, component));
        buf.append("._handleResponse({");
        buf.append("action:'").append(action).append("',");
        buf.append("state:'").append(status.getState().name()).append("',");
        buf.append("phase:").append(status.getPhase()).append(",");
        buf.append("percentage:").append(status.getPercentage()).append(",");
        buf.append("message:").append(HtmlEncoder.enquote(message, '\''));
        buf.append("});");

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.writeScript(buf.toString());
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
