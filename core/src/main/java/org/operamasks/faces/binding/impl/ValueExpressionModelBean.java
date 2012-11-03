/*
 * $Id: ValueExpressionModelBean.java,v 1.1 2007/10/19 10:29:38 daniel Exp $
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.operamasks.faces.binding.ModelBean;

public class ValueExpressionModelBean extends DelegatingModelBean
{
    private ValueExpression value;
    private ModelBean delegate;

    public ValueExpressionModelBean(ValueExpression value) {
        this.value = value;
    }

    protected ModelBean getDelegate() {
        if (this.delegate == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            Object target = this.value.getValue(fc.getELContext());

            if (target == null) {
                return NULL_MODEL_BEAN;
            } else {
                this.delegate = ModelBean.wrap(target);
            }
        }

        return this.delegate;
    }


    public boolean equals(Object obj) {
        if (obj instanceof ValueExpressionModelBean) {
            ValueExpressionModelBean other = (ValueExpressionModelBean)obj;
            return this.value.equals(other.value);
        }

        return getDelegate().equals(obj);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}
