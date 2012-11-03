/*
 * $Id: ToolBarRenderer.java,v 1.5 2008/04/24 05:17:19 lishaochuan Exp $
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

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;

public class ToolBarRenderer extends HtmlRenderer implements ResourceProvider {
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component.getParent() instanceof UIDataGrid)
        	return;
        
        UIToolBar toolBar = (UIToolBar)component;
        
        String style = toolBar.getStyle();
        String styleClass = toolBar.getStyleClass();

        ExtJsUtils.encodeContainerForComponentBegin(context, component, style, styleClass);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    	if (component.getParent() instanceof UIDataGrid)
    			return;
    	
    	UIToolBar toolBar = (UIToolBar)component;
    	
        String style = toolBar.getStyle();
        String styleClass = toolBar.getStyleClass();
  		ExtJsUtils.encodeContainerForComponentEnd(context, component, style, styleClass);
    }

    public void provideResource(final ResourceManager rm, final UIComponent component) {
        YuiExtResource.register(rm, "Ext.Toolbar");
        encodeScript(rm, component);
        ToolBarUtils.adjustToolBarItemsRenderer((UIToolBar)component);
    }
    
    private void encodeScript(ResourceManager manager, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(manager, "Ext.Toolbar");
        FacesContext context = FacesContext.getCurrentInstance();
        
        String jsvar = resource.allocVariable(component);
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);
        
        fmt.format(
        		"if (typeof %s == 'undefined') {" +
                "\n%s = new Ext.Toolbar({});\n" +
                "}",
                jsvar,
                jsvar
        );
        
        fmt.format(ToolBarUtils.renderToolbar(context, (UIToolBar)component));
        
        resource.addInitScript(buf.toString());
    }
}
