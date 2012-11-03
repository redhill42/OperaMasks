/*
 * $Id: RowDataValueAdapter.java,v 1.1 2007/09/25 22:06:35 daniel Exp $
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
package org.operamasks.faces.binding.impl;

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.faces.component.UIData;

class RowDataValueAdapter extends AbstractValueAdapter
{
    private final UIData data;

    public RowDataValueAdapter(UIData data) {
        this.data = data;
    }

    public Object getValue(ELContext context) {
        if (this.data.isRowAvailable()) {
            return this.data.getRowData();
        } else {
            return null;
        }
    }

    public void setValue(ELContext context, Object value) {
        throw new PropertyNotWritableException();
    }

    public boolean isReadOnly(ELContext context) {
        return true;
    }

    public Class<?> getType(ELContext context) {
        return Object.class;
    }

    public Class<?> getExpectedType() {
        return Object.class;
    }
}
