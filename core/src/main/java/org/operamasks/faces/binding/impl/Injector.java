/*
 * $Id: Injector.java,v 1.3 2007/10/15 21:09:47 daniel Exp $
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
package org.operamasks.faces.binding.impl;

import javax.faces.context.FacesContext;
import org.operamasks.faces.binding.ModelBean;

/**
 * Binding class that implements this interface supports value injection.
 */
interface Injector
{
    /**
     * Returns the injection order.
     */
    int getOrder();

    /**
     * Inject model value.
     */
    void inject(FacesContext context, ModelBean bean);

    /**
     * Outject model value.
     */
    void outject(FacesContext context, ModelBean bean);
}
