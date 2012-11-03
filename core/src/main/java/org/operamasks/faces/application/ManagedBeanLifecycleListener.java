/*
 * $Id: ManagedBeanLifecycleListener.java,v 1.4 2007/10/20 03:28:19 daniel Exp $
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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.operamasks.faces.annotation.ManagedBeanScope;

public class ManagedBeanLifecycleListener implements
    ServletContextListener,
    HttpSessionListener,
    ServletRequestListener
{
    private ServletContext context;
    private Logger logger = Logger.getLogger("operamasks.ManagedBeanLifecycleListener");

    public void contextInitialized(ServletContextEvent event) {
        this.context = event.getServletContext();
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        for (Enumeration en = context.getAttributeNames(); en.hasMoreElements();) {
            String beanName = (String)en.nextElement();
            destroyBean(beanName, context.getAttribute(beanName), ManagedBeanScope.APPLICATION);
        }
    }

    public void sessionCreated(HttpSessionEvent evnet) {
        // do nothing
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        for (Enumeration en = session.getAttributeNames(); en.hasMoreElements(); ) {
            String beanName = (String)en.nextElement();
            destroyBean(beanName, session.getAttribute(beanName), ManagedBeanScope.SESSION);
        }
    }

    public void requestInitialized(ServletRequestEvent event) {
        // do nothing
    }

    public void requestDestroyed(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
            String name = (String)en.nextElement();
            destroyBean(name, request.getAttribute(name), ManagedBeanScope.REQUEST);
        }
    }

    private void destroyBean(String beanName, Object bean, ManagedBeanScope scope) {
        try {
            ManagedBeanContainer container = ManagedBeanContainer.getInstance(this.context);
            if (container != null) {
                ManagedBeanFactory factory = container.getBeanFactory(beanName);
                if ((factory != null) && (scope == factory.getScope()) && factory.isInstance(bean)) {
                    factory.destroyBean(bean);
                }
            }
        } catch (Throwable ex) {
            logger.log(Level.FINE, ex.getMessage(), ex);
        }
    }
}
