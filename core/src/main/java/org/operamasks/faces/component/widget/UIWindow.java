/*
 * $Id: UIWindow.java,v 1.7 2008/03/11 03:21:00 lishaochuan Exp $
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

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

/**
 * @deprecated 此类已废弃
 */
@Deprecated
public class UIWindow extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Window";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Window";

    public UIWindow() {
        setRendererType("org.operamasks.faces.widget.Window");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String jsvar;
    private String icon;
    private String label;
    private Boolean showIcon;
    private Boolean canMove;
    private Boolean canResize;
    private Boolean canMinimize;
    private Boolean canMaximize;
    private Boolean canClose;
    private Integer left;
    private Integer top;
    private Integer width;
    private Integer height;
    private Boolean show;
    private String onbeforeclose;
    private String onclose;
    private String style;
    private String styleClass;
    private String captionStyle;
    private String captionStyleClass;
    private String labelStyle;
    private String labelStyleClass;
    private String contentPaneStyle;
    private String contentPaneStyleClass;

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

    public String getIcon() {
        if (this.icon != null) {
            return this.icon;
        }
        ValueExpression ve = getValueExpression("icon");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        if (this.label != null) {
            return this.label;
        }
        ValueExpression ve = getValueExpression("label");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean getShowIcon() {
        if (this.showIcon != null) {
            return this.showIcon;
        }
        ValueExpression ve = getValueExpression("showIcon");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public boolean getCanMove() {
        if (this.canMove != null) {
            return this.canMove;
        }
        ValueExpression ve = getValueExpression("canMove");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean getCanResize() {
        if (this.canResize != null) {
            return this.canResize;
        }
        ValueExpression ve = getValueExpression("canResize");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setCanResize(boolean canResize) {
        this.canResize = canResize;
    }

    public boolean getCanMinimize() {
        if (this.canMinimize != null) {
            return this.canMinimize;
        }
        ValueExpression ve = getValueExpression("canMinimize");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setCanMinimize(boolean canMinimize) {
        this.canMinimize = canMinimize;
    }

    public boolean getCanMaximize() {
        if (this.canMaximize != null) {
            return this.canMaximize;
        }
        ValueExpression ve = getValueExpression("canMaximize");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setCanMaximize(boolean canMaximize) {
        this.canMaximize = canMaximize;
    }

    public boolean getCanClose() {
        if (this.canClose != null) {
            return this.canClose;
        }
        ValueExpression ve = getValueExpression("canClose");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }

    public int getLeft() {
        if (this.left != null) {
            return this.left;
        }
        ValueExpression ve = getValueExpression("left");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 0;
        }
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        if (this.top != null) {
            return this.top;
        }
        ValueExpression ve = getValueExpression("top");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 0;
        }
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        if (this.width != null) {
            return this.width;
        }
        ValueExpression ve = getValueExpression("width");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        if (this.height != null) {
            return this.height;
        }
        ValueExpression ve = getValueExpression("height");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean getShow() {
        if (this.show != null) {
            return this.show;
        }
        ValueExpression ve = getValueExpression("show");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getOnbeforeclose() {
        if (this.onbeforeclose != null) {
            return this.onbeforeclose;
        }
        ValueExpression ve = getValueExpression("onbeforeclose");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnbeforeclose(String onbeforeclose) {
        this.onbeforeclose = onbeforeclose;
    }

    public String getOnclose() {
        if (this.onclose != null) {
            return this.onclose;
        }
        ValueExpression ve = getValueExpression("onclose");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setOnclose(String onclose) {
        this.onclose = onclose;
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

    public String getCaptionStyle() {
        if (this.captionStyle != null) {
            return this.captionStyle;
        }
        ValueExpression ve = getValueExpression("captionStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCaptionStyle(String captionStyle) {
        this.captionStyle = captionStyle;
    }

    public String getCaptionStyleClass() {
        if (this.captionStyleClass != null) {
            return this.captionStyleClass;
        }
        ValueExpression ve = getValueExpression("captionStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCaptionStyleClass(String captionStyleClass) {
        this.captionStyleClass = captionStyleClass;
    }

    public String getLabelStyle() {
        if (this.labelStyle != null) {
            return this.labelStyle;
        }
        ValueExpression ve = getValueExpression("labelStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabelStyle(String labelStyle) {
        this.labelStyle = labelStyle;
    }

    public String getLabelStyleClass() {
        if (this.labelStyleClass != null) {
            return this.labelStyleClass;
        }
        ValueExpression ve = getValueExpression("labelStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabelStyleClass(String labelStyleClass) {
        this.labelStyleClass = labelStyleClass;
    }

    public String getContentPaneStyle() {
        if (this.contentPaneStyle != null) {
            return this.contentPaneStyle;
        }
        ValueExpression ve = getValueExpression("contentPaneStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContentPaneStyle(String contentPaneStyle) {
        this.contentPaneStyle = contentPaneStyle;
    }

    public String getContentPaneStyleClass() {
        if (this.contentPaneStyleClass != null) {
            return this.contentPaneStyleClass;
        }
        ValueExpression ve = getValueExpression("contentPaneStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContentPaneStyleClass(String contentPaneStyleClass) {
        this.contentPaneStyleClass = contentPaneStyleClass;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            icon,
            label,
            showIcon,
            canMove,
            canResize,
            canMinimize,
            canMaximize,
            canClose,
            left,
            top,
            width,
            height,
            show,
            onbeforeclose,
            onclose,
            style,
            styleClass,
            captionStyle,
            captionStyleClass,
            labelStyle,
            labelStyleClass,
            contentPaneStyle,
            contentPaneStyleClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        icon = (String)values[i++];
        label = (String)values[i++];
        showIcon = (Boolean)values[i++];
        canMove = (Boolean)values[i++];
        canResize = (Boolean)values[i++];
        canMinimize = (Boolean)values[i++];
        canMaximize = (Boolean)values[i++];
        canClose = (Boolean)values[i++];
        left = (Integer)values[i++];
        top = (Integer)values[i++];
        width = (Integer)values[i++];
        height = (Integer)values[i++];
        show = (Boolean)values[i++];
        onbeforeclose = (String)values[i++];
        onclose = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        captionStyle = (String)values[i++];
        captionStyleClass = (String)values[i++];
        labelStyle = (String)values[i++];
        labelStyleClass = (String)values[i++];
        contentPaneStyle = (String)values[i++];
        contentPaneStyleClass = (String)values[i++];
    }
}
