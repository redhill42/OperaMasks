/*
 * $Id: CompositeActionListener.java,v 1.2 2007/09/28 00:04:25 daniel Exp $
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

import javax.faces.event.ActionListener;
import javax.faces.event.ActionEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseId;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.ArrayList;

public final class CompositeActionListener implements ActionListener, StateHolder
{
    private List<ActionListener> listeners = new ArrayList<ActionListener>();
    private PhaseId phaseId;
    private boolean isTransient = false;

    public PhaseId getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addActionListener(ActionListener listener) {
        this.listeners.add(listener);
    }

    public void processAction(ActionEvent event)
        throws AbortProcessingException
    {
        if (this.listeners.size() > 0) {
            for (ActionListener listener : this.listeners) {
                listener.processAction(event);
            }
        }
    }

    public Object saveState(FacesContext context) {
        // no need to save state, validators restored by injector
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        // no need to save state, validators restored by injector
    }

    public boolean isTransient() {
        return this.isTransient;
    }

    public void setTransient(boolean newTransientValue) {
        this.isTransient = newTransientValue;
    }
}
