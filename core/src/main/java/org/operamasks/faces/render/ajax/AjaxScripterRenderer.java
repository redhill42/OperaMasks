/*
 * $Id: AjaxScripterRenderer.java,v 1.6 2008/04/11 06:03:26 jacky Exp $
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

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

import org.operamasks.faces.component.ajax.AjaxScripter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.util.FacesUtils;

public class AjaxScripterRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        AjaxScripter scripter = (AjaxScripter)component;
        String script = scripter.getScript();
        scripter.clearScript();
        
        // clear script in model
        ValueExpression ve = scripter.getValueExpression("script");
        if (ve != null) {
            ve.setValue(context.getELContext(), null);
        }

        if (isAjaxResponse(context)) {
            if (script != null && script.length() != 0) {
                AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
                out.writeActionScript(script);
            }
        } else {
            if (script == null) {
                script = FacesUtils.encodeComponentChildren(context, component).trim();
            }
            if (script != null && script.length() != 0) {
                ResponseWriter out = context.getResponseWriter();
                out.write("\n<script type=\"text/javascript\">");
                out.write(script);
                out.write("</script>\n");
            }
        }
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component) {
        // do nothing
    }
}
