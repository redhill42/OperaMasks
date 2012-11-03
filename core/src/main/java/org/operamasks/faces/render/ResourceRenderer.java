/*
 * $Id: ResourceRenderer.java,v 1.2 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render;

import javax.faces.component.UIComponent;

import org.operamasks.faces.render.resource.ResourceManager;

/**
 * 此接口用于实现在页面初始装载时，对资源的处理，如：引入css、初始化script脚本等行为
 */
public interface ResourceRenderer {
    /**
     * 由资源管理器调用的方法，组件可以在这时向资源管理器登记资源。
     *
     * @param manager 资源管理器
     * @param component 依赖外部资源的UI组件
     */
    public void encodeResourceBegin(ResourceManager manager, UIComponent component);

    /**
     * 由资源管理器调用的方法，组件可以在这时向资源管理器登记资源。
     *
     * @param manager 资源管理器
     * @param component 依赖外部资源的UI组件
     */
    public void encodeResourceChildren(ResourceManager manager, UIComponent component);
    
    /**
     * 由资源管理器调用的方法，组件可以在这时向资源管理器登记资源。
     *
     * @param manager 资源管理器
     * @param component 依赖外部资源的UI组件
     */
    public void encodeResourceEnd(ResourceManager manager, UIComponent component);
    
    /**
     * 是否处理子组件的资源
     */
    public boolean getEncodeResourceChildren();
}
