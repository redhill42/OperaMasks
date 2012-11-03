/*
 * $Id: UICheckBoxGroupBase.java,v 1.3 2008/03/27 02:55:54 lishaochuan Exp $
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

import javax.faces.component.UIComponentBase;

import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.render.form.CheckBoxGroupRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="checkBoxGroup")
@Component(renderHandler=CheckBoxGroupRenderHandler.class)
public abstract class UICheckBoxGroupBase extends UIComponentBase
{
	protected String direction;
	
	//event
    //@AjaxActionEvent protected String oncheck;
}
