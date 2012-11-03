/*
 * $Id: UIChooseSkin.java,v 1.5 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.el.ValueExpression;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.component.widget.menu.UIMenu;

/**
 * A select box that choose skin from all available skins.
 */
public class UIChooseSkin extends UIInput
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.ChooseSkin";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.ChooseSkin";

    public static final String DEFAULT_RENDERER_TYPE = "javax.faces.Menu";
    public static final String MENU_RENDERER_TYPE = "org.operamasks.faces.widget.RadioMenuItem";

    private String cookie;
    private Integer cookieMaxAge;

    public UIChooseSkin() {
        setRendererType(null);
    }
    
    public UIChooseSkin(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    public String getRendererType() {
        String rendererType = super.getRendererType();
        if (rendererType != null) {
            return rendererType;
        }
        if (getParent() instanceof UIMenu) {
            return MENU_RENDERER_TYPE;
        }
        return DEFAULT_RENDERER_TYPE;
    }

    public String getCookie() {
        if (cookie != null) {
            return this.cookie;
        }
        ValueExpression ve = getValueExpression("cookie");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getCookieMaxAge() {
        if (this.cookieMaxAge != null) {
            return this.cookieMaxAge;
        }
        ValueExpression ve = getValueExpression("cookieMaxAge");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 30*86400; // defaults to 30 days
        }
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    @Override
    public Object getValue() {
        Object value = super.getValue();
        if (value == null) {
            FacesContext context = getFacesContext();
            String cookie = getCookie();
            if (cookie != null) {
                ExternalContext ectx = context.getExternalContext();
                Cookie c = (Cookie)ectx.getRequestCookieMap().get(cookie);
                if (c != null) {
                    value = c.getValue();
                }
            }
            if (value == null) {
                value = SkinManager.getCurrentSkin(context);
            }
        }
        return value;
    }

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);

        String skin = (String)super.getValue();
        if (skin != null && skin.length() != 0) {
            String cookie = getCookie();
            if (cookie != null) {
                ExternalContext ectx = context.getExternalContext();
                if (ectx.getResponse() instanceof HttpServletResponse) {
                    HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
                    Cookie c = new Cookie(cookie, skin);
                    c.setMaxAge(getCookieMaxAge());
                    response.addCookie(c);
                }
            }
            SkinManager.setCurrentSkin(context, skin);
        }
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            cookie,
            cookieMaxAge
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        cookie = (String)values[1];
        cookieMaxAge = (Integer)values[2];
    }
}
