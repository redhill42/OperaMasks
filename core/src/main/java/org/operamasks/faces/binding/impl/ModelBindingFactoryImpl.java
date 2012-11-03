/*
 * $Id: ModelBindingFactoryImpl.java,v 1.10 2008/02/22 02:15:48 jacky Exp $
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ELContext;

import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.ModelBeanCreator;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.component.misc.UIUseBean;
import org.operamasks.faces.application.ViewMapper;
import org.operamasks.faces.application.impl.DefaultViewMapper;
import org.operamasks.util.SimpleCache;

public class ModelBindingFactoryImpl extends ModelBindingFactory
{
    private ViewMapper viewMapper;
    private ModelBeanCreator modelBeanCreator;

    private SimpleCache<String,ModelBinderImpl> cache = SimpleCache.make(200);

    private static final String MODEL_BEANS_ATTRIBUTE = "org.operamasks.faces.MODEL_BEANS-";

    private static final String ELITE_SCRIPT_PREFIX = "/WEB-INF/";

    protected ModelBindingFactoryImpl() {
        this.viewMapper = DefaultViewMapper.getInstance();
    }
    
    public ViewMapper getViewMapper() {
        return this.viewMapper;
    }

    public void setViewMapper(ViewMapper viewMapper) {
        if (viewMapper == null)
            throw new NullPointerException();
        this.viewMapper = viewMapper;
    }

    public ModelBeanCreator getModelBeanCreator() {
        return this.modelBeanCreator;
    }

    public void setModelBeanCreator(ModelBeanCreator creator) {
        this.modelBeanCreator = creator;
    }
    
    public ModelBinder createBinder(Class targetClass) {
        String key = targetClass.getName();
        ModelBinderImpl result = cache.get(key);
        if (result == null || result.getTargetClass() != targetClass) {
            result = new ModelBinderImpl(targetClass);
            cache.put(key, result);
        }
        return result;
    }

    public ModelBindingContext createContext(PhaseId phaseId, String viewId, ModelBean[] modelBeans) {
        return new ModelBindingContextImpl(phaseId, viewId, modelBeans);
    }

    public ModelBean[] getModelBeans(String viewId, UIComponent root) {
        if (viewId == null || root == null) {
            throw new NullPointerException();
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        ELContext elctx = ctx.getELContext();
        List<ModelBean> list = new ArrayList<ModelBean>();

        // add the ELiteBean for embeded ELite script object binding
        ELiteBean elbean = ELiteBean.make(elctx);
        if (elbean != null) {
            list.add(elbean);
        }

        // lookup from configured model view mappings
        List<String> beanList = this.viewMapper.mapViewId(viewId);
        if (beanList != null) {
            for (String beanName : beanList) {
                ModelBean bean = null;
                if (FacesUtils.isValueExpression(beanName)) {
                    bean = ModelBean.wrap(FacesUtils.createValueExpression(beanName, Object.class));
                } else {
                    if (beanName.startsWith(ELITE_SCRIPT_PREFIX)) {
                        bean = ELiteBeanCache.getInstance().get(ctx, beanName);
                    } else {
                        Object target = elctx.getELResolver().getValue(elctx, null, beanName);
                        if (elctx.isPropertyResolved() && target != null) {
                            bean = ModelBean.wrap(target);
                            bean.addName(beanName);
                        }
                    }
                }
                if (bean != null) {
                    addModelBeanIntoList(list, bean);
                }
            }
        }

        // lookup model beans from UIUseBean components
        Iterator<UIComponent> iter = FacesUtils.createFacetsAndChildrenIterator(root, true);
        while (iter.hasNext()) {
            UIComponent comp = iter.next();
            if (comp instanceof UIUseBean) {
                for (ModelBean bean : ((UIUseBean)comp).getBeans(true)) {
                    addModelBeanIntoList(list, bean);
                }
            }
        }

        ModelBean[] result = list.toArray(new ModelBean[list.size()]);

        Map<String,Object> requestMap = ctx.getExternalContext().getRequestMap();
        requestMap.put(MODEL_BEANS_ATTRIBUTE + viewId, result);
        return result;
    }

    /**
     * Get managed model beans associated with given view identifier.
     */
    public ModelBean[] getModelBeans(String viewId) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        return (ModelBean[])requestMap.get(MODEL_BEANS_ATTRIBUTE + viewId);
    }

    private void addModelBeanIntoList(List<ModelBean> list, ModelBean bean) {
        // find existing mapping
        for (ModelBean exist : list) {
            if (exist.equals(bean)) {
                exist.addNames(bean.getNames());
                return;
            }
        }

        // not found, so add new mapping
        list.add(bean.clone());
    }
}
