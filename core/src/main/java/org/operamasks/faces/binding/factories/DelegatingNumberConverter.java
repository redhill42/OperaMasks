/*
 * $Id: DelegatingNumberConverter.java,v 1.1 2007/10/11 09:53:56 daniel Exp $
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

import javax.faces.convert.Converter;
import javax.faces.convert.NumberConverter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import org.operamasks.faces.util.FacesUtils;

public class DelegatingNumberConverter implements Converter
{
    private NumberConverter delegate;

    public DelegatingNumberConverter() {
        this.delegate = (NumberConverter)
            FacesContext.getCurrentInstance().getApplication()
                .createConverter(NumberConverter.CONVERTER_ID);
    }

    public void setCurrencyCode(String value) {
        if (value != null && value.length() != 0) {
            delegate.setCurrencyCode(value);
        }
    }

    public void setCurrencySymbol(String value) {
        if (value != null && value.length() != 0) {
            delegate.setCurrencySymbol(value);
        }
    }

    public void setGroupingUsed(boolean value) {
        delegate.setGroupingUsed(value);
    }

    public void setIntegerOnly(boolean value) {
        delegate.setIntegerOnly(value);
    }

    public void setMaxFractionDigits(int value) {
        if (value != Integer.MIN_VALUE) {
            delegate.setMaxFractionDigits(value);
        }
    }

    public void setMinFractionDigits(int value) {
        if (value != Integer.MIN_VALUE) {
            delegate.setMinFractionDigits(value);
        }
    }

    public void setMinIntegerDigits(int value) {
        if (value != Integer.MIN_VALUE) {
            delegate.setMinIntegerDigits(value);
        }
    }

    public void setPattern(String pattern) {
        if (pattern.length() != 0) {
            delegate.setPattern(pattern);
        }
    }

    public void setType(String type) {
        if (type.length() != 0) {
            delegate.setType(type);
        }
    }

    public void setLocale(String locale) {
        if (locale.length() != 0) {
            delegate.setLocale(FacesUtils.getLocaleFromString(locale));
        }
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return delegate.getAsObject(context, component, value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return delegate.getAsString(context, component, value);
    }
}
