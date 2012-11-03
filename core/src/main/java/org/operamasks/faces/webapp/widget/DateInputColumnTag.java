/*
 * $Id 
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

import org.operamasks.faces.component.widget.grid.DateInputColumn;

/**
 * @jsp.tag name="dateInputColumn" body-content="JSP"
 * description_zh_CN="一个可以通过点击下拉按钮弹出日历，允许在日历上方便选择日期的日期输入框，具有日期验证功能。" 
 */
public class DateInputColumnTag extends DateFieldTag
{
    @Override
    public String getComponentType() {
        return DateInputColumn.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return null;
    }
}
