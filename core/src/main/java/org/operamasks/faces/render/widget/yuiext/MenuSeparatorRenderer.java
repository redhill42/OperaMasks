/*
 * $Id: MenuSeparatorRenderer.java,v 1.3 2007/07/02 07:37:50 jacky Exp $
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

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.menu.UIMenu;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.*;
import org.operamasks.faces.util.FacesUtils;

public class MenuSeparatorRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        UIMenu menu = getParentMenu(component);
        if (menu != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            String menuvar = FacesUtils.getJsvar(context, menu);
            YuiExtResource resource = YuiExtResource.register(rm);
            resource.addInitScript(menuvar + ".addSeparator();\n");
        }
    }

    // no-op for markup
    public void encodeBegin(FacesContext context, UIComponent component) {}
    public void encodeEnd(FacesContext context, UIComponent component) {}
    public void encodeChildren(FacesContext context, UIComponent component) {}
    public boolean getRendersChildren() { return true; }
}
