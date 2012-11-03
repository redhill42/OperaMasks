/*
 * $Id: CheckMenuItemRenderer.java,v 1.3 2007/07/02 07:37:49 jacky Exp $
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
import java.util.Formatter;
import java.util.Map;
import java.io.IOException;

import org.operamasks.faces.render.html.UIInputRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;
import org.operamasks.faces.util.FacesUtils;

public class CheckMenuItemRenderer extends UIInputRenderer
    implements ResourceProvider
{
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

    public void renderCurrentValue(FacesContext context, UIComponent component, String currentValue)
        throws IOException
    {
        String clientId = component.getClientId(context);

        // Render hidden field to transfer value
        ResponseWriter out = context.getResponseWriter();
        out.startElement("input", component);
        out.writeAttribute("type", "hidden", null);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("name", clientId, "clientId");
        out.writeAttribute("value", currentValue, "value");
        out.endElement("input");
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        UIMenu menu = getParentMenu(component);
        if (menu == null) {
            return;
        }

        YuiExtResource resource = YuiExtResource.register(rm);
        FacesContext context = FacesContext.getCurrentInstance();
        String text = getItemText(context, component, false);
        String value = getCurrentValue(context, component);
        String onbeforechange = (String)component.getAttributes().get("onbeforechange");
        String onchange = (String)component.getAttributes().get("onchange");
        String onclick = (String)component.getAttributes().get("onclick");

        Formatter fmt = new Formatter();

        String var = resource.allocTempVariable();
        fmt.format("%s = new Ext.menu.CheckItem({", var);
        encodeItemConfig(fmt, context, component, text);
        if (value != null)
            fmt.format(",checked:%b", Boolean.valueOf(value));
        fmt.format("});\n");

        if (onbeforechange != null) {
            fmt.format("%s.on('beforecheckchange', function(item,checked){%s});\n",
                       var, onbeforechange);
        }

        UIForm form = getParentForm(component);
        if (form != null) {
            if (onchange == null) {
                onchange = "";
            } else if (!onchange.endsWith(";")) {
                onchange += ";";
            }

            String formId = form.getClientId(context);
            String clientId = component.getClientId(context);
            fmt.format("%1$s.on('checkchange', function(item,checked){" +
                       "%2$s" +
                       "document.getElementById('%3$s').value=checked;" +
                       "document.forms['%4$s'].submit();" +
                       "});\n",
                       var, onchange, clientId, formId);
        } else {
            if (onchange != null) {
                fmt.format("%s.on('checkchange', function(item,checked){%s});\n",
                           var, onchange);
            }
        }

        if (onclick != null) {
            fmt.format("%s.on('click', function(){%s});\n", var, onclick);
        }

        fmt.format("%s.addItem(%s);\n", FacesUtils.getJsvar(context, menu), var);

        resource.releaseVariable(var);
        resource.addInitScript(fmt.toString());
    }
}
