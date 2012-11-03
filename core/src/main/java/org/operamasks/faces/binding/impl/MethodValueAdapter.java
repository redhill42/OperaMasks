/*
 * $Id: MethodValueAdapter.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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
import java.lang.reflect.Method;

import org.operamasks.faces.binding.ModelBean;

class MethodValueAdapter extends AbstractValueAdapter
{
    private final ModelBean bean;
    private final Method    read;
    private final Method    write;
    private final Class     type;

    public MethodValueAdapter(ModelBean bean, Method read, Method write) {
        this.bean = bean;
        this.read = read;
        this.write = write;

        if (read != null) {
            this.type = read.getReturnType();
        } else if (write != null) {
            this.type = write.getParameterTypes()[0];
        } else {
            this.type = null;
        }
    }

    public Object getValue(ELContext context) {
        if (this.read != null) {
            try {
                return this.bean.invoke(this.read);
            } catch (Exception ex) {
                throw new ELException(ex);
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object value) {
        if (this.write != null) {
            try {
                this.bean.invoke(this.write, value);
            } catch (Exception ex) {
                throw new ELException(ex);
            }
        } else {
            throw new PropertyNotWritableException();
        }
    }

    public boolean isReadOnly(ELContext context) {
        return this.write == null;
    }

    public Class<?> getType(ELContext context) {
        return this.type;
    }

    public Class<?> getExpectedType() {
        return this.type;
    }
}
