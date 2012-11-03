/*
 * $Id: Convert.java,v 1.4 2008/03/21 00:40:39 patrick Exp $
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
 * <p>声明一个转换方法, 用于将输入值转换成实际值. 使用转换方法能够简化数据转换逻辑的
 * 实现, 不需要编写单独的转换器, 也不需要在faces-config.xml中进行配置. 转换方法既
 * 可以直接在模型对象所在的托管bean中实现(应用于耦合度较高的场合), 也可以编写和模型
 * 对象分离的转换器(应用于松散耦合的场合).</p>
 *
 * @see Format
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Convert
{
    /**
     * 指定转换方法所在的视图作用域, 当模型对象绑定到多个视图时可以用此属性加以区分.
     */
    String view() default "";

    /**
     * <p>指定被转换的视图组件的标识, 如果未指定并且转换方法的名称以"convert"打头,
     * 则组件标识是将convert去掉以后剩余字符串首字母小写的形式. 例如:</p>
     * <pre>
     *   &#064;Convert
     *   private double convertPrice(String value) { ... }
     * </pre>
     * <p>则对应的组件标识为"price".</p>
     *
     * <p>注意id的类型是一个数组, 因此可以同时转换多个视图组件, 例如:</p>
     * <pre>
     *   &#064;Convert(id={"price", "amount"})
     *   private double convertCurrency(String value) { ... }
     * </pre>
     */
    String[] id() default {};
}
