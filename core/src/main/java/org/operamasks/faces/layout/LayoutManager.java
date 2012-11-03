/*
 * $Id: LayoutManager.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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
import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * Defines the interface for classes that know how to lay out
 * <code>FaceletContainer</code>s.
 */
public interface LayoutManager
{
    /**
     * Return a mutable <code>List</code> representing the
     * {@link org.operamasks.faces.layout.Facelet}s associated
     * with this layout manager.
     */
    List<Facelet> getFacelets();

    /**
     * Return a mutable <code>Map</code> representing the attributes
     * associated with this LayoutManager keyed by attribute name
     * (which must be a String).
     */
    Map<String, Object> getAttributes();

    /**
     * Lays out the facelets.
     */
    void encodeAll(FacesContext context) throws IOException;
}
