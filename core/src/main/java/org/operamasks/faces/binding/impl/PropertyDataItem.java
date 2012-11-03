/*
 * $Id: PropertyDataItem.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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

import javax.el.PropertyNotWritableException;
import org.operamasks.faces.binding.ModelBean;

class PropertyDataItem extends DataItem
{
    private final PropertyBinding binding;

    public PropertyDataItem(PropertyBinding binding) {
        this.binding = binding;
    }

    public Object getValue(ModelBean bean) {
        return binding.getModelValue(bean);
    }

    public void setValue(ModelBean bean, Object value) {
        if (binding.isReadOnly()) {
            throw new PropertyNotWritableException(binding.getName());
        }

        binding.setModelValue(bean, value);
    }

    public boolean isReadOnly() {
        return binding.isReadOnly();
    }

    public Class<?> getType() {
        return binding.getType();
    }
}
