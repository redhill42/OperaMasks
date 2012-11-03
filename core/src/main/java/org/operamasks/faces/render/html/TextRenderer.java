/*
 * $Id: TextRenderer.java,v 1.7 2007/12/11 04:20:12 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.operamasks.faces.component.widget.UITextField;

import java.io.IOException;

public class TextRenderer extends UIInputRenderer
{
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (component instanceof UIInput) {
            super.decode(context, component);
        }
    }
    
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component instanceof UIInput) {
            encodeInputBegin(context, component);
        } else {
            encodeOutputBegin(context, component);
        }
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component instanceof UIInput) {
            encodeInputText(context, component);
        } else {
            encodeOutputText(context, component);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component instanceof UIInput) {
            encodeInputEnd(context, component);
        } else {
            encodeOutputEnd(context, component);
        }
    }

    protected void encodeInputBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String currentValue = getCurrentValue(context, component);

        out.startElement("input", component);
        writeIdAttributeIfNecessary(context, out, component);
        String type = "text";
        String passwordText = "password";
        if(component instanceof UITextField){
        	UITextField textField = (UITextField)component;
        	if(passwordText.equalsIgnoreCase(textField.getInputType())){
        		type = passwordText;
        	}
        }
        out.writeAttribute("type", type, null);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        String autocomplete = (String)component.getAttributes().get("autocomplete");
        if (autocomplete != null && "off".equals(autocomplete))
            out.writeAttribute("autocomplete", "off", "autocomplete");
        out.writeAttribute("value", currentValue, "value");
        renderPassThruAttributes(out, component);
    }

    protected void encodeInputText(FacesContext context, UIComponent component)
        throws IOException
    {
    }

    protected void encodeInputEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        out.endElement("input");
    }

    protected void encodeOutputBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (shouldWriteIdAttribute(context, component) || hasPassThruAttributes(component)) {
            ResponseWriter out = context.getResponseWriter();
            out.startElement("span", component);
            writeIdAttributeIfNecessary(context, out, component);
            renderPassThruAttributes(out, component);
        }
    }

    protected void encodeOutputText(FacesContext context, UIComponent component)
        throws IOException
    {
        Object value = getValue(component);

        if (value == null) {
            super.encodeChildren(context, component);
        } else {
            ResponseWriter out = context.getResponseWriter();
            String text = getFormattedValue(context, component, value);
            if (text != null && text.length() != 0) {
                if (needsEscape(component)) {
                    out.writeText(text, "value");
                } else {
                    out.write(text);
                }
            }
        }
    }

    protected void encodeOutputEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (shouldWriteIdAttribute(context, component) || hasPassThruAttributes(component)) {
            ResponseWriter out = context.getResponseWriter();
            out.endElement("span");
        }
    }
}
