/*
 * $Id: EventBroadcaster.java,v 1.3 2007/10/04 08:34:57 daniel Exp $
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

import java.util.List;
import org.operamasks.util.ConcurrentListMap;
import org.operamasks.faces.application.ApplicationAssociate;

public class EventBroadcaster
{
    private static final String ALL_EVENT_TYPE = "*";

    public static EventBroadcaster getInstance() {
        return ApplicationAssociate.getInstance().getSingleton(EventBroadcaster.class);
    }

    private EventBroadcaster() {}

    private ConcurrentListMap<String,ModelEventListener> listeners =
        new ConcurrentListMap<String, ModelEventListener>();

    public void addEventListener(String eventType, ModelEventListener listener) {
        this.listeners.putIfAbsent(eventType, listener);
    }

    public void removeEventListener(String eventType, ModelEventListener listener) {
        this.listeners.remove(eventType, listener);
    }

    public void broadcast(Object source, String eventType, Object... params) {
        if (eventType == null || eventType.length() == 0 || eventType.equals(ALL_EVENT_TYPE)) {
            throw new IllegalArgumentException(eventType);
        }

        ModelEvent event = new ModelEvent(source, eventType, params);
        String key = eventType;

        do {
            // broadcast to listeners that interest on the given event type
            // in the hierarchical fasion.
            List<ModelEventListener> list = this.listeners.list(key);
            if (list != null) {
                for (ModelEventListener listener : list) {
                    listener.processModelEvent(event);
                }
            }

            // move up in the hierarchy until reachs top level type.
            int sep = key.lastIndexOf('.');
            if (sep != -1) {
                key = key.substring(0, sep);
            } else if (key.equals(ALL_EVENT_TYPE)) {
                break;
            } else {
                key = ALL_EVENT_TYPE;
            }
        } while (true);
    }
}
