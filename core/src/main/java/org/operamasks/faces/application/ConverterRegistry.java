/*
 * $Id: ConverterRegistry.java,v 1.3 2007/10/26 16:30:11 daniel Exp $
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
import java.util.Iterator;

import org.operamasks.faces.application.impl.DefaultConverterRegistry;

/**
 * This class contains centre registry for Converters.
 */
public abstract class ConverterRegistry
{
    private static final String KEY = ConverterRegistry.class.getName();

    /**
     * Returns a singleton instance of ValidatorRegistry.
     */
    public static final ConverterRegistry getInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        return assoc.getSingleton(KEY, DefaultConverterRegistry.class);
    }

    /**
     * <p>Create a {@link ConverterFactory} based on the given {@link TargetCreator}.
     *
     * @param creator the TargetFactory that create underlying converter target object
     * @return the instance of converter factory
     */
    public abstract ConverterFactory createConverterFactory(TargetCreator creator);

    /**
     * <p>Create a {@link ConverterFactory} based on the given converter class.
     *
     * @param converterClass the converter class
     * @return the instance of converter factory
     */
    public abstract ConverterFactory createConverterFactory(Class<?> converterClass);

    /**
     * <p>Register a new mapping of converter id to the corresponding
     * {@link ConvererFactory}. This allows subsequent calls to
     * <code>createConverter()</code> to serve as a factory for
     * {@link Converter} instances.</p>
     *
     * @param converterId The converter id to be registered
     * @param converterFactory The converter factory to be registered
     */
    public abstract void addConverterFactory(String converterId, ConverterFactory converterFactory);

    /**
     * <p>Register a new converter factory that is capable of performing
     * conversions for the specified target class.</p>
     *
     * @param targetClass The class for which this converter is registered
     * @param converterFactory The converter factory to be registered
     */
    public abstract void addConverterFactory(Class targetClass, ConverterFactory converterFactory);

    /**
     * Get a {@link ConverterFactory} that previously registered with addConverterFactory().
     *
     * @param converterId the converter id
     * @return the registered ConverterFactory
     */
    public abstract ConverterFactory getConverterFactory(String converterId);

    /**
     * Get a {@link ConverterFactory} that previously registered with addConverterFactory().
     *
     * @param converterId the converter id
     * @return the registered ConverterFactory
     */
    public abstract ConverterFactory getConverterFactory(Class targetClass);
    
    /**
     * <p>Instantiate and return a new {@link Converter} instance of the
     * class specified by a previous call to <code>addConverter()</code>
     * for the specified converter id. If there is no such registration
     * for this converter id, return <code>null</code>.</p>
     *
     * @param converterId the converter id for which to create and
     *  return a new {@link Converter} instance.
     *
     * @throws FacesException if the {@link Converter} cannot be created
     * @throws NullPointerException if <code>converterId</code> is <code>null</code>
     */
    public abstract Converter createConverter(String converterId);

    /**
     * <p>Instantiate and return a new {@link Converter} instance of the
     * class that has registered itself as capable of performing conversions
     * for objects of the specified type. If no such {@link Converter} class
     * can be identified, return <code>null</code>.</p>
     *
     * <p>To locate an appropriate {@link Converter} class, the following
     * algorithm is performed, stopping as soon as an appropriate {@link
     * Converter} class is found:</p>
     * <ul>
     * <li>Locate a {@link Converter} registered for the target class itself.
     *     </li>
     * <li>Locate a {@link Converter} registered for interfaces that are
     *     implemented by the target class (directly or indirectly).</li>
     * <li>Locate a {@link Converter} registered for the superclass (if any)
     *     of the target class, recursively working up the inheritance
     *     hierarchy.</li>
     * </ul>
     *
     * @param targetClass Target class for which to return a {@link converter}
     *
     * @throws FacesException if the {@link Converter} cannot be created
     * @throws NullPointerException if <code>targetClass</code> is <code>null</code>
     */
    public abstract Converter createConverter(Class targetClass);

    /**
     * <p>Return an <code>Iterator</code> over the set of currently registered
     * converter ids for this <code>ConverterRegistry</code>.</p>
     */
    public abstract Iterator<String> getConverterIds();

    /**
     * <p>Return an <code>Iterator</code> over the set of <code>Class</code>
     * instances for which {@link Converter} classes have been explicitly
     * registered.</p>
     */
    public abstract Iterator<Class> getConverterTypes();
}
