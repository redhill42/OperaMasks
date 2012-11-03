/*
 * $Id: UILinkMenuItem.java,v 1.3 2007/07/02 07:38:18 jacky Exp $
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

package org.operamasks.faces.component.widget.menu;

import javax.el.ValueExpression;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;

public class UILinkMenuItem extends HtmlOutputLink
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.LinkMenuItem";

    public static final String DEFAULT_RENDERER_TYPE = "javax.faces.Link";
    public static final String MENU_RENDERER_TYPE = "org.operamasks.faces.widget.LinkMenuItem";

    public UILinkMenuItem() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    private String label;

    public String getLabel() {
        if (this.label != null) {
            return this.label;
        }
        ValueExpression ve = getValueExpression("label");
        if (label != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String image;

    public String getImage() {
        if (this.image != null) {
            return this.image;
        }
        ValueExpression ve = getValueExpression("image");
        if (image != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String disabledClass;

    public String getDisabledClass() {
        if (this.disabledClass != null) {
            return this.disabledClass;
        }
        ValueExpression ve = getValueExpression("disabledClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDisabledClass(String disabledClass) {
        this.disabledClass = disabledClass;
    }

    private String activeClass;

    public String getActiveClass() {
        if (this.activeClass != null) {
            return this.activeClass;
        }
        ValueExpression ve = getValueExpression("activeClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setActiveClass(String activeClass) {
        this.activeClass = activeClass;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            label,
            image,
            disabledClass,
            activeClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        label = (String)values[i++];
        image = (String)values[i++];
        disabledClass = (String)values[i++];
        activeClass = (String)values[i++];
    }
}
