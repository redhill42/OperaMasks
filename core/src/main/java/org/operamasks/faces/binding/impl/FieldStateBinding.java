/*
 * $Id: FieldStateBinding.java,v 1.1 2007/12/21 03:00:24 daniel Exp $
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

import java.lang.reflect.Field;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

class FieldStateBinding extends StateBinding
{
    private Field field;

    FieldStateBinding(String viewId, String key, Field field, boolean inServer) {
        super(viewId, key, field.getType(), inServer);
        field.setAccessible(true);
        this.field = field;
    }

    protected Object getStateValue(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean bean = mbc.getModelBean();
        return bean.getField(this.field);
    }

    protected void setStateValue(FacesContext ctx, ModelBindingContext mbc, Object value) {
        ModelBean bean = mbc.getModelBean();
        bean.setField(this.field, value);
    }

    protected ValueExpression createValueAdapter(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean bean = mbc.getModelBean();
        return new FieldValueAdapter(bean, this.field, false);
    }
}
