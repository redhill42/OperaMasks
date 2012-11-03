/*
 * $Id: UIPanelBox.java,v 1.4 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import org.operamasks.faces.util.FacesUtils;

public class UIPanelBox extends UIPanel
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.PanelBox";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Box";

    private String  bgcolor;
    private String  color;
    private String  color2;
    private String  color3;
    private Integer gradientExtent;
    private Integer border;
    private String  borderColor;
    private Integer borderRadius;
    private String  roundedCorners;
    
    public UIPanelBox() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UIPanelBox(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    public String getBgcolor() {
        if (this.bgcolor != null) {
            return this.bgcolor;
        }
        ValueExpression ve = getValueExpression("bgcolor");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getColor() {
        if (this.color != null) {
            return this.color;
        }
        ValueExpression ve = getValueExpression("color");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor2() {
        if (this.color2 != null) {
            return this.color2;
        }
        ValueExpression ve = getValueExpression("color2");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor3() {
        if (this.color3 != null) {
            return this.color3;
        }
        ValueExpression ve = getValueExpression("color3");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public int getGradientExtent() {
        if (this.gradientExtent != null) {
            return this.gradientExtent;
        }
        ValueExpression ve = getValueExpression("gradientExtent");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 20;
        }
    }

    public void setGradientExtent(int gradientExtent) {
        this.gradientExtent = gradientExtent;
    }
    
    public int getBorder() {
        if (this.border != null) {
            return this.border;
        }
        ValueExpression ve = getValueExpression("border");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return -1;
        }
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public String getBorderColor() {
        if (this.borderColor != null) {
            return this.borderColor;
        }
        ValueExpression ve = getValueExpression("borderColor");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderRadius() {
        if (this.borderRadius != null) {
            return this.borderRadius;
        }
        ValueExpression ve = getValueExpression("borderRadius");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return -1;
        }
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public String getRoundedCorners() {
        if (this.roundedCorners != null) {
            return this.roundedCorners;
        }
        ValueExpression ve = getValueExpression("roundedCorners");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setRoundedCorners(String roundedCorners) {
        this.roundedCorners = roundedCorners;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            bgcolor,
            color,
            color2,
            color3,
            gradientExtent,
            border,
            borderColor,
            borderRadius,
            roundedCorners,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        bgcolor = (String)values[i++];
        color = (String)values[i++];
        color2 = (String)values[i++];
        color3 = (String)values[i++];
        gradientExtent = (Integer)values[i++];
        border = (Integer)values[i++];
        borderColor = (String)values[i++];
        borderRadius = (Integer)values[i++];
        roundedCorners = (String)values[i++];
    }
}
