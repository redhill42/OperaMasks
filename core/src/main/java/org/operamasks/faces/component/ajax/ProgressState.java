/*
 * $Id: ProgressState.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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

public final class ProgressState implements java.io.Serializable
{
    /**
     * The ordinal value of this object.
     */
    private final int ordinal;

    /**
     * The name of this object.
     */
    private final String name;

    public static final int _RUNNING   = 0;
    public static final int _STOPPED   = 1;
    public static final int _PAUSED    = 2;
    public static final int _COMPLETED = 3;
    public static final int _FAILED    = 4;

    public static final ProgressState RUNNING   = new ProgressState(_RUNNING, "running");
    public static final ProgressState STOPPED   = new ProgressState(_STOPPED, "stopped");
    public static final ProgressState PAUSED    = new ProgressState(_PAUSED, "paused");
    public static final ProgressState COMPLETED = new ProgressState(_COMPLETED, "completed");
    public static final ProgressState FAILED    = new ProgressState(_FAILED, "failed");

    /**
     * Create a named progress action type with a given ordinal value.
     */
    protected ProgressState(int ordinal, String name) {
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

    private static final ProgressState[] VALUES = {
        RUNNING, STOPPED, PAUSED, COMPLETED, FAILED
    };

    public static ProgressState valueOf(String name) {
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
