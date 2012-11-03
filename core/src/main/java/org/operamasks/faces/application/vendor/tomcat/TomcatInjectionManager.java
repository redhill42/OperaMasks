/*
 * $Id: TomcatInjectionManager.java,v 1.1 2007/10/19 10:26:39 daniel Exp $
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
package org.operamasks.faces.application.vendor.tomcat;

import javax.faces.FacesException;
import org.operamasks.faces.application.InjectionManager;
import org.apache.AnnotationProcessor;

public class TomcatInjectionManager implements InjectionManager
{
    private AnnotationProcessor processor;

    public TomcatInjectionManager(Object processor) {
        this.processor = (AnnotationProcessor)processor;
    }

    public void inject(Object bean) {
        try {
            this.processor.processAnnotations(bean);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    public void invokePostConstruct(Object bean) {
        try {
            this.processor.postConstruct(bean);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    public void invokePreDestroy(Object bean) {
        try {
            this.processor.preDestroy(bean);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }
}
