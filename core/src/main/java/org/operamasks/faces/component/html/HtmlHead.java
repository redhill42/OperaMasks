/*
 * $Id: HtmlHead.java,v 1.5 2007/07/02 07:38:13 jacky Exp $
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

package org.operamasks.faces.component.html;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.Resource;
import org.operamasks.faces.render.resource.AbstractResource;

import java.io.IOException;

/**
 * Represents an HTML HEAD element in an HTML document.
 */
public class HtmlHead extends UIComponentBase
    implements ResourceProvider
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.HtmlHead";

    /**
     * The component family for this component.
     */
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.HtmlDocument";
    
    /**
     * Create a new {@link HtmlHead} instance with default property values.
     */
    public HtmlHead() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public void provideResource(ResourceManager manager, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = "urn:head:" + component.getClientId(context);

        Resource resource = new AbstractResource(id) {
            public int getPriority() { return LOW_PRIORITY; }

            public void encodeBegin(FacesContext context)
                throws IOException
            {
                for (UIComponent kid : getChildren()) {
                    kid.encodeAll(context);
                }
            }
        };

        manager.registerResource(resource);
    }

    public boolean getRendersChildren() {
        return true; // bypass default rendering
    }

    // nothing to render at the body of the page
    public void encodeBegin(FacesContext context) {}
    public void encodeChildren(FacesContext context) {}
    public void encodeEnd(FacesContext context) {}
}
