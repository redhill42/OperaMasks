/*
 * $Id: DataColumn.java,v 1.4 2008/04/21 18:10:24 jacky Exp $
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
package org.operamasks.faces.component.data;

import java.io.Serializable;
import java.util.Formatter;

@SuppressWarnings("serial")
public class DataColumn implements Serializable
{
    private String id;
    private String caption;
    private Class<?> type;
    private boolean isPrimaryKey;
    
    public DataColumn(String id, Class<?> type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
    
    @Override
    public String toString() {
        return String.format("[id=%s, caption=%s, type=%s, isPrimaryKey=%b]", id, caption, type.getName(), isPrimaryKey);
    }

}
