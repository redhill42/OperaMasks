/*
 * $Id: SeparatorRenderer.java,v 1.3 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;

import java.io.IOException;

import org.operamasks.faces.render.html.HtmlRenderer;

public class SeparatorRenderer extends HtmlRenderer
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        out.startElement("hr", component);
        renderPassThruAttributes(out, component);
        out.endElement("hr");
    }
}
