/*
 * $Id: Bind.java,v 1.7 2007/10/12 06:21:46 daniel Exp $
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
 * <p>将模型对象属性绑定到视图组件上.</p>
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Accessible
public @interface Bind
{
    /**
     * <p>指定模型属性的视图作用域, 当模型对象和多个视图绑定时可以用这个属性加以区分.</p>
     *
     * @return 视图标识符
     */
    String view() default "";
    
    /**
     * <p>指定视图组件的标识, 如果未指定则使用属性名称作为标识.</p>
     *
     * @return 视图组件标识
     */
    String id() default "";

    /**
     * <p>指定视图组件的绑定属性, 如果未指定则默认为"value"属性.</p>
     *
     * @return 视图组件属性名
     */
    String attribute() default "";

    /**
     * <p>指定一个EL表达式, 该表达式将在初次绑定时被求值并初始化模型属性值.</p>
     *
     * @return 用于初始化属性值的EL表达式
     */
    String value() default "";

    /**
     * <p>当模型绑定存在依赖关系时可以用此属性指定绑定顺序.</p>
     *
     * @return 绑定顺序
     */
    int order() default 0;
}
