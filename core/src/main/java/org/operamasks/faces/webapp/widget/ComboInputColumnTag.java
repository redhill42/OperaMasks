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

import org.operamasks.faces.component.widget.grid.ComboInputColumn;
/**
 * @jsp.tag name="comboInputColumn" body-content="JSP"
 */

public class ComboInputColumnTag extends ComboTag 
{
    @Override
    public String getComponentType() {
        return ComboInputColumn.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return null;
    }
}
