/*
 * $Id: FacesContextFactoryImpl.java,v 1.4 2007/07/02 07:38:20 jacky Exp $
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

package org.operamasks.faces.context;

import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacesContextFactoryImpl extends FacesContextFactory
{
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
        throws FacesException
    {
        if (context == null || request == null || response == null || lifecycle == null) {
            throw new NullPointerException();
        }
        if (!(context instanceof ServletContext)) {
            throw new UnsupportedOperationException("non servlet context is not supported");
        }

        ExternalContext extCtx = new ServletExternalContext((ServletContext)context,
                                                            (HttpServletRequest)request,
                                                            (HttpServletResponse)response);
        return new FacesContextImpl(extCtx);
    }
}
