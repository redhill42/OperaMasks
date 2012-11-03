/*
 * $Id: Outject.java,v 1.3 2007/10/12 06:21:46 daniel Exp $
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
 * <p>注出依赖对象.</p>
 *
 * <p>此注释必须应用到一个类成员变量或对象属性的get或set方法, 但以上三个地方不能同时使用.</p>
 *
 * @see Inject
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Outject
{
    /**
     * <p>此属性具有以下两种形式.</p>
     * <ul>
     *   <li>与上下文相关的对象名称, 在注出时对象值将被赋予到由scope指定的上下文中.</li>
     *   <li>EL表达式, 在注出时将对象值赋予到由该表达式所指定的目标中.</li>
     * </ul>
     *
     * <p>当Outject和{@link Inject}联用时可以省略此属性, 此时将使用Inject中指定的value. 如果未和
     * Inject联用也未指定value, 则使用被注释属性的名称作为注出对象名称.</p>
     */
    String value() default "";

    /**
     * 指定注出对象的上下文作用域.
     */
    ManagedBeanScope scope() default ManagedBeanScope.REQUEST;
}
