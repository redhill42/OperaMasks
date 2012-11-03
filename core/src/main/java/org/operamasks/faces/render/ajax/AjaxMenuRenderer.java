/*
 * $Id: AjaxMenuRenderer.java,v 1.6 2007/07/02 07:37:50 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.FacesException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Arrays;
import java.security.MessageDigest;
import org.operamasks.faces.render.html.MenuRenderer;
import org.operamasks.faces.util.FacesUtils;

public class AjaxMenuRenderer extends MenuRenderer
{
    private static final String SELECT_ITEMS_CHECKSUM = "org.operamasks.faces.SELECT_ITEMS_CHECKSUM";

    @Override
    protected void renderMenu(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component, items);
        } else if (isAjaxHtmlResponse(context)) {
            renderAjaxHtmlResponse(context, component, items);
        } else {
            super.renderMenu(context, component, items);
        }
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        // render outer span
        ResponseWriter out = context.getResponseWriter();
        out.startElement("span", null);
        out.writeAttribute("id", getOuterClientId(context, component), null);
        out.write("");

        // render inner HTML
        super.renderMenu(context, component, items);

        // render outer span end
        out.endElement("span");

        // save checksum of select items to detect changes
        byte[] checksum = computeSelectItemsChecksum(context, component, items);
        component.getAttributes().put(SELECT_ITEMS_CHECKSUM, checksum);
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
        super.renderMenu(context, component, items);
        context.setResponseWriter(out);

        // output javascript to set inner HTML
        out.writeInnerHtmlScript(clientId, buf.toString());
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {
        // if select items changed, rerender whole control
        byte[] oldChecksum = (byte[])component.getAttributes().get(SELECT_ITEMS_CHECKSUM);
        byte[] newChecksum = computeSelectItemsChecksum(context, component, items);
        if (oldChecksum == null || !Arrays.equals(oldChecksum, newChecksum)) {
            renderInnerHtmlResponse(context, component, items);
            component.getAttributes().put(SELECT_ITEMS_CHECKSUM, newChecksum);
            return;
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        String clientId = component.getClientId(context);

        if (component instanceof UISelectOne) {
            String value = getCurrentValue(context, component);
            out.writeAttributeScript(clientId, "value", value);
        } else {
            StringBuilder buf = new StringBuilder();
            int idx = 0;

            // OM.SEL('outerClientId',[1,3,5...]);)
            buf.append("OM.SEL('");
            buf.append(getOuterClientId(context, component));
            buf.append("',[");
            for (SelectItem item : items) {
                if (item instanceof SelectItemGroup) {
                    for (SelectItem groupItem : ((SelectItemGroup)item).getSelectItems()) {
                        if (isSelected(context, component, groupItem))
                            buf.append(idx).append(',');
                        idx++;
                    }
                } else {
                    if (isSelected(context, component, item))
                        buf.append(idx).append(',');
                    idx++;
                }
            }
            if (buf.charAt(buf.length()-1) == ',') {
                buf.setLength(buf.length()-1);
            }
            buf.append("]);\n");

            out.writeScript(buf.toString());
        }
    }

    private String getOuterClientId(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        return clientId + NamingContainer.SEPARATOR_CHAR + "_outer";
    }

    private byte[] computeSelectItemsChecksum(FacesContext context,
                                              UIComponent component,
                                              List<SelectItem> items)
    {
        try {
            String itemsString = getSelectItemsString(context, component, items);
            MessageDigest md5 = FacesUtils.getMD5();
            byte[] result = md5.digest(itemsString.getBytes("UTF-8"));
            FacesUtils.returnMD5(md5);
            return result;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private String getSelectItemsString(FacesContext context,
                                        UIComponent component,
                                        List<SelectItem> items)
    {
        StringBuilder buf = new StringBuilder();
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                buf.append("OPTGROUP\n");
                for (SelectItem groupItem : ((SelectItemGroup)item).getSelectItems()) {
                    addItemString(buf, context, component, groupItem);
                }
            } else {
                addItemString(buf, context, component, item);
            }
        }
        return buf.toString();
    }

    private void addItemString(StringBuilder buf, FacesContext context, UIComponent component, SelectItem item) {
        buf.append("OPTION\n");
        buf.append(item.getLabel());
        buf.append('\n');
        buf.append(getFormattedValue(context, component, item.getValue()));
        buf.append('\n');
        if (item.isDisabled())
            buf.append("disabled\n");
    }
}
