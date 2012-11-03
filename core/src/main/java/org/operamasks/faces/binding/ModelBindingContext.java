/*
 * $Id: ModelBindingContext.java,v 1.4 2007/10/15 21:09:47 daniel Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.PhaseId;

/**
 * 为视图模型绑定过程提供上下文信息。
 */
public interface ModelBindingContext
{
    /**
     * 设置一个过滤器，以选择需要被绑定的模型。
     */
    public void setModelBeanFilter(ModelBeanFilter filter);

    /**
     * 返回一个过滤器，此过滤器将能够选择被绑定的模型。
     */
    public ModelBeanFilter getModelBeanFilter();

    /**
     * 返回当前的生命周期阶段，可取值只能是<code>PhaseId.RESTOE_VIEW</code>及
     * <code>PhaseId.RENDER_RESPONSE</code>。绑定时可以根据不同的阶段执行不同
     * 的操作。
     */
    public PhaseId getPhaseId();

    /**
     * 返回当前正在绑定的视图ID。
     */
    public String getViewId();

    /**
     * 返回正在绑定的模型对象。
     */
    public ModelBean getModelBean();

    /**
     * 返回当前正在绑定的视图组件。
     */
    public UIComponent getComponent();

    /**
     * 如果给定的ID值与当前正在绑定的视图组件的ID相匹配，则返回当前视图组件，否则返回null。
     */
    public UIComponent getComponent(String id);

    /**
     * 应用全局性模型绑定。
     */
    public void applyGlobal(FacesContext context);

    /**
     * 将视图组件与模型对象互相绑定。
     */
    public void applyModel(FacesContext context, UIComponent component);

    /**
     * 将一个嵌入视图的组件与模型对象互相绑定。
     */
    public void applyInnerModel(FacesContext ctx, String uri, UIComponent component);

    /**
     * 将视图中的数据组件与模型对象互相绑定。数据组件是一种特殊的组件，它的绑定
     * 方法和普通组件有一定的区别。
     */
    public void applyDataModel(FacesContext ctx, UIData data, Class<?> itemType);

    /**
     * 注入模型依赖数据。
     */
    public void inject(FacesContext ctx);

    /**
     * 注出模型依赖数据。
     */
    public void outject(FacesContext ctx);
}
