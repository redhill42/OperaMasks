/*
 * $Id: AjaxDateMenuRenderer.java,v 1.5 2007/07/02 07:37:49 jacky Exp $
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
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Formatter;

import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.getParentMenu;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.getItemText;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.encodeItemConfig;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.component.widget.UIDatePicker;

public class AjaxDateMenuRenderer extends DateMenuRenderer
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            String jsvar = FacesUtils.getJsvar(context, component);
            String value = getCurrentValue(context, component);
            Formatter fmt = new Formatter();

            if (value != null && value.length() != 0) {
                fmt.format("%s.picker.setValue(Date.parseDate('%s','%s'));\n",
                           jsvar, value, UIDatePicker.CLIENT_FORMAT);
            }
            if (FacesUtils.isDynamicValue(component, "disabled")) {
                fmt.format("%s.setDisabled(%b);\n", jsvar, isDisabled(component));
            }

            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.writeScript(fmt.toString());
        }
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.menu.DateMenu");
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String jsvar = resource.allocVariable(component);
        String value = getCurrentValue(context, component);
        String onchange = (String)component.getAttributes().get("onchange");

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        fmt.format("%s = new Ext.menu.DateMenu({id:'%s',cls:'x-date-menu'});\n",
                   jsvar, clientId);

        if (value != null) {
            fmt.format("%s.picker.setValue(Date.parseDate('%s', '%s'));\n",
                       jsvar, value, UIDatePicker.CLIENT_FORMAT);
        }

        fmt.format("%s.on('select', function(o,d){", jsvar);
        if (onchange != null) {
            buf.append(onchange);
            if (!onchange.endsWith(";")) {
                buf.append(";");
            }
        }
        buf.append(encodeAjaxSubmit(context, component,
                                    HtmlEncoder.enquote(clientId),
                                    "d.format('" + UIDatePicker.CLIENT_FORMAT + "')"));
        buf.append("});\n");

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

    // no-op for markup
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {}
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {}
    public boolean getRendersChildren() { return true; }
}
