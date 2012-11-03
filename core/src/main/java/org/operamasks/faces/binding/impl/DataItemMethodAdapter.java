/*
 * $Id: DataItemMethodAdapter.java,v 1.3 2007/10/15 21:09:47 daniel Exp $
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

import javax.el.MethodInfo;
import javax.el.ELContext;
import javax.el.ELException;
import javax.faces.component.UIData;
import java.lang.reflect.Method;

import org.operamasks.faces.binding.ModelBean;

class DataItemMethodAdapter extends AbstractMethodAdapter
{
    private UIData data;
    private Method method;

    public DataItemMethodAdapter(UIData data, Method method) {
        this.data = data;
        this.method = method;
    }

    public MethodInfo getMethodInfo(ELContext context) {
        return new MethodInfo(this.method.getName(),
                              this.method.getReturnType(),
                              this.method.getParameterTypes());
    }

    public Object invoke(ELContext context, Object[] params) {
        if (data.isRowAvailable()) {
            Object target = data.getRowData();
            if (target != null) {
                try {
                    ModelBean bean = ModelBean.wrap(target);
                    return bean.invokeAction(this.method, params);
                } catch (Exception ex) {
                    throw new ELException(ex);
                }
            }
        }
        return null;
    }
}
