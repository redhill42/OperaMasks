/*
 * $Id: SliderRenderer.java,v 1.5 2007/07/30 01:57:37 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.util.Formatter;

import org.operamasks.faces.render.html.UIInputRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.util.FacesUtils.isDynamicValue;

public class SliderRenderer extends UIInputRenderer
    implements ResourceProvider
{
    private static final String PROXY_ID_SUFFIX = ":slider";

    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component);
        } else {
            String clientId = component.getClientId(context);
            String proxyId = clientId + PROXY_ID_SUFFIX;
            String value = getCurrentValue(context, component);

            ResponseWriter out = context.getResponseWriter();
            out.startElement("div", component);
            out.writeAttribute("id", clientId, "clientId");
            renderPassThruAttributes(out, component, "disabled,width,onchange");
            out.endElement("div");
            out.write("\n");

            if (component.getAttributes().get("proxy") == null) {
                out.startElement("input", null);
                out.writeAttribute("id", proxyId, null);
                out.writeAttribute("name", clientId, null);
                out.writeAttribute("type", "hidden", null);
                if (value != null)
                    out.writeAttribute("value", value, null);
                out.endElement("input");
                out.write("\n");
            }
        }
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.om.Slider");

        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String jsvar = resource.allocVariable(component);

        String proxyId = (String)component.getAttributes().get("proxy");
        if (proxyId != null) {
            UIComponent proxy = FacesUtils.getForComponent(context, proxyId, component);
            if (proxy != null) {
                proxyId = proxy.getClientId(context);
            }
        } else {
            proxyId = clientId + PROXY_ID_SUFFIX;
        }

        Object orientation = component.getAttributes().get("orientation");
        Object width = component.getAttributes().get("width");
        Object minimum = component.getAttributes().get("minimum");
        Object maximum = component.getAttributes().get("maximum");
        Object unitIncrement = component.getAttributes().get("unitIncrement");
        Object blockIncrement = component.getAttributes().get("blockIncrement");
        Object disabled = component.getAttributes().get("disabled");
        String value = getCurrentValue(context, component);
        String link = getLink(context, component);

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        fmt.format("%s=new Ext.om.Slider('%s',{proxy:'%s'", jsvar, clientId, proxyId);
        if (orientation != null)
            fmt.format(",orientation:'%s'", orientation);
        if (width != null)
            fmt.format(",width:%s", width);
        if (minimum != null)
            fmt.format(",minimum:%s", minimum);
        if (maximum != null)
            fmt.format(",maximum:%s", maximum);
        if (unitIncrement != null)
            fmt.format(",unitIncrement:%s", unitIncrement);
        if (blockIncrement != null)
            fmt.format(",blockIncrement:%s", blockIncrement);
        if (disabled != null)
            fmt.format(",disabled:%s", disabled);
        if (value != null && value.length() != 0)
            fmt.format(",value:%s", value);
        if (link != null)
            fmt.format(",link:%s", link);
        buf.append("});\n");

        String onchange = (String)component.getAttributes().get("onchange");
        if (onchange != null && onchange.length() != 0) {
            if (!onchange.endsWith(";"))
                onchange += ";";
            fmt.format("%s.on('change', function(){%s});\n", jsvar, onchange);
        }

        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
    }

    private String getLink(FacesContext context, UIComponent component) {
        String link = (String)component.getAttributes().get("link");
        if (link == null)
            return null;

        if (link.indexOf(",") == -1) {
            UIComponent forLink = FacesUtils.getForComponent(context, link, component);
            if (forLink != null) {
                link = forLink.getClientId(context);
            }
            return "'" + link + "'";
        }

        String[] links = link.split(",");
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < links.length; i++) {
            UIComponent forLink = FacesUtils.getForComponent(context, links[i], component);
            if (forLink != null) {
                links[i] = forLink.getClientId(context);
            }
            buf.append((i==0) ? "['" : ",'");
            buf.append(links[i]);
            buf.append("'");
        }
        buf.append("]");
        return buf.toString();
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        String jsvar = FacesUtils.getJsvar(context, component);
        Formatter fmt = new Formatter();

        if (isDynamicValue(component, "minimum")) {
            Object minimum = component.getAttributes().get("minimum");
            if (minimum != null) {
                fmt.format("%s.setMinimum(%s);", jsvar, minimum);
            }
        }

        if (isDynamicValue(component, "maximum")) {
            Object maximum = component.getAttributes().get("maximum");
            if (maximum != null) {
                fmt.format("%s.setMaximum(%s);", jsvar, maximum);
            }
        }

        if (isDynamicValue(component, "unitIncrement")) {
            Object unitIncrement = component.getAttributes().get("unitIncrement");
            if (unitIncrement != null) {
                fmt.format("%s.unitIncrement=%s;", jsvar, unitIncrement);
            }
        }

        if (isDynamicValue(component, "blockIncrement")) {
            Object blockIncrement = component.getAttributes().get("blockIncrement");
            if (blockIncrement != null) {
                fmt.format("%s.blockIncrement=%s;", jsvar, blockIncrement);
            }
        }

        if (isDynamicValue(component, "disabled")) {
            Object disabled = component.getAttributes().get("disabled");
            if (disabled != null) {
                fmt.format("%s.setDisabled(%s);", jsvar, disabled);
            }
        }

        String value = getCurrentValue(context, component);
        if (value != null && value.length() != 0) {
            fmt.format("%s.setValue(%s);", jsvar, value);
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.writeScript(fmt.toString());
    }
}
