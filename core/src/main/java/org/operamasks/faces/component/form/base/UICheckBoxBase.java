/*
 * $Id: UICheckBoxBase.java,v 1.4 2008/03/27 02:56:04 lishaochuan Exp $
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

import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.render.form.CheckboxRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="checkBox")
@Component(renderHandler=CheckboxRenderHandler.class)
public abstract class UICheckBoxBase extends UIField
{
    @ExtConfigOption protected String boxLabel;
    @ExtConfigOption protected Boolean checked;
    @ExtConfigOption protected String fieldClass;
    @ExtConfigOption protected String focusClass;
    @ExtConfigOption protected String inputValue;
    
    //event
    @AjaxActionEvent protected String oncheck;
}
