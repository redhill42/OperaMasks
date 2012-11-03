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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.layout.impl.UICardLayout;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.util.FacesUtils;

@ExtClass("Ext.Panel")
public class CardLayoutRenderHandler extends LayoutRenderHandler
{
    @Override
    protected void processExtConfig(FacesContext context,
            UIComponent component, ExtConfig config) {
        config.set("layout", "card");
        config.set("header", false);
        config.set("activeItem", 0);
    }
    
    @OperationListener("setActiveItem")
    public void activeItem(UICardLayout cardLayout, Integer item) throws IOException{
        FacesContext context = FacesContext.getCurrentInstance();
        cardLayout.setActiveItem(item);
        String jsvar = FacesUtils.getJsvar(context, cardLayout);
        addOperationScript(jsvar + ".layout.setActiveItem(" + item + ");");
        
    }
}
