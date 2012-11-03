/*
 * $Id: ProgressAction.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.component.ajax;

public final class ProgressAction implements java.io.Serializable
{
    /**
     * The ordinal value of this object.
     */
    private final int ordinal;

    /**
     * The name of this object.
     */
    private final String name;

    public static final int _START  = 0;
    public static final int _STOP   = 1;
    public static final int _PAUSE  = 2;
    public static final int _RESUME = 3;
    public static final int _POLL   = 4;

    public static final ProgressAction START  = new ProgressAction(_START, "start");
    public static final ProgressAction STOP   = new ProgressAction(_STOP, "stop");
    public static final ProgressAction PAUSE  = new ProgressAction(_PAUSE, "pause");
    public static final ProgressAction RESUME = new ProgressAction(_RESUME, "resume");
    public static final ProgressAction POLL   = new ProgressAction(_POLL, "poll");

    /**
     * Create a named progress action type with a given ordinal value.
     */
    protected ProgressAction(int ordinal, String name) {
        this.ordinal = ordinal;
        this.name = name;
    }

    /**
     * Return the ordinal value of this object.
     */
    public final int ordinal() {
        return ordinal;
    }

    /**
     * Return the string name of this object.
     */
    public final String name() {
        return name;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object other) {
        return this == other;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    private static final ProgressAction[] VALUES = {
        START, STOP, PAUSE, RESUME, POLL
    };

    public static ProgressAction valueOf(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            if (name.equals(VALUES[i].name()))
                return VALUES[i];
        }
        throw new IllegalArgumentException("Invalid constant name");
    }

    private Object readResolve() {
        return valueOf(this.name);
    }
}
