/*
 * $Id: Pattern.java,v 1.7 2008/04/15 03:20:36 patrick Exp $
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
import org.operamasks.faces.binding.factories.PatternConverter;

/**
 * 声明一个转换器，使用模式字符串将一个数字或者日期值与其对应的字符串表达形式进行转换。 
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Converter(PatternConverter.class)
public @interface Pattern
{
    /**
     * 转换失败时的出错信息 
     */
    String message() default "";
    
    /**
     * 用于转换的模式字符串。
     * 若声明了一个数值类型的方法或字段，则模式字符串应符合<code>java.text.DecimalFormat</code>所使用的模式字符串格式。
     * 若声明了一个日期类型的方法或字段，则模式字符串应符合<code>java.text.SimpleDateFormat</code>所使用的模式字符串格式。
     * @see <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/text/DecimalFormat.html">DecimalFormat javadoc</a>
     * @see <a href="http://java.sun.com/docs/books/tutorial/i18n/format/decimalFormat.html">数值模式格式教程</a>
     * @see <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/text/DecimalFormat.html">SimpleDateFormat javadoc</a>
     * @see <a href="http://java.sun.com/docs/books/tutorial/i18n/format/simpleDateFormat.html">日期模式格式教程</a>
     */
    String value();
}
