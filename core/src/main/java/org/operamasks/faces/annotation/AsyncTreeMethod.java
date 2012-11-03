/*
 * $Id: AsyncTreeMethod.java,v 1.3 2008/02/22 10:04:17 yangdong Exp $
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>声明装载tree组件异步节点数据的方法.</p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncTreeMethod {
    /**
     * <p>指定此动作方法的视图作用域, 当模型对象和多个视图绑定时可以用这个属性区分视图.</p>
     *
     * @return 视图标识符
     */
    String view() default "";

    /**
     * <p>指定此动作方法所绑定的视图组件的标识, 此组件必须为UITree，如果未指定则使用方法名中'_'前的字符串作为标识.</p>
     *
     * @return 视图组件标识
     */
    String id() default "";
    
    /**
     * <p>指定.</p>
     *
     * @return 异步装载数据时获取节点信息的方法，可能为AsyncTreeMethodType.asyncData，AsyncTreeMethodType.nodeText，
     * AsyncTreeMethodType.nodeImage，AsyncTreeMethodType.nodeHasChildren，AsyncTreeMethodType.initAction，
     * AsyncTreeMethodType.postCreate之一，如果未指定则使用方法名称做为标识.
     */
    AsyncTreeMethodType value() default AsyncTreeMethodType.NULL;
}
