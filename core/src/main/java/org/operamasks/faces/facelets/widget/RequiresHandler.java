/*
 * $Id: RequiresHandler.java,v 1.1 2007/09/17 16:21:48 daniel Exp $
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
package org.operamasks.faces.facelets.widget;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.FaceletContext;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public class RequiresHandler extends TagHandler
{
    private TagAttribute pkg;

    public RequiresHandler(TagConfig config) {
        super(config);
        this.pkg = this.getRequiredAttribute("package");
    }

    public void apply(FaceletContext ctx, UIComponent parent) {
        String pkg = this.pkg.getValue(ctx);

        if (pkg.equalsIgnoreCase("OM.ajax")) {
            UIViewRoot viewRoot = ctx.getFacesContext().getViewRoot();
            String renderKitId = viewRoot.getRenderKitId();
            if (renderKitId == null || "HTML_BASIC".equals(renderKitId)) {
                viewRoot.setRenderKitId("AJAX");
            }
        } else {
            ResourceManager rm = ResourceManager.getInstance(ctx.getFacesContext());
            YuiExtResource.register(rm, pkg);
        }
    }
}
