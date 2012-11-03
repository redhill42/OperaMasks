/*
 * $Id: TargetCreator.java,v 1.1 2007/10/26 16:30:11 daniel Exp $
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

/**
 * The abstract factory interface used to create underlying target object.
 */
public interface TargetCreator
{
    /**
     * Create the underlying target object.
     *
     * @param arg the optional argument passed to the object constructor.
     * @return the target object.
     */
    public Object createTarget(Object arg);

    /**
     * Returns the target class.
     *
     * @return the target class
     */
    public Class<?> getTargetClass();
}
