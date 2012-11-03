/*
 * $Id: FieldValueAdapter.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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
import javax.el.ELException;
import javax.el.PropertyNotWritableException;
import java.lang.reflect.Field;

import org.operamasks.faces.binding.ModelBean;

class FieldValueAdapter extends AbstractValueAdapter
{
    private final ModelBean bean;
    private final Field field;
    private final boolean readonly;

    public FieldValueAdapter(ModelBean bean, Field field, boolean readonly) {
        this.bean = bean;
        this.field = field;
        this.readonly = readonly;
    }

    public Object getValue(ELContext context) {
        try {
            return this.bean.getField(this.field);
        } catch (Exception ex) {
            throw new ELException(ex);
        }
    }

    public void setValue(ELContext context, Object value) {
        if (this.readonly) {
            throw new PropertyNotWritableException();
        }

        try {
            this.bean.setField(this.field, value);
        } catch (Exception ex) {
            throw new ELException(ex);
        }
    }

    public boolean isReadOnly(ELContext context) {
        return this.readonly;
    }

    public Class<?> getType(ELContext context) {
        return this.field.getType();
    }

    public Class<?> getExpectedType() {
        return this.field.getType();
    }
}
