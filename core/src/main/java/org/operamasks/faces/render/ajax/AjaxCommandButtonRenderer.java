/*
 * $Id: AjaxCommandButtonRenderer.java,v 1.8 2007/07/02 07:37:51 jacky Exp $
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
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.html.CommandButtonRenderer;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxCommandButtonRenderer extends CommandButtonRenderer
{
    @Override
    public String getOnclickScript(FacesContext context, UIComponent component) {
        String type = (String)component.getAttributes().get("type");
        if (type != null && type.equalsIgnoreCase("reset"))
            return super.getOnclickScript(context, component);

        String onnclick = (String)component.getAttributes().get("onclick");
        if (onnclick != null) {
            onnclick = onnclick.trim();
            if (onnclick.length() == 0) {
                onnclick = null;
            }
        }

        String param = HtmlEncoder.enquote(component.getClientId(context));
        String submit = encodeAjaxSubmit(context, component, param, "''");

        if (onnclick == null) {
            return "return " + submit;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append("var a=function(){");
            buf.append(onnclick);
            buf.append("};var b=function(){");
            buf.append("return ").append(submit);
            buf.append("};return (a.apply(this)==false)?false:b.apply(this);");
            return buf.toString();
        }
    }
}
