/*
 * $Id: SpringManagedBeanFactory.java,v 1.4 2007/10/23 08:22:51 daniel Exp $
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

package org.operamasks.faces.spring;

import javax.faces.context.FacesContext;
import javax.faces.FacesException;

import org.operamasks.faces.application.impl.AbstractManagedBeanFactory;
import org.operamasks.faces.config.ManagedBeanConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.aop.framework.Advised;

class SpringManagedBeanFactory extends AbstractManagedBeanFactory
{
    private BeanFactory beanFactory;

    SpringManagedBeanFactory(BeanFactory beanFactory, ManagedBeanConfig config) {
        super(config);
        this.beanFactory = beanFactory;
    }

    @Override
    protected Object instantiateBean(FacesContext context) throws Exception {
        return beanFactory.getBean(this.getBeanName());
    }

    @Override
    protected Object populateBean(Object bean) {
        if (bean instanceof Advised) {
            try {
                Object target = ((Advised)bean).getTargetSource().getTarget();
                super.populateBean(target);
                return bean;
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else {
            return super.populateBean(bean);
        }
    }

    @Override
    protected void injectBean(Object bean) {
        if (bean instanceof Advised) {
            try {
                Object target = ((Advised)bean).getTargetSource().getTarget();
                super.injectBean(target);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else {
            super.injectBean(bean);
        }
    }
    
    @Override
    protected void invokePostConstruct(Object bean) {
        if (bean instanceof Advised) {
            try {
                Object target = ((Advised)bean).getTargetSource().getTarget();
                super.invokePostConstruct(target);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else {
            super.invokePostConstruct(bean);
        }
    }

    @Override
    protected void invokePreDestroy(Object bean) {
        if (bean instanceof Advised) {
            try {
                Object target = ((Advised)bean).getTargetSource().getTarget();
                super.invokePreDestroy(target);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else {
            super.invokePreDestroy(bean);
        }
    }


    public boolean isInstance(Object bean) {
        if (bean instanceof Advised) {
            Class<?> targetClass = ((Advised)bean).getTargetClass();
            return this.getBeanClassName().equals(targetClass.getName());
        } else {
            return this.getBeanClassName().equals(bean.getClass().getName());
        }
    }
}
