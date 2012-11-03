/*
 * $Id: ContextMenuRenderer.java,v 1.4 2007/07/02 07:37:49 jacky Exp $
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

import org.operamasks.faces.util.FacesUtils;

public class ContextMenuRenderer extends MenuRenderer
{
    @Override
    protected void encodeMenu(FacesContext context, YuiExtResource resource, UIComponent component) {
        super.encodeMenu(context, resource, component);

        String forId = (String)component.getAttributes().get("for");
        if (forId != null) {
            UIComponent forComponent = FacesUtils.getForComponent(context, forId, component);
            if (forComponent != null) {
                forId = forComponent.getClientId(context);
            }
        } else {
            UIComponent parent = component.getParent();
            if (parent != null) {
                forId = parent.getClientId(context);
            }
        }

        if (forId != null) {
            String eventType = (String)component.getAttributes().get("eventType");
            if (eventType == null)
                eventType = "contextmenu";

            String script = String.format(
                "document.getElementById('%1$s').on%3$s = function( e ) {" +
                    "e = e || window.event ;" +
                    "%2$s.showAt( Ext.lib.Event.getXY(e) , null ,false ) ;" +
                    "if( window.event ) {" +
                    "window.event.returnValue = false ;" +
                    "}" +
                    "return false ;" +
                    "};\n" ,
                forId, FacesUtils.getJsvar(context, component), eventType
            );
            resource.addInitScript(script);
        }
    }
}
