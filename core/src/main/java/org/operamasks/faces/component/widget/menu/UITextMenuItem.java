/*
 * $Id: UITextMenuItem.java,v 1.3 2007/07/02 07:38:18 jacky Exp $
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

import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

public class UITextMenuItem extends HtmlOutputText
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.TextMenuItem";

    public static final String DEFAULT_RENDERER_TYPE = "javax.faces.Text";
    public static final String MENU_RENDERER_TYPE = "org.operamasks.faces.widget.TextMenuItem";

    public UITextMenuItem() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
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

    private Boolean disabled;

    public boolean isDisabled() {
        if (this.disabled != null) {
            return this.disabled;
        }
        ValueExpression ve = getValueExpression("disabled");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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

    private String onclick;

    public String getOnclick() {
        if (this.onclick != null) {
            return this.onclick;
        }
        ValueExpression ve = getValueExpression("onclick");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            label,
            image,
            disabled,
            disabledClass,
            activeClass,
            onclick
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        label = (String)values[i++];
        image = (String)values[i++];
        disabled = (Boolean)values[i++];
        disabledClass = (String)values[i++];
        activeClass = (String)values[i++];
        onclick = (String)values[i++];
    }
}
