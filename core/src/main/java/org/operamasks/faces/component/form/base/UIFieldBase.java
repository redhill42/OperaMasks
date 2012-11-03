/*
 * $Id: UIFieldBase.java,v 1.13 2008/04/24 05:48:47 patrick Exp $
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
package org.operamasks.faces.component.form.base;

import static org.operamasks.resources.Resources.UI_MISSING_PARENT_FORM_WARNING;
import static org.operamasks.resources.Resources.UI_UNEXPECTED_ATTRIBUTE_VALUE;
import static org.operamasks.resources.Resources._T;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.UIInput;

import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.SensitivePropertyChecker.Sensitive;
import org.operamasks.faces.component.SensitivePropertyChecker.SensitiveProperties;
import org.operamasks.faces.component.widget.FormMessageTarget;
import org.operamasks.faces.component.widget.UIForm;
import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.event.ModelEvent;
import org.operamasks.faces.event.ModelEventListener;
import org.operamasks.faces.event.ThreadLocalEventBroadcaster;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.tools.annotation.ComponentMeta;
import org.operamasks.faces.util.FacesUtils;

@ComponentMeta(tagBaseClass="org.operamasks.faces.webapp.html.HtmlBasicELTag")
public abstract class UIFieldBase extends UIInput implements ModelEventListener 
{
    @ExtConfigOption protected String autoCreate;
    @ExtConfigOption protected String clearCls;
    @ExtConfigOption protected String cls;
    @ExtConfigOption @Sensitive protected Boolean disabled;
    @ExtConfigOption protected String fieldClass;
    @ExtConfigOption protected String fieldLabel;
    @ExtConfigOption protected String focusClass;
    @ExtConfigOption protected Boolean hideLabel;
    @ExtConfigOption protected String inputType;
    @ExtConfigOption protected String invalidClass;
    @ExtConfigOption protected String invalidText;
    @ExtConfigOption protected String itemCls;
    @ExtConfigOption protected String labelSeparator;
    @ExtConfigOption protected String labelStyle;
    @ExtConfigOption protected String msgFx;
    @ExtConfigOption protected String msgTarget;
    @ExtConfigOption protected String name;
    @ExtConfigOption protected Boolean readOnly;
    @ExtConfigOption protected Integer tabIndex;
    @ExtConfigOption protected Boolean validateOnBlur;
    @ExtConfigOption protected Boolean validationDelay;
    @ExtConfigOption protected String validationEvent;
    
    //from BoxComponent
    @ExtConfigOption protected Boolean autoWidth;
	@ExtConfigOption protected Boolean autoHeight;
    @ExtConfigOption @Sensitive protected Integer height;
    @ExtConfigOption @Sensitive protected Integer width;
    
    //event
    @AjaxActionEvent protected String onfocus;
    @AjaxActionEvent protected String onblur;
    @AjaxActionEvent protected String onchange;
    @AjaxActionEvent protected String ondisable;
    @AjaxActionEvent protected String onenable;
    @AjaxActionEvent protected String onshow;
    @AjaxActionEvent protected String onhide;
    
    public UIFieldBase() {
        ThreadLocalEventBroadcaster.getInstance().addEventListenerOnce(EventTypes.BEFORE_RENDER_VIEW, this);
    }
    
	public void setDisabled(Boolean value) {
		this.disabled = value;
		if(this.disabled){
			disable();
		}else{
			enable();
		}
	}
    
    public java.lang.Boolean getDisabled() {
		if (this.disabled != null) {
			return this.disabled;
		}
		javax.el.ValueExpression ve = this.getValueExpression("disabled");
		if (ve != null) {
			try {
				return (java.lang.Boolean) ve.getValue(this.getFacesContext()
						.getELContext());
			} catch (javax.el.ELException e) {
				throw new javax.faces.FacesException(e);
			}
		}
		return null;
	}
    
    protected static final Logger logger = Logger.getLogger("org.operamasks.faces.view"); 

    public void processModelEvent(ModelEvent event) {
        if (EventTypes.BEFORE_RENDER_VIEW.equals(event.getEventType())) {
            if (logger.isLoggable(Level.FINE) && FacesUtils.getParentForm(this) == null) {
                logger.fine(_T(UI_MISSING_PARENT_FORM_WARNING, FacesUtils.getComponentDesc(this)));
            }
        }
        if(msgTarget == null){
            UIForm form = (UIForm)FormRenderer.getParentForm(this);
            if(form != null){
                msgTarget = form.getMessageTarget();
            }
        }
        if(msgTarget != null && !FormMessageTarget.isSupport(msgTarget)){
            throw new FacesException(_T(UI_UNEXPECTED_ATTRIBUTE_VALUE, 
                    FacesUtils.getComponentDesc(this), "msgTarget", msgTarget, 
                    FormMessageTarget.getSupportTargets()));
        }
    }
    
    @Operation
    public void enable(){
    	this.disabled = false;
    }
    
    @Operation
    public void disable(){
    	this.disabled = true;
    }
    
    @Operation
    public void show(){
    }
    
    @Operation
    public void hide(){
    }
    
    @Operation
    public void focus(){
    }
}
