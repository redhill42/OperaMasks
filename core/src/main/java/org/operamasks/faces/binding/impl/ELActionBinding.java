/*
 * $Id: ELActionBinding.java,v 1.5 2008/01/31 04:12:24 daniel Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.component.ActionSource2;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UICommand;
import javax.faces.event.PhaseId;
import javax.faces.event.ActionListener;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.ValueExpression;

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.component.ajax.AjaxAction;
import org.operamasks.faces.util.FacesUtils;

class ELActionBinding extends Binding
{
    private String  id;
    private String  event;
    private boolean immediate;
    private Closure action;
    private Closure actionListener;

    private static final MethodInfo ActionMethodInfo =
        new MethodInfo("action", Object.class, new Class[0]);

    ELActionBinding(String id, String event, boolean immediate) {
        super(null);
        this.id = id;
        this.event = event;
        this.immediate = immediate;
    }

    public String getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public Closure getAction() {
        return this.action;
    }

    public void setAction(Closure action) {
        this.action = action;
    }

    public Closure getActionListener() {
        return this.actionListener;
    }

    public void setActionListener(Closure actionListener) {
        this.actionListener = actionListener;
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        PhaseId phaseId = mbc.getPhaseId();

        if ("*".equals(this.id)) {
            if (this.event == null && this.actionListener != null && phaseId == PhaseId.RESTORE_VIEW) {
                UIComponent comp = mbc.getComponent();
                if (comp instanceof ActionSource2) {
                    applyActionListenerClosure(phaseId, (ActionSource2)comp, this.actionListener);
                }
            }
        } else {
            UIComponent comp = mbc.getComponent(this.id);
            if (comp != null) {
                if (this.event != null) {
                    applyEventAction(phaseId, comp, this.event);
                } else {
                    if ((comp instanceof ActionSource2) && (phaseId == PhaseId.RESTORE_VIEW)) {
                        applyAction(phaseId, (ActionSource2)comp);
                    }
                    if ((comp instanceof UICommand) && (phaseId == PhaseId.RENDER_RESPONSE)) {
                        ELiteBean bean = (ELiteBean)mbc.getModelBean();
                        setText(comp, bean, "label", "value");
                        setText(comp, bean, "description", "title");
                    }
                }
            }
        }
    }

    private void applyEventAction(PhaseId phaseId, UIComponent comp, String event) {
        String actionId = comp.getId() + "-" + event;

        AjaxAction source = null;
        for (UIComponent kid : comp.getChildren()) {
            if ((kid instanceof AjaxAction) && (actionId.equals(kid.getId()))) {
                source = (AjaxAction)kid;
                break;
            }
        }

        // add an AjaxAction component to handle asynchronous event
        if (source == null) {
            source = new AjaxAction();
            source.setId(actionId);
            source.setEvent(event);
            source.setTransient(true);
            comp.getChildren().add(source);
            source.attachEvent(event, comp);
        }

        // attach event handler to the AjaxAction component
        applyAction(phaseId, source);

        // enable AJAX functionality for event action
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
            String renderKitId = view.getRenderKitId();
            if (renderKitId == null || "HTML_BASIC".equals(renderKitId)) {
                view.setRenderKitId("AJAX");
            }
        }
    }

    private void applyAction(PhaseId phaseId, ActionSource2 source) {
        if (this.action != null) {
            applyActionClosure(phaseId, source, this.action);
        }
        if (this.actionListener != null) {
            applyActionListenerClosure(phaseId, source, this.actionListener);
        }
    }

    private void applyActionClosure(PhaseId phaseId, ActionSource2 source, Closure closure) {
        MethodExpression previous = source.getActionExpression();
        if ((previous != null) && !(previous instanceof CompositeMethodAdapter)) {
            return; // already has action binding
        }

        CompositeMethodAdapter adapter;
        if ((previous == null) || (phaseId != ((CompositeMethodAdapter)previous).getPhaseId())) {
            adapter = new CompositeMethodAdapter(ActionMethodInfo);
            adapter.setPhaseId(phaseId);
            source.setActionExpression(adapter);
            source.setImmediate(this.immediate);
        } else {
            adapter = (CompositeMethodAdapter)previous;
        }

        adapter.addMethodBinding(new ClosureMethodAdapter(closure));
    }

    private void applyActionListenerClosure(PhaseId phaseId, ActionSource2 source, Closure closure) {
        CompositeActionListener adapter = null;
        for (ActionListener l : source.getActionListeners()) {
            if (l instanceof CompositeActionListener) {
                if (phaseId != ((CompositeActionListener)l).getPhaseId()) {
                    source.removeActionListener(l);
                } else {
                    adapter = (CompositeActionListener)l;
                    break;
                }
            }
        }

        if (adapter == null) {
            adapter = new CompositeActionListener();
            adapter.setPhaseId(phaseId);
            source.addActionListener(adapter);
            source.setImmediate(this.immediate);
        }

        adapter.addActionListener(new ClosureActionListener(closure));
    }

    private void setText(UIComponent comp, ELiteBean bean, String key, String attribute) {
        if (comp.getValueExpression(attribute) == null) {
            String text = bean.getLocalString(id + "." + key);
            if (text == null && key.equals("label")) {
                text = FacesUtils.toCamelCase(id);
            }

            if (text != null) {
                ValueExpression ve = BindingUtils.createValueWrapper(null, text, String.class);
                comp.setValueExpression(attribute, ve);
            }
        }
    }
}
