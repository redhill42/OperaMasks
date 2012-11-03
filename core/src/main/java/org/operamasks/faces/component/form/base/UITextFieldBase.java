/*
 * $Id: UITextFieldBase.java,v 1.2 2008/03/11 02:51:18 lishaochuan Exp $
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

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.render.form.TextFieldRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="textField")
@Component(renderHandler=TextFieldRenderHandler.class)
public abstract class UITextFieldBase extends UIField
{
    @ExtConfigOption protected Boolean allowBlank;
    @ExtConfigOption protected String blankText;
    @ExtConfigOption protected Boolean disableKeyFilter;
    @ExtConfigOption protected String emptyClass;
    @ExtConfigOption protected String emptyText;
    @ExtConfigOption protected Boolean grow;
    @ExtConfigOption protected Integer growMax;
    @ExtConfigOption protected Integer growMin;
    @ExtConfigOption protected String maskRe;
    @ExtConfigOption protected Integer maxLength;
    @ExtConfigOption protected String maxLengthText;
    @ExtConfigOption protected Integer minLength;
    @ExtConfigOption protected String minLengthText;
    @ExtConfigOption protected String regex;
    @ExtConfigOption protected String regexText;
    @ExtConfigOption protected Boolean selectOnFocus;
    @ExtConfigOption protected String vtype;
    @ExtConfigOption protected String vtypeText;
    @ExtConfigOption protected String value;
    
}
