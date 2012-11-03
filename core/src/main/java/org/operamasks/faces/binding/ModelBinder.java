/*
 * $Id: ModelBinder.java,v 1.5 2008/03/05 12:50:40 jacky Exp $
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
import javax.faces.component.UIData;

/**
 * Perform injection, outjection, and bijection between View and Model.
 */
public abstract class ModelBinder
{
    /**
     * Apply global bindings for the model.
     */
    public abstract void applyGlobal(FacesContext ctx, ModelBindingContext mbc);

    /**
     * Apply model bindings into view.
     */
    public abstract void applyModel(FacesContext ctx, ModelBindingContext mbc);

    /**
     * Apply data model items in the view.
     */
    public abstract void applyDataModel(FacesContext context, ModelBindingContext mbc, UIData data);
    
    /**
     * Inject values from view to model.
     */
    public abstract void inject(FacesContext context, ModelBean bean);

    /**
     * Outject values from model to view.
     */
    public abstract void outject(FacesContext context, ModelBean bean);
}
