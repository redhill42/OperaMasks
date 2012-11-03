/*
 * $Id: AdaptingConverterFactory.java,v 1.2 2007/12/17 23:24:12 daniel Exp $
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

import javax.faces.convert.Converter;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.application.impl.DefaultTargetCreator;
import org.operamasks.faces.application.TargetCreator;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.annotation.Convert;
import org.operamasks.faces.annotation.Format;
import org.operamasks.faces.binding.impl.ConverterAdapter;
import org.operamasks.faces.binding.impl.MethodConvertAction;
import org.operamasks.faces.binding.impl.MethodFormatAction;
import org.operamasks.faces.binding.ModelBean;
import static org.operamasks.resources.Resources.*;

public class AdaptingConverterFactory implements ConverterFactory
{
    private TargetCreator creator;
    private Method convertMethod;
    private Method formatMethod;

    public AdaptingConverterFactory(TargetCreator creator) {
        this.creator = creator;
    }

    public AdaptingConverterFactory(Class<?> converterClass) {
        this.creator = new DefaultTargetCreator(converterClass, Class.class);
    }

    public Converter createConverter(Class type) {
        Object target = this.creator.createTarget(type);

        if (target instanceof Converter) {
            return (Converter)target;
        } else {
            return createConverterAdapter(target);
        }
    }

    private Converter createConverterAdapter(Object target) {
        if (this.convertMethod == null && this.formatMethod == null) {
            this.initialize(this.creator.getTargetClass());
        }

        ModelBean targetBean = ModelBean.wrap(target);
        ConverterAdapter adapter = new ConverterAdapter();

        if (convertMethod != null)
            adapter.setConvertAction(new MethodConvertAction(targetBean, convertMethod));
        if (formatMethod != null)
            adapter.setFormatAction(new MethodFormatAction(targetBean, formatMethod));
        return adapter;
    }

    private void initialize(Class targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Convert.class)) {
                if (!checkConvertMethod(method)) {
                    throw new FacesException(_T(MVB_INVALID_CONVERT_METHOD, method.getName()));
                } else {
                    this.convertMethod = method;
                }
            } else if (method.isAnnotationPresent(Format.class)) {
                if (!checkFormatMethod(method)) {
                    throw new FacesException(_T(MVB_INVALID_FORMAT_METHOD, method.getName()));
                } else {
                    this.formatMethod = method;
                }
            }
        }

        if (this.convertMethod == null && this.formatMethod == null) {
            throw new FacesException(_T(MVB_CONVERT_OR_FORMAT_METHOD_NOT_FOUND, targetClass.getName()));
        }
    }

    private boolean checkConvertMethod(Method method) {
        Class[] params = method.getParameterTypes();
        if (params.length == 1) {
            return (params[0] == String.class);
        } else if (params.length == 3) {
            return params[0] != FacesContext.class
                && params[1] != UIComponent.class
                && params[2] != String.class;
        } else {
            return false;
        }
    }

    private boolean checkFormatMethod(Method method) {
        if (method.getReturnType() != String.class) {
            return false;
        }

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
