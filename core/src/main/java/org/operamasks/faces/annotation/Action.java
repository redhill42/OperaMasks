/*
 * $Id: Action.java,v 1.5 2007/10/12 06:21:46 daniel Exp $
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
 * <p>声明一个动作方法. 此方法将和视图中的一个动作组件(例如commandButton)进行绑定,
 * 当动作组件被激活时调用动作方法.</p>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action
{
    /**
     * <p>指定此动作方法的视图作用域, 当模型对象和多个视图绑定时可以用这个属性区分视图.</p>
     *
     * @return 视图标识符
     */
    String view() default "";

    /**
     * <p>指定此动作方法所绑定的视图组件的标识, 如果未指定则使用动作方法名称作为标识.</p>
     *
     * @return 视图组件标识
     */
    String id() default "";

    /**
     * <p>此动作方法可以和任意组件的客户端事件进行绑定, 当发生客户端事件时通过Ajax
     * (asynchronous javascript and XML) 方式异步调用此动作方法.</p>
     *
     * @return 客户端事件名称
     */
    String event() default "";

    /**
     * <p>指定是否立即执行动作方法.</p>
     *
     * <p>在某些场景下需要立即执行动作方法而跳过数据转换, 数据校验, 更新模型数据等JSF生命
     * 周期阶段, 例如直接跳转到其他页面, 关闭对话框等.</p>
     *
     * @return 当为true时跳过数据转换与校验等JSF生命周期阶段, 直接执行应用调用阶段;
     *         当为false时按正常JSF生命周期执行.
     */
    boolean immediate() default false;
}
