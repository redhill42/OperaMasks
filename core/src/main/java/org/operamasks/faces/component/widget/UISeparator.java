/*
 * $Id: UISeparator.java,v 1.4 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.component.widget;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

/**
 * Represents a separator, such as a menu separator.
 */
public class UISeparator extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Separator";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Separator";

    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.Separator";
    public static final String MENU_RENDERER_TYPE = "org.operamasks.faces.widget.MenuSeparator";

    public UISeparator() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }
    
    public UISeparator(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String style;

    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueExpression ve = getValueExpression("style");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStyle(String style) {
        this.style = style;
    }

    private String styleClass;

    public String getStyleClass() {
        if (this.styleClass != null) {
            return this.styleClass;
        }
        ValueExpression ve = getValueExpression("styleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            style,
            styleClass
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        style = (String)values[1];
        styleClass = (String)values[2];
    }
}
