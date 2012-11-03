/*
 * $Id: ModelBindingFactory.java,v 1.8 2008/02/22 02:15:48 jacky Exp $
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

import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.operamasks.faces.binding.impl.ModelBindingFactoryImpl;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ViewMapper;

/**
 * The factory class that create implementation of {@link ModelBinder} and
 * {@link ModelBindingContext}.
 */
public abstract class ModelBindingFactory
{
    /**
     * Get the singleton instance of ModelBindingFactory associated with
     * current JavaServer Faces application.
     */
    public static ModelBindingFactory instance() {
        return ApplicationAssociate.getInstance().getSingleton(ModelBindingFactoryImpl.class);
    }

    /**
     * Get a mapper that maps view identifier to model beans.
     */
    public abstract ViewMapper getViewMapper();

    /**
     * Set a mapper that maps view identifier to model beans.
     */
    public abstract void setViewMapper(ViewMapper viewMapper);

    /**
     * Returns the {@link ModelBeanCreator} object that used to wrap a model bean.
     */
    public abstract ModelBeanCreator getModelBeanCreator();

    /**
     * Set the {@link ModelBeanCreator} object that used to wrap a model bean.
     */
    public abstract void setModelBeanCreator(ModelBeanCreator creator);

    /**
     * Create the {@link ModelBinder} object.
     */
    public abstract ModelBinder createBinder(Class targetClass);

    /**
     * Create the {@link ModelBindingContext} object for the given model beans.
     */
    public abstract ModelBindingContext createContext(PhaseId phaseId, String viewId, ModelBean[] modelBeans);

    /**
     * Build and get model beans associated with given view.
     *
     * @param viewId the view identifier
     * @param root the root component in the view
     * @return the model beans associated with given view
     */
    public abstract ModelBean[] getModelBeans(String viewId, UIComponent root);

    /**
     * Get model beans associated with given view.
     *
     * @param viewId the view identifier
     * @return model beans associated with given view, or null if no model beans
     *         associated with given view.
     */
    public abstract ModelBean[] getModelBeans(String viewId);

    /**
     * Convenient method that apply all model bindings associated with current view.
     */
    public static void applyModelBindings(FacesContext ctx, PhaseId phaseId) {
        UIViewRoot view = ctx.getViewRoot();
        applyModelBindings(ctx, phaseId, view.getViewId(), view);
    }

    /**
     * Convenient method that apply all model bindings associated with current view.
     */
    public static void applyModelBindings(FacesContext ctx, PhaseId phaseId, String viewId, UIComponent root) {
        ModelBindingFactory factory = ModelBindingFactory.instance();
        ModelBean[] beans = factory.getModelBeans(viewId, root);
        ModelBindingContext mbc = factory.createContext(phaseId, viewId, beans);

        mbc.applyGlobal(ctx);
        mbc.applyModel(ctx, root);
    }
}
