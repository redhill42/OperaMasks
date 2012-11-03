/*
 * $Id: ELPhaseListenerBinding.java,v 1.3 2008/01/31 04:12:24 daniel Exp $
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

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.util.FacesUtils;

public class ELPhaseListenerBinding extends Binding
{
    private Closure onPageLoad;

    private static final MethodInfo PhaseMethodInfo =
        new MethodInfo("phaseListener", Void.TYPE, new Class[] {PhaseEvent.class} );

    ELPhaseListenerBinding() {
        super(null);
    }

    public Closure getOnPageLoadClosure() {
        return onPageLoad;
    }

    public void setOnPageLoadClosure(Closure closure) {
        this.onPageLoad = closure;
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        UIViewRoot view = ctx.getViewRoot();
        PhaseId phaseId = mbc.getPhaseId();

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

        MethodExpression binding = new BeforeRenderClosureAdapter(onPageLoad);
        adapter.addMethodBinding(binding);
    }

    private static class BeforeRenderClosureAdapter extends AbstractMethodAdapter {
        private Closure closure;

        BeforeRenderClosureAdapter(Closure closure) {
            this.closure = closure;
        }

        public MethodInfo getMethodInfo(ELContext context) {
            return PhaseMethodInfo;
        }

        public Object invoke(ELContext context, Object[] args) {
            assert (args.length == 1) && (args[0] instanceof PhaseEvent);
            PhaseEvent event = (PhaseEvent)args[0];
            if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
                if (closure.arity(context) == 0) {
                    closure.call(context);
                } else {
                    boolean postback = FacesUtils.isPostback(FacesContext.getCurrentInstance());
                    closure.call(context, postback);
                }
            }
            return null;
        }
    }
}
