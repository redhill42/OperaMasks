/*
 * $Id: CommandMenuItemRenderer.java,v 1.4 2007/07/02 07:37:49 jacky Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;

import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.html.CommandLinkRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;

public class CommandMenuItemRenderer extends CommandLinkRenderer
    implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        UIMenu menu = getParentMenu(component);
        if (menu == null) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        String text = getItemText(context, component, true);
        String handler = getHandler(context, component);

        Formatter fmt = new Formatter();
        fmt.format("%s.addItem(new Ext.menu.Item({", FacesUtils.getJsvar(context, menu));
        encodeItemConfig(fmt, context, component, text);
        if (handler != null)
            fmt.format(",handler:function(){%s}", handler);
        fmt.format("}));\n");

        YuiExtResource.register(rm).addInitScript(fmt.toString());
    }

    private String getHandler(FacesContext context, UIComponent component) {
        UIForm form = getParentForm(component);
        if (form != null) {
            return super.getOnclickScript(context, form, component, false);
        } else {
            return (String)component.getAttributes().get("onclick");
        }
    }

    // no-op for markup
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {}
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException  {}
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {}
    public boolean getRendersChildren() { return true; }
}
