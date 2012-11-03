/*
 * $Id: GlobalResourceService.java,v 1.5 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.FacesException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class GlobalResourceService extends ResourceService
{
    public void service(FacesContext context)
        throws IOException
    {
        Object requestObj = context.getExternalContext().getRequest();
        if (!(requestObj instanceof HttpServletRequest))
            return;

        HttpServletRequest request = (HttpServletRequest)requestObj;
        String uri = request.getRequestURI();

        // Verify locale string
        String locale = request.getQueryString();
        if (locale != null) {
            if (locale.length() > 5 && locale.charAt(5) == '_')
                locale = locale.substring(0, 5); // strip variants
            if ((locale.length() != 2 && locale.length() != 5) ||
                (locale.length() == 5 && locale.charAt(2) != '_'))
                locale = null; // ignore invalid locale
        }

        // Return compressed resource if gzip supported by client
        boolean gzip = false;
        String header = request.getHeader("Accept-Encoding");
        if (header != null && header.indexOf("gzip") != -1)
            gzip = true;

        URL resource;
        try {
            ResourceManager rm = ResourceManager.getInstance(context);
            resource = rm.getLocalResource(uri, locale, gzip);
        } catch (FileNotFoundException ex) {
            context.responseComplete();

            Object response = context.getExternalContext().getResponse();
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse)response).sendError(404, ex.getMessage());
                return;
            } else {
                throw new FacesException(ex);
            }
        }

        if (resource != null) {
            context.responseComplete();
            sendResource(context, resource);
        }
    }

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static void sendResource(FacesContext context, URL resource)
        throws IOException
    {
        ServletContext sctx = (ServletContext)
            context.getExternalContext().getContext();
        HttpServletResponse response = (HttpServletResponse)
            context.getExternalContext().getResponse();

        String filename = resource.getFile();
        if (filename.endsWith(".gz")) {
            response.setHeader("Content-Encoding", "gzip");
            filename = filename.substring(0, filename.length()-3);
        }

        String type = sctx.getMimeType(filename);
        if (type == null)
            type = DEFAULT_MIME_TYPE;
        type += ";charset=" + DEFAULT_CHARSET;
        response.setContentType(type);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, 1, 1);
        response.setDateHeader("Last-Modified", calendar.getTimeInMillis());
        response.setHeader("Cache-Control", "max-age=8640000");
        

        InputStream in = resource.openStream();
        OutputStream out = response.getOutputStream();
        byte[] buf = new byte[8192];
        for (int n; (n = in.read(buf)) != -1; ) {
            out.write(buf, 0, n);
        }
        in.close();
    }
}
