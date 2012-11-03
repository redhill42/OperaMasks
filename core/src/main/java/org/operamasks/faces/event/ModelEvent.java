/*
 * $Id: ModelEvent.java,v 1.1 2007/09/25 13:36:19 daniel Exp $
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

import java.util.EventObject;

public class ModelEvent extends EventObject
{
    private String eventType;
    private Object[] params;

    public ModelEvent(Object source, String eventType, Object[] params) {
        super(source);
        this.eventType = eventType;
        this.params = params;
    }

    public String getEventType() {
        return this.eventType;
    }

    public Object[] getParameters() {
        return this.params;
    }
}
