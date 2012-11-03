/*
 * $Id: DrawImagePhaseListener.java,v 1.8 2008/04/29 05:21:13 lishaochuan Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseEvent;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DrawImagePhaseListener implements PhaseListener
{
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        Object responseObj = context.getExternalContext().getResponse();
        if (!(responseObj instanceof HttpServletResponse)) {
            return;
        }

        DrawImageHelper info = DrawImageHelper.restore(context);
        if (info == null) {
            return;
        }
        if(Boolean.FALSE.equals(info.getComponent().isNeedRefresh())){
            return;
        }

        HttpServletResponse response = (HttpServletResponse)responseObj;
        response.reset();
        response.setContentType(info.getType());
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            info.encode(context, out);
            response.getOutputStream().write(out.toByteArray());
            context.responseComplete();
        } catch (IOException ex) {
            throw new FacesException(ex);
        }
    }

    public void afterPhase(PhaseEvent event) {}
}
