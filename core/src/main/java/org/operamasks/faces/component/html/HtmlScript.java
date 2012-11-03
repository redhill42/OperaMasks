/*
 * $Id: HtmlScript.java,v 1.4 2007/07/02 07:38:13 jacky Exp $
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
 * Represents an HTML SCRIPT element in an HTML document.
 */
public class HtmlScript extends UIOutput
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.HtmlScript";

    /**
     * Create a new {@link HtmlScript} instance with default property values.
     */
    public HtmlScript() {
        setRendererType("org.operamasks.faces.HtmlScript");
    }

    private String type;

    /**
     * Returns the script type, default is "text/javascript".
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
     * Set the script type.
     */
    public void setType(String type) {
        this.type = type;
    }

    private String language;

    /**
     * Returns the script language, default is "Javascript".
     */
    public String getLanguage() {
        if (this.language != null) {
            return this.language;
        }
        ValueExpression ve = getValueExpression("language");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the script language.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    private String src;

    /**
     * Returns the external resource URL of the script.
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
            language,
            src
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        type = (String)values[1];
        language = (String)values[2];
        src = (String)values[3];
    }
}
