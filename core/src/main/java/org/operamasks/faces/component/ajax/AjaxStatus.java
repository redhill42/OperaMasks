/*
 * $Id: AjaxStatus.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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
import javax.el.ValueExpression;

public class AjaxStatus extends UIComponentBase
{
    /**
     * <p>The component type for this component.</p>
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxStatus";

    /**
     * <p>The component family for this component.</p>
     */
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.AjaxStatus";

    /**
     * <p>Create a new {@link AjaxStatus} instance with default property values.</p>
     */
    public AjaxStatus() {
        super();
        setRendererType("org.operamasks.faces.AjaxStatus");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String layout;

    public String getLayout() {
        if (layout != null) {
            return layout;
        }
        ValueExpression ve = getValueExpression("layout");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    private String style;

    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueExpression ve = getValueExpression("style");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    private String styleClass;

    public String getStyleClass() {
        if (style != null) {
            return styleClass;
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

    private String startStyle;

    public String getStartStyle() {
        if (startStyle != null) {
            return startStyle;
        }
        ValueExpression ve = getValueExpression("startStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStartStyle(String startStyle) {
        this.startStyle = startStyle;
    }

    private String startStyleClass;

    public String getStartStyleClass() {
        if (startStyleClass != null) {
            return startStyleClass;
        }
        ValueExpression ve = getValueExpression("startStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStartStyleClass(String startStyleClass) {
        this.startStyleClass = startStyleClass;
    }

    private String stopStyle;

    public String getStopStyle() {
        if (stopStyle != null) {
            return stopStyle;
        }
        ValueExpression ve = getValueExpression("stopStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStopStyle(String stopStyle) {
        this.stopStyle = stopStyle;
    }

    private String stopStyleClass;

    public String getStopStyleClass() {
        if (stopStyleClass != null) {
            return stopStyleClass;
        }
        ValueExpression ve = getValueExpression("stopStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStopStyleClass(String stopStyleClass) {
        this.stopStyleClass = stopStyleClass;
    }

    private String onstart;

    public String getOnstart() {
        if (onstart != null) {
            return onstart;
        }
        ValueExpression ve = getValueExpression("onstart");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    private String onstop;

    public String getOnstop() {
        if (onstop != null) {
            return onstop;
        }
        ValueExpression ve = getValueExpression("onstop");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnstop(String onstop) {
        this.onstop = onstop;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            layout,
            style,
            styleClass,
            startStyle,
            startStyleClass,
            stopStyle,
            stopStyleClass,
            onstart,
            onstop
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        layout = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        startStyle = (String)values[i++];
        startStyleClass = (String)values[i++];
        stopStyle = (String)values[i++];
        stopStyleClass = (String)values[i++];
        onstart = (String)values[i++];
        onstop = (String)values[i++];
    }
}
