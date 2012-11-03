/*
 * $Id: DelegatingDateTimeConverter.java,v 1.2 2008/03/14 02:42:18 lishaochuan Exp $
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

package org.operamasks.faces.binding.factories;

import java.util.TimeZone;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import org.operamasks.faces.util.FacesUtils;

public class DelegatingDateTimeConverter implements Converter
{
    private DateTimeConverter delegate;

    public DelegatingDateTimeConverter() {
        this.delegate = (DateTimeConverter)
            FacesContext.getCurrentInstance().getApplication()
                .createConverter(DateTimeConverter.CONVERTER_ID);
    }

    public void setDateStyle(String value) {
        if (value.length() != 0) {
            delegate.setDateStyle(value);
        }
    }

    public void setTimeStyle(String value) {
        if (value.length() != 0) {
            delegate.setTimeStyle(value);
        }
    }

    public void setPattern(String value) {
        if (value.length() != 0) {
            delegate.setPattern(value);
        }
    }
    
    public String getPattern() {
        return delegate.getPattern();
    }

    public void setType(String value) {
        if (value.length() != 0) {
            delegate.setType(value);
        }
    }

    public void setLocale(String value) {
        if (value.length() != 0) {
            delegate.setLocale(FacesUtils.getLocaleFromString(value));
        }
    }

    public void setTimeZone(String value) {
        if (value.length() != 0) {
            delegate.setTimeZone(TimeZone.getTimeZone(value));
        }
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        checkType();
        return delegate.getAsObject(context, component, value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        checkType();
        return delegate.getAsString(context, component, value);
    }

    private void checkType() {
        if (delegate.getType() == null && delegate.getPattern() == null) {
            if (delegate.getDateStyle() != null && delegate.getTimeStyle() != null) {
                delegate.setType("both");
            } else if (delegate.getDateStyle() != null) {
                delegate.setType("date");
            } else if (delegate.getTimeStyle() != null) {
                delegate.setType("time");
            }
        }
    }
}
