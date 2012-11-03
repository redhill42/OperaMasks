/*
 * $Id: UITemplateContainer.java,v 1.5 2007/08/14 07:32:23 daniel Exp $
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

package org.operamasks.faces.component.layout;

import javax.faces.component.UINamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class UITemplateContainer extends UINamingContainer
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.TemplateContainer";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.Layout";

    public UITemplateContainer() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        UIComponent parent = this.getParent();
        assert (parent instanceof TemplateLayout);
        return parent.getClientId(context);
    }
}
