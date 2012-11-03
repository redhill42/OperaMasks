/*
 * $Id: SelectItemsValueAdapter.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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
import javax.faces.model.SelectItem;

import org.operamasks.faces.binding.ModelBean;

class SelectItemsValueAdapter extends AbstractValueAdapter
{
    private final SelectItemsBinding binding;
    private final ModelBean bean;

    public SelectItemsValueAdapter(SelectItemsBinding binding, ModelBean bean) {
        this.binding = binding;
        this.bean = bean;
    }

    public Object getValue(ELContext ctx) {
        return binding.getValue(this.bean);
    }

    public void setValue(ELContext ctx, Object value) {
        throw new PropertyNotWritableException();
    }

    public boolean isReadOnly(ELContext ctx) {
        return true;
    }

    public Class<?> getType(ELContext ctx) {
        return SelectItem[].class;
    }

    public Class<?> getExpectedType() {
        return SelectItem[].class;
    }
}
