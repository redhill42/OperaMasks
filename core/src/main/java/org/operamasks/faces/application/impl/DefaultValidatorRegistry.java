/*
 * $Id: DefaultValidatorRegistry.java,v 1.3 2007/10/26 16:30:11 daniel Exp $
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

package org.operamasks.faces.application.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.validator.Validator;
import org.operamasks.faces.application.ValidatorRegistry;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.TargetCreator;
import org.operamasks.faces.binding.factories.AdaptingValidatorFactory;
import static org.operamasks.resources.Resources.*;

public class DefaultValidatorRegistry extends ValidatorRegistry
{
    private Map<String,ValidatorFactory> registry;

    protected DefaultValidatorRegistry() {
        this.registry = new ConcurrentHashMap<String,ValidatorFactory>();
    }

    public ValidatorFactory createValidatorFactory(TargetCreator creator) {
        return new AdaptingValidatorFactory(creator);
    }

    public ValidatorFactory createValidatorFactory(Class<?> validatorClass) {
        return new AdaptingValidatorFactory(validatorClass);
    }

    public void addValidatorFactory(String validatorId, ValidatorFactory validatorFactory) {
        this.registry.put(validatorId, validatorFactory);
    }

    public ValidatorFactory getValidatorFactory(String validatorId) {
        return this.registry.get(validatorId);
    }
    
    public Validator createValidator(String validatorId) {
        ValidatorFactory validatorFactory = this.registry.get(validatorId);
        if (validatorFactory == null) {
            throw new FacesException(_T(JSF_NO_SUCH_VALIDATOR_ID, validatorId));
        }

        return validatorFactory.createValidator();
    }

    public Iterator<String> getValidatorIds() {
        return this.registry.keySet().iterator();
    }
}
