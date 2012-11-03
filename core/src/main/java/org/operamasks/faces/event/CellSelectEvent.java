/*
 * $Id: CellSelectEvent.java,v 1.3 2007/07/02 07:38:18 jacky Exp $
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

package org.operamasks.faces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesListener;
import javax.faces.event.FacesEvent;

public class CellSelectEvent extends FacesEvent
{
    private int rowIndex;
    private int colIndex;

    public CellSelectEvent(UIComponent component, int rowIndex) {
        this(component, rowIndex, -1);
    }

    public CellSelectEvent(UIComponent component, int rowIndex, int colIndex) {
        super(component);
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return colIndex;
    }

    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof CellSelectListener);
    }

    public void processListener(FacesListener listener) {
        ((CellSelectListener)listener).processCellSelect(this);
    }
}
