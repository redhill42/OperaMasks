/*
 * $Id: UIComboBase.java,v 1.2 2008/03/11 02:51:18 lishaochuan Exp $
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
import org.operamasks.faces.component.form.impl.UITriggerField;
import org.operamasks.faces.render.form.ComboRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="combo")
@Component(renderHandler=ComboRenderHandler.class)
public abstract class UIComboBase extends UITriggerField
{
    @ExtConfigOption protected String allQuery;
    @ExtConfigOption protected String displayField;
    @ExtConfigOption protected Boolean editable;
    @ExtConfigOption protected Boolean forceSelection;
    @ExtConfigOption protected Integer handleHeight;
    @ExtConfigOption protected String hiddenName;
    @ExtConfigOption protected Boolean lazyInit;
    @ExtConfigOption protected Boolean lazyRender;
    @ExtConfigOption protected String listAlign;
    @ExtConfigOption protected String listClass;
    @ExtConfigOption protected Integer listWidth;
    @ExtConfigOption protected String loadingText;
    @ExtConfigOption protected Integer minChars;
    @ExtConfigOption protected Integer minListWidth;
    @ExtConfigOption protected String mode;
    @ExtConfigOption protected Integer pageSize;
    @ExtConfigOption protected Integer queryDelay;
    @ExtConfigOption protected String queryParam;
    @ExtConfigOption protected Boolean resizable;
    @ExtConfigOption protected Boolean selectOnFocus;
    @ExtConfigOption protected String selectedClass;
    @ExtConfigOption protected String shadow;
    @ExtConfigOption protected String title;
    @ExtConfigOption protected String tpl;
    @ExtConfigOption protected String transform;
    @ExtConfigOption protected String triggerAction;
    @ExtConfigOption protected String triggerClass;
    @ExtConfigOption protected Boolean typeAhead;
    @ExtConfigOption protected Integer typeAheadDelay;
    @ExtConfigOption protected String valueField;
    @ExtConfigOption protected String valueNotFoundText;
    
    //event
    @AjaxActionEvent  protected String oncollapse;
    @AjaxActionEvent  protected String onexpand;
    @AjaxActionEvent  protected String onselect;
}
