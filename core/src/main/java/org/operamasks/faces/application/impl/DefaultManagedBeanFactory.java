/*
 * $Id: DefaultManagedBeanFactory.java,v 1.3 2007/10/20 03:28:19 daniel Exp $
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
import org.operamasks.faces.config.ManagedBeanConfig;

public class DefaultManagedBeanFactory extends AbstractManagedBeanFactory
{
    public DefaultManagedBeanFactory(ManagedBeanConfig mbean) {
        super(mbean);
    }

    protected Object instantiateBean(FacesContext context) throws Exception {
        return java.beans.Beans.instantiate(this.loader, this.mbean.getManagedBeanClass());
    }
}
