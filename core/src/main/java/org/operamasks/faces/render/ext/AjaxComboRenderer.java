/*
 * $Id: AjaxComboRenderer.java,v 1.4 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.operamasks.faces.render.AjaxRenderer2;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

public class AjaxComboRenderer extends ComboRenderer implements AjaxRenderer2 {
    public void encodeAjax(FacesContext context, UIComponent component,
            AjaxResponseWriter out) throws IOException {
        String jsvar = FacesUtils.getJsvar(context, component);
        String jsvar_store = jsvar + STORE_VAR_SUFFIX;

        List<SelectItem> items = getSelectItems(context, component);
        byte[] oldChecksum = (byte[])component.getAttributes().get(SELECT_ITEMS_CHECKSUM);
        byte[] newChecksum = computeSelectItemsChecksum(context, component, items);        
        if (oldChecksum == null || !Arrays.equals(oldChecksum, newChecksum)) {            
            StringBuilder buf = new StringBuilder(jsvar_store).append(".removeAll();\n");           
            encodeOptions(buf, context, component, items, jsvar);            
            out.writeScript(buf.toString());
            
            component.getAttributes().put(SELECT_ITEMS_CHECKSUM, newChecksum);
            return;
        }
        
        String selectedValue = getCurrentValue(context, component);
        StringBuilder buf = new StringBuilder();                    
        encodeSelectValue(buf, context, component, selectedValue);        
        out.writeScript(buf.toString());
    }
}
