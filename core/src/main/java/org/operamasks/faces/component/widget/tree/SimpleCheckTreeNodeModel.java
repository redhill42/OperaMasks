package org.operamasks.faces.component.widget.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeCheckEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;

public class SimpleCheckTreeNodeModel extends DefaultTreeNodeModel implements TreeNodeModel {
	public UITreeNode createTreeNode() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	if (context == null)
    		throw new IllegalArgumentException("Null FacesContext");
    	
    	UISimpleCheckTreeNode node = (UISimpleCheckTreeNode)context.getApplication(
       			).createComponent(UISimpleCheckTreeNode.COMPONENT_TYPE);

       	return node;
	}

	public TreeEventType[] getEventTypes() {
		List<TreeEventType> eventTypes = new ArrayList<TreeEventType>(
				Arrays.asList(super.getEventTypes()));
		eventTypes.add(UICheckTreeNode.CHECK);
		
		return eventTypes.toArray(new TreeEventType[eventTypes.size()]);
	}

	public Class<? extends UITreeNode> getNodeClass() {
		return UISimpleCheckTreeNode.class;
	}

	public void processEvent(FacesContext context, UITree tree,
			UITreeNode node, TreeEventType eventType, Map<String, Object> params) {
		if (!UISimpleCheckTreeNode.CHECK.equals(eventType)) {
			super.processEvent(context, tree, node, eventType, params);
			return;
		}
		
		boolean checkType = (Boolean)params.get("checkType");
		
		if (checkType) {
			((UISimpleCheckTreeNode)node).check();
		} else {
			((UISimpleCheckTreeNode)node).uncheck();
		}
		
		if (isRegisteredEvent(tree, eventType)) {
			TreeEvent event = new TreeCheckEvent(tree, node, checkType);
			node.queueEvent(event);
		}
	}
}
