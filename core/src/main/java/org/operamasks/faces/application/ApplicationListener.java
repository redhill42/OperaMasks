/*
 * $Id: ApplicationListener.java,v 1.1 2007/09/06 14:35:39 daniel Exp $
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

import java.util.EventListener;

/**
 * Implementations of this interface receive notifications about changes to the
 * per-web-application singleton object for JavaServer Faces.
 */
public interface ApplicationListener extends EventListener
{
    /**
     * Notification that the JSF application is created but before initialized.
     */
    public void applicationCreated(ApplicationEvent event);

    /**
     * Notification that the JSF application initialization process is starting.
     */
    public void applicationInitialized(ApplicationEvent event);

    /**
     * Notification that the JSF application is about to be shut down.
     */
    public void applicationDestroyed(ApplicationEvent event);
}
