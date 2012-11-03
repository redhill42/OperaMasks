/*
 * $Id: UIWindowBase.java,v 1.4 2008/04/16 02:25:46 lishaochuan Exp $
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


import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.layout.impl.UIPanel;
import org.operamasks.faces.render.layout.WindowRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="window")
@Component(renderHandler=WindowRenderHandler.class)
public abstract class UIWindowBase extends UIPanel {
    @ExtConfigOption protected String animateTarget;
    @ExtConfigOption protected String baseCls ;
    @ExtConfigOption protected Boolean closable;
    @ExtConfigOption protected Boolean closeAction;
    @ExtConfigOption protected Boolean constrain;
    @ExtConfigOption protected Boolean constrainHeader;
    @ExtConfigOption protected Boolean expandOnShow;
    @ExtConfigOption protected Boolean maximizable;
    @ExtConfigOption protected Integer minHeight;
    @ExtConfigOption protected Integer minWidth;
    @ExtConfigOption protected Boolean minimizable;
    @ExtConfigOption protected Boolean modal;
    @ExtConfigOption protected String onEsc;
    @ExtConfigOption protected Boolean plain;
    @ExtConfigOption protected Boolean resizable;
    @ExtConfigOption protected String resizeHandles;
    
    @AjaxActionEvent(eventName="onhide") 
    protected String onclose;
    
    @Operation
    public void show(){}
    @Operation("hide")
    public void close(){}
    
    @Operation
    public void setModal(java.lang.Boolean value) {
    	this.modal = value;
    }
}
