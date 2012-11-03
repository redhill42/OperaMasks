/*
 * $Id: SimplePool.java,v 1.4 2007/07/02 07:37:54 jacky Exp $
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

/**
 * A simple object pool.
 */
public class SimplePool<T>
{
    private Object pool[];
    private int size;
    private int current;
    private Object lock;

    public SimplePool(int size) {
	this.size = size;
	current = size;
	pool = new Object[size];
	lock = new Object();
    }

    /**
     * Returns the size of the pool.
     */
    public int getSize() {
	return size;
    }

    /**
     * Add the object to the pool, returns <code>false</code> if the pool is
     * full.
     */
    public boolean put(T o) {
	synchronized (lock) {
	    if (current > 0) {
		pool[--current] = o;
		return true;
	    }
	}
	return false;
    }

    /**
     * Get an object from the pool, returns <code>null</code> if the pool is
     * empty.
     */
    @SuppressWarnings("unchecked")
    public T get() {
	synchronized (lock) {
	    if (current < size) {
		return (T)pool[current++];
	    }
	}
	return null;
    }
}
