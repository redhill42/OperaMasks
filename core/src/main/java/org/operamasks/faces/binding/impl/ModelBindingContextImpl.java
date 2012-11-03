/*
 * $Id: ModelBindingContextImpl.java,v 1.10 2008/03/05 12:50:40 jacky Exp $
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
package org.operamasks.faces.binding.impl;

import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIData;
import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import java.util.Iterator;

import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBeanFilter;
import org.operamasks.faces.binding.ComponentBinder;
import org.operamasks.faces.binding.ModelBean;

final class ModelBindingContextImpl implements ModelBindingContext
{
    private PhaseId phaseId;
    private String viewId;
    private ModelBean[] modelBeans;
    private ModelBeanFilter modelBeanFilter;
    private ModelBean currentModelBean;
    private UIComponent currentComponent;

    public ModelBindingContextImpl(PhaseId phaseId, String viewId, ModelBean[] modelBeans) {
        this.phaseId = phaseId;
        this.viewId = viewId;
        this.modelBeans = modelBeans;
    }

    public void setModelBeanFilter(ModelBeanFilter filter) {
        this.modelBeanFilter = filter;
    }

    public ModelBeanFilter getModelBeanFilter() {
        return this.modelBeanFilter;
    }

    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public String getViewId() {
        return this.viewId;
    }

    public ModelBean getModelBean() {
        return this.currentModelBean;
    }

    public UIComponent getComponent() {
        return this.currentComponent;
    }

    public UIComponent getComponent(String id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        UIComponent comp = this.currentComponent;

        // do exact ID matching
        if (id.equals(comp.getId()) || id.equals(comp.getClientId(ctx))) {
            return comp;
        }

        // If the component id matches the current model bean name prefix
        // and the search id suffix, then the component get matched.
        if (this.currentModelBean != null) {
            String compId = comp.getId();
            if (compId != null && compId.endsWith(id)) {
                int i = compId.length() - id.length() - 1;
                char c = compId.charAt(i);
                if (c == '_' || c == '-') {
                    String prefix = compId.substring(0, i);
                    if (this.currentModelBean.isMatchingPrefix(prefix)) {
                        return comp;
                    }
                }
            }
        }

        return null;
    }

    public void applyGlobal(FacesContext context) {
        for (ModelBean bean : this.modelBeans) {
            ModelBinder binder = bean.getModelBinder();
            if (binder != null) {
                this.currentModelBean = bean;
                binder.applyGlobal(context, this);
            }
        }
    }

    public void applyModel(FacesContext context, UIComponent component) {
        this.currentComponent = component;

        String id = component.getId(); // no binding if no id present
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            for (ModelBean bean : this.modelBeans) {
                if (this.modelBeanFilter == null || this.modelBeanFilter.accept(bean)) {
                    ModelBinder binder = bean.getModelBinder();
                    if (binder != null) {
                        this.currentModelBean = bean;
                        binder.applyModel(context, this);
                    }
                }
            }
        }

        if (component instanceof ComponentBinder) {
            ((ComponentBinder)component).applyModel(context, this);
        } else if (component instanceof UIData) {
            // Children of UIData has already been applied, only facets needs to apply.
            for (UIComponent facet : component.getFacets().values()) {
                applyModel(context, facet);
            }
        } else {
            Iterator<UIComponent> kids = component.getFacetsAndChildren();
            while (kids.hasNext()) {
                applyModel(context, kids.next());
            }
        }
    }

    public void applyInnerModel(FacesContext context, String uri, UIComponent component) {
        if (uri != null && uri.length() != 0) {
            ModelBean[] beans = ModelBindingFactory.instance().getModelBeans(uri, component);
            ModelBindingContext inner = new ModelBindingContextImpl(this.phaseId, uri, beans);

            inner.setModelBeanFilter(this.modelBeanFilter);
            inner.applyGlobal(context);
            for (UIComponent kid : component.getChildren()) {
                inner.applyModel(context, kid);
            }
        } else {
            Iterator<UIComponent> kids = component.getFacetsAndChildren();
            while (kids.hasNext()) {
                applyModel(context, kids.next());
            }
        }
    }

    public void applyDataModel(FacesContext ctx, UIData data, final Class<?> itemType) {
        ModelBinder binder = ModelBindingFactory.instance().createBinder(itemType);
        ModelBindingContextImpl inner = new ModelBindingContextImpl(this.phaseId, this.viewId, this.modelBeans);

        // Set a ModelBeanFilter to exclude data model bean when
        // apply components other than data item component.
        inner.setModelBeanFilter(new ModelBeanFilter() {
            public boolean accept(ModelBean bean) {
                Class<?> targetClass = bean.getTargetClass();
                return targetClass != null && !itemType.isAssignableFrom(targetClass);
            }
        });

        for (UIComponent kid : data.getChildren()) {
            if (kid instanceof UIColumn) {
                inner.applyDataItemModel(ctx, binder, data, kid);
                inner.applyModel(ctx, kid);
            } else {
                inner.applyModel(ctx, kid);
            }
        }
    }

    private void applyDataItemModel(FacesContext ctx, ModelBinder binder, UIData data, UIComponent comp) {
        String id = comp.getId();
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            this.currentModelBean = null;
            this.currentComponent = comp;
            binder.applyDataModel(ctx, this, data);
        }

        if (comp.getFacetCount() > 0) {
            if (comp instanceof UIColumn) {
                for (UIComponent facet : comp.getFacets().values()) {
                    applyModel(ctx, facet);
                }
            } else {
                for (UIComponent facet : comp.getFacets().values()) {
                    applyDataItemModel(ctx, binder, data, facet);
                }
            }
        }

        if (comp.getChildCount() > 0) {
            for (UIComponent kid : comp.getChildren()) {
                applyDataItemModel(ctx, binder, data, kid);
            }
        }
    }

    public void inject(FacesContext ctx) {
        for (ModelBean bean : this.modelBeans) {
            if (this.modelBeanFilter == null || this.modelBeanFilter.accept(bean)) {
                ModelBinder binder = bean.getModelBinder();
                if (binder != null) {
                    binder.inject(ctx, bean);
                }
            }
        }
    }

    public void outject(FacesContext ctx) {
        for (ModelBean bean : this.modelBeans) {
            if (this.modelBeanFilter == null || this.modelBeanFilter.accept(bean)) {
                ModelBinder binder = bean.getModelBinder();
                if (binder != null) {
                    binder.outject(ctx, bean);
                }
            }
        }
    }
}
