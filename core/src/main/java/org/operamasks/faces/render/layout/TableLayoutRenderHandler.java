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
package org.operamasks.faces.render.layout;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.layout.impl.UITableLayout;
import org.operamasks.faces.component.widget.ExtConfig;

@ExtClass("Ext.Panel")
public class TableLayoutRenderHandler extends LayoutRenderHandler
{
    @Override
    protected void processExtConfig(FacesContext context,
            UIComponent component, ExtConfig config) {
        UITableLayout tableLayout = (UITableLayout) component;
        config.set("layout", "table");
        config.set("layoutConfig", "{columns:" + tableLayout.getColumns() + "}", true);
    }
    
    protected String getContainerDefaultStyle(){
        return "height:100%;width:100%;";
    }
}
