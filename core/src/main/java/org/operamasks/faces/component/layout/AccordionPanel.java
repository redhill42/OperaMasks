/*
 * $Id:
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
 * @deprecated 此类已经废弃
 */
@Deprecated
public class AccordionPanel extends UIFacelet{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.Panel";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.Panel";
    public static final String RENDERER_TYPE = "org.operamasks.faces.layout.Panel";
    
    public AccordionPanel() {
        setRendererType( RENDERER_TYPE ) ;
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    private Boolean animate         ;
    private String  bodyClass       ;
    private Boolean collapsed       ;
    private Boolean collapseOnUnpin ;
    private Boolean collapsible     ;
    private Boolean draggable       ;
    private String  duration        ;
    private String  easingCollapse  ;
    private String  easingExpand    ;
    private String  icon            ;
    private String  minWidth        ;
    private String  maxWidth        ;
    private String  minHeight       ;
    private String  maxHeight       ;
    private String  panelClass      ;
    private Boolean pinned          ;
    private Boolean resizable       ;
    private String  shadowMode      ;
    private Boolean showPin         ;
    private String  trigger         ;
    private Boolean useShadow       ;
    private String  jsvar           ;
    private String  title           ;
    private Boolean autoScroll      ;

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
    
    public String getTitle() {
        if (this.title != null) {
            return this.title;
        }
        ValueExpression ve = getValueExpression("title");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAnimate() {
        if (this.animate != null ) {
            return this.animate;
        }
        ValueExpression ve = getValueExpression("animate");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAnimate(Boolean animate) {
        this.animate = animate;
    }

    public Boolean getAutoScroll() {
        if (this.autoScroll != null ) {
            return this.autoScroll;
        }
        ValueExpression ve = getValueExpression("autoScroll");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAutoScroll(Boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public String getBodyClass() {
        if (this.bodyClass != null) {
            return this.bodyClass;
        }
        ValueExpression ve = getValueExpression("bodyClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBodyClass(String bodyClass) {
        this.bodyClass = bodyClass;
    }

    public Boolean getCollapsed() {
        if (this.collapsed != null ) {
            return this.collapsed;
        }
        ValueExpression ve = getValueExpression("collapsed");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    public Boolean getCollapseOnUnpin() {
        if (this.collapseOnUnpin != null ) {
            return this.collapseOnUnpin;
        }
        ValueExpression ve = getValueExpression("collapseOnUnpin");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCollapseOnUnpin(Boolean collapseOnUnpin) {
        this.collapseOnUnpin = collapseOnUnpin;
    }

    public Boolean getCollapsible() {
        if (this.collapsible != null ) {
            return this.collapsible;
        }
        ValueExpression ve = getValueExpression("collapsible");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCollapsible(Boolean collapsible) {
        this.collapsible = collapsible;
    }

    public Boolean getDraggable() {
        if (this.draggable != null ) {
            return this.draggable;
        }
        ValueExpression ve = getValueExpression("draggable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDraggable(Boolean draggable) {
        this.draggable = draggable;
    }

    public String getDuration() {
        if (this.duration != null) {
            return this.duration;
        }
        ValueExpression ve = getValueExpression("duration");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEasingCollapse() {
        if (this.easingCollapse != null) {
            return this.easingCollapse;
        }
        ValueExpression ve = getValueExpression("easingCollapse");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setEasingCollapse(String easingCollapse) {
        this.easingCollapse = easingCollapse;
    }

    public String getEasingExpand() {
        if (this.easingExpand != null) {
            return this.easingExpand;
        }
        ValueExpression ve = getValueExpression("easingExpand");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setEasingExpand(String easingExpand) {
        this.easingExpand = easingExpand;
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

    public String getMinWidth() {
        if (this.minWidth != null) {
            return this.minWidth;
        }
        ValueExpression ve = getValueExpression("minWidth");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMinWidth(String minWidth) {
        this.minWidth = minWidth;
    }

    public String getMaxWidth() {
        if (this.maxWidth != null) {
            return this.maxWidth;
        }
        ValueExpression ve = getValueExpression("maxWidth");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public String getMinHeight() {
        if (this.minHeight != null) {
            return this.minHeight;
        }
        ValueExpression ve = getValueExpression("minHeight");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
   }

    public void setMinHeight(String minHeight) {
        this.minHeight = minHeight;
    }

    public String getMaxHeight() {
        if (this.maxHeight != null) {
            return this.maxHeight;
        }
        ValueExpression ve = getValueExpression("maxHeight");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setMaxHeight(String maxHeight) {
        this.maxHeight = maxHeight;
    }

    public String getPanelClass() {
        if (this.panelClass != null) {
            return this.panelClass;
        }
        ValueExpression ve = getValueExpression("panelClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public Boolean getPinned() {
        if (this.pinned != null ) {
            return this.pinned;
        }
        ValueExpression ve = getValueExpression("pinned");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public Boolean getResizable() {
        if (this.resizable != null ) {
            return this.resizable;
        }
        ValueExpression ve = getValueExpression("resizable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
    }

    public String getShadowMode() {
        if (this.shadowMode != null) {
            return this.shadowMode;
        }
        ValueExpression ve = getValueExpression("shadowMode");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setShadowMode(String shadowMode) {
        this.shadowMode = shadowMode;
    }

    public Boolean getShowPin() {
        if (this.showPin != null ) {
            return this.showPin;
        }
        ValueExpression ve = getValueExpression("showPin");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setShowPin(Boolean showPin) {
        this.showPin = showPin;
    }

    public String getTrigger() {
        if (this.trigger != null) {
            return this.trigger;
        }
        ValueExpression ve = getValueExpression("trigger");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Boolean getUseShadow() {
        if (this.useShadow != null ) {
            return this.useShadow;
        }
        ValueExpression ve = getValueExpression("useShadow");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setUseShadow(Boolean useShadow) {
        this.useShadow = useShadow;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            animate         ,
            bodyClass       ,
            collapsed       ,
            collapseOnUnpin ,
            collapsible     ,
            draggable       ,
            duration        ,
            easingCollapse  ,
            easingExpand    ,
            icon            ,
            minWidth        ,
            maxWidth        ,
            minHeight       ,
            maxHeight       ,
            panelClass      ,
            pinned          ,
            resizable       ,
            shadowMode      ,
            showPin         ,
            trigger         ,
            useShadow       ,
            jsvar           ,
            title           
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        animate         = (Boolean)values[i++] ;
        bodyClass       = (String)values[i++] ;
        collapsed       = (Boolean)values[i++] ;
        collapseOnUnpin = (Boolean)values[i++] ;
        collapsible     = (Boolean)values[i++] ;
        draggable       = (Boolean)values[i++] ;
        duration        = (String)values[i++] ;
        easingCollapse  = (String)values[i++] ;
        easingExpand    = (String)values[i++] ;
        icon            = (String)values[i++] ;
        minWidth        = (String)values[i++] ;
        maxWidth        = (String)values[i++] ;
        minHeight       = (String)values[i++] ;
        maxHeight       = (String)values[i++] ;
        panelClass      = (String)values[i++] ;
        pinned          = (Boolean)values[i++] ;
        resizable       = (Boolean)values[i++] ;
        shadowMode      = (String)values[i++] ;
        showPin         = (Boolean)values[i++] ;
        trigger         = (String)values[i++] ;
        useShadow       = (Boolean)values[i++] ;
        jsvar           = (String)values[i++] ;
        title           = (String)values[i++] ;
    }

}
