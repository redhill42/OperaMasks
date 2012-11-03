/*
 * $Id: UITabLayoutBase.java,v 1.3 2008/03/20 01:44:09 lishaochuan Exp $
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
package org.operamasks.faces.component.layout.base;


import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.layout.impl.UILayout;
import org.operamasks.faces.render.layout.TabLayoutRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="tabLayout")
@Component(renderHandler=TabLayoutRenderHandler.class)
public abstract class UITabLayoutBase extends UILayout {
    @ExtConfigOption protected Integer activeTab;
    @ExtConfigOption protected Boolean animScroll;
    @ExtConfigOption protected String autoTabSelector;
    @ExtConfigOption protected Boolean autoTabs;
    @ExtConfigOption protected Boolean deferredRender;
    @ExtConfigOption protected Boolean enableTabScroll;
    @ExtConfigOption protected Integer minTabWidth;
    @ExtConfigOption protected Boolean monitorResize;
    @ExtConfigOption protected Boolean plain;
    @ExtConfigOption protected Boolean resizeTabs;
    @ExtConfigOption protected Boolean scrollDuration;
    @ExtConfigOption protected Integer scrollIncrement;
    @ExtConfigOption protected Integer scrollRepeatInterval;
    @ExtConfigOption protected Integer tabMargin;
    @ExtConfigOption protected String tabPosition;
    @ExtConfigOption protected Integer tabWidth;
    @ExtConfigOption protected Integer wheelIncrement;
}
