/*
 * $Id: CheckTreeNodeModel.java,v 1.4 2008/01/16 02:45:40 yangdong Exp $
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

package org.operamasks.faces.component.widget.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;

public class CheckTreeNodeModel extends SimpleCheckTreeNodeModel implements TreeNodeModel {

	public UITreeNode createTreeNode() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	if (context == null)
    		throw new IllegalArgumentException("Null FacesContext");
    	
    	UICheckTreeNode node = (UICheckTreeNode)context.getApplication(
       			).createComponent(UICheckTreeNode.COMPONENT_TYPE);

       	return node;
	}

	public TreeEventType[] getEventTypes() {
		List<TreeEventType> eventTypes = new ArrayList<TreeEventType>(
				Arrays.asList(super.getEventTypes()));
		eventTypes.add(UICheckTreeNode.CHECK_STATE_CHANGED);
		
		return eventTypes.toArray(new TreeEventType[eventTypes.size()]);
	}

	public Class<? extends UITreeNode> getNodeClass() {
		return UICheckTreeNode.class;
	}
}