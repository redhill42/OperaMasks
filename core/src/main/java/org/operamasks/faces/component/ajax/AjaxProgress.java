/*
 * $Id: AjaxProgress.java,v 1.6 2007/07/02 07:38:12 jacky Exp $
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
import javax.faces.context.FacesContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

public class AjaxProgress extends UIComponentBase
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxProgress";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.AjaxProgress";

    public AjaxProgress() {
        setRendererType("org.operamasks.faces.AjaxProgress");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
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

    private String _for;

    public String getFor() {
        if (this._for != null) {
            return this._for;
        }
        ValueExpression ve = getValueExpression("for");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    private Integer interval;

    public int getInterval() {
        if (this.interval != null) {
            return this.interval;
        }
        ValueExpression ve = getValueExpression("interval");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 1;
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    private Boolean start;

    public boolean getStart() {
        if (this.start != null) {
            return this.start;
        }
        ValueExpression ve = getValueExpression("start");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    // Server side actions

    private MethodExpression action;

    public MethodExpression getAction() {
        return action;
    }

    public void setAction(MethodExpression action) {
        this.action = action;
    }

    // Client side actions

    private String onstatechange;

    public String getOnstatechange() {
        if (this.onstatechange != null) {
            return this.onstatechange;
        }
        ValueExpression ve = getValueExpression("onstatechange");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnstatechange(String onstatechange) {
        this.onstatechange = onstatechange;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, action),
            onstatechange,
            jsvar,
            _for,
            interval,
            start,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        action = (MethodExpression)restoreAttachedState(context, values[i++]);
        onstatechange = (String)values[i++];
        jsvar = (String)values[i++];
        _for = (String)values[i++];
        interval = (Integer)values[i++];
        start = (Boolean)values[i++];
    }
}
