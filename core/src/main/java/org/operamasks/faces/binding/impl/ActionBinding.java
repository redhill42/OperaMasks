/*
 * $Id: ActionBinding.java,v 1.13 2008/03/18 09:20:59 lishaochuan Exp $
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
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UICommand;
import javax.faces.event.ActionListener;
import javax.faces.event.PhaseId;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.ValueExpression;
import java.lang.reflect.Method;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.component.ajax.AjaxAction;
import org.operamasks.faces.util.FacesUtils;

final class ActionBinding extends Binding
{
    private String id;
    private String event;
    private String label;
    private String description;
    private boolean immediate;
    private Method actionMethod;
    private Method actionListenerMethod;
    private Class<?> declaringClass;
    private ModelSecurity security;

    private static final MethodInfo ActionMethodInfo =
        new MethodInfo("action", Object.class, new Class[0]);

    ActionBinding(String viewId, String id) {
        super(viewId);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public void setActionMethod(Method method) {
        method.setAccessible(true);
        this.actionMethod = BindingUtils.getInterfaceMethod(method);
        this.declaringClass = method.getDeclaringClass();
        this.security = ModelSecurity.scan(method);
    }

    public Method getActionListenerMethod() {
        return this.actionListenerMethod;
    }

    public void setActionListenerMethod(Method method) {
        method.setAccessible(true);
        this.actionListenerMethod = BindingUtils.getInterfaceMethod(method);
        this.declaringClass = method.getDeclaringClass();
    }

    public Class getDeclaringClass() {
        return this.declaringClass;
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        PhaseId phaseId = mbc.getPhaseId();
        ModelBean bean = mbc.getModelBean();

        if ("*".equals(this.id)) {
            if (this.event == null && this.actionListenerMethod != null && phaseId == PhaseId.RESTORE_VIEW) {
                UIComponent comp = mbc.getComponent();
                if (comp instanceof ActionSource2) {
                    applyActionListenerMethod(phaseId, (ActionSource2)comp, bean, this.actionListenerMethod);
                }
            }
        } else {
            UIComponent comp = mbc.getComponent(this.id);
            if (comp != null) {
                if (this.event != null) {
                    applyEventAction(phaseId, comp, bean, this.event);
                } else {
                    boolean disabled = false;
                    if (this.security != null && comp.getValueExpression("disabled") == null) {
                        disabled = !this.security.isUserInRole(ctx);
                        comp.setValueExpression("disabled", new ValueWrapper(disabled, Boolean.TYPE));
                    }

                    if (!disabled && (comp instanceof ActionSource2) && (phaseId == PhaseId.RESTORE_VIEW)) {
                        applyAction(phaseId, (ActionSource2)comp, bean);
                    }

                    if ((comp instanceof UICommand) && (phaseId == PhaseId.RENDER_RESPONSE)) {
                    	if (!disabled) {
                    		applyAction(phaseId, (ActionSource2)comp, bean);
                    	}
                        setText(comp, bean, this.label, "label", "value");
                        setText(comp, bean, this.description, "description", "title");
                    }
                }
            }
        }
    }

    public void applyDataItem(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        PhaseId phaseId = mbc.getPhaseId();

        if ("*".equals(this.id)) {
            if (this.event == null && this.actionListenerMethod != null && phaseId == PhaseId.RESTORE_VIEW) {
                UIComponent comp = mbc.getComponent();
                if (comp instanceof ActionSource2) {
                    applyDataItemActionListener(phaseId, (ActionSource2)comp, data, this.actionListenerMethod);
                }
            }
        } else {
            UIComponent comp = mbc.getComponent(this.id);
            if (comp != null) {
                if (this.event != null) {
                    applyDataItemEventAction(phaseId, comp, data, this.event);
                } else {
                    boolean disabled = false;
                    if (this.security != null && comp.getValueExpression("disabled") == null) {
                        disabled = !this.security.isUserInRole(ctx);
                        comp.setValueExpression("disabled", new ValueWrapper(disabled, Boolean.TYPE));
                    }

                    if (!disabled && (comp instanceof ActionSource2) && (phaseId == PhaseId.RESTORE_VIEW)) {
                        applyDataItemAction(phaseId, (ActionSource2)comp, data);
                    }

                    if ((comp instanceof UICommand) && (phaseId == PhaseId.RENDER_RESPONSE)) {
                        ValueExpression rowData = new RowDataValueAdapter(data);
                        setText(comp, rowData, this.label, "label", "value");
                        setText(comp, rowData, this.description, "description", "title");
                    }
                }
            }
        }
    }

    private void setText(UIComponent comp, Object scope, String text, String messageId, String attribute) {
        if (comp.getValueExpression(attribute) == null) {
            if (text == null || text.length() == 0) {
                text = FacesUtils.getLocalString(getDeclaringClass(), this.id + "." + messageId);
                if (text == null && messageId.equals("label")) {
                    text = FacesUtils.toCamelCase(this.id); // for debugging perpose
                }
            }

            if (text != null) {
                ValueExpression ve = BindingUtils.createValueWrapper(scope, text, String.class);
                comp.setValueExpression(attribute, ve);
            }
        }
    }

    private void applyEventAction(PhaseId phaseId, UIComponent comp, ModelBean bean, String event) {
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
        applyAction(phaseId, source, bean);

        // enable AJAX functionality for event action
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
            String renderKitId = view.getRenderKitId();
            if (renderKitId == null || "HTML_BASIC".equals(renderKitId)) {
                view.setRenderKitId("AJAX");
            }
        }
    }

    private void applyAction(PhaseId phaseId, ActionSource2 source, ModelBean bean) {
        if (this.actionMethod != null) {
            applyActionMethod(phaseId, source, bean, this.actionMethod);
        }
        if (this.actionListenerMethod != null) {
            applyActionListenerMethod(phaseId, source, bean, this.actionListenerMethod);
        }
    }

    private void applyActionMethod(PhaseId phaseId, ActionSource2 source, ModelBean bean, Method method) {
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

        adapter.addMethodBinding(new ActionMethodAdapter(bean, method));
    }

    private void applyActionListenerMethod(PhaseId phaseId, ActionSource2 source, ModelBean bean, Method method) {
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

        adapter.addActionListener(new MethodActionListener(bean, method));
    }

    private void applyDataItemEventAction(PhaseId phaseId, UIComponent comp, UIData data, String event) {
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
        applyDataItemAction(phaseId, source, data);

        // enable AJAX functionality for event action
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
            String renderKitId = view.getRenderKitId();
            if (renderKitId == null || "HTML_BASIC".equals(renderKitId)) {
                view.setRenderKitId("AJAX");
            }
        }
    }
    
    private void applyDataItemAction(PhaseId phaseId, ActionSource2 source, UIData data) {
        if (this.actionMethod != null) {
            applyDataItemActionMethod(phaseId, source, data, this.actionMethod);
        }
        if (this.actionListenerMethod != null) {
            applyDataItemActionListener(phaseId, source, data, this.actionListenerMethod);
        }
    }

    private void applyDataItemActionMethod(PhaseId phaseId, ActionSource2 source, UIData data, Method method) {
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

        adapter.addMethodBinding(new DataItemMethodAdapter(data, method));
    }

    private void applyDataItemActionListener(PhaseId phaseId, ActionSource2 source, UIData data, Method method) {
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

        adapter.addActionListener(new DataItemActionListener(data, method));
    }
}
