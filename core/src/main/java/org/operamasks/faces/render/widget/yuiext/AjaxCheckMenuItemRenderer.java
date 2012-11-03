/*
 * $Id: AjaxCheckMenuItemRenderer.java,v 1.5 2007/07/02 07:37:49 jacky Exp $
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

import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;

public class AjaxCheckMenuItemRenderer extends CheckMenuItemRenderer
    implements ResourceProvider
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            String jsvar = FacesUtils.getJsvar(context, component);
            String value = getCurrentValue(context, component);
            Formatter fmt = new Formatter();

            fmt.format("%s.setChecked(%b,true);\n", jsvar, Boolean.valueOf(value));
            if (FacesUtils.isDynamicValue(component, "label")) {
                String text = HtmlEncoder.enquote(getItemText(context, component, false), '\'');
                fmt.format("%s.setText(%s);\n", jsvar, text);
            }
            if (FacesUtils.isDynamicValue(component, "disabled")) {
                fmt.format("%s.setDisabled(%b);\n", jsvar, isDisabled(component));
            }

            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.writeScript(fmt.toString());
        }
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        UIMenu menu = getParentMenu(component);
        if (menu == null) {
            return;
        }

        YuiExtResource resource = YuiExtResource.register(rm);
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        String text = getItemText(context, component, false);
        String value = getCurrentValue(context, component);

        String onbeforechange = (String)component.getAttributes().get("onbeforechange");
        String onchange = (String)component.getAttributes().get("onchange");
        String onclick = (String)component.getAttributes().get("click");

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        String var = resource.allocVariable(component);
        fmt.format("%s = new Ext.menu.CheckItem({", var);
        encodeItemConfig(fmt, context, component, text);
        if (value != null)
            fmt.format(",checked:%b", Boolean.valueOf(value));
        fmt.format("});\n");

        if (onbeforechange != null) {
            fmt.format("%s.on('beforecheckchange', function(item,checked){%s});\n",
                       var, onbeforechange);
        }

        fmt.format("%s.on('checkchange', function(item,checked){", var);
        if (onchange != null) {
            buf.append(onchange);
            if (!onchange.endsWith(";")) {
                buf.append(";");
            }
        }
        buf.append(encodeAjaxSubmit(context, component, HtmlEncoder.enquote(clientId), "checked"));
        buf.append("});\n");

        if (onclick != null) {
            fmt.format("%s.on('click', function(){%s};\n", var, onclick);
        }
        
        fmt.format("%s.addItem(%s);\n", FacesUtils.getJsvar(context, menu), var);

        resource.releaseVariable(var);
        resource.addInitScript(fmt.toString());
    }

    // no-op for markup
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {}
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {}
    public boolean getRendersChildren() { return true; }
}
