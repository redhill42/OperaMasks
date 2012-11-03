/*
 * $Id: UINumberFieldBase.java,v 1.2 2008/03/11 02:51:18 lishaochuan Exp $
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
import org.operamasks.faces.component.form.impl.UITextField;
import org.operamasks.faces.render.form.NumberFieldRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="numberField")
@Component(renderHandler=NumberFieldRenderHandler.class)
public abstract class UINumberFieldBase extends UITextField
{
    @ExtConfigOption protected Boolean allowDecimals;
    @ExtConfigOption protected Boolean allowNegative;
    @ExtConfigOption protected String baseChars;
    @ExtConfigOption protected Integer decimalPrecision;
    @ExtConfigOption protected String decimalSeparator;
    @ExtConfigOption protected String fieldClass;
    @ExtConfigOption protected String maxText;
    @ExtConfigOption protected Integer maxValue ;
    @ExtConfigOption protected String minText;
    @ExtConfigOption protected Integer minValue ;
    @ExtConfigOption protected String nanText;
}
