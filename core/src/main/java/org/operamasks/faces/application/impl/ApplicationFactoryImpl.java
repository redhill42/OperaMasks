/*
 * $Id: ApplicationFactoryImpl.java,v 1.3 2007/10/20 03:28:19 daniel Exp $
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

import javax.faces.application.ApplicationFactory;
import javax.faces.application.Application;
import javax.faces.FacesException;

import org.operamasks.faces.application.ApplicationAssociate;

public class ApplicationFactoryImpl extends ApplicationFactory
{
    private Application application;

    public Application getApplication() {
        if (application == null) {
            ApplicationAssociate associate = createApplicationAssociate();
            this.application = new ApplicationImpl(associate);
        }

        return application;
    }

    public void setApplication(Application application) {
        if (application == null) {
            throw new NullPointerException();
        }

        this.application = application;
    }

    private static final String ASSOCIATE_PROPERTY = "org.operamasks.faces.ApplicationAssociate";

    private ApplicationAssociate createApplicationAssociate() {
        // try system property
        String associateClass = System.getProperty(ASSOCIATE_PROPERTY);
        if (associateClass != null) {
            try {
                Class cl = Class.forName(associateClass);
                return (ApplicationAssociate)cl.newInstance();
            } catch (Exception ex) {
                throw new FacesException("Cannot instantiate ApplicationAssociate instance: " +
                                         associateClass);
            }
        }

        // returns the default ApplicationAssociate instance
        return new ApplicationAssociate();
    }
}
