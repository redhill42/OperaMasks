package org.operamasks.faces.component.widget.tree.event;

import java.util.ArrayList;
import java.util.List;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;

@SuppressWarnings("serial")
public class CheckStateChangedEvent extends TreeEvent {
	private List<CheckStateChange> checkStateChanges;
	
	public CheckStateChangedEvent(UITree source, UITreeNode affectedNode) {
		super(source, affectedNode);
	}

	@Override
	public TreeEventType getEventType() {
		return UICheckTreeNode.CHECK_STATE_CHANGED;
	}

	public List<CheckStateChange> getCheckStateChanges() {
		if (checkStateChanges == null)
			checkStateChanges = new ArrayList<CheckStateChange>();
		
		return checkStateChanges;
	}

	public void setCheckStateChanges(List<CheckStateChange> checkStateChanges) {
		this.checkStateChanges = checkStateChanges;
	}
}
