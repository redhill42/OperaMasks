/*
 * $Id: ApplicationEvent.java,v 1.1 2007/09/06 14:35:39 daniel Exp $
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
package org.operamasks.faces.application;

import javax.faces.application.Application;
import javax.servlet.ServletContext;
import java.util.EventObject;

/**
 * This is the event class for notifications about changes to the per-web-application
 * singleton object for JavaServer Faces.
 */
public class ApplicationEvent extends EventObject
{
    private final ServletContext context;

    /**
     * Construct a ApplicationEvent from the given Application object.
     *
     * @param source the Application that is sending the event.
     * @param context the servlet context associated with the event.
     */
    public ApplicationEvent(Application source, ServletContext context) {
        super(source);
        this.context = context;
    }

    /**
     * Return the Application that changed.
     *
     * @return the Application that sent the event.
     */
    public Application getApplication() {
        return (Application)this.getSource();
    }

    /**
     * Return the servlet context associated with the event.
     */
    public ServletContext getServletContext() {
        return this.context;
    }
}
