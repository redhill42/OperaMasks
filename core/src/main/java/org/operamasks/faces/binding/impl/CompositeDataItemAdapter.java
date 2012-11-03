/*
 * $Id: CompositeDataItemAdapter.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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
import javax.faces.event.PhaseId;
import javax.faces.component.UIData;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.binding.ModelBean;

class CompositeDataItemAdapter extends AbstractValueAdapter
{
    private UIData data;
    private List<DataItem> bindings;
    private PhaseId phaseId;

    public CompositeDataItemAdapter() {
        this.bindings = new ArrayList<DataItem>();
    }

    public CompositeDataItemAdapter(UIData data) {
        this();
        this.data = data;
    }

    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addDataItem(DataItem dataItem) {
        this.bindings.add(dataItem);
    }

    public Object getValue(ELContext context) {
        if (data.isRowAvailable()) {
            Object target = data.getRowData();
            if (target != null) {
                ModelBean bean = ModelBean.wrap(target);
                for (DataItem item : this.bindings) {
                    Object result = item.getValue(bean);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public void setValue(ELContext context, Object value) {
        if (data.isRowAvailable()) {
            Object target = data.getRowData();
            if (target != null) {
                ModelBean bean = ModelBean.wrap(target);
                for (DataItem item : this.bindings) {
                    if (!item.isReadOnly()) {
                        item.setValue(bean, value);
                    }
                }
            }
        }
    }

    public boolean isReadOnly(ELContext context) {
        return false;
    }

    public Class<?> getType(ELContext context) {
        return getExpectedType();
    }

    public Class<?> getExpectedType() {
        for (DataItem item : this.bindings) {
            Class<?> type = item.getType();
            if (type != null) {
                return type;
            }
        }
        return null;
    }
}
