/*
 * $Id: AfterRender.java,v 1.3 2007/10/12 06:21:46 daniel Exp $
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
 * <p>声明一个生命周期侦听方法, 当视图渲染结束时调用此方法.</p>

 * <p>视图渲染侦听方法必须使用以下方法签名:</p>
 * <pre>
 *     void afterRender();
 * </pre>
 *
 * @see BeforeRender
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterRender
{
    /**
     * <p>指定此生命周期侦听方法的视图作用域, 当模型对象和多个视图绑定时可以用这个属性区分视图.</p>
     *
     * @return 视图标识符
     */
    String view() default "";
}
