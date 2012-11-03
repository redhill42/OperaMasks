/*
 * $Id: HttpResourceService.java,v 1.3 2007/07/02 07:38:03 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class HttpResourceService extends ResourceService
{
    protected String serviceName;

    protected HttpResourceService(String serviceName) {
        if (serviceName == null)
            throw new NullPointerException();
        if (serviceName.length() == 0 || serviceName.indexOf('/') != -1)
            throw new IllegalArgumentException(serviceName);
        this.serviceName = serviceName;
    }

    public void service(FacesContext context)
        throws IOException
    {
        Object requestObj = context.getExternalContext().getRequest();
        Object responseObj = context.getExternalContext().getResponse();
        if (!(requestObj instanceof HttpServletRequest) ||
            !(responseObj instanceof HttpServletResponse)) {
            return;
        }

        HttpServletRequest request = (HttpServletRequest)requestObj;
        HttpServletResponse response = (HttpServletResponse)responseObj;
        String uri = request.getRequestURI();

        // For global resource service
        String viewId = ResourceManager.VIEW_ID_PREFIX + "/" + serviceName;
        if (uri.indexOf(viewId) != -1) {
            context.responseComplete();
            service(request, response);
            return;
        }

        // For skin resource service, the URL pattern is:
        //     /_global/skin/%skin-name%/%service-name%
        //
        // This type of resource URL is useful to reference resource
        // service from a skinnable css file.
        int pos = uri.indexOf(ResourceManager.SKIN_VIEW_ID);
        if (pos != -1) {
            pos = uri.indexOf('/', pos + ResourceManager.SKIN_VIEW_ID.length());
            if (pos == -1 || pos == uri.length() - 1)
                return;

            // skin skin name in uri
            pos = uri.indexOf('/', pos+1);
            if (pos == -1 || pos == uri.length() - 1)
                return;
            uri = uri.substring(pos+1);

            if (uri.equals(serviceName)) {
                // It's our service resource URL, handle it!
                context.responseComplete();
                service(request, response);
                return;
            }
        }
    }

    protected abstract void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException;
}
