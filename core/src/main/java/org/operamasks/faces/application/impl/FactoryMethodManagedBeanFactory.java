/*
 * $Id: FactoryMethodManagedBeanFactory.java,v 1.5 2007/10/20 03:28:19 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import java.lang.reflect.Method;

import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.config.ManagedBeanConfig.FactoryMethod;
import org.operamasks.faces.binding.ModelBean;

public class FactoryMethodManagedBeanFactory extends AbstractManagedBeanFactory
{
    public FactoryMethodManagedBeanFactory(FactoryMethod mbean) {
        super(mbean);
    }

    @Override
    protected Object instantiateBean(FacesContext context)
        throws Exception
    {
        FactoryMethod factory = (FactoryMethod)this.mbean;
        String factoryName = factory.getFactoryName();

        // find factory bean from scope.
        Object factoryBean = ManagedBeanContainer.getInstance().getBean(context, factoryName);
        if (factoryBean == null) {
            throw new FacesException("The factory managed bean '" + factoryName + "' " +
                                     "was not found in scope.");
        }

        ModelBean factoryModelBean = ModelBean.wrap(factoryBean);

        // find appropriate factory method.
        Method factoryMethod = factory.getMethod(factoryModelBean.getTargetClass());
        if (factoryMethod == null) {
            throw new FacesException("Cannot instantiate managed bean '" + getBeanName() + "'. " +
                                     "The source class may have being changed.");
        }

        // invoke the factory method to create the bean.
        return factoryModelBean.invoke(factoryMethod);
    }
}
