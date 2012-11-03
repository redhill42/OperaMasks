/*
 * $Id: DateMenuRenderer.java,v 1.3 2007/07/02 07:37:50 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.convert.ConverterException;
import java.util.Map;
import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.render.html.UIInputRenderer;
import org.operamasks.faces.component.widget.UIDatePicker;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;
import org.operamasks.faces.util.FacesUtils;

public class DateMenuRenderer extends UIInputRenderer
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

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        String clientId = component.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();

        if (paramMap.containsKey(clientId)) {
            String newValue = paramMap.get(clientId);
            setSubmittedValue(component, newValue);

            // Queue ActionEvent for parent menu
            ActionEvent event = new ActionEvent(component);
            component.queueEvent(event);
        }
    }

    public void renderCurrentValue(FacesContext context, UIComponent component, String value)
        throws IOException
    {
        String clientId = component.getClientId(context);

        // Render hidden field to transfer value
        ResponseWriter out = context.getResponseWriter();
        out.startElement("input", component);
        out.writeAttribute("type", "hidden", null);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("name", clientId, "clientId");
        out.writeAttribute("value", value, "value");
        out.endElement("input");
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.menu.DateMenu");
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String jsvar = resource.allocVariable(component);
        String value = getCurrentValue(context, component);

        Formatter fmt = new Formatter();
        fmt.format("%s = new Ext.menu.DateMenu({id:'%s',cls:'x-date-menu'});\n",
                   jsvar, clientId);

        if (value != null) {
            fmt.format("%s.picker.setValue(Date.parseDate('%s', '%s'));\n",
                       jsvar, value, UIDatePicker.CLIENT_FORMAT);
        }

        UIForm form = getParentForm(component);
        String onchange = (String)component.getAttributes().get("onchange");

        if (form != null) {
            if (onchange == null) {
                onchange = "";
            } else if (!onchange.endsWith(";")) {
                onchange += ";";
            }

            String formId = form.getClientId(context);
            fmt.format("%s.picker.on('select', function(o,d){" +
                       "%s" +
                       "Ext.getDom('%s').value = d.format('%s');" +
                       "document.forms['%s'].submit();" +
                       "});\n",
                       jsvar, onchange, clientId, UIDatePicker.CLIENT_FORMAT, formId);
        } else {
            if (onchange != null) {
                fmt.format("%s.picker.on('select', function(){%s});\n", jsvar, onchange);
            }
        }

        UIMenu parentMenu = getParentMenu(component);
        if (parentMenu != null) {
            String text = getItemText(context, component, false);
            fmt.format("%s.addItem(new Ext.menu.Item({", FacesUtils.getJsvar(context, parentMenu));
            encodeItemConfig(fmt, context, component, text);
            fmt.format(",menu:%s", jsvar);
            fmt.format("}));\n");
        }

        resource.releaseVariable(jsvar);
        resource.addInitScript(fmt.toString());
    }
}
