/*
 * $Id: ResourceProvider.java,v 1.4 2007/07/02 07:38:04 jacky Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.component.UIComponent;

/**
 * <code>ResourceProvider</code>是一个依赖外部资源的UI组件。当渲染一个页面时JSF
 * 运行机制将调用组件树中所有实现了<code>ResourceProvider</code>接口的组件，组件
 * 可以在这时向{@link ResourceManager 资源管理器}登记资源，而这些资源最终将在页面
 * 的开始或结束部分被渲染。多个组件可能多次登记同一个资源，但只有一个资源实例被渲染。
 */
public interface ResourceProvider
{
    /**
     * 由资源管理器调用的方法，组件可以在这时向资源管理器登记资源。
     *
     * @param manager 资源管理器
     * @param component 依赖外部资源的UI组件
     */
    public void provideResource(ResourceManager manager, UIComponent component);
}
