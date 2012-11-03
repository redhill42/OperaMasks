/*
 * $Id: AdaptingValidatorFactory.java,v 1.2 2007/10/28 08:03:16 daniel Exp $
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

package org.operamasks.faces.binding.factories;

import java.lang.reflect.Method;
import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.binding.impl.ValidatorAdapter;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.application.impl.DefaultTargetCreator;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.TargetCreator;
import org.operamasks.faces.annotation.Validate;
import static org.operamasks.resources.Resources.*;

public class AdaptingValidatorFactory implements ValidatorFactory
{
    private TargetCreator creator;
    private Method validateMethod;

    public AdaptingValidatorFactory(TargetCreator creator) {
        this.creator = creator;
    }

    public AdaptingValidatorFactory(Class<?> validatorClass) {
        this.creator = new DefaultTargetCreator(validatorClass);
    }

    public Validator createValidator() {
        Object target = creator.createTarget(null);

        if (target instanceof Validator) {
            return (Validator)target;
        } else {
            return createValidatorAdapter(target);
        }
    }

    private Validator createValidatorAdapter(Object target) {
        if (this.validateMethod == null) {
            this.validateMethod = getValidateMethod(this.creator.getTargetClass());
        }

        ModelBean bean = ModelBean.wrap(target);
        Method method = this.validateMethod;
        Validate meta = method.getAnnotation(Validate.class);
        return new ValidatorAdapter(bean, method, meta.script(), meta.message());
    }

    private Method getValidateMethod(Class targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Validate.class)) {
                if (!checkValidateMethod(method)) {
                    throw new FacesException(_T(MVB_INVALID_VALIDATE_METHOD, method.getName()));
                } else {
                    return method;
                }
            }
        }

        throw new FacesException(_T(MVB_VALIDATE_METHOD_NOT_FOUND, targetClass.getName()));
    }

    private boolean checkValidateMethod(Method method) {
        Class[] params = method.getParameterTypes();
        if (params.length == 1) {
            return true;
        } else if (params.length == 3) {
            return params[0] == FacesContext.class
                && params[1] == UIComponent.class;
        } else {
            return false;
        }
    }
}
