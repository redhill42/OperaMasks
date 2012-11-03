/*
 * $Id: ModelEventListener.java,v 1.2 2007/09/25 22:06:35 daniel Exp $
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
package org.operamasks.faces.event;

import java.util.EventListener;

/**
 * A listener interface for receiving {@link ModelEvent}s. A class that
 * is interested in receiving such events implements this interface, and then
 * registers itself with the {@link EventBroadcaster}  singleton object, 
 * by calling <code>addEventListener()</code>.
 */
public interface ModelEventListener extends EventListener
{
    /**
     * Invoked when the action described by the specified
     * {@link ModelEvent} occurs.
     *
     * @param event the {@link ModelEvent} that has occurred
     */
    public void processModelEvent(ModelEvent event);
}
