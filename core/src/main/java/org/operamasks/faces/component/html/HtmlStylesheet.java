/*
 * $Id: HtmlStylesheet.java,v 1.3 2007/07/02 07:38:13 jacky Exp $
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

package org.operamasks.faces.component.html;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

/**
 * Represents an HTML STYLE element in an HTML document.
 */
public class HtmlStylesheet extends UIOutput
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.HtmlStylesheet";

    /**
     * Create a new {@link HtmlStylesheet} instance with default property values.
     */
    public HtmlStylesheet() {
        setRendererType("org.operamasks.faces.HtmlStylesheet");
    }

    private String type;

    /**
     * Returns the stylesheet type, default is "text/css".
     */
    public String getType() {
        if (this.type != null) {
            return this.type;
        }
        ValueExpression ve = getValueExpression("type");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the stylesheet type.
     */
    public void setType(String type) {
        this.type = type;
    }

    private String src;

    /**
     * Returns the external resource URL of the stylesheet.
     */
    public String getSrc() {
        if (this.src != null) {
            return this.src;
        }
        ValueExpression ve = getValueExpression("src");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            type,
            src
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        type = (String)values[1];
        src = (String)values[2];
    }
}
