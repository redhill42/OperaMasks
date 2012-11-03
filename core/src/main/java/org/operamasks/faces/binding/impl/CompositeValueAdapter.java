/*
 * $Id: CompositeValueAdapter.java,v 1.1 2007/09/25 22:06:35 daniel Exp $
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
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.faces.event.PhaseId;
import java.util.List;
import java.util.ArrayList;

class CompositeValueAdapter extends AbstractValueAdapter
{
    private List<ValueExpression> bindings;
    private PhaseId phaseId;

    public CompositeValueAdapter() {
        this.bindings = new ArrayList<ValueExpression>();
    }
    
    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addValueBinding(ValueExpression binding) {
        this.bindings.add(binding);
    }

    public Object getValue(ELContext context) {
        for (ValueExpression b : this.bindings) {
            Object result = b.getValue(context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object value) {
        boolean readonly = true;
        for (ValueExpression b : this.bindings) {
            if (!b.isReadOnly(context)) {
                b.setValue(context, value);
                readonly = false;
            }
        }

        if (readonly) {
            throw new PropertyNotWritableException();
        }
    }

    public boolean isReadOnly(ELContext context) {
        return false;
    }

    public Class<?> getType(ELContext context) {
        for (ValueExpression b : this.bindings) {
            Class<?> type = b.getType(context);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public Class<?> getExpectedType() {
        for (ValueExpression b : this.bindings) {
            Class<?> type = b.getExpectedType();
            if (type != null) {
                return type;
            }
        }
        return null;
    }
}
