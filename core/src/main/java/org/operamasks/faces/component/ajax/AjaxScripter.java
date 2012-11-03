/*
 * $Id: AjaxScripter.java,v 1.5 2007/09/21 20:20:10 daniel Exp $
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
package org.operamasks.faces.component.ajax;

import javax.faces.component.UIComponentBase;
import javax.el.ValueExpression;

public class AjaxScripter extends UIComponentBase
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxScripter";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.AjaxScripter";

    public AjaxScripter() {
        setRendererType("org.operamasks.faces.AjaxScripter");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String script;

    public String getScript() {
        if (this.script != null) {
            return this.script;
        }
        ValueExpression ve = getValueExpression("script");
        if (ve != null) {
            String script = (String)ve.getValue(getFacesContext().getELContext());
            return normalizeScript(script);
        } else {
            return null;
        }
    }

    public void setScript(String script) {
        this.script = normalizeScript(script);
    }

    public void addScript(String script) {
        script = normalizeScript(script);
        if (script != null) {
            if (this.script == null) {
                this.script = script;
            } else {
                this.script += script;
            }
        }
    }

    public void clearScript() {
        this.script = null;
    }

    private String normalizeScript(String script) {
        if (script != null) {
            script = script.trim();
            if (script.length() == 0) {
                script = null;
            } else if (!script.endsWith(";")) {
                script += ";";
            }
        }
        return script;
    }
}
