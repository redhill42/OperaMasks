/*
 * $Id: AjaxAction.java,v 1.11 2007/09/09 07:57:42 daniel Exp $
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

package org.operamasks.faces.component.ajax;

import javax.faces.context.FacesContext;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.event.ActionEvent;
import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import java.util.Map;

import org.operamasks.faces.render.html.HtmlRenderer;

public class AjaxAction extends UICommand
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxAction";

    public AjaxAction() {
        setRendererType("org.operamasks.faces.AjaxAction");
    }

    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            event
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        event = (String)values[i++];
    }

    public void decode(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }

        String clientId = this.getClientId(context);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        if (paramMap.containsKey(clientId)) {
            ActionEvent event = new ActionEvent(this);
            this.queueEvent(event);
        }
    }

    /**
     * This method is called after this component is just constructed. Attach
     * the declared event type to the parent componnet.
     */
    public void attachEvent(String event, UIComponent parent) {
        ValueExpression binding = parent.getValueExpression(event);
        if ((binding != null) && (binding instanceof EventHandlerGenerator)) {
            // already bound
            ((EventHandlerGenerator)binding).setAction(this);
            return;
        }

        // Create a value expression to generate evenet handler
        String userEvent;
        try {
            userEvent = (String)parent.getAttributes().remove(event);
        } catch (IllegalArgumentException ex) {
            // property descriptor is not null, the component has real event property, set it to null.
            userEvent = (String)parent.getAttributes().get(event);
            parent.getAttributes().put(event, null);
        }

        EventHandlerGenerator ve = new EventHandlerGenerator(this, userEvent);
        ve.setLiteralText(false); // this will add handler into value bindings
        parent.setValueExpression(event, ve);
        ve.setLiteralText(true);  // this will tell ajax response writer no to write dynamic value
    }

    private static final class EventHandlerGenerator extends ValueExpression {
        private static final long serialVersionUID = -5718942236759049948L;

        private transient AjaxAction action;
        private String actionId;
        private String userEvent;
        private boolean isLiteralText;

        public EventHandlerGenerator(AjaxAction action, String userEvent) {
            setAction(action);

            if (userEvent != null) {
                userEvent = userEvent.trim();
                if (userEvent.length() != 0 && !userEvent.endsWith(";")) {
                    userEvent += ";";
                }
            }
            this.userEvent = userEvent;
        }

        public void setAction(AjaxAction action) {
            this.action = action;
            this.actionId = action.getClientId(FacesContext.getCurrentInstance());
        }

        public String getEventHandler() {
            if (this.action == null) {
                try {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    UIComponent comp = ctx.getViewRoot().findComponent(this.actionId);
                    if ((comp != null) && (comp instanceof AjaxAction)) {
                        this.action = (AjaxAction)comp;
                    } else {
                        return null;
                    }
                } catch (IllegalArgumentException e) {
                    // could not found action
                    return null;
                }
            }

            FacesContext context = FacesContext.getCurrentInstance();
            UIForm form = HtmlRenderer.getParentForm(this.action);
            String handler = String.format(
                "OM.ajax.action(%s,null,'%s',%b);return true;",
                ((form == null) ? "null" : "'" + form.getClientId(context) + "'"),
                this.action.getClientId(context),
                this.action.isImmediate()
            );

            if (this.userEvent != null) {
                handler = this.userEvent + handler;
            }
            return handler;
        }

        public Object getValue(ELContext context) {
            return getEventHandler();
        }

        public void setValue(ELContext context, Object value) {
            throw new PropertyNotWritableException();
        }

        public boolean isReadOnly(ELContext context) {
            return true;
        }

        public Class<?> getType(ELContext context) {
            return String.class;
        }

        public Class<?> getExpectedType() {
            return String.class;
        }

        public String getExpressionString() {
            return getEventHandler();
        }

        public boolean equals(Object obj) {
            return this == obj;
        }

        public int hashCode() {
            return System.identityHashCode(this);
        }

        public boolean isLiteralText() {
            return this.isLiteralText;
        }

        public void setLiteralText(boolean isLiteralText) {
            this.isLiteralText = isLiteralText;
        }
    }
}
