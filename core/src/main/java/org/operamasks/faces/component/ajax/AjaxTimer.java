/*
 * $Id: AjaxTimer.java,v 1.6 2007/07/02 07:38:12 jacky Exp $
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.component.UICommand;

public class AjaxTimer extends UICommand
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxTimer";

    public AjaxTimer() {
        setRendererType("org.operamasks.faces.AjaxTimer");
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

    private Integer delay;

    public int getDelay() {
        if (this.delay != null) {
            return this.delay;
        }
        ValueExpression ve = getValueExpression("delay");
        if (ve != null) {
            Integer result = (Integer)ve.getValue(getFacesContext().getELContext());
            return (result == null) ? 0 : result;
        } else {
            return 0;
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private Integer period;

    public int getPeriod() {
        if (this.period != null) {
            return this.period;
        }
        ValueExpression ve = getValueExpression("period");
        if (ve != null) {
            Integer result = (Integer)ve.getValue(getFacesContext().getELContext());
            return (result == null) ? 0 : result;
        } else {
            return 0;
        }
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    private Boolean start;

    public boolean getStart() {
        if (this.start != null) {
            return this.start;
        }
        ValueExpression ve = getValueExpression("start");
        if (ve != null) {
            Boolean result = (Boolean)ve.getValue(getFacesContext().getELContext());
            return (result == null) ? false : result;
        } else {
            return false;
        }
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    private String ontimeout;

    public String getOntimeout() {
        if (this.ontimeout != null) {
            return this.ontimeout;
        }
        ValueExpression ve = getValueExpression("ontimeout");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOntimeout(String ontimeout) {
        this.ontimeout = ontimeout;
    }

    private Boolean sendForm;

    public boolean getSendForm() {
        if (this.sendForm != null) {
            return this.sendForm;
        }
        ValueExpression ve = getValueExpression("sendForm");
        if (ve != null) {
            Boolean result = (Boolean)ve.getValue(getFacesContext().getELContext());
            return (result == null) ? false : result;
        } else {
            return false;
        }
    }

    public void setSendForm(boolean sendForm) {
        this.sendForm = sendForm;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            delay,
            period,
            start,
            ontimeout,
            sendForm,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        delay = (Integer)values[i++];
        period = (Integer)values[i++];
        start = (Boolean)values[i++];
        ontimeout = (String)values[i++];
        sendForm = (Boolean)values[i++];
    }
}
