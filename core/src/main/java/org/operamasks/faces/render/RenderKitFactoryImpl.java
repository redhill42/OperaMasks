/*
 * $Id: RenderKitFactoryImpl.java,v 1.5 2007/07/02 07:38:17 jacky Exp $
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

package org.operamasks.faces.render;

import javax.faces.render.RenderKitFactory;
import javax.faces.render.RenderKit;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.operamasks.faces.render.html.HtmlBasicRenderKit;

public class RenderKitFactoryImpl extends RenderKitFactory
{
    private Map<String,RenderKit> renderKits = new HashMap<String,RenderKit>();

    public RenderKitFactoryImpl() {
        addRenderKit(HTML_BASIC_RENDER_KIT, new HtmlBasicRenderKit());
    }

    public void addRenderKit(String renderKitId, RenderKit renderKit) {
        renderKits.put(renderKitId, renderKit);
    }

    public RenderKit getRenderKit(FacesContext context, String renderKitId) {
        return renderKits.get(renderKitId);
    }

    public Iterator<String> getRenderKitIds() {
        return renderKits.keySet().iterator();
    }
}
