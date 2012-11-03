/*
 * $Id 
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
package org.operamasks.faces.tools.annotation;

public @interface ComponentPackage {
    String value() default "";//生成的类存放的包路径
    String catalog() default "";//生成的组件输入哪个分类，例如widget,layout,graph等
    String configFilePath() default "";//可以具体指定组件信息配置文件的路径，如果这个属性指定了，catalog()则失效
    boolean enable() default true;//设置一个开关，可以不用每次都生成
}
