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
package org.operamasks.faces.render.form;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.form.impl.UISimpleHtmlEditor;
import org.operamasks.faces.component.widget.ExtConfig;

@ExtClass("Ext.form.HtmlEditor")
@DependPackages( { "Ext.form", "Ext.Toolbar", "Ext.QuickTips", "Ext.menu.Menu",
		"Ext.ColorPalette" })
public class SimpleHtmlEditorRenderHandler extends AbstractFieldRenderHandler {
	@Override
	protected String getHtmlMarkup() {
		return "textarea";
	}
	
	@Override
    protected void processExtConfig(FacesContext context, UIComponent component, ExtConfig config) {
        UISimpleHtmlEditor simpleHtmlEditor = (UISimpleHtmlEditor)component;
        if(simpleHtmlEditor.getWidth() == null){
            config.set("width", 550);
        }
    }
}
