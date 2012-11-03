/*
 * $Id: ManagedBeanConfigurer.java,v 1.5 2007/10/24 04:40:43 daniel Exp $
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

import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.util.PatternMatchUtils;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ManagedBeanFactory;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.impl.DefaultManagedBeanFactory;
import org.operamasks.faces.config.FacesConfigLoader;
import org.operamasks.faces.config.BootstrapFacesContext;
import org.operamasks.faces.annotation.ManagedBeanScope;

public class ManagedBeanConfigurer
    implements BeanFactoryPostProcessor, BeanPostProcessor, ServletContextAware
{
    private ServletContext servletContext;
    private String[] beanNames;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setBeanNames(String[] beanNames) {
        this.beanNames = beanNames;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
        throws BeansException
    {
        if ((this.servletContext != null) && (beanFactory instanceof BeanDefinitionRegistry)) {
            // Initialize JSF container if it doesn't load.
            ApplicationAssociate assoc = ApplicationAssociate.getInstance(this.servletContext);
            if (assoc == null) {
                FacesConfigLoader loader = new FacesConfigLoader();
                loader.loadFacesConfig(this.servletContext);
                assoc = ApplicationAssociate.getInstance(this.servletContext);
            }

            // Must use BootstrapFacesContext because we are outside the JSF initialization phase.
            if (assoc != null) {
                BootstrapFacesContext bootstrap = new BootstrapFacesContext(this.servletContext);
                try {
                    registerManagedBeans((BeanDefinitionRegistry)beanFactory);
                } finally {
                    bootstrap.release();
                }
            }
        }
    }

    private void registerManagedBeans(BeanDefinitionRegistry registry) {
        ManagedBeanContainer mbcon = ManagedBeanContainer.getInstance();

        for (ManagedBeanFactory mbf : mbcon.getBeanFactories()) {
            if (mbf instanceof DefaultManagedBeanFactory) {
                String beanName = mbf.getBeanName();
                String beanClassName = mbf.getBeanClassName();
                String scope = toSpringScope(mbf.getScope());

                if (!registry.containsBeanDefinition(beanName) && isMatch(beanName)) {
                    // Register managed bean definition in spring IoC container
                    BeanDefinition definition = new RootBeanDefinition();
                    definition.setBeanClassName(beanClassName);
                    definition.setScope(scope);
                    registry.registerBeanDefinition(beanName, definition);
                }

                if (registry.containsBeanDefinition(beanName)) {
                    // Replace managed bean factory
                    mbf = new SpringManagedBeanFactory((BeanFactory)registry, mbf.getConfig());
                    mbcon.addBeanFactory(mbf);
                }
            }
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException
    {
        // TODO: Spring 3.0.x API changed
        /*
        if (bean instanceof AnnotationTransactionAttributeSource) {
            // Replace with extended AnnotationTransactionAttributeSource
            // to support our own Transactinal annotation. The OperaMasks's
            // Transactional annotation has the same meaning for Spring's
            // homonymous.
            return new AnnotationTransactionAttributeSourceExtension();
        }
        */

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException
    {
        return bean;
    }

    private boolean isMatch(String beanName) {
        if (this.beanNames == null) {
            return true;
        }
        return PatternMatchUtils.simpleMatch(this.beanNames, beanName);
    }

    private static String toSpringScope(ManagedBeanScope scope) {
        if (scope == ManagedBeanScope.APPLICATION) {
            return BeanDefinition.SCOPE_SINGLETON;
        } else if (scope == ManagedBeanScope.SESSION) {
            return WebApplicationContext.SCOPE_SESSION;
        } else if (scope == ManagedBeanScope.REQUEST) {
            return WebApplicationContext.SCOPE_REQUEST;
        } else {
            return BeanDefinition.SCOPE_PROTOTYPE;
        }
    }
}
