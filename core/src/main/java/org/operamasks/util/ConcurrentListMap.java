/*
 * $Id: ConcurrentListMap.java,v 1.1 2007/09/25 13:36:19 daniel Exp $
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
package org.operamasks.util;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/**
 * A simple concurrent map that contains list of values.
 */
public class ConcurrentListMap<K,V>
{
    private ConcurrentMap<K, CopyOnWriteArrayList<V>> map;

    public ConcurrentListMap() {
        this.map = new ConcurrentHashMap<K, CopyOnWriteArrayList<V>>();
    }

    public void put(K key, V value) {
        addList(key).add(value);
    }

    public void putIfAbsent(K key, V value) {
        addList(key).addIfAbsent(value);
    }

    private CopyOnWriteArrayList<V> addList(K key) {
        CopyOnWriteArrayList<V> list = this.map.get(key);
        if (list == null) {
            list = new CopyOnWriteArrayList<V>();
            CopyOnWriteArrayList<V> prev = this.map.putIfAbsent(key, list);
            if (prev != null) {
                list = prev;
            }
        }
        return list;
    }

    public void remove(K key, V value) {
        CopyOnWriteArrayList<V> list = this.map.get(key);
        if (list != null) {
            list.remove(value);
            if (list.isEmpty()) {
                this.map.remove(key, list);
            }
        }
    }

    public void removeAll() {
        this.map.clear();
    }

    public List<V> list(K key) {
        return this.map.get(key);
    }
}
