/*
 * $Id: ConvertNumber.java,v 1.6 2007/10/26 08:40:16 daniel Exp $
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
import org.operamasks.faces.binding.factories.DelegatingNumberConverter;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Converter(DelegatingNumberConverter.class)
public @interface ConvertNumber
{
    String message() default "";
    String currencyCode() default "";
    String currencySymbol() default "";
    boolean groupingUsed() default true;
    boolean integerOnly() default false;
    int maxFractionDigits() default Integer.MIN_VALUE;
    int minFractionDigits() default Integer.MIN_VALUE;
    int minIntegerDigits() default Integer.MIN_VALUE;
    String locale() default "";
    String pattern() default "";
    String type() default "";
}
