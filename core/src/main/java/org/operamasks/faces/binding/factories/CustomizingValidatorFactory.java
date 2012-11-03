/*
 * $Id: CustomizingValidatorFactory.java,v 1.3 2008/03/10 08:35:18 lishaochuan Exp $
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

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.validator.Validator;

import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.impl.ValidatorAdapter;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;
import org.operamasks.el.eval.Coercion;
import static org.operamasks.resources.Resources.*;

public class CustomizingValidatorFactory implements ValidatorFactory
{
    private ValidatorFactory delegate;
    private Map<String,Object> properties;

    private static Logger log = Logger.getLogger(ValidatorFactory.class.getName());

    public CustomizingValidatorFactory(ValidatorFactory delegate) {
        this.delegate = delegate;
        this.properties = new HashMap<String, Object>();
    }

    public void setValidatorProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public Validator createValidator() {
        return this.createValidator(null);
    }

    public Validator createValidator(ModelBean scope) {
        Validator validator = delegate.createValidator();
        if (validator != null && this.properties.size() > 0) {
            setValidatorProperties(scope, validator);
        }
        return validator;
    }

    protected void setValidatorProperties(ModelBean scope, Validator validator) {
        ModelBean bean = getValidatorBean(validator);
        Class tclass = bean.getTargetClass();

        for (String name : this.properties.keySet()) {
            try {
                // Set property value only if the target object has a setter method
                BeanProperty p = BeanUtils.getProperty(tclass, name);
                if (p == null) {
                    log.warning(_T(MVB_VALIDATOR_PROPERTY_NOT_FOUND, name, tclass.getName()));
                    continue;
                } else if (p.isReadOnly()) {
                    log.warning(_T(MVB_VALIDATOR_PROPERTY_READONLY, name, tclass.getName()));
                    continue;
                }

                // Evaluate expression string value and coerce to appropriate property type
                Object value = this.properties.get(name);
                if (value instanceof String) {
                    value = convertStringValue(scope, (String)value, p.getType());
                } else if (!p.getType().isInstance(value)) {
                    value = Coercion.coerce(value, p.getType());
                }

                // Set the property value
                if (value != null) {
                    bean.invoke(p.getWriteMethod(), value);
                }
            } catch (Exception ex) {
                throw new FacesException(_T(MVB_SET_VALIDATOR_PROPERTY_FAILED, name, tclass.getName()), ex);
            }
        }
    }

    private Object convertStringValue(ModelBean scope, String value, Class<?> type) {
        if (value.length() == 0) {
            return null;
        } else if (scope != null) {
            return scope.evaluateExpression(value, type);
        } else {
            return FacesUtils.evaluateExpressionGet(value, type);
        }
    }

    private ModelBean getValidatorBean(Object validator) {
        if (validator instanceof ValidatorAdapter) {
            return ((ValidatorAdapter)validator).getModelBean();
        } else {
            return ModelBean.wrap(validator);
        }
    }
}
