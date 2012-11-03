/*
 * $Id: Facelet.java,v 1.5 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.layout;

import javax.faces.context.FacesContext;
import java.util.Map;
import java.io.IOException;

/**
 * The Facelet is a portion of UI components that rendered in the screen.
 */
public interface Facelet
{
    /**
     * Get the associated name of this Facelet.
     */
    String getName();

    /**
     * Set the associated name of this Facelet.
     */
    void setName(String name);

    /**
     * Get the constraint object associated with this Facelet.
     * The constraint object specify how and where Facelet
     * should be added to the layout.
     */
    Object getConstraints();

    /**
     * Set the constraint object associated with this Facelet.
     * The constraint object specify how and where Facelet
     * should be added to the layout.
     */
    void setConstraints(Object constraints);

    /**
     * Return a mutable <code>Map</code> representing the attributes
     * associated with this {@link Facelet}, keyed by attribute name.
     */
    Map<String,Object> getAttributes();


    /**
     * Render this Facelet.
     */
    void encodeAll(FacesContext context) throws IOException;
}
