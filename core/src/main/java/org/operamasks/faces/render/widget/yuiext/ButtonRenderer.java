/*
 * $Id: ButtonRenderer.java,v 1.19 2008/04/29 01:29:37 lishaochuan Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;
import java.util.Map;
import java.util.logging.Level;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class ButtonRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
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

        ResponseWriter out = context.getResponseWriter();

        out.startElement("span", component);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        renderPassThruAttributes(out, component, "disabled,onclick");
        out.endElement("span");
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) {
        // children encoded as button label
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.Button");

        String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
        resource.addVariable(jsvar);
        component.getAttributes().get("actionBinding");
        StringBuilder buf = new StringBuilder();
        encodeButton(rm, buf, component, jsvar);
        encodeOnclick(buf, component, jsvar);
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Init script of " + FacesUtils.getComponentDesc(component) + 
                    " with id " + component.getId() + " is encoded as [" + buf.toString() + "]");
        }
        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
    }

    protected void encodeButton(ResourceManager rm, StringBuilder buf, UIComponent component, String jsvar) {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);

        Formatter fmt = new Formatter(buf);

        // Create button instance
        fmt.format("%s=new Ext.Button({renderTo:'%s',", jsvar, clientId);

        String text = getText(context, component);
        String tooltip = (String)component.getAttributes().get("tooltip");
        Object disabled = component.getAttributes().get("disabled");
        Object minWidth = component.getAttributes().get("minWidth");
        Object width = component.getAttributes().get("width");
        String icon = getIcon(component);

        // Config button
        fmt.format("text:%s", HtmlEncoder.enquote(text, '\''));
        if (tooltip != null)
            fmt.format(",tooltip:%s", HtmlEncoder.enquote(tooltip, '\''));
        if (disabled != null)
            fmt.format(",disabled:%b", disabled);
        if (minWidth != null)
            fmt.format(",minWidth:%s", minWidth);
        if (width != null)
            fmt.format(",minWidth:%s", width);
        if (icon != null) {
        	fmt.format(",icon:%s", HtmlEncoder.enquote(icon, '\''));
        	fmt.format(",iconCls:%s", HtmlEncoder.enquote("x-btn-text-icon", '\''));
        }
        buf.append("});\n");
    }

    private String getIcon(UIComponent component) {
        return (String)component.getAttributes().get("image");
	}

    protected void encodeOnclick(StringBuilder buf, UIComponent component, String jsvar) {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        UIForm form = getParentForm(component);

        String onclick = (String)component.getAttributes().get("onclick");
        if (onclick != null) {
            onclick = onclick.trim();
            if (onclick.length() == 0) {
                onclick = null;
            } else if (!onclick.endsWith(";")) {
                onclick += ";";
            }
        }

        Formatter fmt = new Formatter(buf);

        Object alwaysSubmit = component.getAttributes().get("alwaysSubmit");
        if (form != null && (alwaysSubmit == null || (alwaysSubmit != null && (Boolean.parseBoolean(alwaysSubmit.toString()))))) {
            fmt.format("%s.on('click', function(){", jsvar);
            if (onclick != null) {
                fmt.format("if(function(){%s}.apply(this)==false)return;", onclick);
            }
            buf.append(encodeSubmit(context, form, null,
                                    HtmlEncoder.enquote(clientId),
                                    HtmlEncoder.enquote("")));
            buf.append("});\n");
        } else if (onclick != null) {
            fmt.format("%s.on('click',function(){%s});\n", jsvar, onclick);
        }
    }

    protected String getText(FacesContext context, UIComponent component) {
        String text = (String)component.getAttributes().get("label");
        if (text == null) {
            Object value = ((UICommand)component).getValue();
            if (value != null) {
                text = value.toString();
            } else {
                text = FacesUtils.encodeComponentChildren(context, component);
            }
        }
        return text;
    }

    protected String getHiddenFieldName(FacesContext context, UIComponent component) {
        String result = null;
        UIForm form = getParentForm(component);
        if (form != null) {
            result = form.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "_link";
        }
        return result;
    }
}
