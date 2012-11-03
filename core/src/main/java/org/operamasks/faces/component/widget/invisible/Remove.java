/*
 * $Id: Remove.java,v 1.1 2007/07/09 20:49:47 jacky Exp $
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
 * 
 */
package org.operamasks.faces.component.widget.invisible;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class Remove extends UIComponentBase
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Remove";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Invisible";

    public Remove() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Object var;
    private String scope;

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        this.scope = getScope();
        this.var = getVar();
        if(var == null || var.toString().trim().length()==0) {
            return;
        }
        if("application".equalsIgnoreCase(scope)) {
            context.getExternalContext().getApplicationMap().remove(var.toString());
        }
        else if("session".equalsIgnoreCase(scope)) {
            context.getExternalContext().getSessionMap().remove(var.toString());
        }
        else if("request".equalsIgnoreCase(scope)) {
            context.getExternalContext().getRequestMap().remove(var.toString());
        }
        else {
            context.getExternalContext().getRequestMap().remove(var.toString());
        }
    }

    public Object getVar() {
        if (this.var != null) {
            return this.var;
        }
        ValueExpression ve = getValueExpression("var");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setVar(Object var) {
        this.var = var;
    }

    public String getScope() {
        if (this.scope != null) {
            return this.scope;
        }
        ValueExpression ve = getValueExpression("scope");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context), 
            var, 
            scope
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        var = values[i++];
        scope = (String) values[i++];
    }

}
