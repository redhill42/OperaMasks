/*
 * $Id: InvokeApplication.java,v 1.4 2007/10/24 04:40:43 daniel Exp $
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
package org.operamasks.faces.lifecycle;

import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;

import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

public class InvokeApplication extends Phase
{
    public InvokeApplication() {
        super(INVOKE_APPLICATION);
    }

    public void execute(FacesContext context) {
        UIViewRoot view = context.getViewRoot();
        ModelBindingContext mbc = getModelBindingContext(context);

        if (mbc != null) {
            mbc.inject(context);
            view.processApplication(context);
            mbc.outject(context);
        } else {
            view.processApplication(context);
        }
    }

    private ModelBindingContext getModelBindingContext(FacesContext context) {
        ModelBindingFactory mbf = ModelBindingFactory.instance();
        String viewId = context.getViewRoot().getViewId();
        ModelBean[] beans = mbf.getModelBeans(viewId);

        if (beans != null) {
            return mbf.createContext(INVOKE_APPLICATION, viewId, beans);
        } else {
            return null;
        }
    }
}
