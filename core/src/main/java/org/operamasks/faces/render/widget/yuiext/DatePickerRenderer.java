/*
 * $Id: DatePickerRenderer.java,v 1.3 2007/07/02 07:37:50 jacky Exp $
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
import javax.faces.convert.ConverterException;
import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;

import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.html.UIInputRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.component.widget.UIDatePicker;
import org.operamasks.faces.util.FacesUtils;

public class DatePickerRenderer extends UIInputRenderer
    implements ResourceProvider
{
    @Override
    protected String getFormattedValue(FacesContext context, UIComponent component, Object currentValue)
        throws ConverterException
    {
        return UIDatePicker.getFormattedValue(currentValue);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        return UIDatePicker.getConvertedValue((String)submittedValue);
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
            String value = getCurrentValue(context, component);
            ResponseWriter out = context.getResponseWriter();

            // Render DatePicker container
            out.startElement("div", component);
            out.writeAttribute("id", clientId + NamingContainer.SEPARATOR_CHAR + "_container", null);
            renderPassThruAttributes(out, component, "disabled,onchange");
            out.endElement("div");
            out.write("\n");

            // Render proxy input field
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", clientId, "clientId");
            out.writeAttribute("name", clientId, "clientId");
            out.writeAttribute("value", value, "value");
            out.endElement("input");
            out.write("\n");
        }
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.DatePicker");

        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String containerId = clientId + NamingContainer.SEPARATOR_CHAR + "_container";
        String jsvar = resource.allocVariable(component);
        String value = getCurrentValue(context, component);
        Formatter fmt = new Formatter();

        fmt.format("%s = new Ext.DatePicker({disabled:%b});\n",
                   jsvar, isDisabled(component));

        if (value != null) {
            fmt.format("%s.setValue(Date.parseDate('%s','%s'));\n",
                       jsvar, value, UIDatePicker.CLIENT_FORMAT);
        }

        fmt.format("%s.render('%s');\n", jsvar, containerId);

        String onchange = (String)component.getAttributes().get("onchange");
        if (onchange == null) {
            onchange = "";
        } else if (!onchange.endsWith(";")) {
            onchange += ";";
        }

        fmt.format("%s.on('select', function(o,d){" +
                   "Ext.getDom('%s').value = d.format('%s');" +
                   "%s" +
                   "});\n",
                   jsvar, clientId, UIDatePicker.CLIENT_FORMAT, onchange);

        resource.releaseVariable(jsvar);
        resource.addInitScript(fmt.toString());
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        String jsvar = FacesUtils.getJsvar(context, component);
        String value = getCurrentValue(context, component);
        Formatter fmt = new Formatter();

        if (value != null && value.length() != 0) {
            fmt.format("%s.setValue(Date.parseDate('%s', '%s'));\n",
                       jsvar, value, UIDatePicker.CLIENT_FORMAT);
        }
        if (FacesUtils.isDynamicValue(component, "disabled")) {
            fmt.format("%s.setDisabled(%b);\n", jsvar, isDisabled(component));
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.writeScript(fmt.toString());
    }
}
