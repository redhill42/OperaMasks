/*
 * $Id: LabelRenderer.java,v 1.6 2007/09/17 16:21:48 daniel Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import org.operamasks.faces.util.FacesUtils;

public class LabelRenderer extends UIOutputRenderer
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String forClientId = getForComponentClientId(context, component);

        out.startElement("label", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (forClientId != null)
            out.writeAttribute("for", forClientId, "for");
        renderPassThruAttributes(out, component);
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        Object value = getValue(component);

        if (value == null) {
            super.encodeChildren(context, component);
        } else {
            ResponseWriter out = context.getResponseWriter();
            String text = getFormattedValue(context, component, value);
            if (text != null && text.length() != 0) {
                if (needsEscape(component)) {
                    out.writeText(text, "value");
                } else {
                    out.write(text);
                }
            }
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.endElement("label");
    }

    private String getForComponentClientId(FacesContext context, UIComponent component) {
        String forId = (String)component.getAttributes().get("for");
        if (forId == null || forId.length() == 0) {
            return null;
        }

        UIComponent forComponent = FacesUtils.getForComponent(context, forId, component);
        if (forComponent != null) {
            return forComponent.getClientId(context);
        }

        UIComponent base = component;
        while (base != null) {
            if (base instanceof NamingContainer) {
                break;
            }
            base = base.getParent();
        }
        if (base != null) {
            return base.getClientId(context) + NamingContainer.SEPARATOR_CHAR + forId;
        }
        return null;
    }
}
