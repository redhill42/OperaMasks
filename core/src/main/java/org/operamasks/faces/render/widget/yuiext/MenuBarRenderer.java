/*
 * $Id: MenuBarRenderer.java,v 1.5 2008/01/16 11:38:42 yangdong Exp $
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
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;
import java.io.IOException;

import org.operamasks.faces.util.FacesUtils;

public class MenuBarRenderer extends MenuRenderer
{
    @Override
    protected void encodeMenu(FacesContext context, YuiExtResource resource, UIComponent component) {
        if (MenuRendererHelper.getParentMenu(component) != null) {
            throw new FacesException("The menu bar cannot have a parent menu.");
        }
        
        String clientId = component.getClientId(context);
        String jsvar = FacesUtils.getJsvar(context, component);

        Object autoExpand = component.getAttributes().get("autoExpand");
        if (autoExpand == null)
            autoExpand = false;

        String script = String.format(
            "%1$s = new Ext.menu.MenuBar({renderTo:'%2$s', id:'%2$s', autoExpand:%3$b, subMenuAlign: 'tl-bl?'});\n",
            jsvar, clientId, autoExpand);

        resource.addPackageDependency("Ext.menu.MenuBar");
        resource.addVariable(jsvar);
        resource.addInitScript(script);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        // encode menu bar container
        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", component);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        renderPassThruAttributes(out, component);
        out.endElement("div");
    }
}
