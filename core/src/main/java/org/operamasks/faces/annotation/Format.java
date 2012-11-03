/*
 * $Id: Format.java,v 1.3 2007/10/12 06:21:46 daniel Exp $
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
 * <p>声名一个格式化方法, 用于将实际值转换成输出值. 使用格式化方法能够简化数据转换
 * 逻辑的实现, 不需要编写单独的转换器, 也不需要在faces-config.xml中进行配置. 格式
 * 化方法既可以直接在模型对象所在的托管bean中实现(应用于耦合度较高的场合), 也可以编
 * 写和模型对象分离的转换器(应用于松散耦合的场合).</p>
 *
 * @see Convert
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Format
{
    /**
     * 指定格式化方法所在的视图作用域, 当模型对象绑定到多个视图时可以用此属性加以区分.
     */
    String view() default "";

    /**
     * <p>指定被格式化的视图组件的标识, 如果未指定并且格式化方法的名称以"format"打头,
     * 则组件标识是将format去掉以后剩余字符串首字母小写的形式. 例如:</p>
     * <pre>
     *   &#064;Format
     *   private String formatPrice(double value) { ... }
     * </pre>
     * <p>则对应的组件标识为"price".</p>

     * <p>注意id的类型是一个数组, 因此可以同时格式化多个视图组件, 例如:</p>
     * <pre>
     *   &#064;Format(id={"price", "amount"})
     *   private String formatCurrency(double value) { ... }
     * </pre>
     */
    String[] id() default {};
}
