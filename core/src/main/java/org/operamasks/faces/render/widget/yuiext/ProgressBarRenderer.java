/*
 * $Id: ProgressBarRenderer.java,v 1.5 2007/07/30 01:57:37 daniel Exp $
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
import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.html.UIOutputRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

public class ProgressBarRenderer extends UIOutputRenderer
    implements ResourceProvider
{
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
            Integer width = (Integer)component.getAttributes().get("width");
            if (width == null || width <= 0)
                width = 200;

            String style = (String)component.getAttributes().get("style");
            if (style == null) {
                style = "";
            } else if (!style.endsWith(";")) {
                style += ";";
            }
            style += "width:" + width + "px;";

            ResponseWriter out = context.getResponseWriter();
            out.startElement("div", component);
            out.writeAttribute("id", component.getClientId(context), "clientId");
            out.writeAttribute("style", style, "style");
            renderPassThruAttributes(out, component, "style");
            out.endElement("div");
        }
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        String jsvar = FacesUtils.getJsvar(context, component);
        boolean repaint = false;
        Formatter fmt = new Formatter();

        if (FacesUtils.isDynamicValue(component, "minimum")) {
            Object minimum = component.getAttributes().get("minimum");
            if (minimum != null) {
                fmt.format("%s.minimum=%s;", jsvar, minimum);
                repaint = true;
            }
        }

        if (FacesUtils.isDynamicValue(component, "maximum")) {
            Object maximum = component.getAttributes().get("maximum");
            if (maximum != null) {
                fmt.format("%s.maximum=%s;", jsvar, maximum);
                repaint = true;
            }
        }

        if (FacesUtils.isDynamicValue(component, "value")) {
            String value = getCurrentValue(context, component);
            if (value != null && value.length() != 0) {
                fmt.format("%s.setValue(%s);", jsvar, value);
                repaint = false;
            }
        }

        if (repaint) {
            fmt.format("%s.repaint();\n", jsvar);
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.writeScript(fmt.toString());
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.om.ProgressBar");
        
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String jsvar = resource.allocVariable(component);

        Object minimum = component.getAttributes().get("minimum");
        Object maximum = component.getAttributes().get("maximum");
        if (minimum == null) minimum = 0;
        if (maximum == null) maximum = 100;

        String value = getCurrentValue(context, component);
        if (value == null || value.length() == 0)
            value = "null";

        String script = String.format(
            "%s=new Ext.om.ProgressBar('%s',{minimum:%s,maximum:%s,value:%s});\n",
            jsvar, clientId, minimum, maximum, value);

        resource.addInitScript(script);
        resource.releaseVariable(jsvar);
    }
}
