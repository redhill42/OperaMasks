/*
 * $Id: ComponentMeta.java,v 1.3 2008/03/10 08:28:52 lishaochuan Exp $
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

public @interface ComponentMeta
{
    String family() default "";
    String type() default "";
    String rendererType() default "";
    String tagName() default "";
    String catalog() default "";
    boolean isProxy() default false;
    String tagBaseClass() default "";//指定生成JSP Tag类的基类
}
