/*
 * $Id: PatternConverter.java,v 1.1 2007/10/11 09:53:56 daniel Exp $
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

import java.util.Date;
import java.util.Calendar;
import javax.faces.convert.Converter;
import javax.faces.convert.NumberConverter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

public class PatternConverter implements Converter
{
    private Converter delegate;

    public PatternConverter(Class<?> type) {
        if ((type.isPrimitive() && type != Boolean.TYPE) || Number.class.isAssignableFrom(type)) {
            this.delegate = FacesContext.getCurrentInstance().getApplication()
                .createConverter(NumberConverter.CONVERTER_ID);
        } else if (Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)) {
            this.delegate = FacesContext.getCurrentInstance().getApplication()
                .createConverter(DateTimeConverter.CONVERTER_ID);
        } else {
            throw new IllegalArgumentException("Invalid type for pattern converter.");
        }
    }

    public void setValue(String pattern) {
        if (delegate instanceof NumberConverter) {
            ((NumberConverter)delegate).setPattern(pattern);
        } else if (delegate instanceof DateTimeConverter) {
            ((DateTimeConverter)delegate).setPattern(pattern);
        }
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return delegate.getAsObject(context, component, value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return delegate.getAsString(context, component, value);
    }
}
