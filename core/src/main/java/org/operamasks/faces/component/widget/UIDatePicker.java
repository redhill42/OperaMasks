/*
 * $Id: UIDatePicker.java,v 1.4 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.component.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.convert.ConverterException;

import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.util.FacesUtils;

public class UIDatePicker extends UIInput
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.DatePicker";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.DatePicker";

    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.DatePicker";
    public static final String MENU_RENDERER_TYPE = "org.operamasks.faces.widget.Menu";

    // Format used to synchronize date value between client and server
    public static final String CLIENT_FORMAT = "m/d/Y";
    public static final DateFormat SERVER_FORMAT;
    static {
        SERVER_FORMAT = new SimpleDateFormat("M/d/yyyy");
        TimeZone initTimeZone = FacesUtils.getInitTimeZone();
        
        if (initTimeZone != null)
        	SERVER_FORMAT.setTimeZone(initTimeZone);
    }

    public UIDatePicker() {
        setRendererType(null);
    }
    
    public UIDatePicker(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    public String getRendererType() {
        String rendererType = super.getRendererType();
        if (rendererType != null) {
            return rendererType;
        }
        if (getParent() instanceof UIMenu) {
            return MENU_RENDERER_TYPE;
        }
        return DEFAULT_RENDERER_TYPE;
    }

    /**
     * Get the formatted value to be used by client.
     */
    public static String getFormattedValue(Object value) {
        return SERVER_FORMAT.format(value);
    }

    /**
     * Get the converted value submitted from client.
     */
    public static Object getConvertedValue(String value)
        throws ConverterException
    {
        try {
            return SERVER_FORMAT.parse(value);
        } catch (ParseException ex) {
            throw new ConverterException(ex.getMessage(), ex);
        }
    }
}
