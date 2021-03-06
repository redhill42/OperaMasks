/*
 * $Id: ManagedBeanNamespace.java,v 1.1 2007/08/27 21:49:33 daniel Exp $
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
package org.operamasks.faces.application;

public final class ManagedBeanNamespace
{
    private final String fqn;

    public ManagedBeanNamespace(String fqn) {
        this.fqn = fqn;
    }

    public String getFQN() {
        return this.fqn;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ManagedBeanNamespace) {
            return this.fqn.equals(((ManagedBeanNamespace)obj).fqn);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.fqn.hashCode();
    }
}
