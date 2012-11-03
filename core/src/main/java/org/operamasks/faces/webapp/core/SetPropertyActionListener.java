/*
 * $Id: SetPropertyActionListener.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

package org.operamasks.faces.webapp.core;

import javax.faces.event.ActionListener;
import javax.faces.event.ActionEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.el.ELException;

public class SetPropertyActionListener implements ActionListener, StateHolder
{
    private ValueExpression target;
    private ValueExpression value;

    private boolean _transient;

    public SetPropertyActionListener() {
        // default constructor required by StateHolder
    }
    
    public SetPropertyActionListener(ValueExpression target, ValueExpression value) {
        this.target = target;
        this.value = value;
    }

    public void processAction(ActionEvent event) throws AbortProcessingException {
        try {
            ELContext context = FacesContext.getCurrentInstance().getELContext();
            target.setValue(context, value.getValue(context));
        } catch (ELException ex) {
            throw new AbortProcessingException(ex);
        }
    }

    public void setTransient(boolean _transient) {
        this._transient = _transient;
    }

    public boolean isTransient() {
        return _transient;
    }

    public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = target;
        state[1] = value;
        return state;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] stateArray = (Object[])state;
        target = (ValueExpression)stateArray[0];
        value = (ValueExpression)stateArray[1];
    }
}
