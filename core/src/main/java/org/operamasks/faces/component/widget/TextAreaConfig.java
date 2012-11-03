/*
 * $Id: TextAreaConfig.java,v 1.5 2008/03/11 03:21:00 lishaochuan Exp $
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

package org.operamasks.faces.component.widget;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class TextAreaConfig extends TextFieldConfig {
    private static final long serialVersionUID = -791767949359393514L;

    public Boolean getPreventScrollbars() {
        return get("preventScrollbars", null);
    }

    public void setPreventScrollbars(Boolean value) {
        set("preventScrollbars", value);
    }
}
