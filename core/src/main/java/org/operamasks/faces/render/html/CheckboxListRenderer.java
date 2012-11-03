/*
 * $Id: CheckboxListRenderer.java,v 1.4 2007/07/02 07:37:48 jacky Exp $
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
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.util.List;

public class CheckboxListRenderer extends UISelectRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        List<SelectItem> items = getSelectItems(context, component);
        renderCheckboxList(context, component, items);
    }

    protected void renderCheckboxList(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        String layout = (String)component.getAttributes().get("layout");
        boolean vertical = "pageDirection".equalsIgnoreCase(layout);
        
        int border = 0;
        Object borderObj = component.getAttributes().get("border");
        if (borderObj instanceof Integer) {
            border = ((Integer)borderObj).intValue();
        } else if (borderObj != null) {
            try {
                border = Integer.valueOf(borderObj.toString()).intValue();
            } catch (Exception ex) {
                border = 0;
            }
        }

        ResponseWriter out = context.getResponseWriter();
        int idx = 0;

        renderTableBegin(context, component, border, vertical, true);
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                if (item.getLabel() != null) {
                    if (vertical)
                        out.startElement("tr", component);
                    out.startElement("td", component);
                    out.writeText(item.getLabel(), "label");
                    out.endElement("td");
                    if (vertical)
                        out.endElement("tr");
                }

                if (vertical)
                    out.startElement("tr", component);
                out.startElement("td", component);

                renderTableBegin(context, component, 0, vertical, false);
                for (SelectItem groupItem : ((SelectItemGroup)item).getSelectItems())
                    renderOption(context, component, groupItem, vertical, idx++);
                renderTableEnd(context, component, vertical, false);

                out.endElement("td");
                if (vertical) {
                    out.endElement("tr");
                }
            } else {
                renderOption(context, component, item, vertical, idx++);
            }
        }
        renderTableEnd(context, component, vertical, true);
    }
    
    private void renderTableBegin(FacesContext context, UIComponent component,
                                  int border, boolean vertical, boolean outerTable)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        
        out.startElement("table", component);
        if (border != Integer.MIN_VALUE)
            out.writeAttribute("border", new Integer(border), "border");
        if (outerTable) {
            String styleClass = (String)component.getAttributes().get("styleClass");
            String style = (String)component.getAttributes().get("style");
            writeIdAttributeIfNecessary(context, out, component);
            if (styleClass != null)
                out.writeAttribute("class", styleClass, "styleClass");
            if (style != null)
                out.writeAttribute("style", style, "style");
        }
        if (!vertical) {
            out.startElement("tr", component);
        }
    }
    
    protected void renderTableEnd(FacesContext context, UIComponent component,
                                  boolean vertical, boolean outerTable)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        if (!vertical)
            out.endElement("tr");
        out.endElement("table");
    }
    
    protected void renderOption(FacesContext context, UIComponent component,
                                SelectItem item, boolean vertical, int itemNum)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String itemId = clientId + NamingContainer.SEPARATOR_CHAR + itemNum;
        String itemValueStr = getFormattedValue(context, component, item.getValue());
        boolean selected = isSelected(context, component, item.getValue(), itemValueStr);
        boolean disabled = item.isDisabled() || isDisabled(component);

        if (vertical)
            out.startElement("tr", component);
        out.startElement("td", component);

        out.startElement("input", component);
        out.writeAttribute("type", getOptionType(), null);
        out.writeAttribute("id", itemId, "clientId");
        out.writeAttribute("name", clientId, "clientId");
        out.writeAttribute("value", itemValueStr, "value");
        out.writeAttribute(getSelectedOption(), selected, "value");
        out.writeAttribute("disabled", disabled, "disabled");
        renderPassThruAttributes(out, component, "style,styleClass,disabled,border");
        out.endElement("input");

        if (item.getLabel() != null) {
            String labelClass = null;
            if (isDisabled(component) || item.isDisabled()) {
                labelClass = (String)component.getAttributes().get("disabledClass");
            } else {
                labelClass = (String)component.getAttributes().get("enabledClass");
            }

            out.startElement("label", component);
            out.writeAttribute("for", itemId, "for");
            if (labelClass != null)
                out.writeAttribute("class", labelClass, "labelClass");
            if (item.getDescription() != null)
                out.writeAttribute("title", item.getDescription(), "title");
            out.writeText(" ", null);
            out.writeText(item.getLabel(), "label");
            out.endElement("label");
        }

        out.endElement("td");
        if (vertical) {
            out.endElement("tr");
        }
    }

    protected String getOptionType() {
        return "checkbox";
    }

    protected String getSelectedOption() {
        return "checked";
    }
}
