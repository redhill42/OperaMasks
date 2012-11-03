/*
 * $Id: ViewMapper.java,v 1.5 2008/03/05 12:50:40 jacky Exp $
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

import java.util.List;

/**
 * This interface maps a view identifier to the name of a corresponding
 * managed bean that serves as the model bean for this view.
 */
public interface ViewMapper
{
    /**
     * Returns the list of name of the managed beans that serves as the model bean
     * for the specified view identifier
     */
    public List<String> mapViewId(String viewId);
}
