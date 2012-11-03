/*
 * $Id: UIDataSeries.java,v 1.6 2007/07/02 07:37:55 jacky Exp $
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

package org.operamasks.faces.component.graph;

import javax.faces.component.UIData;
import javax.faces.component.UIComponent;

public class UIDataSeries extends UIData
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.graph.DataSeries";

    public UIDataSeries() {
        setRendererType(null);
    }

    public UIDataLabel getLabel() {
        for (UIComponent kid : getChildren()) {
            if (kid.isRendered() && (kid instanceof UIDataLabel)) {
                return (UIDataLabel)kid;
            }
        }
        return null;
    }

    public UIDataItem[] getItems() {
        return getItems(UIDataItem.class);
    }

    @SuppressWarnings("unchecked")
    protected <T extends UIDataItem> T[] getItems(Class<T> itemType) {
        int count = 0;
        for (UIComponent kid : getChildren()) {
            if (kid.isRendered() && itemType.isInstance(kid)) {
                count++;
            }
        }

        T[] result = (T[])java.lang.reflect.Array.newInstance(itemType, count);
        if (count > 0) {
            int next = 0;
            for (UIComponent kid : getChildren()) {
                if (kid.isRendered() && itemType.isInstance(kid)) {
                    result[next++] = itemType.cast(kid);
                }
            }
        }
        return result;
    }

    public void resetDataModel() {
        setDataModel(null); // needed by renderer to re-evaluate data model
    }
}
