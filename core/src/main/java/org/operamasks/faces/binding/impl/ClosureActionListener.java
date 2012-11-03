/*
 * $Id: ClosureActionListener.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.faces.component.ajax.AjaxAction;

class ClosureActionListener implements ActionListener
{
    private final Closure closure;

    ClosureActionListener(Closure closure) {
        this.closure = closure;
    }

    public void processAction(ActionEvent event) {
        ELContext ctx = FacesContext.getCurrentInstance().getELContext();

        if (closure.arity(ctx) == 0) {
            closure.call(ctx);
        } else {
            if (event.getComponent() instanceof AjaxAction) {
                // adjust event source
                PhaseId phaseId = event.getPhaseId();
                event = new ActionEvent(event.getComponent().getParent());
                event.setPhaseId(phaseId);
            }
            closure.call(ctx, event);
        }
    }
}
