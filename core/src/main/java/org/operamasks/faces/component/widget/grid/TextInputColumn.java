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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.operamasks.faces.component.widget.UITextField;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;


public class TextInputColumn extends UITextField implements UIInputColumn,ResourceProvider
{
    public static final String COMPONENT_TYPE = "javax.faces.TextInputColumn";
    public static final String COMPONENT_FAMILY = "javax.faces.Column";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public void render(StringBuilder buf) {
        buf.append(",editor: ");
        buf.append("new Ext.grid.GridEditor(");
        FacesContext context = FacesContext.getCurrentInstance();
        String jsvar = FacesUtils.getJsvar(context, this);
        buf.append(jsvar);
        buf.append(")\n");
    }
    
    public void provideResource(final ResourceManager rm, final UIComponent component) {
        String id = "urn:textInputColumn:" + component.getClientId(FacesContext.getCurrentInstance());
        rm.registerResource(new AbstractResource(id) {
            public void encodeBegin(FacesContext context) throws IOException {
                Renderer renderer = null;
                String family = UITextField.COMPONENT_FAMILY;
                String rendererType = "org.operamasks.faces.widget.TextField";
                if (rendererType != null) {
                    renderer = context.getRenderKit().getRenderer(family, rendererType);
                }
                if (renderer instanceof ResourceProvider) {
                    ((ResourceProvider)renderer).provideResource(rm, component);
                    String jsvar = FacesUtils.getJsvar(context, component);
                    YuiExtResource resource = YuiExtResource.register(rm);
                    StringBuffer buf = new StringBuffer();
                    buf.append(jsvar).append(".validationEvent='keyup';").append(jsvar).append(".validateOnBlur=true;");
                    resource.addInitScript(buf.toString());
                }
            }
        });
    }
}
