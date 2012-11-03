/*
 * $Id: RadioMenuItemRenderer.java,v 1.3 2007/07/02 07:37:49 jacky Exp $
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
import javax.faces.component.UIForm;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.Map;
import java.util.List;
import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.html.UISelectRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class RadioMenuItemRenderer extends UISelectRenderer
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
        String menuId = FacesUtils.getJsvar(context, menu);
        String clientId = component.getClientId(context);
        String jsvar = resource.allocTempVariable();
        List<SelectItem> items = getSelectItems(context, component);

        boolean disabled = isDisabled(component);
        String onbeforechange = (String)component.getAttributes().get("onbeforechange");
        String onchange = (String)component.getAttributes().get("onchange");
        String onclick = (String)component.getAttributes().get("onclick");

        UIForm form = getParentForm(component);
        String formId = (form != null) ? form.getClientId(context) : null;

        if (onchange == null) {
            onchange = "";
        } else if (!onchange.endsWith(";")) {
            onchange += ";";
        }

        Formatter fmt = new Formatter();

        for (int i = 0; i < items.size(); i++) {
            SelectItem item = items.get(i);

            fmt.format("%s = new Ext.menu.CheckItem({", jsvar);
            encodeItemConfig(fmt, context, component, item.getLabel());
            fmt.format(",group:'%s'", clientId);
            if (isOneSelected(context, component, item.getValue()))
                fmt.format(",checked:true");
            if (!disabled && item.isDisabled())
                fmt.format(",disabled:true");
            fmt.format("});\n");

            if (onbeforechange != null) {
                fmt.format("%s.on('beforecheckchange', function(item,checked){%s});\n",
                           jsvar, onbeforechange);
            }

            if (form != null) {
                String itemValue = getFormattedValue(context, component, item.getValue());
                itemValue = HtmlEncoder.enquote(itemValue, '\'');

                fmt.format("%1$s.on('checkchange', function(item,checked){" +
                           "%2$s" +
                           "if(checked){" +
                               "document.getElementById('%3$s').value = %4$s;" +
                               "document.forms['%5$s'].submit();" +
                           "}});\n",
                           jsvar, onchange, clientId, itemValue, formId);
            } else {
                if (onchange != null) {
                    fmt.format("%s.on('checkchange', function(item,checked){%s});\n",
                               jsvar, onchange);
                }
            }

            if (onclick != null) {
                fmt.format("%s.on('click', function(){%s});\n", jsvar, onclick);
            }

            fmt.format("%s.addItem(%s);\n", menuId, jsvar);
        }

        resource.releaseVariable(jsvar);
        resource.addInitScript(fmt.toString());
    }
}
