/*
 * $Id: ViewBuilder.java,v 1.2 2007/09/11 12:50:48 daniel Exp $
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

import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * The <code>ViewBuilder</code> is the pluggable interface to the standard
 * JavaServer Faces <code>ViewHandler</code> interface to provide a separate
 * <em>Build View</em> phase in the JSF lifecycle. Building a view means
 * restoring the component tree of a faces view without the need of a serialized
 * tree structure state.
 */
public interface ViewBuilder
{
    /**
     * Build the view associated with the specified FacesContext and viewId.
     */
    public UIViewRoot buildView(FacesContext context, String viewId);

    /**
     * Build a subview with the given view id and apply to parent component.
     */
    public void buildSubview(FacesContext context, String viewId, UIComponent parent);
}
