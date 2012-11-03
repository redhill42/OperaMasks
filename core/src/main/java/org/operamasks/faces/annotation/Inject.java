/*
 * $Id: Inject.java,v 1.4 2007/10/12 06:21:46 daniel Exp $
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

/**
 * <p>注入依赖对象。</p>
 *
 * <p>此注释必须应用到一个类成员变量或对象属性的get或set方法, 但以上三个地方不能同时使用.</p>
 *
 * @see Outject
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject
{
    /**
     * <p>指定一个EL表达式，当注入时对表达式求值，并将结果注入依赖对象。
     * 如果不指定EL表达式，则根据数据类型自动判断</p>
     */
    String value() default "";

    /**
     * 指定依赖对象的注入顺序。
     */
    int order() default 0;

    /**
     * 指定是否每次对象被访问时都重新注入新的依赖值。 
     */
    boolean renew() default false;
}
