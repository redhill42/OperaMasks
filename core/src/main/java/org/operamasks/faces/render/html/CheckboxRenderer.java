/*
 * $Id: CheckboxRenderer.java,v 1.4 2007/07/02 07:37:47 jacky Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import java.io.IOException;

public class CheckboxRenderer extends UIInputRenderer
{
    @Override
    public void setSubmittedValue(UIComponent component, Object submittedValue) {
        String newValue = (String)submittedValue;
        if (newValue == null) {
            newValue = "false";
        } else if (newValue.equalsIgnoreCase("on") ||
                   newValue.equalsIgnoreCase("yes") ||
                   newValue.equalsIgnoreCase("true")) {
            newValue = "true";
        } else {
            newValue = "false";
        }
        super.setSubmittedValue(component, newValue);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        return Boolean.valueOf((String)submittedValue);
    }

    @Override
    protected void renderCurrentValue(FacesContext context, UIComponent component, String currentValue)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();

        out.startElement("input", component);
        out.writeAttribute("type", "checkbox", null);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        out.writeAttribute("checked", Boolean.valueOf(currentValue), "value");
        renderPassThruAttributes(out, component);
        out.endElement("input");
    }
}
