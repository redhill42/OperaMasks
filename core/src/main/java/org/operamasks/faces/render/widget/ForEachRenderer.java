/*
 * $Id: ForEachRenderer.java,v 1.2 2007/12/11 04:20:12 jacky Exp $
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
 * 
 */
package org.operamasks.faces.render.widget;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.invisible.ForEach;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;

public class ForEachRenderer extends HtmlRenderer implements ResourceProvider
{
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        ForEach data = (ForEach)component;
        int rowIndex = data.getFirst();
        int rows = data.getRows();
        int step = data.getStep() == null ? 1 : data.getStep();
        for (int curRow = 0; rows == 0 || curRow < rows; curRow+=step) {
            data.setRowIndex(rowIndex++);
            if (!data.isRowAvailable()) {
                break;
            }
            for (UIComponent child : component.getChildren()) {
                child.processDecodes(context);
            }
        }
        data.setRowIndex(-1);
    }
    
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ForEach data = (ForEach)component;
        int rowIndex = data.getFirst();
        int rows = data.getRows();
        int step = data.getStep() == null ? 1 : data.getStep();
        for (int curRow = 0; rows == 0 || curRow < rows; curRow+=step) {
            data.setRowIndex(rowIndex++);
            if (!data.isRowAvailable()) {
                break;
            }
            ResourceManager manager = ResourceManager.getInstance(context);
            for (UIComponent child : component.getChildren()) {
                manager.removeIgnoreChildren(child);
                manager.consumeResources(context, child);
            }
            super.encodeChildren(context, data);
        }
        data.setRowIndex(-1);
    }

    public void provideResource(ResourceManager manager, UIComponent component) {
        for (UIComponent child : component.getChildren()) {
            manager.setIgnoreChildren(child, true);
        }
    }
    
    
}
