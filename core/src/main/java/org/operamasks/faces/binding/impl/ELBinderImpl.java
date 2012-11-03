/*
 * $Id: ELBinderImpl.java,v 1.4 2008/03/05 12:50:40 jacky Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.component.UIData;

import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

final class ELBinderImpl extends ModelBinder
{
    private List<Binding> bindings;

    ELBinderImpl(List<Binding> bindings) {
        this.bindings = bindings;
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        for (Binding b : bindings) {
            b.applyGlobal(ctx, mbc);
        }
    }

    public void applyModel(FacesContext ctx, ModelBindingContext mbc) {
        for (Binding b : bindings) {
            b.apply(ctx, mbc);
        }
    }

    public void applyDataModel(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        // no data item binding for EL binding
    }

    public void inject(FacesContext ctx, ModelBean bean) {
        for (Binding b : bindings) {
            if (b instanceof Injector) {
                ((Injector)b).inject(ctx, bean);
            }
        }
    }

    public void outject(FacesContext ctx, ModelBean bean) {
        for (Binding b : bindings) {
            if (b instanceof Injector) {
                ((Injector)b).outject(ctx, bean);
            }
        }
    }
}
