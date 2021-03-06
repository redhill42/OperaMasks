/*
 * $Id: UIRadioGroupBase.java,v 1.5 2008/04/29 07:09:04 lishaochuan Exp $
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

import javax.faces.component.UIInput;

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.render.form.RadioGroupRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="radioGroup",tagBaseClass="org.operamasks.faces.webapp.html.HtmlBasicELTag")
@Component(renderHandler=RadioGroupRenderHandler.class)
public abstract class UIRadioGroupBase extends UIInput 
{
	protected String direction;
	
	//event
	protected String onchange;
}
