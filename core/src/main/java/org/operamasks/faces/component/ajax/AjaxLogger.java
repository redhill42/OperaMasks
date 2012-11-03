/*
 * $Id: AjaxLogger.java,v 1.6 2008/01/30 07:58:15 yangdong Exp $
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

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

public class AjaxLogger extends UIPanel
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxLogger";

    public AjaxLogger() {
        setRendererType("org.operamasks.faces.AjaxLogger");
    }

    private String level;
    private String style;
    private String styleClass;
    private Boolean serverLog;

    public Boolean getServerLog() {
        if (this.serverLog != null) {
            return this.serverLog;
        }
        ValueExpression ve = getValueExpression("serverLog");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return Boolean.FALSE;
        }
    }

    public void setServerLog(Boolean serverLog) {
        this.serverLog = serverLog;
    }
    
    public String getLevel() {
        if (this.level != null) {
            return this.level;
        }
        ValueExpression ve = getValueExpression("level");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLevel(String level) {
        this.level = level;
    }

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
            level,
            style,
            styleClass,
            serverLog
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        level = (String)values[1];
        style = (String)values[2];
        styleClass = (String)values[3];
        serverLog = (Boolean)values[4];
    }
}
