/*
 * $Id: AjaxButtonRenderer.java,v 1.16 2008/04/09 11:22:40 patrick Exp $
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
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxButtonRenderer extends ButtonRenderer
{
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        super.encodeBegin(context, component);

        if (component.isRendered() && isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            String jsvar = FacesUtils.getJsvar(context, component);

            if (FacesUtils.isDynamicValue(component, "value") || component.getAttributes().get("actionBinding") != null) {
                String text = HtmlEncoder.enquote(getText(context, component), '\'');
                out.writeScript(jsvar + ".setText(" + text + ");");
            }

            if (FacesUtils.isDynamicValue(component, "disabled") || component.getAttributes().get("actionBinding") != null) {
                Boolean disabled = (Boolean)component.getAttributes().get("disabled");
                if (disabled != null) {
                    if (disabled) {
                        out.writeScript(jsvar + ".disable();");
                    } else {
                        out.writeScript(jsvar + ".enable();");
                    }
                }
            }
        }
    }

    @Override
    protected void encodeOnclick(StringBuilder buf, UIComponent component, String jsvar) {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = component.getClientId(context);
        UIForm form = getParentForm(component);
        

        String onclick = (String)component.getAttributes().get("onclick");
        if (onclick != null) {
            onclick = onclick.trim();
            if (onclick.length() == 0) {
                onclick = null;
            } else if (!onclick.endsWith(";")) {
                onclick += ";";
            }
        }

        Formatter fmt = new Formatter(buf);

        Object alwaysSubmit = component.getAttributes().get("alwaysSubmit");
        if (form != null && (alwaysSubmit == null || (alwaysSubmit != null && (Boolean.parseBoolean(alwaysSubmit.toString()))))) {
            fmt.format("%s.on('click', function(){", jsvar);
            if (onclick != null) {
                fmt.format("if (function(){%s}.apply(this)==false)return;", onclick);
            }
            buf.append(encodeAjaxSubmit(context, component,
                                        HtmlEncoder.enquote(clientId),
                                        HtmlEncoder.enquote("")));
            buf.append("});\n");
        } else if (onclick != null) {
            fmt.format("%s.on('click',function(){%s});\n", jsvar, onclick);
        }
        
    }
}
