/*
 * $Id: ConvertDateTime.java,v 1.7 2008/03/14 00:57:49 patrick Exp $
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
package org.operamasks.faces.annotation;

import java.lang.annotation.*;

import javax.faces.convert.ConverterException;

import org.operamasks.faces.binding.factories.DelegatingDateTimeConverter;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Converter(DelegatingDateTimeConverter.class)
public @interface ConvertDateTime
{
    /**
     * The message to describe a convertion failure. 
     */
    String message() default "";
    
    /**
     * The type of value to be formatted or parsed.
     * Valid values are <code>both</code>, <code>date</code>, or
     * <code>time</code>.
     */
    String type() default "";
    
    /**
     * The style to be used to format or parse dates.  Valid values
     * are <code>default</code>, <code>short</code>, <code>medium</code>,
     * <code>long</code>, and <code>full</code>.
     */
    String dateStyle() default "default";
    
    /**
     * <p>Set the style to be used to format or parse times.  Valid values
     * are <code>default</code>, <code>short</code>, <code>medium</code>,
     * <code>long</code>, and <code>full</code>.
     */
    String timeStyle() default "default";

    /**
     * The format pattern to be used when formatting and parsing
     * dates and times.  Valid values are those supported by
     * <code>java.text.SimpleDateFormat</code>.
     */
    String pattern() default "";
    
    /**
     * The <code>Locale</code> to be used when parsing or formatting
     * dates and times.  If set to <code>null</code>, the <code>Locale</code>
     * stored in the {@link javax.faces.component.UIViewRoot} for the current
     * request will be utilized.
     */
    String locale() default "";
    
    /**
     * The <code>TimeZone</code> used to interpret a time value.
     */
    String timeZone() default "";
}
