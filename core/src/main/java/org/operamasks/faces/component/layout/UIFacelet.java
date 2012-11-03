/*
 * $Id: UIFacelet.java,v 1.8 2007/09/30 21:08:41 daniel Exp $
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

package org.operamasks.faces.component.layout;

import javax.faces.component.UIComponentBase;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import java.io.IOException;

import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.binding.ComponentBinder;
import org.operamasks.faces.binding.ModelBindingContext;

public class UIFacelet extends UIComponentBase
    implements Facelet, ComponentBinder, NamingContainer
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.Facelet";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.Layout";

    private String name;
    private Object constraints;
    private Facelet delegate;
    private String uri;

    public UIFacelet() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        }

        ValueExpression ve = getValueExpression("name");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getConstraints() {
        if (this.constraints != null) {
            return this.constraints;
        }

        ValueExpression ve = getValueExpression("constraints");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setConstraints(Object constraints) {
        this.constraints = constraints;
    }

    public Facelet getDelegate() {
        if (this.delegate != null) {
            return this.delegate;
        }

        ValueExpression ve = getValueExpression("delegate");
        if (ve != null) {
            return (Facelet)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDelegate(Facelet delegate) {
        this.delegate = delegate;
    }

    public String getUri() {
        if (this.uri != null) {
            return this.uri;
        }

        ValueExpression ve = getValueExpression("uri");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void applyModel(FacesContext ctx, ModelBindingContext mbc) {
        mbc.applyInnerModel(ctx, getUri(), this);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            name,
            constraints,
            uri,
            saveDelegateState(context, delegate)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        name = (String)values[i++];
        constraints = values[i++];
        uri = (String)values[i++];
        delegate = restoreDelegateState(context, values[i++]);
    }

    private Object saveDelegateState(FacesContext context, Facelet delegate) {
        if ((delegate == null) || (delegate instanceof UIComponent)) {
            return null;
        } else {
            return saveAttachedState(context, delegate);
        }
    }

    private Facelet restoreDelegateState(FacesContext context, Object state) {
        if (state == null) {
            return null;
        } else {
            return (Facelet)restoreAttachedState(context, state);
        }
    }

    public void encodeAll(FacesContext context) throws IOException {
        Facelet delegate = this.getDelegate();
        if (delegate != null) {
            delegate.setName(this.getName());
            delegate.setConstraints(this.getConstraints());
            delegate.encodeAll(context);
        } else {
            super.encodeAll(context);
        }
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        if (getDelegate() != null || getUri() != null) {
            // If this facelet has external contents then a container client id is
            // prepended to component client id
            return super.getContainerClientId(context);
        } else {
            // Otherwise, no container client id is prepended
            UIComponent parent = this.getParent();
            while (parent != null) {
                if (parent instanceof NamingContainer) {
                    return parent.getContainerClientId(context);
                }
                parent = parent.getParent();
            }
        }
        return null;
    }
}
