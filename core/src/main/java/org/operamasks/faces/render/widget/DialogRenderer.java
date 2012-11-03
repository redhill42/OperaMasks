/*
 * $Id: DialogRenderer.java,v 1.5 2007/07/02 07:38:07 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import java.util.Iterator;

public class DialogRenderer extends WindowRenderer
{
    protected void createScript(FacesContext context, UIComponent component,
                                String jsvar, StringBuilder buf)
    {
        super.createScript(context, component, jsvar, buf);

        String defaultButtonId = (String)component.getAttributes().get("defaultButton");
        if (defaultButtonId != null) {
            UIComponent defaultButton = findComponent(component, defaultButtonId);
            if (defaultButton != null) {
                buf.append(jsvar);
                buf.append(".setDefaultButton('");
                buf.append(defaultButton.getClientId(context));
                buf.append("');");
            }
        }

        String cancelButtonId = (String)component.getAttributes().get("cancelButton");
        if (cancelButtonId != null) {
            UIComponent cancelButton = findComponent(component, cancelButtonId);
            if (cancelButton != null) {
                buf.append(jsvar);
                buf.append(".setCancelButton('");
                buf.append(cancelButton.getClientId(context));
                buf.append("');");
            }
        }
    }

    @Override
    protected String getClassName() {
        return "UIDialog";
    }

    private UIComponent findComponent(UIComponent base, String id) {
        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        UIComponent found = null;
        while (found == null && kids.hasNext()) {
            UIComponent component = kids.next();
            if (component instanceof NamingContainer) {
                found = component.findComponent(id);
            } else {
                found = findComponent(component, id);
            }
        }
        return found;
    }
}
