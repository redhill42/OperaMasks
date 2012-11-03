/*
 * $Id 
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
package org.operamasks.faces.component.widget.grid;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public class CheckBoxInputColumn extends UIComponentBase implements UIInputColumn
{
    public static final String COMPONENT_TYPE = "javax.faces.CheckBoxInputColumn";
    public static final String COMPONENT_FAMILY = "javax.faces.Column";

    public void render(StringBuilder buf) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceManager rm = ResourceManager.getInstance(context);
        YuiExtResource.register(rm, "Ext.QuickTips");
        buf.append(",editor: new Ext.grid.GridEditor(new Ext.form.Checkbox())\n");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
