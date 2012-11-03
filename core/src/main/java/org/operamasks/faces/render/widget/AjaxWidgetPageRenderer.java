/*
 * $Id: AjaxWidgetPageRenderer.java,v 1.7 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.io.IOException;

import org.operamasks.faces.render.ajax.AjaxHtmlPageRenderer;
import org.operamasks.faces.render.resource.SkinManager;

public class AjaxWidgetPageRenderer extends AjaxHtmlPageRenderer
{
    // XXX Copy & Paste from WidgetPageRenderer
    
    @Override
    protected void encodeBodyBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        String skin = SkinManager.getCurrentSkin(context);
        String styleClass = (String)component.getAttributes().get("styleClass");
        String skinClass = "skin-" + skin;

        if (styleClass != null) {
            if (styleClass.indexOf(skinClass) != -1) {
                skinClass = styleClass;
            } else {
                skinClass += " " + styleClass;
            }
        }

        component.getAttributes().put("styleClass", skinClass);
        super.encodeBodyBegin(context, component);
        component.getAttributes().put("styleClass", styleClass);
    }
}
