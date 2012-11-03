/*
 * $Id: TreeEventListener.java,v 1.1 2008/01/23 05:33:07 yangdong Exp $
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
 * <p>声明一个tree事件处理监听接口. 此监听接口将和视图中的一个tree组件进行绑定, 当tree组件的特定事
 * 件产生时调用接口的事件处理方法.</p>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeEventListener {
    /**
     * <p>指定此动作方法的视图作用域, 当模型对象和多个视图绑定时可以用这个属性区分视图.</p>
     *
     * @return 视图标识符
     */
    String view() default "";

    /**
     * <p>指定此动作方法所绑定的视图组件的标识, 如果未指定则使用方法名中'_'前的字符串作为标识.</p>
     *
     * @return 视图组件标识
     */
    String id() default "";
    
    /**
     * <p>是否监控树节点的单击事件.</p>
     *
     * @return 返回true监控事件，返回false则不监控
     */
    boolean click() default false;
    
    /**
     * <p>是否监控树节点的双击事件.</p>
     *
     * @return 返回true监控事件，返回false则不监控
     */
    boolean dblClick() default false;
    
    /**
     * <p>是否监控树节点的展开事件.</p>
     *
     * @return 返回true监控事件，返回false则不监控
     */
    boolean expand() default false;
    
    /**
     * <p>是否监控树节点的缩起子节点事件.</p>
     *
     * @return 返回true监控事件，返回false则不监控
     */
    boolean collapse() default false;
    
    /**
     * <p>是否监控树节点的选中事件.</p>
     *
     * @return 返回true监控事件，返回false则不监控
     */
    boolean select() default false;
    
    /**
     * <p>指定要监控的事件列表，每个事件用字符串表示. 例如树的单击事件使用字符串"click"表示，多个事件中间用','分隔.</p>
     *
     * @return 返回true监控单击事件，返回false则不监控
     */
    String[] events() default {};
}
