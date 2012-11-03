/*
 * $Id: AjaxCheckboxListRenderer.java,v 1.5 2007/07/02 07:37:51 jacky Exp $
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

package org.operamasks.faces.render.ajax;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.operamasks.faces.render.html.CheckboxListRenderer;

public class AjaxCheckboxListRenderer extends CheckboxListRenderer
{
    private static final String SELECT_ITEM_COUNT = "org.operamasks.faces.SELECT_ITEM_COUNT";

    @Override
    protected void renderCheckboxList(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component, items);
        } else if (isAjaxHtmlResponse(context)) {
            renderAjaxHtmlResponse(context, component, items);
        } else {
            super.renderCheckboxList(context, component, items);
        }
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        // render outer div
        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", null);
        out.writeAttribute("id", getOuterClientId(context, component), null);
        out.writeText("", null);

        // render inner HTML
        super.renderCheckboxList(context, component, items);

        // render outer div end
        out.endElement("div");

        // save number of items rendered
        int itemCount = getSelectItemCount(context, component);
        component.getAttributes().put(SELECT_ITEM_COUNT, itemCount);
    }

    private void renderInnerHtmlResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        String clientId = getOuterClientId(context, component);

        // create a temporary ResponseWriter to get inner HTML
        StringWriter buf = new StringWriter();
        ResponseWriter inner = out.cloneWithHtmlWriter(buf);

        // encode inner HTML
        context.setResponseWriter(inner);
        super.renderCheckboxList(context, component, items);
        context.setResponseWriter(out);

        // output javascript to set inner HTML
        out.writeInnerHtmlScript(clientId, buf.toString());
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        // if select item count changed, rerender whole control
        Integer oldItemCount = (Integer)component.getAttributes().get(SELECT_ITEM_COUNT);
        int newItemCount = getSelectItemCount(context, component);
        if (oldItemCount == null || oldItemCount != newItemCount) {
            renderInnerHtmlResponse(context, component, items);
            component.getAttributes().put(SELECT_ITEM_COUNT, newItemCount);
            return;
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        String itemIdPrefix = component.getClientId(context) + NamingContainer.SEPARATOR_CHAR;
        boolean componentDisabled = isDisabled(component);
        String selectedOption = getSelectedOption();

        int idx = 0;
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                for (SelectItem groupItem : ((SelectItemGroup)item).getSelectItems()) {
                    String itemId = itemIdPrefix + (idx++);
                    boolean selected = isSelected(context, component, groupItem);
                    boolean disabled = componentDisabled || groupItem.isDisabled();
                    out.writeAttributeScript(itemId, selectedOption, selected);
                    out.writeAttributeScript(itemId, "disabled", disabled);
                }
            } else {
                String itemId = itemIdPrefix + (idx++);
                boolean selected = isSelected(context, component, item);
                boolean disabled = componentDisabled || item.isDisabled();
                out.writeAttributeScript(itemId, selectedOption, selected);
                out.writeAttributeScript(itemId, "disabled", disabled);
            }
        }
    }

    private String getOuterClientId(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        return clientId + NamingContainer.SEPARATOR_CHAR + "_outer";
    }
}
