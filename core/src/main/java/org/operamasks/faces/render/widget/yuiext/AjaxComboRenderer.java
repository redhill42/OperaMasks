/*
 * $Id: AjaxComboRenderer.java,v 1.9 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;

public class AjaxComboRenderer extends ComboRenderer implements ResourceProvider {
    private static final String SELECT_ITEMS_CHECKSUM = "org.operamasks.faces.SELECT_ITEMS_CHECKSUM";
        
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;    
        
        super.encodeEnd(context, component);
        
        List<SelectItem> items = getSelectItems(context, component);
        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component, items);
        } else if (isAjaxHtmlResponse(context)) {
            renderAjaxHtmlResponse(context, component, items);
        }
    }
    
    protected void renderAjaxResponse(FacesContext context, UIComponent component, List<SelectItem> items) throws IOException {
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        
        byte[] oldChecksum = (byte[])component.getAttributes().get(SELECT_ITEMS_CHECKSUM);
        byte[] newChecksum = computeSelectItemsChecksum(context, component, items);        
        if (oldChecksum == null || !Arrays.equals(oldChecksum, newChecksum)) {            
            StringBuilder buf = new StringBuilder(jsvar_store).append(".removeAll();\n");           
            encodeOptions(buf, context, component, items);            
            out.writeScript(buf.toString());
            
            component.getAttributes().put(SELECT_ITEMS_CHECKSUM, newChecksum);
            return;
        }
        
        String selectedValue = getCurrentValue(context, component);
        StringBuilder buf = new StringBuilder();                    
        encodeSelectValue(buf, context, component, selectedValue);        
        out.writeScript(buf.toString());
    }
    protected void renderAjaxHtmlResponse(FacesContext context, UIComponent component, List<SelectItem> items)
        throws IOException
    {    
        // save checksum of select items to detect changes
        byte[] checksum = computeSelectItemsChecksum(context, component, items);
        component.getAttributes().put(SELECT_ITEMS_CHECKSUM, checksum);
    }    
    
    private static byte[] computeSelectItemsChecksum(FacesContext context, UIComponent component, List<SelectItem> items) {
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

    private static String getSelectItemsString(FacesContext context, UIComponent component, List<SelectItem> items) {
        StringBuilder buf = new StringBuilder();
        for (SelectItem item : items) {
            if (item instanceof SelectItemGroup) {
                buf.append("OPTGROUP\n");
                for (SelectItem groupItem : ((SelectItemGroup) item).getSelectItems()) {
                    addItemString(buf, context, component, groupItem);
                }
            } else {
                addItemString(buf, context, component, item);
            }
        }
        return buf.toString();
    }

    private static void addItemString(StringBuilder buf, FacesContext context, UIComponent component, SelectItem item) {
        buf.append("OPTION\n");
        buf.append(item.getLabel());
        buf.append('\n');
        buf.append(FacesUtils.getFormattedValue(context, component, item.getValue()));
        buf.append('\n');
        if (item.isDisabled())
            buf.append("disabled\n");
    }
}
