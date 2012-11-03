/*
 * $Id: ConvertDateTimeTag.java,v 1.6 2007/12/11 04:20:13 jacky Exp $
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

package org.operamasks.faces.webapp.core;

import static org.operamasks.resources.Resources.JSF_TIMEZONE_TYPE_EXPECTED;
import static org.operamasks.resources.Resources._T;

import java.util.Locale;
import java.util.TimeZone;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.webapp.ConverterELTag;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.util.FacesUtils;

public class ConvertDateTimeTag extends ConverterELTag
{
    private ValueExpression dateStyle;
    private ValueExpression locale;
    private ValueExpression pattern;
    private ValueExpression timeStyle;
    private ValueExpression timeZone;
    private ValueExpression type;
    private ValueExpression binding;

    public void release() {
        super.release();
        dateStyle = null;
        locale = null;
        pattern = null;
        timeStyle = null;
        timeZone = null;
        type = null;
        binding = null;
    }

    public void setDateStyle(ValueExpression dateStyle) {
        this.dateStyle = dateStyle;
    }

    public void setLocale(ValueExpression locale) {
        this.locale = locale;
    }

    public void setPattern(ValueExpression pattern) {
        this.pattern = pattern;
    }

    public void setTimeStyle(ValueExpression timeStyle) {
        this.timeStyle = timeStyle;
    }

    public void setTimeZone(ValueExpression timeZone) {
        this.timeZone = timeZone;
    }

    public void setType(ValueExpression type) {
        this.type = type;
    }

    public void setBinding(ValueExpression binding) {
        this.binding = binding;
    }

    protected Converter createConverter()
        throws JspException
    {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elctx = context.getELContext();
        DateTimeConverter converter;

        if (binding != null) {
            converter = (DateTimeConverter)binding.getValue(elctx);
            if (converter != null) return converter;
        }

        converter = (DateTimeConverter) context.getApplication().createConverter(DateTimeConverter.CONVERTER_ID);

        if (pattern != null)
            converter.setPattern((String)pattern.getValue(elctx));
        if (dateStyle != null)
            converter.setDateStyle((String)dateStyle.getValue(elctx));
        if (timeStyle != null)
            converter.setTimeStyle((String)timeStyle.getValue(elctx));

        if (type != null) {
            converter.setType((String)type.getValue(elctx));
        } else if (dateStyle != null && timeStyle != null) {
            converter.setType("both");
        } else if (dateStyle != null) {
            converter.setType("date");
        } else if (timeStyle != null) {
            converter.setType("time");
        }

        Locale loc = null;
        if (locale != null)
            loc = FacesUtils.getLocaleFromExpression(context, locale);
        if (loc == null)
            loc = context.getViewRoot().getLocale();
        converter.setLocale(loc);

        if (timeZone != null) {
            TimeZone tz;
            Object tzval = timeZone.getValue(elctx);
            if (tzval instanceof TimeZone) {
                tz = (TimeZone)tzval;
            } else if (tzval instanceof String) {
                tz = TimeZone.getTimeZone((String)tzval);
            } else {
                throw new JspException(_T(JSF_TIMEZONE_TYPE_EXPECTED, timeZone.getExpressionString()));
            }
            converter.setTimeZone(tz);
        } else {
        	TimeZone initTimeZone = FacesUtils.getInitTimeZone();
        	
        	if (initTimeZone != null)
        		converter.setTimeZone(initTimeZone);
        }

        if (binding != null) {
            binding.setValue(elctx, converter);
        }
        return converter;
    }
}
