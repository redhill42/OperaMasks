/*
 * $Id: MenuRenderer.java,v 1.4 2007/07/02 07:37:47 jacky Exp $
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
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.util.List;

public class MenuRenderer extends UISelectRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        List<SelectItem> items = getSelectItems(context, component);
        renderMenu(context, component, items);
    }
    
    protected void renderMenu(FacesContext context, UIComponent component,
                              List<SelectItem> items)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();

        out.startElement("select", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        if (component instanceof UISelectMany)
            out.writeAttribute("multiple", Boolean.TRUE, "multiple");
        Integer size = getSizeAttribute(context, component);
        if (size != null && size.intValue() != Integer.MIN_VALUE)
            out.writeAttribute("size", size, "size");
        renderPassThruAttributes(out, component, "size");
        out.writeText("\n", null);
        renderOptions(context, component, items);
        out.endElement("select");
    }

    protected void renderOptions(FacesContext context, UIComponent component,
                                 List<SelectItem> items)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();

        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                SelectItem[] groupItems = ((SelectItemGroup)item).getSelectItems();
                out.startElement("optgroup", component);
                out.writeAttribute("label", item.getLabel(), "label");
                for (int i = 0; i < groupItems.length; i++)
                    renderOption(context, component, groupItems[i]);
                out.endElement("optgroup");
            } else {
                renderOption(context, component, item);
            }
        }
    }

    protected void renderOption(FacesContext context, UIComponent component, SelectItem item)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String itemValueStr = getFormattedValue(context, component, item.getValue());
        String labelClass = null;

        out.startElement("option", component);
        out.writeAttribute("value", itemValueStr, "value");
        if (isSelected(context, component, item.getValue(), itemValueStr))
            out.writeAttribute("selected", Boolean.TRUE, "selected");
        if (!isDisabled(component) && item.isDisabled())
            out.writeAttribute("disabled", Boolean.TRUE, "disabled");
        if (item.isDisabled()) {
            labelClass = (String)component.getAttributes().get("disabledClass");
        } else {
            labelClass = (String)component.getAttributes().get("enabledClass");
        }
        if (labelClass != null)
            out.writeAttribute("class", labelClass, "labelClass");
        out.writeText(item.getLabel(), "label");
        out.endElement("option");
        out.writeText("\n", null);
    }

    protected Integer getSizeAttribute(FacesContext context, UIComponent component) {
        Integer size = (Integer)component.getAttributes().get("size");
        if (size == null || size.intValue() == Integer.MIN_VALUE)
            size = 1;
        return size;
    }
}
