/*
 * $Id: Accessible.java,v 1.5 2007/10/29 10:48:56 daniel Exp $
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
import static java.lang.annotation.ElementType.*;

/**
 * <p>声明类成员的可访问性.</p>
 * 
 * <p>当此注释应用到一个类成员(成员变量或方法)时, 表示类成员可以在EL表达式中被访问.
 * 特别地, 对于一个类成员变量即使没有实现get和set方法也具有可访问性. 
 * 如果不希望类成员具有可访问性, 可以使用<code>@Accessible(false)</code>
 * 的形式.</p>
 *
 * <p>当此注释应用到其他注释类型, 例如Bind, Inject等, 表示所有使用了目标注释类型
 * 的类成员都将具有可访问性.</p>
 */
@Documented
@Target({ANNOTATION_TYPE, FIELD, METHOD, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessible
{
    /**
     * 指定类成员的可访问性.
     *
     * @return true表示可访问, false表示不可访问
     */
    boolean value() default true;
}
