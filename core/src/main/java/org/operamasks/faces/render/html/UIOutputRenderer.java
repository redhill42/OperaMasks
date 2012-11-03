/*
 * $Id: UIOutputRenderer.java,v 1.5 2007/07/02 07:37:48 jacky Exp $
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
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.Converter;

import org.operamasks.faces.util.FacesUtils;

import java.io.IOException;

public abstract class UIOutputRenderer extends HtmlRenderer
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        String currentValue = getCurrentValue(context, component);
        renderCurrentValue(context, component, currentValue);
    }

    protected String getCurrentValue(FacesContext context, UIComponent component) {
        Object currentValue = getValue(component);
        if (currentValue != null)
            return getFormattedValue(context, component, currentValue);
        return null;
    }

    protected Object getValue(UIComponent component) {
        if (component instanceof ValueHolder)
            return ((ValueHolder)component).getValue();
        return null;
    }

    protected String getFormattedValue(FacesContext context, UIComponent component, Object currentValue)
        throws ConverterException
    {
        return FacesUtils.getFormattedValue(context, component, currentValue);
    }

    protected void renderCurrentValue(FacesContext context, UIComponent component, String currentValue)
        throws IOException
    {
        // implemented by subclass
    }
}
