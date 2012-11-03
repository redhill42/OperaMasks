/*
 * $Id: ELSelectItemsValueAdapter.java,v 1.1 2007/12/22 17:19:55 daniel Exp $
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

public class ELSelectItemsValueAdapter extends AbstractValueAdapter
{
    private final ELSelectItemsBinding binding;
    private final ELiteBean bean;

    public ELSelectItemsValueAdapter(ELSelectItemsBinding binding, ELiteBean bean) {
        this.binding = binding;
        this.bean = bean;
    }

    public Object getValue(ELContext ctx) {
        return binding.getValue(ctx, bean);
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
