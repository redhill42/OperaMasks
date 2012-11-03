/*
 * $Id: UIPanelBase.java,v 1.7 2008/04/14 07:00:02 lishaochuan Exp $
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
package org.operamasks.faces.component.layout.base;

import javax.faces.component.UIComponentBase;

import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.Container;
import org.operamasks.faces.annotation.component.ContainerItem;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.render.layout.PanelRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="panel", tagBaseClass="org.operamasks.faces.webapp.html.HtmlBasicELTag")
@Container
@ContainerItem
@Component(renderHandler=PanelRenderHandler.class)
public abstract class UIPanelBase extends UIComponentBase
{
    @ExtConfigOption protected Boolean animCollapse;
    @ExtConfigOption protected Boolean autoHeight;
    @ExtConfigOption protected Boolean autoScroll;
    @ExtConfigOption protected Boolean autoShow;
    @ExtConfigOption protected Boolean autoWidth;
    @ExtConfigOption protected Boolean bodyBorder;
    @ExtConfigOption protected String bodyStyle;
    @ExtConfigOption protected Boolean border;
    @ExtConfigOption protected Boolean collapseFirst;
    @ExtConfigOption protected Boolean collapsed;
    @ExtConfigOption protected Boolean collapsible;
    @ExtConfigOption protected Boolean draggable;
    @ExtConfigOption protected Boolean floating;
    @ExtConfigOption protected Boolean footer;
    @ExtConfigOption protected Boolean frame;
    @ExtConfigOption protected Boolean header;
    @ExtConfigOption protected Boolean headerAsText;
    @ExtConfigOption protected Integer height;
    @ExtConfigOption protected Boolean hideBorders;
    @ExtConfigOption protected Boolean hideCollapseTool;
    @ExtConfigOption protected Boolean hideParent;
    @ExtConfigOption protected String layout;
    @ExtConfigOption protected Boolean maskDisabled;
    @ExtConfigOption protected Boolean shadow;
    @ExtConfigOption protected Integer shadowOffset;
    @ExtConfigOption protected Boolean shim;
    @ExtConfigOption protected String title;
    @ExtConfigOption protected Boolean titleCollapse;
    @ExtConfigOption protected Integer width;

    // inner borderLayout
    @ExtConfigOption protected Boolean animFloat ;
    @ExtConfigOption protected Boolean autoHide ;
    @ExtConfigOption protected String cmargins;
    @ExtConfigOption protected String collapseMode ;
    @ExtConfigOption protected Boolean floatable ;
    @ExtConfigOption protected String margins;
    @ExtConfigOption protected String region;
    @ExtConfigOption protected Integer minSize;
    @ExtConfigOption protected Integer maxSize;
    @ExtConfigOption protected Boolean split;

    // inner columnLayout
    @ExtConfigOption protected Float columnWidth;

    // inner tableLayout
    @ExtConfigOption protected Integer rowspan;
    @ExtConfigOption protected Integer colspan;
    
    // inner absoluteLayout
    @ExtConfigOption protected Integer x;
    @ExtConfigOption protected Integer y;
    
    //inner tabLayout
    @ExtConfigOption protected Boolean closable;
    
    //event
    @AjaxActionEvent protected String onactivate;
    
    protected String jsvar;
    
    @Operation
    public void setTitle(String title){
        this.title = title;
    }
    
    public String getTitle() {
        if (this.title != null) {
            return this.title;
        }
        javax.el.ValueExpression ve = this.getValueExpression("title");
        if (ve != null) {
            try {
            return (java.lang.String)ve.getValue(this.getFacesContext().getELContext());
            } catch (javax.el.ELException e) {
            throw new javax.faces.FacesException(e);
            }
        }
        return null;
    }
    
}
