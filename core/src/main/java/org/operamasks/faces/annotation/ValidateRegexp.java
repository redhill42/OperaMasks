/*
 * $Id: ValidateRegexp.java,v 1.2 2008/04/11 06:54:24 lishaochuan Exp $
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

import org.operamasks.faces.validator.RegexpValidator;

/**
 * 声明一个校验器，该校验器使用正则表达式匹配输入值，当输入值与模式字符串匹配时校验
 * 成功，否则校验失败。
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Validator(id= RegexpValidator.VALIDATOR_ID)
public @interface ValidateRegexp
{
    /**
     * 用于正则表达式匹配的模式字符串。正则表达式语法请参阅
     * <a href="http://gceclub.sun.com.cn/Java_Docs/html/zh_CN/api/java/util/regex/Pattern.html">JDK文档</a>。
     */
    String value();

    /**
     * 模式匹配失败时的出错信息。
     */
    String message() default "";

}
