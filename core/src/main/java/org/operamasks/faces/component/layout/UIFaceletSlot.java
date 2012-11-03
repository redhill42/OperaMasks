/*
 * $Id: UIFaceletSlot.java,v 1.4 2007/07/02 07:38:04 jacky Exp $
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

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

import org.operamasks.faces.layout.Facelet;

public class UIFaceletSlot extends UIComponentBase
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.FaceletSlot";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.Layout";

    private String name;
    private Integer index;
    private Facelet facelet;

    public UIFaceletSlot() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            name,
            index
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        name = (String)values[i++];
        index = (Integer)values[i++];
    }

    public Facelet getFacelet() {
        return facelet;
    }

    public void setFacelet(Facelet facelet) {
        this.facelet = facelet;
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context)
        throws IOException
    {
        if (!isRendered())
            return;

        if (facelet != null) {
            facelet.encodeAll(context);
        } else {
            if (getChildCount() > 0) {
                for (UIComponent kid : getChildren()) {
                    kid.encodeAll(context);
                }
            }
        }
    }
}
