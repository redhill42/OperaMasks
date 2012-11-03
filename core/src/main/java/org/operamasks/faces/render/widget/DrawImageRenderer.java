/*
 * $Id: DrawImageRenderer.java,v 1.7 2008/04/29 05:21:13 lishaochuan Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import org.operamasks.faces.render.html.ImageRenderer;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.component.widget.UIDrawImage;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.util.Base64;

public class DrawImageRenderer extends ImageRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        
        if(Boolean.FALSE.equals(((UIDrawImage)component).isNeedRefresh())){
            return;
        }

        Object value = component.getAttributes().get("value");
        if ((value instanceof String) && !((String)value).startsWith("resource:")) {
            super.encodeEnd(context, component);
            return;
        }

        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter();

        DrawImageHelper info = new DrawImageHelper((UIDrawImage)component);
        boolean inlined = isInline(context, component);

        String url;
        if (inlined) {
            url = encodeInlineImage(context, info);
        } else {
            url = getURL(context, component);
            info.save(context); // save draw info to session
        }

        if (!isAjaxResponse(context)) {
            out.startElement("img", component);
            writeIdAttributeIfNecessary(context, out, component);
            out.writeAttribute("src", url, null);
            renderPassThruAttributes(out, component);
            out.endElement("img");
        } else {
            // refresh image
            String script;
            if (inlined) {
                script = String.format("OM.F('%s','src','%s');\n", clientId, url);
            } else {
                script = String.format("OM.F('%s','src','%s&_rnd='+new Date().getTime());\n",
                                       clientId, url);
            }
            ((AjaxResponseWriter)out).writeScript(script);
        }
    }

    private String getURL(FacesContext context, UIComponent component) {
        StringBuilder buf = new StringBuilder();

        String clientId = component.getClientId(context);
        String url = getActionURL(context);

        buf.append(url);
        buf.append((url.indexOf('?') == -1) ? '?' : '&');
        buf.append(DrawImageHelper.DRAW_IMAGE_PARAM).append('=').append(clientId);
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                UIParameter param = (UIParameter)kid;
                buf.append('&');
                buf.append(param.getName());
                buf.append('=');
                buf.append(param.getValue());
            }
        }

        String encoding = context.getResponseWriter().getCharacterEncoding();
        return HtmlEncoder.encodeURI(buf.toString(), encoding);
    }

    private boolean isInline(FacesContext context, UIComponent component) {
        if (!((UIDrawImage)component).isInline()) {
            return false;
        }

        // Internet Explorer doesn't support inlined image
        String ua = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        if (ua != null) {
            ua = ua.toLowerCase();
            if (ua.indexOf("msie") != -1 && ua.indexOf("opera") == -1) {
                return false;
            }
        }

        return true;
    }

    private String encodeInlineImage(FacesContext context, DrawImageHelper info)
        throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        info.encode(context, stream);

        String data = Base64.encode(stream.toByteArray());
        return "data:" + info.getType() + ";base64," + data;
    }
}
