/*
 * $Id: EventTypes.java,v 1.3 2008/04/24 05:48:47 patrick Exp $
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

public final class EventTypes
{
    private EventTypes() {}

    public static final String MANAGED_BEAN_CREATED =
        "org.operamasks.faces.MANAGED_BEAN_CREATED";

    public static final String MANAGED_BEAN_DESTROYED =
        "org.operamasks.faces.MANAGED_BEAN_DESTROYED";
    
    public static final String BEFORE_RENDER_VIEW =
        "org.operamasks.faces.BEFORE_RENDER_VIEW";
}
