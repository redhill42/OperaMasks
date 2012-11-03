/*
 * $Id: FormatRenderer.java,v 1.5 2007/07/02 07:37:48 jacky Exp $
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
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

public class FormatRenderer extends UIOutputRenderer
{
    protected void renderCurrentValue(FacesContext context, UIComponent component, String currentValue)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String message = formatMessage(component, currentValue);
        boolean wroteSpan = false;

        if (shouldWriteIdAttribute(context, component) || hasPassThruAttributes(component)) {
            out.startElement("span", component);
            writeIdAttributeIfNecessary(context, out, component);
            renderPassThruAttributes(out, component);
            wroteSpan = true;
        }
        if (needsEscape(component)) {
            out.writeText(message, "value");
        } else {
            out.write(message);
        }
        if (wroteSpan) {
            out.endElement("span");
        }
    }

    protected String formatMessage(UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return "";
        } else {
            Object[] params = getParameters(component);
            if (params != null) {
                return MessageFormat.format(value, params);
            } else {
                return value;
            }
        }
    }

    protected Object[] getParameters(UIComponent component) {
        ArrayList<Object> paramList = null;
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UIParameter) {
                if (paramList == null)
                    paramList = new ArrayList<Object>();
                paramList.add(((UIParameter)kid).getValue());
            }
        }
        if (paramList == null)
            return null;
        return paramList.toArray();
    }
}
