/*
 * $Id: Binding.java,v 1.4 2008/03/05 12:50:40 jacky Exp $
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

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import org.operamasks.faces.binding.ModelBindingContext;

abstract class Binding
{
    protected String viewId;
    protected int order;

    protected Binding(String viewId) {
        this.viewId = viewId;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    /**
     * Apply global bindings in the model.
     */
    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        // default is do nothing
    }

    /**
     * Apply model bindings into view
     */
    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        // default is do nothing
    }

    /**
     * Apply data model bindings into view
     */
    public void applyDataItem(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        // default is do nothing;
    }

    public boolean isMatchingView(String currentViewId) {
        if (this.viewId == null || this.viewId.length() == 0) {
            return true;
        }

        if (this.viewId.equals(currentViewId)) {
            return true;
        }

        if (this.viewId.indexOf('.') == -1) {
            int slash = currentViewId.lastIndexOf('/');
            int dot = currentViewId.lastIndexOf('.');
            if ((slash < dot) && this.viewId.equals(currentViewId.substring(slash+1, dot))) {
                return true;
            }
        }

        return false;
    }
}
