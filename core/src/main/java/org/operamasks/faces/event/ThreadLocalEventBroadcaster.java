/*
 * $Id:
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A threadlocal event broadcaster to handle events limited to current thread
 * @author patrick
 * 
 * TODO Integrate this class with {@link EventBroadcaster}
 */
public class ThreadLocalEventBroadcaster {
    
    private static final String ALL_EVENT_TYPE = "*";
    
    private Map<String, ArrayList<ModelEventListener>> listeners = new HashMap<String, ArrayList<ModelEventListener>>();
    
    private static ThreadLocal<ThreadLocalEventBroadcaster> instance = new ThreadLocal<ThreadLocalEventBroadcaster>() {
        protected ThreadLocalEventBroadcaster initialValue() {return null;}
    };
    
    public static ThreadLocalEventBroadcaster getInstance() {
        ThreadLocalEventBroadcaster currentIns = instance.get();
        if (currentIns == null) {
            currentIns = new ThreadLocalEventBroadcaster();
        }
        return currentIns;
    }
    
    public static void setInstance(ThreadLocalEventBroadcaster broadcaster) {
        instance.set(broadcaster);
    }
    
    private ThreadLocalEventBroadcaster() {
        setInstance(this);
    }
    
    public void addEventListenerOnce(String eventType, ModelEventListener listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<ModelEventListener>());
        }
        ArrayList<ModelEventListener> l = listeners.get(eventType);
        if (!l.contains(listener))
            l.add(listener);
    }
    
    public void addEventListener(String eventType, ModelEventListener listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<ModelEventListener>());
        }
        listeners.get(eventType).add(listener);
    }
    
    public void removeEventListener(String eventType, ModelEventListener listener) {
        ArrayList<ModelEventListener> l = listeners.get(eventType);
        if (l == null) return;
        l.remove(listener);
    }
    
    public void removeEventType(String eventType) {
        listeners.remove(eventType);
    }
    
    public void clear() {
        listeners.clear();
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
            List<ModelEventListener> list = listeners.get(key);
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
