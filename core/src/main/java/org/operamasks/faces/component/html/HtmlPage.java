/*
 * $Id: HtmlPage.java,v 1.10 2008/04/16 03:07:19 patrick Exp $
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

import static org.operamasks.resources.Resources.UI_IGNORING_NESTED_PAGE_TAG;
import static org.operamasks.resources.Resources._T;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

public class HtmlPage extends UIComponentBase
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.HtmlPage";

    /**
     * The component family for this component.
     */
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.HtmlDocument";
    
    private Logger logger = Logger.getLogger("org.operamasks.faces.view"); 

    /**
     * <p>Create a new {@link HtmlPage} instance with default property values.</p>
     */
    public HtmlPage() {
        setRendererType("org.operamasks.faces.HtmlPage");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String bgcolor;

    public String getBgcolor() {
        if (this.bgcolor != null) {
            return this.bgcolor;
        }
        ValueExpression ve = getValueExpression("bgcolor");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    private String dir;

    public String getDir() {
        if (this.dir != null) {
            return this.dir;
        }
        ValueExpression ve = getValueExpression("dir");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    private String lang;

    public String getLang() {
        if (this.lang != null) {
            return this.lang;
        }
        ValueExpression ve = getValueExpression("lang");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    private String style;

    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueExpression ve = getValueExpression("style");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
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
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    private String title;

    public String getTitle() {
        if (this.title != null) {
            return this.title;
        }
        ValueExpression ve = getValueExpression("title");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String onload;

    public String getOnload() {
        if (this.onload != null) {
            return this.onload;
        }
        ValueExpression ve = getValueExpression("onload");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setOnload(String onload) {
        this.onload = onload;
    }

    private String onunload;

    public String getOnunload() {
        if (this.onunload != null) {
            return this.onunload;
        }
        ValueExpression ve = getValueExpression("onunload");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }

    public void setOnunload(String onunload) {
        this.onunload = onunload;
    }
    
    private Boolean loadMask;

    public boolean getLoadMask() {
        if (this.loadMask != null) {
            return this.loadMask;
        }
        ValueExpression ve = getValueExpression("loadMask");
        if (ve != null) {
            try {
                return (Boolean)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return false;
        }
    }

    public void setLoadMask(boolean loadMask) {
        this.loadMask = loadMask;
    }
    
    private String duration;
    public String getDuration() {
        if (this.duration != null) {
            return this.duration;
        }
        ValueExpression ve = getValueExpression("duration");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException ex) {
                throw new FacesException(ex);
            }
        } else {
            return null;
        }
    }
    
    @Override
    public void encodeAll(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        if (hasParentPage()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(_T(UI_IGNORING_NESTED_PAGE_TAG, this.getId()));
            }
            encodeChildren(context);
        } else {
            super.encodeAll(context);
        }
    }
    
    public boolean hasParentPage() {
        UIComponent parent = this.getParent();
        boolean hasParentPage = false;
        while (parent != null) {
            if (parent instanceof HtmlPage) {
                hasParentPage = true;
                break;
            }
            parent = parent.getParent();
        }
        return hasParentPage;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            bgcolor,
            dir,
            lang,
            style,
            styleClass,
            title,
            onload,
            onunload,
            loadMask,
            duration
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        bgcolor = (String)values[i++];
        dir = (String)values[i++];
        lang = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        title = (String)values[i++];
        onload = (String)values[i++];
        onunload = (String)values[i++];
        loadMask = (Boolean)values[i++];
        duration = (String)values[i++];
    }
}
