/*
 * $Id: TabLayout.java,v 1.5 2008/03/11 03:21:00 lishaochuan Exp $
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.layout.impl.UITabLayout代替
 */
@Deprecated
public class TabLayout extends LayoutManagerSupport
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.TabLayout";

    private String jsvar;
    private String style;
    private String styleClass;
    private String tabPosition;
    private Boolean resizeTabs;
    private Boolean monitorResize;
    private Integer minTabWidth;
    private Integer maxTabWidth;
    private Integer preferredTabWidth;

    public TabLayout() {
        setRendererType("org.operamasks.faces.layout.TabLayout");
    }

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

    public String getTabPosition() {
        if (this.tabPosition != null) {
            return this.tabPosition;
        }
        ValueExpression ve = getValueExpression("tabPosition");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTabPosition(String tabPosition) {
        this.tabPosition = tabPosition;
    }

    public boolean getResizeTabs() {
        if (this.resizeTabs != null) {
            return this.resizeTabs;
        }
        ValueExpression ve = getValueExpression("resizeTabs");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setResizeTabs(boolean resizeTabs) {
        this.resizeTabs = resizeTabs;
    }

    public boolean getMonitorResize() {
        if (this.monitorResize != null) {
            return this.monitorResize;
        }
        ValueExpression ve = getValueExpression("monitorResize");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setMonitorResize(Boolean monitorResize) {
        this.monitorResize = monitorResize;
    }

    public int getMinTabWidth() {
        if (this.minTabWidth != null) {
            return this.minTabWidth;
        }
        ValueExpression ve = getValueExpression("minTabWidth");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void setMinTabWidth(int minTabWidth) {
        this.minTabWidth = minTabWidth;
    }

    public int getMaxTabWidth() {
        if (this.maxTabWidth != null) {
            return this.maxTabWidth;
        }
        ValueExpression ve = getValueExpression("maxTabWidth");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void setMaxTabWidth(int maxTabWidth) {
        this.maxTabWidth = maxTabWidth;
    }

    public int getPreferredTabWidth() {
        if (this.preferredTabWidth != null) {
            return this.preferredTabWidth;
        }
        ValueExpression ve = getValueExpression("preferredTabWidth");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void setPreferredTabWidth(int preferredTabWidth) {
        this.preferredTabWidth = preferredTabWidth;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            style,
            styleClass,
            tabPosition,
            resizeTabs,
            monitorResize,
            minTabWidth,
            maxTabWidth,
            preferredTabWidth,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        tabPosition = (String)values[i++];
        resizeTabs = (Boolean)values[i++];
        monitorResize = (Boolean)values[i++];
        minTabWidth = (Integer)values[i++];
        maxTabWidth = (Integer)values[i++];
        preferredTabWidth = (Integer)values[i++];
    }
}
