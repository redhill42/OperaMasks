/*
 * $Id: AjaxRenderGroupRenderer.java,v 1.5 2007/07/02 07:37:53 jacky Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.security.MessageDigest;
import org.operamasks.faces.render.html.GroupRenderer;
import org.operamasks.faces.util.FacesUtils;

public class AjaxRenderGroupRenderer extends GroupRenderer
{
    private static final String CONTENT_CHECKSUM = "org.operamasks.faces.CONTENT_CHECKSUM";

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext context, UIComponent component) {
        // already rendered
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxResponse(context)) {
            renderAjaxResponse(context, component);
        } else {
            super.encodeBegin(context, component);
            super.encodeChildren(context, component);
            super.encodeEnd(context, component);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) {
        // already rendered
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();

        // create a temporary ResponseWriter to get inner HTML
        StringWriter buf = new StringWriter();
        AjaxHtmlResponseWriter inner = out.cloneWithHtmlWriter(buf);

        // encode inner HTML
        context.setResponseWriter(inner);
        super.encodeChildren(context, component);
        context.setResponseWriter(out);

        String content = buf.toString();

        // detect changes on group content
        byte[] oldChecksum = (byte[])component.getAttributes().get(CONTENT_CHECKSUM);
        byte[] newChecksum = computeChecksum(content);
        if (oldChecksum == null || !Arrays.equals(oldChecksum, newChecksum)) {
            // output javascript to set inner HTML
            out.writeInnerHtmlScript(component.getClientId(context), content);
            component.getAttributes().put(CONTENT_CHECKSUM, newChecksum);
        }
    }

    private byte[] computeChecksum(String content) {
        try {
            MessageDigest md5 = FacesUtils.getMD5();
            byte[] result = md5.digest(content.getBytes("UTF-8"));
            FacesUtils.returnMD5(md5);
            return result;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    @Override
    protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }
}
