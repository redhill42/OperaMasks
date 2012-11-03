/*
 * $Id: TomcatInjectionManagerFactory.java,v 1.2 2007/10/24 04:40:43 daniel Exp $
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

import javax.servlet.ServletContext;
import javax.faces.context.FacesContext;

import org.operamasks.faces.application.InjectionManagerFactory;
import org.operamasks.faces.application.InjectionManager;

public class TomcatInjectionManagerFactory extends InjectionManagerFactory
{
    private static final String ANNOTATION_PROCESSOR_KEY = "org.apache.AnnotationProcessor";

    protected InjectionManager newInjectionManager(FacesContext context) {
        Object annotationProcessor = context.getExternalContext().getApplicationMap()
            .get(ANNOTATION_PROCESSOR_KEY);

        if (annotationProcessor != null) {
            try {
                return new TomcatInjectionManager(annotationProcessor);
            } catch (Throwable ex) {
                return null;
            }
        }

        return null;
    }
}
