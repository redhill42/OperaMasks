/*
 * $Id: UIProgressBar.java,v 1.5 2007/12/11 04:20:12 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import org.operamasks.faces.util.FacesUtils;

public class UIProgressBar extends UIOutput
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.ProgressBar";

    public UIProgressBar() {
        setRendererType("org.operamasks.faces.widget.ProgressBar");
    }
    
    public UIProgressBar(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    private String width;

    public String getWidth() {
        if (this.width != null) {
            return this.width;
        }
        ValueExpression ve = getValueExpression("width");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setWidth(String width) {
        this.width = width;
    }

    private Integer minimum;

    public int getMinimum() {
        if (this.minimum != null) {
            return this.minimum;
        }
        ValueExpression ve = getValueExpression("minimum");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 0;
        }
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    private Integer maximum;

    public int getMaximum() {
        if (this.maximum != null) {
            return this.maximum;
        }
        ValueExpression ve = getValueExpression("maximum");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 100;
        }
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    private String jsvar;

    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
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
            width,
            minimum,
            maximum,
            jsvar,
            style,
            styleClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        width = (String)values[i++];
        minimum = (Integer)values[i++];
        maximum = (Integer)values[i++];
        jsvar = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
    }
}
