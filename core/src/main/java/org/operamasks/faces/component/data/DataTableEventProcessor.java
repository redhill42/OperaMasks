/*
 * $Id: DataTableEventProcessor.java,v 1.1 2008/04/21 13:06:55 jacky Exp $
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
package org.operamasks.faces.component.data;

import java.util.List;

import org.operamasks.util.ConcurrentListMap;

/**
 * DataTable事件处理器
 *
 */
public class DataTableEventProcessor
{
    private ConcurrentListMap<DataTableEventType,DataTableListener> listeners =
        new ConcurrentListMap<DataTableEventType,DataTableListener>();

    public void addDataTableListener(DataTableEventType eventType,
            DataTableListener listener) {
        this.listeners.putIfAbsent(eventType, listener);
    }


    public void removeDataTableListener(DataTableEventType eventType,
            DataTableListener listener) {
        this.listeners.remove(eventType, listener);
    }
    
    public void broadcast(DataTableEventType eventType, DataTableEvent event) {
        List<DataTableListener> list = this.listeners.list(eventType);
        event.setEventType(eventType);
        if (list != null) {
            for (DataTableListener listener : list) {
                listener.handleEvent(event);
            }
        }
    }
}
