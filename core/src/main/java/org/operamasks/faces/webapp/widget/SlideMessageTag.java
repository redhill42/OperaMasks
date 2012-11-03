/*
 * $Id: SlideMessageTag.java,v 1.4 2007/07/02 07:37:58 jacky Exp $
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
package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;

import org.operamasks.faces.component.widget.UISlideMessage;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * The jsp tag displays slide messages.
 * @jsp.tag name = "slideMessage" body-content = "empty"
 */
public class SlideMessageTag extends HtmlBasicELTag {
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.SlideMessage";
	/**
	 * @jsp.attribute name="effectTime" required = "false" type = "java.lang.Integer"
	 */
	public void setEffectTime(ValueExpression effectTime) {
		setValueExpression("effectTime", effectTime);
	}
	
	/**
	 * @jsp.attribute name="pauseTime" required = "false" type = "java.lang.Integer"
	 */
	public void setPauseTime(ValueExpression pauseTime) {
		setValueExpression("pauseTime", pauseTime);
	}
	
	/**
	 * @jsp.attribute name="messages" required = "true" type = "java.lang.Object"
	 */
	
	public void setMessages(ValueExpression messages) {
		setValueExpression("messages", messages);
	}
	
	@Override
	public String getComponentType() {
		return UISlideMessage.COMPONENT_TYPE;
	}
	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
    
    
}