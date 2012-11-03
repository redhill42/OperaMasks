/*
 * $Id: PhaseListenerBinding.java,v 1.6 2008/04/10 09:29:35 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseEvent;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.ELContext;
import java.lang.reflect.Method;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.util.FacesUtils;

class PhaseListenerBinding extends Binding
{
    private Method beforePhaseMethod;
    private Method afterPhaseMethod;
    private Method beforeRenderMethod;
    private Method afterRenderMethod;

    private static final MethodInfo PhaseMethodInfo =
        new MethodInfo("phaseListener", Void.TYPE, new Class[] {PhaseEvent.class} );

    PhaseListenerBinding(String viewId) {
        super(viewId);
    }

    public Method getBeforePhaseMethod() {
        return this.beforePhaseMethod;
    }

    public void setBeforePhaseMethod(Method method) {
        method.setAccessible(true);
        this.beforePhaseMethod = BindingUtils.getInterfaceMethod(method);
    }

    public Method getAfterPhaseMethod() {
        return this.afterPhaseMethod;
    }

    public void setAfterPhaseMethod(Method method) {
        method.setAccessible(true);
        this.afterPhaseMethod = BindingUtils.getInterfaceMethod(method);
    }

    public Method getBeforeRenderMethod() {
        return this.beforeRenderMethod;
    }

    public void setBeforeRenderMethod(Method method) {
        method.setAccessible(true);
        this.beforeRenderMethod = BindingUtils.getInterfaceMethod(method);
    }

    public Method getAfterRenderMethod() {
        return this.afterRenderMethod;
    }

    public void setAfterRenderMethod(Method method) {
        method.setAccessible(true);
        this.afterRenderMethod = BindingUtils.getInterfaceMethod(method);
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        UIViewRoot view = ctx.getViewRoot();
        PhaseId phaseId = mbc.getPhaseId();
        ModelBean bean = mbc.getModelBean();

        if (this.beforePhaseMethod != null) {
            MethodExpression binding = new MethodAdapter(bean, this.beforePhaseMethod);
            applyBeforePhaseMethod(view, phaseId, binding);
        }
        if (this.afterPhaseMethod != null) {
            MethodExpression binding = new MethodAdapter(bean, this.afterPhaseMethod);
            applyAfterPhaseMethod(view, phaseId, binding);
        }
        if (this.beforeRenderMethod != null) {
            MethodExpression binding = new BeforeRenderMethodAdapter(bean, this.beforeRenderMethod);
            applyBeforePhaseMethod(view, phaseId, binding);
        }
        if (this.afterRenderMethod != null) {
            MethodExpression binding = new AfterRenderMethodAdapter(bean, this.afterRenderMethod);
            applyAfterPhaseMethod(view, phaseId, binding);
        }
    }

    private void applyBeforePhaseMethod(UIViewRoot view, PhaseId phaseId, MethodExpression binding) {
        MethodExpression previous = view.getBeforePhaseListener();
        if ((previous != null) && !(previous instanceof CompositeMethodAdapter)) {
            return; // already has before phase method
        }

        CompositeMethodAdapter adapter;
        if ((previous == null) || (phaseId != ((CompositeMethodAdapter)previous).getPhaseId())) {
            adapter = new CompositeMethodAdapter(PhaseMethodInfo);
            adapter.setPhaseId(phaseId);
            view.setBeforePhaseListener(adapter);
        } else {
            adapter = (CompositeMethodAdapter)previous;
        }

        adapter.addMethodBinding(binding);
    }

    private void applyAfterPhaseMethod(UIViewRoot view, PhaseId phaseId, MethodExpression binding) {
        MethodExpression previous = view.getAfterPhaseListener();
        if ((previous != null) && !(previous instanceof CompositeMethodAdapter)) {
            return; // already has before phase method
        }

        CompositeMethodAdapter adapter;
        if ((previous == null) || (phaseId != ((CompositeMethodAdapter)previous).getPhaseId())) {
            adapter = new CompositeMethodAdapter(PhaseMethodInfo);
            adapter.setPhaseId(phaseId);
            view.setAfterPhaseListener(adapter);
        } else {
            adapter = (CompositeMethodAdapter)previous;
        }

        adapter.addMethodBinding(binding);
    }

    private static class BeforeRenderMethodAdapter extends MethodAdapter
    {
        public BeforeRenderMethodAdapter(ModelBean bean, Method method) {
            super(bean, method);
        }

        public Object invoke(ELContext context, Object[] params) {
            assert (params.length == 1) && (params[0] instanceof PhaseEvent);
            PhaseEvent event = (PhaseEvent)params[0];
            if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
                boolean postBack = FacesUtils.isPostback(FacesContext.getCurrentInstance());
                return super.invoke(context, new Object[]{postBack});
            } else {
                return null;
            }
        }
    }

    private static class AfterRenderMethodAdapter extends MethodAdapter
    {
        public AfterRenderMethodAdapter(ModelBean bean, Method method) {
            super(bean, method);
        }

        public Object invoke(ELContext context, Object[] params) {
            assert (params.length == 1) && (params[0] instanceof PhaseEvent);
            PhaseEvent event = (PhaseEvent)params[0];
            if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
                return super.invoke(context, new Object[0]);
            } else {
                return null;
            }
        }
    }
}
