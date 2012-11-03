/*
 * $Id: ConvertNumberTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.faces.convert.Converter;
import javax.faces.convert.NumberConverter;
import javax.faces.context.FacesContext;
import javax.faces.webapp.ConverterELTag;
import javax.servlet.jsp.JspException;
import java.util.Locale;
import org.operamasks.faces.util.FacesUtils;

public class ConvertNumberTag extends ConverterELTag
{
    private ValueExpression currencyCode;
    private ValueExpression currencySymbol;
    private ValueExpression groupingUsed;
    private ValueExpression integerOnly;
    private ValueExpression maxFractionDigits;
    private ValueExpression minFractionDigits;
    private ValueExpression minIntegerDigits;
    private ValueExpression locale;
    private ValueExpression pattern;
    private ValueExpression type;
    private ValueExpression binding;

    public void setCurrencyCode(ValueExpression currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencySymbol(ValueExpression currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void setGroupingUsed(ValueExpression groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    public void setIntegerOnly(ValueExpression integerOnly) {
        this.integerOnly = integerOnly;
    }

    public void setMaxFractionDigits(ValueExpression maxFractionDigits) {
        this.maxFractionDigits = maxFractionDigits;
    }

    public void setMinFractionDigits(ValueExpression minFractionDigits) {
        this.minFractionDigits = minFractionDigits;
    }

    public void setMinIntegerDigits(ValueExpression minIntegerDigits) {
        this.minIntegerDigits = minIntegerDigits;
    }

    public void setLocale(ValueExpression locale) {
        this.locale = locale;
    }

    public void setPattern(ValueExpression pattern) {
        this.pattern = pattern;
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

        if (binding != null) {
            Converter boundConverter = (NumberConverter)binding.getValue(elctx);
            if (boundConverter != null) return boundConverter;
        }

        NumberConverter converter = new NumberConverter();

        if (currencyCode != null)
            converter.setCurrencyCode((String)currencyCode.getValue(elctx));
        if (currencySymbol != null)
            converter.setCurrencySymbol((String)currencySymbol.getValue(elctx));
        if (groupingUsed != null)
            converter.setGroupingUsed((Boolean)groupingUsed.getValue(elctx));
        if (integerOnly != null)
            converter.setIntegerOnly((Boolean)integerOnly.getValue(elctx));
        if (maxFractionDigits != null)
            converter.setMaxFractionDigits((Integer)maxFractionDigits.getValue(elctx));
        if (minFractionDigits != null)
            converter.setMinFractionDigits((Integer)minFractionDigits.getValue(elctx));
        if (minIntegerDigits != null)
            converter.setMinIntegerDigits((Integer)minIntegerDigits.getValue(elctx));
        if (pattern != null)
            converter.setPattern((String)pattern.getValue(elctx));
        if (type != null)
            converter.setType((String)type.getValue(elctx));

        Locale loc = null;
        if (locale != null)
            loc = FacesUtils.getLocaleFromExpression(context, locale);
        if (loc == null)
            loc = context.getViewRoot().getLocale();
        converter.setLocale(loc);
        
        if (binding != null)
            binding.setValue(elctx, converter);
        return converter;
    }
}
