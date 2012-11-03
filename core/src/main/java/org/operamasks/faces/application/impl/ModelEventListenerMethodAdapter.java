/*
 * $Id: ModelEventListenerMethodAdapter.java,v 1.7 2007/10/19 02:07:58 daniel Exp $
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
package org.operamasks.faces.application.impl;

import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.el.ELContext;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.operamasks.faces.event.ModelEventListener;
import org.operamasks.faces.event.ModelEvent;
import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.util.Utils;

public class ModelEventListenerMethodAdapter implements ModelEventListener
{
    private String beanName;
    private Method method;

    public ModelEventListenerMethodAdapter(String beanName, Method method) {
        this.beanName = beanName;
        this.method = method;
    }

    private Method getMethod(Class targetClass) {
        if (this.method != null) {
            this.method = Utils.checkMethod(targetClass, this.method);
        }
        return this.method;
    }

    public void processModelEvent(ModelEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        ModelBean bean = getListenerBean(context, event);
        if (bean == null) {
            return;
        }

        Method method = getMethod(bean.getTargetClass());
        if (method == null) {
            return;
        }

        try {
            Class[]  types  = method.getParameterTypes();
            Object[] params = event.getParameters();
            Object[] values;
            Object   result;

            if (types.length == 1 && types[0] == ModelEvent.class) {
                values = new Object[] { event };
            } else {
                values = Utils.buildParameterList(types, params, method.isVarArgs());
            }

            bean.inject(context);
            result = bean.invokeAction(method, values);
            bean.outject(context);

            if (result != null && !context.getRenderResponse()) {
                String outcome = result.toString();
                if (outcome.startsWith(DefaultNavigationHandler.VIEW_SCHEME)) {
                    context.getApplication().getNavigationHandler()
                        .handleNavigation(context, null, outcome);
                    context.renderResponse();
                }
            }
        } catch (InvocationTargetException ex) {
            throw new FacesException(ex.getTargetException());
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private ModelBean getListenerBean(FacesContext context, ModelEvent event) {
        // If the managed bean is being created or destroyed, return the bean
        // instance if the bean is our listener bean.
        if (EventTypes.MANAGED_BEAN_CREATED.equals(event.getEventType()) ||
            EventTypes.MANAGED_BEAN_DESTROYED.equals(event.getEventType()))
        {
            Object[] params = event.getParameters(); // (name, scope, bean)
            if (this.beanName.equals(params[0])) {
                return ModelBean.wrap(params[2]);
            }
        }

        // Get or create listener bean from context.
        ELContext elContext = context.getELContext();
        Object bean = elContext.getELResolver().getValue(elContext, null, this.beanName);
        if (elContext.isPropertyResolved() && bean != null) {
            return ModelBean.wrap(bean);
        } else {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof ModelEventListenerMethodAdapter) {
            ModelEventListenerMethodAdapter other = (ModelEventListenerMethodAdapter)obj;

            if (!this.beanName.equals(other.beanName)) {
                return false;
            }

            if (this.method == null) {
                return other.method == null;
            } else {
                return this.method.equals(other.method);
            }
        }

        return false;
    }

    public int hashCode() {
        int h = this.beanName.hashCode();
        if (this.method != null)
            h ^= this.method.hashCode();
        return h;
    }
}
