/*
 * $Id: ChartResourceService.java,v 1.3 2007/07/02 07:37:45 jacky Exp $
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

package org.operamasks.faces.render.graph;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

import org.operamasks.faces.render.resource.HttpResourceService;

public class ChartResourceService extends HttpResourceService
{
    public ChartResourceService() {
        super("chart");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        FacesContext context = FacesContext.getCurrentInstance();
        context.responseComplete();

        String uri = request.getRequestURI();
        String filename = uri.substring(uri.lastIndexOf('/')+1);

        ChartKeeper keeper = ChartKeeper.getInstance(context);
        byte[] data = keeper.retrieve(filename);

        if (data == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ServletContext sctx = (ServletContext)context.getExternalContext().getContext();
        String type = sctx.getMimeType(filename);
        if (type != null) {
            response.setContentType(type);
        }
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
    }
}
