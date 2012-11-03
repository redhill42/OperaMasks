/*
 * $Id: UISlideMessage.java,v 1.4 2007/07/02 07:37:44 jacky Exp $
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

import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;


public class UISlideMessage extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.SlideMessage";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.SlideMessage";
    
    private Integer effectTime;
    private Integer pauseTime;
    private String position;
    private List<MessageBean> messages;
    
    public UISlideMessage() {
        setRendererType("org.operamasks.faces.widget.SlideMessage");
    }

    /*
     * effectTime
     */
    public Integer getEffectTime() {
        return (Integer)getProperty(effectTime, "effectTime");
    }
    public void setEffectTime(Integer effectTime) {
        this.effectTime = effectTime;
    }
    
    /*
     * pauseTime
     */
    public Integer getPauseTime() {
        return (Integer)getProperty(pauseTime, "pauseTime");
    }
    public void setPauseTime(Integer pauseTime) {
        this.pauseTime = pauseTime;
    }
    
    /*
     * position
     */
    public String getPosition() {
        return (String)getProperty(position, "position");
    }
    public void setPosition(String position) {
        this.position = position;
    }


    private Object getProperty(Object property, String veName) {
        if (property != null)
            return property;
        
        ValueExpression ve = getValueExpression(veName);
        
        if (ve != null)
            return ve.getValue(getFacesContext().getELContext());
        
        return null;
    }
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @SuppressWarnings("unchecked")
    public List<MessageBean> getMessages() {
        Object message = getProperty(messages, "messages");
        if (message instanceof List) {
            List<MessageBean> messageList = (List<MessageBean>) message;
            return messageList;
        }
        //其他类型
        
        return null;
    }
}
