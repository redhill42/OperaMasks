/*
 * $Id: ActionListenerImpl.java,v 1.4 2008/03/10 08:35:18 lishaochuan Exp $
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

import javax.faces.event.ActionListener;
import javax.faces.event.ActionEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.context.FacesContext;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.ActionSource2;
import javax.faces.application.NavigationHandler;
import javax.faces.FacesException;

import javax.el.MethodExpression;
import javax.faces.el.MethodBinding;

import org.operamasks.faces.component.action.Action;
import org.operamasks.faces.component.action.ActionSupport;

public class ActionListenerImpl implements ActionListener
{
    private static final Object[] NO_ARGS = new Object[0];

    public void processAction(ActionEvent event)
        throws AbortProcessingException
    {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = event.getComponent();

        String fromAction = null, outcome = null;
        // if exists ActionBinding, first perform actionBinding method
        if (component instanceof ActionSupport) {
        	Action action = ((ActionSupport)component).getActionBinding();
        	if (action != null) {
            	org.operamasks.faces.component.action.ActionEvent evt = 
            		new org.operamasks.faces.component.action.ActionEvent(component, context, event.getPhaseId());
        		action.processAction(evt);
        	}
        }

        // Invoke the action method associated to the action source
        if (component instanceof ActionSource2) {
            MethodExpression binding = ((ActionSource2)component).getActionExpression();
            if (binding != null) {
                fromAction = binding.getExpressionString();
                try {
                    Object result = binding.invoke(context.getELContext(), NO_ARGS);
                    if (result != null) {
                        outcome = result.toString();
                    }
                } catch (Exception ex) {
                    throw new FacesException(fromAction + ": " + ex.getMessage(), ex);
                }
            }
        } else {
            @SuppressWarnings("deprecation")
            MethodBinding binding = ((ActionSource)component).getAction();
            if (binding != null) {
                fromAction = binding.getExpressionString();
                try {
                    Object result = binding.invoke(context, NO_ARGS);
                    if (result != null) {
                        outcome = result.toString();
                    }
                } catch (Exception ex) {
                    throw new FacesException(fromAction + ": " + ex.getMessage(), ex);
                }
            }
        }

        // Perform the navigation handling
        NavigationHandler navHandler = context.getApplication().getNavigationHandler();
        navHandler.handleNavigation(context, fromAction, outcome);

        // Skip remaining phase in the lifecycle
        context.renderResponse();
    }
}
