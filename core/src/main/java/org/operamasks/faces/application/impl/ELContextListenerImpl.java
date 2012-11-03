/*
 * $Id: ELContextListenerImpl.java,v 1.1 2007/09/09 16:42:26 daniel Exp $
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

import javax.el.ELContextListener;
import javax.el.ELContextEvent;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

public class ELContextListenerImpl implements ELContextListener
{
    public void contextCreated(ELContextEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null)
            return;

        ELContext source = event.getELContext();
        source.putContext(FacesContext.class, context);

        ELContextListener[] listeners = context.getApplication().getELContextListeners();
        if (listeners != null) {
            for (ELContextListener l : listeners) {
                l.contextCreated(event);
            }
        }
    }
}
