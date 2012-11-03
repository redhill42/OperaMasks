/*
 * $Id: UISimpleHtmlEditorBase.java,v 1.2 2008/03/11 02:51:18 lishaochuan Exp $
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
import org.operamasks.faces.render.form.SimpleHtmlEditorRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="simpleHtmlEditor")
@Component(renderHandler=SimpleHtmlEditorRenderHandler.class)
public abstract class UISimpleHtmlEditorBase extends UIField
{
    @ExtConfigOption protected String createLinkText;
    @ExtConfigOption protected String defaultLinkValue;
    @ExtConfigOption protected Boolean enableAlignments;
    @ExtConfigOption protected Boolean enableColors;
    @ExtConfigOption protected Boolean enableFont;
    @ExtConfigOption protected Boolean enableFontSize;
    @ExtConfigOption protected Boolean enableFormat;
    @ExtConfigOption protected Boolean enableLinks;
    @ExtConfigOption protected Boolean enableLists;
    @ExtConfigOption protected Boolean enableSourceEdit;
    @ExtConfigOption protected String fontFamilies;
}
