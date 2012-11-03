/*
 * $Id: CommandLinkRenderer.java,v 1.8 2007/09/19 08:02:51 daniel Exp $
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
import javax.faces.component.UICommand;
import javax.faces.component.UIForm;
import javax.faces.component.ValueHolder;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;

public class CommandLinkRenderer extends HtmlRenderer
{
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (paramMap.containsKey(clientId)) {
            ActionEvent event = new ActionEvent(component);
            component.queueEvent(event);
        }
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIForm form = getParentForm(component);
        if (isDisabled(component)) {
            renderDisabledLink(context, component);
        } else if (form != null) {
            renderActiveLink(context, form, component);
        } else if (FacesUtils.isTransientStateSupported(context)) {
            renderSingleLink(context, component);
        }
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        String label = getLabel(component);
        if (label != null) {
            ResponseWriter out = context.getResponseWriter();
            out.writeText(label, "value");
        } else {
            super.encodeChildren(context, component);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIForm form = getParentForm(component);

        ResponseWriter out = context.getResponseWriter();
        if (isDisabled(component)) {
            out.endElement("span");
        } else if (form != null || FacesUtils.isTransientStateSupported(context)) {
            out.endElement("a");
        }
    }

    protected void renderDisabledLink(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String type = (String)component.getAttributes().get("type");

        out.startElement("span", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (type != null)
            out.writeAttribute("type", type, "type");
        renderPassThruAttributes(out, component, "disabled");
    }

    protected void renderActiveLink(FacesContext context, UIForm form, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String onclick = getOnclickScript(context, form, component, true);
        String type = (String)component.getAttributes().get("type");

        out.startElement("a", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("href", "#", null);
        out.writeAttribute("onclick", onclick, "onclick");
        if (type != null)
            out.writeAttribute("type", type, "type");
        renderPassThruAttributes(out, component, "disabled,onclick,target");
    }

    protected void renderSingleLink(FacesContext context, UIComponent component)
        throws IOException
    {
        StringBuilder urlbuf = new StringBuilder();
        urlbuf.append(getActionURL(context));
        urlbuf.append('?');
        urlbuf.append(HtmlEncoder.encodeURIComponent(component.getClientId(context)));
        urlbuf.append('=');
        urlbuf.append('&');
        urlbuf.append(FacesUtils.VIEW_ID_PARAM);
        urlbuf.append('=');
        urlbuf.append(HtmlEncoder.encodeURIComponent(context.getViewRoot().getViewId()));
        for (UIParameter param : getParameters(component)) {
            if (param.getName() != null && param.getValue() != null) {
                urlbuf.append('&');
                urlbuf.append(HtmlEncoder.encodeURIComponent(param.getName()));
                urlbuf.append(HtmlEncoder.encodeURIComponent(param.getValue().toString()));
            }
        }

        String type = (String)component.getAttributes().get("type");

        ResponseWriter out = context.getResponseWriter();
        out.startElement("a", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("href", urlbuf.toString(), null);
        if (type != null)
            out.writeAttribute("type", type, "type");
        renderPassThruAttributes(out, component, "disabled");
    }

    protected String getOnclickScript(FacesContext context, UIForm form, UIComponent component, boolean doReturn) {
        String clientId = component.getClientId(context);

        String userOnclick = (String)component.getAttributes().get("onclick");
        if (userOnclick != null) {
            userOnclick = userOnclick.trim();
            if (userOnclick.length() == 0)
                userOnclick = null;
        }

        StringBuilder sb = new StringBuilder();

        if (userOnclick != null) {
            sb.append("var a=function(){");
            sb.append(userOnclick);
            if (!userOnclick.endsWith(";"))
                sb.append(';');
            sb.append("};var b=function(){");
        }

        List<String> paramList = new ArrayList<String>();
        paramList.add(HtmlEncoder.enquote(clientId));
        paramList.add("''");
        for (UIParameter param : getParameters(component)) {
            if (param.getName() != null && param.getValue() != null) {
                paramList.add(HtmlEncoder.enquote(param.getName()));
                paramList.add(HtmlEncoder.enquote(param.getValue().toString()));
            }
        }

        String[] params = paramList.toArray(new String[paramList.size()]);
        String target = (String)component.getAttributes().get("target");
        sb.append(encodeSubmit(context, form, target, params));

        if (doReturn) {
            sb.append("return false;");
        }

        if (userOnclick != null) {
            sb.append("};");
            if (doReturn) {
                sb.append("return (a.apply(this)==false)?false:b();");
            } else {
                sb.append("a.apply(this)==false||b();");
            }
        }

        return sb.toString();
    }

    private String getLabel(UIComponent component) {
        String label = null;
        if (component instanceof UICommand) {
            Object value = ((UICommand)component).getValue();
            if (value != null) label = value.toString();
        } else if (component instanceof ValueHolder) {
            Object value = ((ValueHolder)component).getValue();
            if (value != null) label = value.toString();
        }
        return label;
    }

    protected List<UIParameter> getParameters(UIComponent component) {
        ArrayList<UIParameter> paramList = new ArrayList<UIParameter>();
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                paramList.add((UIParameter)kid);
            }
        }
        return paramList;
    }
}
