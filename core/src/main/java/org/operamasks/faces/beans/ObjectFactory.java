/*
 * $Id: ObjectFactory.java,v 1.1 2007/10/18 08:59:15 daniel Exp $
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

package org.operamasks.faces.beans;

/**
 * 当托管Bean实现了ObjectFactory接口时, 它实际上实现了一种抽象工厂模式(Abstract Factory
 * Pattern). 当访问此托管Bean时, 得到的并不是该托管Bean本身, 而是它所创建的另一个对象.
 * 采用这种模式可以隐藏对象创建的细节, 并且工厂对象本身也是可配置的, 这样将获得更大的灵活性.
 */
public interface ObjectFactory
{
    /**
     * 返回工厂对象所创建的对象实例.
     */
    public Object getObject();
}
