/*
 * $Id: DataItemActionListener.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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
import javax.faces.component.UIData;
import javax.faces.FacesException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.operamasks.faces.component.ajax.AjaxAction;
import org.operamasks.faces.binding.ModelBean;

class DataItemActionListener implements ActionListener
{
    private UIData data;
    private Method method;

    public DataItemActionListener(UIData data, Method method) {
        this.data = data;
        this.method = method;
    }

    public void processAction(ActionEvent event)
        throws AbortProcessingException
    {
        Object target = null;
        if (data.isRowAvailable()) {
            target = data.getRowData();
        }

        if (target != null || Modifier.isStatic(this.method.getModifiers())) {
            ModelBean bean = ModelBean.wrap(target);
            Class[] paramTypes = this.method.getParameterTypes();

            try {
                if (paramTypes.length == 0) {
                    bean.invoke(this.method);
                } else if (paramTypes.length == 1) {
                    if (event.getComponent() instanceof AjaxAction) {
                        // adjust event source
                        PhaseId phaseId = event.getPhaseId();
                        event = new ActionEvent(event.getComponent().getParent());
                        event.setPhaseId(phaseId);
                    }
                    bean.invoke(this.method, event);
                }
            } catch (AbortProcessingException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }
    }
}
