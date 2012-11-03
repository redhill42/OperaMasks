/*
 * $Id: DefaultConverterRegistry.java,v 1.3 2007/10/26 16:30:11 daniel Exp $
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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.faces.convert.Converter;
import javax.faces.FacesException;

import org.operamasks.faces.application.ConverterRegistry;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.TargetCreator;
import org.operamasks.faces.binding.factories.AdaptingConverterFactory;
import org.operamasks.util.Utils;
import static org.operamasks.resources.Resources.*;

public class DefaultConverterRegistry extends ConverterRegistry
{
    private Map<String,ConverterFactory> registryById;
    private Map<Class,ConverterFactory> registryByType;
    private List<Class> converterTypes;

    protected DefaultConverterRegistry() {
        this.registryById = new ConcurrentHashMap<String,ConverterFactory>();
        this.registryByType = new ConcurrentHashMap<Class,ConverterFactory>();
        this.converterTypes = Collections.synchronizedList(new ArrayList<Class>());
    }

    public ConverterFactory createConverterFactory(TargetCreator creator) {
        return new AdaptingConverterFactory(creator);
    }

    public ConverterFactory createConverterFactory(Class<?> converterClass) {
        return new AdaptingConverterFactory(converterClass);
    }

    public void addConverterFactory(String converterId, ConverterFactory converterFactory) {
        this.registryById.put(converterId, converterFactory);
    }

    public void addConverterFactory(Class targetClass, ConverterFactory converterFactory) {
        this.registryByType.put(targetClass, converterFactory);
        this.converterTypes.add(targetClass);
    }

    public ConverterFactory getConverterFactory(String converterId) {
        return this.registryById.get(converterId);
    }

    public ConverterFactory getConverterFactory(Class targetClass) {
        return this.registryByType.get(targetClass);
    }

    public Converter createConverter(String converterId) {
        ConverterFactory converterFactory = this.registryById.get(converterId);
        if (converterFactory == null) {
            throw new FacesException(_T(JSF_NO_SUCH_CONVERTER_ID, converterId));
        }

        return converterFactory.createConverter(null);
    }

    public Converter createConverter(Class targetClass) {
        if (targetClass.isPrimitive()) {
            targetClass = Utils.getWrapperClass(targetClass);
        }
        return createConverterForType(targetClass, targetClass);
    }

    private Converter createConverterForType(Class targetClass, Class baseClass) {
        Converter result = newConverter(targetClass, baseClass);
        if (result != null) {
            return result;
        }

        for (Class c : targetClass.getInterfaces()) {
            result = createConverterForType(c, baseClass);
            if (result != null) {
                return result;
            }
        }

        Class superclass = targetClass.getSuperclass();
        if (superclass != null) {
            result = createConverterForType(superclass, baseClass);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private Converter newConverter(Class targetClass, Class baseClass) {
        ConverterFactory factory = this.registryByType.get(targetClass);
        if (factory == null) {
            return null;
        }

        if (baseClass != targetClass) {
            this.registryByType.put(baseClass, factory);
        }

        return factory.createConverter(baseClass);
    }

    public Iterator<String> getConverterIds() {
        return this.registryById.keySet().iterator();
    }

    public Iterator<Class> getConverterTypes() {
        return this.converterTypes.iterator();
    }
}
