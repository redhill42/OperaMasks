/*
 * $Id: TreeEventType.java,v 1.2 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.component.widget.tree.event;

import java.io.Serializable;
import java.util.Map;

public abstract class TreeEventType implements Serializable {
    public static final String KEY_EVENT_SOURCE = "source";
    public static final String KEY_EVENT_AFFECTED_NODE = "affectedNode";
    
	public abstract TreeEvent createEvent(Map<String, Object> params);
	public abstract String getTypeString();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TreeEventType) {
			TreeEventType other = (TreeEventType)obj;
			
			if (other.getTypeString() == null)
				return false;
			
			return (other.getClass().equals(this.getClass())) &&
					(other.getTypeString().equals(this.getTypeString()));
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode() + getTypeString().hashCode();
	}
	
	@Override
	public String toString() {
		return getTypeString();
	}
}
