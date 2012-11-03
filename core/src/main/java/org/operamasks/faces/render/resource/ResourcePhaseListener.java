/*
 * $Id: ResourcePhaseListener.java,v 1.6 2007/07/02 07:38:03 jacky Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseEvent;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * The PhaseListener that handle resource request.
 */
public class ResourcePhaseListener implements PhaseListener
{
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent event) {}

    public void afterPhase(PhaseEvent event) {
        try {
            FacesContext context = event.getFacesContext();
            ResourceServiceManager.getInstance(context).service(context);
        } catch (IOException ex) {
            throw new FacesException(ex);
        }
    }
}
