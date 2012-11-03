/*
 * $Id: ComponentBinder.java,v 1.1 2007/09/30 21:08:41 daniel Exp $
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
package org.operamasks.faces.binding;

import javax.faces.context.FacesContext;

/**
 * UI组件可实现此接口执行特殊的绑定。
 */
public interface ComponentBinder
{
    /**
     * 执行与特定组件相关的视图模型绑定。
     *
     * @param ctx 当前Faces上下文
     * @param mbc 当前视图模型绑定上下文
     */
    public void applyModel(FacesContext ctx, ModelBindingContext mbc);
}
