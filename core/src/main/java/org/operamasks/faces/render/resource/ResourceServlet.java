/*
 * $Id: ResourceServlet.java,v 1.5 2007/07/02 07:38:04 jacky Exp $
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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.FactoryFinder;
import javax.faces.FacesException;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.io.IOException;

/**
 * This class is a workaround for application server that does not support
 * direct resource resolution using PhaseListener. This servlet should
 * mapped to the URL pattern "/_global/*".
 */
public class ResourceServlet extends HttpServlet
{
    // Factory for FacesContext instances.
    private FacesContextFactory facesContextFactory;

    // The Lifecycle instance to use for request processing.
    // Only needed to initialize FacesContext.
    private Lifecycle lifecycle;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        try {
            facesContextFactory = (FacesContextFactory)
                FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

            LifecycleFactory lifecycleFactory = (LifecycleFactory)
                FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        } catch (FacesException ex) {
            throw new UnavailableException(ex.getMessage());
        }

        if (facesContextFactory == null || lifecycle == null) {
            throw new UnavailableException("No faces context available.");
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        FacesContext context = facesContextFactory.getFacesContext(
            getServletContext(), request, response, lifecycle);

        try {
            ResourceServiceManager.getInstance(context).service(context);
        } finally {
            context.release();
        }
    }
}
