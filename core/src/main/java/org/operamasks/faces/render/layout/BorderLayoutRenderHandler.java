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
import java.util.Arrays;
import java.util.logging.Level;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.layout.impl.UIBorderLayout;
import org.operamasks.faces.component.layout.impl.UIPanel;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.util.FacesUtils;

import static org.operamasks.resources.Resources.*;

public class BorderLayoutRenderHandler extends LayoutRenderHandler
{
    @Override
    protected String getExtClass(UIComponent component) {
        UIBorderLayout borderLayout = (UIBorderLayout) component;
        if (borderLayout.getFitToBody() == null || (borderLayout.getFitToBody() != null && !borderLayout.getFitToBody())) {
            return "Ext.Panel";
        } else {
            return "Ext.Viewport";
        }
    }
    
    @Override
    protected void processExtConfig(FacesContext context,
            UIComponent component, ExtConfig config) {
        UIBorderLayout borderLayout = (UIBorderLayout) component;
        config.set("layout", "border");
        if ((borderLayout.getFitToBody() != null && borderLayout.getFitToBody())) {
            config.remove("applyTo");
        }
    }
    
    private static final String[] regions = {"center", "east", "south", "west", "north"};
    private static final int CENTER_POSITION = 0;
    
    private static void validateStructure(FacesContext context, UIComponent component) {
        assert component instanceof UIBorderLayout;
        boolean[] regionExists = new boolean[regions.length];
        for (int i = 0; i < regionExists.length; i++) {
            regionExists[i] = false;
        }
        for (UIComponent child : component.getChildren()) {
            UIPanel panelChild = (UIPanel) child;
            String region = panelChild.getRegion();
            if (region != null) {
                int i = 0;
                while (i < regions.length && !regions[i].equals(region)) {
                    i++;
                }
                if (i < region.length()) {
                    if (!regionExists[i])
                        regionExists[i] = true;
                    else
                        throw new FacesException(_T(UI_BORDERLAYOUT_DUPLICATE_REGION, FacesUtils.getComponentDesc(component), regions[i]));
                } else {
                    throw new FacesException(_T(UI_BORDERLAYOUT_UNDEFINED_REGION, FacesUtils.getComponentDesc(child), region, Arrays.toString(regions)));
                } 
            }
        }
        if (!regionExists[CENTER_POSITION]) {
            throw new FacesException(_T(UI_BORDERLAYOUT_MISSING_CENTER, FacesUtils.getComponentDesc(component)));
        }
    }
    
    @Override
    public void htmlBegin(FacesContext context, UIComponent component)
            throws IOException {
        validateStructure(context, component);
        super.htmlBegin(context, component);
    }
}
