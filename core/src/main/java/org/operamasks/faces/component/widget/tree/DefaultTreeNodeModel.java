/*
 * $Id: DefaultTreeNodeModel.java,v 1.2 2007/12/11 04:20:12 jacky Exp $
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

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;

public class DefaultTreeNodeModel implements TreeNodeModel {

	public UITreeNode createTreeNode() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	if (context == null)
    		throw new IllegalArgumentException("Null FacesContext");
    	
       	UITreeNode node = (UITreeNode)context.getApplication(
       			).createComponent(UITreeNode.COMPONENT_TYPE);

       	return node;
	}

	public TreeEventType[] getEventTypes() {
		return new TreeEventType[] {
				UITreeNode.SELECT,
				UITreeNode.CLICK,
				UITreeNode.DOUBLE_CLICK,
				UITreeNode.EXPAND	,
				UITreeNode.COLLAPSE
		};
	}

	public void processEvent(FacesContext context, UITree tree,
			UITreeNode node, TreeEventType eventType, Map<String, Object> params) {
		if (eventType.equals(UITreeNode.COLLAPSE)) {
			node.collapse();
		} else if (eventType.equals(UITreeNode.EXPAND)) {
			if (node.getAllowsChildren() && node.getChildCount() == 0) {
				tree.loadAsyncNodes(node);
			}
			
			node.expand();
		} else if (isSelectOperation(eventType)) {
			node.select();
		}
		
		if (isRegisteredEvent(tree, eventType)) {
			TreeEvent event = createTreeEvent(tree, node, eventType);
			node.queueEvent(event);
		}
	}
	private boolean isSelectOperation(TreeEventType eventType) {
		return eventType.equals(UITreeNode.SELECT) ||
				eventType.equals(UITreeNode.CLICK) ||
				eventType.equals(UITreeNode.DOUBLE_CLICK);
	}
	
	protected boolean isRegisteredEvent(UITree tree, TreeEventType eventType) {
		if (tree.getRegisteredEventTypes() == null ||
				tree.getRegisteredEventTypes().size() == 0)
			return false;
		
		return tree.getRegisteredEventTypes().contains(eventType);
	}
	
	private TreeEvent createTreeEvent(UITree source, UITreeNode node, TreeEventType eventType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("affectedNode", node);

		return eventType.createEvent(params);
    }

	public Class<? extends UITreeNode> getNodeClass() {
		return UITreeNode.class;
	}
}
