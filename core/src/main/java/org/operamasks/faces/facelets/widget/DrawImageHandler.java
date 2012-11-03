/*
 * $Id: DrawImageHandler.java,v 1.1 2007/08/14 07:40:34 daniel Exp $
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

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.FaceletContext;
import org.operamasks.faces.component.widget.UIDrawImage;

public class DrawImageHandler extends ComponentHandler
{
    private TagAttribute draw;

    private static Class[] DRAW_SIG = { java.awt.Graphics.class, int.class, int.class };

    public DrawImageHandler(ComponentConfig config) {
        super(config);
        this.draw = this.getAttribute("draw");
        if (this.draw != null) {
            this.getRequiredAttribute("width");
            this.getRequiredAttribute("height");
        } else {
            this.getRequiredAttribute("value");
        }
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        if (this.draw != null) {
            ((UIDrawImage)c).setDrawMethod(this.draw.getMethodExpression(ctx, Void.TYPE, DRAW_SIG));
        }
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignore("draw").ignore("drawMethod");
    }
}
