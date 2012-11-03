/*
 * $Id: AjaxCommandMenuItemRenderer.java,v 1.7 2008/04/24 05:17:19 lishaochuan Exp $
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

import org.operamasks.faces.render.ajax.AjaxCommandLinkRenderer;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;

public class AjaxCommandMenuItemRenderer extends AjaxCommandLinkRenderer
    implements ResourceProvider
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            String jsvar = FacesUtils.getJsvar(context, component);
            Formatter fmt = new Formatter();

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

        FacesContext context = FacesContext.getCurrentInstance();
        String text = getItemText(context, component, true);
        String handler = getOnclickScript(context, getParentForm(component), component, false);

        Formatter fmt = new Formatter();
        String menuJsVar = FacesUtils.getJsvar(context, menu);
        String itemJsVar = FacesUtils.getJsvar(context, component);
        fmt.format("%s = new Ext.menu.Item({", itemJsVar);
        encodeItemConfig(fmt, context, component, text);
        if (handler != null)
            fmt.format(",handler:function(){%s}", handler);
        fmt.format("});\n");

        fmt.format("%s.addItem(%s);\n", menuJsVar, itemJsVar);

        YuiExtResource.register(rm).addInitScript(fmt.toString());
    }

    // no-op for markup
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {}
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {}
    public boolean getRendersChildren() { return true; }
}
