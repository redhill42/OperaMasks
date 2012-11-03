/*
 * $Id: SpringApplicationListener.java,v 1.1 2007/10/15 21:09:47 daniel Exp $
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

import org.operamasks.faces.application.ApplicationListener;
import org.operamasks.faces.application.ApplicationEvent;
import org.operamasks.faces.binding.ModelBindingFactory;

public class SpringApplicationListener implements ApplicationListener
{
    public void applicationCreated(ApplicationEvent event) {
        // do nothing
    }

    public void applicationDestroyed(ApplicationEvent event) {
        // do nothing
    }

    public void applicationInitialized(ApplicationEvent event) {
        ModelBindingFactory mbf = ModelBindingFactory.instance();
        mbf.setModelBeanCreator(new SpringBeanCreator(mbf.getModelBeanCreator()));

        event.getApplication().addELResolver(new SpringBeanELResolver());
    }
}
