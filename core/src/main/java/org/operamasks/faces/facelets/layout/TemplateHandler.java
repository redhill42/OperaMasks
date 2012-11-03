/*
 * $Id: TemplateHandler.java,v 1.3 2007/12/19 01:46:19 yangdong Exp $
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
package org.operamasks.faces.facelets.layout;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.el.VariableMapper;

import java.io.IOException;

import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import org.operamasks.faces.component.layout.UITemplateContainer;
import org.operamasks.faces.component.layout.TemplateLayout;
import org.operamasks.faces.layout.LayoutContext;

public class TemplateHandler extends ComponentHandler
{
    private TagAttribute src;

    public TemplateHandler(ComponentConfig config) {
        super(config);
        this.src = this.getAttribute("src");
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        // create template container
        UIComponent container = new UITemplateContainer();
        UIViewRoot root = ctx.getFacesContext().getViewRoot();
        container.setId(root.createUniqueId());
        c.getChildren().add(container);
    }

    @Override
    protected void applyNextHandler(FaceletContext ctx, UIComponent c)
        throws IOException
    {
        if (this.src != null) {
            TemplateLayout layout = (TemplateLayout)c;
            layout.getTemplateContainer().getChildren().clear();
            LayoutContext.pushLayoutContext(ctx.getFacesContext(), layout);

            VariableMapper orig = ctx.getVariableMapper();
            ctx.setVariableMapper(new VariableMapperWrapper(orig));

            try {
                super.applyNextHandler(ctx, c);
                ctx.includeFacelet(layout.getTemplateContainer(), this.src.getValue(ctx));
            } finally {
                ctx.setVariableMapper(orig);
                LayoutContext.popLayoutContext(ctx.getFacesContext());
            }
        }
    }
}
