/*
 * $Id: ConverterFactory.java,v 1.2 2007/10/26 16:30:11 daniel Exp $
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

import javax.faces.convert.Converter;

/**
 * Responsible to create converter instance.
 */
public interface ConverterFactory
{
    /**
     * <p>Create a new instance of Converter.</p>
     *
     * <p>If the <code>Converter</code> has a single argument constructor that
     * accepts a <code>Class</code>, instantiate the <code>Converter</code>
     * using that constructor, passing the argument <code>targetClass</code> as
     * the sole argument.  Otherwise, simply use the zero-argument constructor.
     * </p>
     *
     * @param targetClass Target class for which to return a {@link Converter}
     * @return a new Converter instance
     *
     * @throws FacesException if the {@link Converter} cannot be created
     */
    public Converter createConverter(Class targetClass);
}
