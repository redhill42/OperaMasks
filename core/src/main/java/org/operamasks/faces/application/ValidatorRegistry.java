/*
 * $Id: ValidatorRegistry.java,v 1.3 2007/10/26 16:30:11 daniel Exp $
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

import javax.faces.FacesException;
import javax.faces.validator.Validator;
import java.util.Iterator;

import org.operamasks.faces.application.impl.DefaultValidatorRegistry;

/**
 * This class contains centre registry for Validators.
 */
public abstract class ValidatorRegistry
{
    private static final String KEY = ValidatorRegistry.class.getName();

    /**
     * Returns a singleton instance of ValidatorRegistry.
     */
    public static final ValidatorRegistry getInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        return assoc.getSingleton(KEY, DefaultValidatorRegistry.class);
    }

    /**
     * <p>Create a {@link ValidatorFactory} based on the given {@link TargetCreator}.
     *
     * @param targetCreator the TargetFactory that create underlying validator target object
     * @return the instance of validator factory
     */
    public abstract ValidatorFactory createValidatorFactory(TargetCreator creator);

    /**
     * <p>Create a {@link ValidatorFactory} based on the given validator class.
     *
     * @param validatorClass the validator class
     * @return the instance of validator factory
     */
    public abstract ValidatorFactory createValidatorFactory(Class<?> validatorClass);

    /**
     * <p>Register a new mapping of validator id to the name of the corresponding
     * {@link ValidatorFactory}.  This allows subsequent calls to
     * <code>createValidator()</code> to serve as a factory for {@link Validator}
     * instances.</p>
     *
     * @param validatorId The validator id to be registered
     * @param validatorFactory The validator factory to be registered.
     */
    public abstract void addValidatorFactory(String validatorId, ValidatorFactory validatorFactory);

    /**
     * Get a {@link ValidatorFactory} that previously registered with addValidator().
     *
     * @param validatorId the validator id
     * @return the registered ValidatorFactory
     */
    public abstract ValidatorFactory getValidatorFactory(String validatorId);

    /**
     * Instantiate and return a new {@link Validator} instance of the
     * validator factory specified by a previous call to <code>addValidator()</code>
     * for the specified validator id.</p>
     *
     * @param validatorId The validator id for which to create and
     *  return a new {@link Validator} instance
     *
     * @throws FacesException if a {@link Validator} of the
     *  specified id cannot be created.
     * @throws NullPointerException if <code>validatorId</code>
     *  is <code>null</code>
     */
    public abstract Validator createValidator(String validatorId)
        throws FacesException;

    /**
     * <p>Return an <code>Iterator</code> over the set of currently
     * registered validator ids for this <code>ValidatorRegistry</code>.</p>
     */
    public abstract Iterator<String> getValidatorIds();
}
