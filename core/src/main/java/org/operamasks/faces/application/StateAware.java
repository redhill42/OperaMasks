/*
 * $Id: StateAware.java,v 1.1 2007/09/08 22:49:10 daniel Exp $
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

import javax.faces.context.FacesContext;

/**
 * The addon interface to UIComponent that support optimized state holding.
 */
public interface StateAware
{
    /**
     * Save the optimized state.
     */
    public Object saveOptimizedState(FacesContext context);

    /**
     * Restore the optimized state.
     */
    public void restoreOptimizedState(FacesContext context, Object state);
}
