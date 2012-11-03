package org.operamasks.faces.component.widget.tree.event;

import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;

public class CheckStateChange {
	private UISimpleCheckTreeNode changedNode;
	private UISimpleCheckTreeNode.CheckType oldCheckType;
	private boolean oldChecked;
	
	public CheckStateChange(UISimpleCheckTreeNode changedNode, UISimpleCheckTreeNode.CheckType oldCheckType, boolean oldChecked) {
		this.changedNode = changedNode;
		this.oldCheckType = oldCheckType;
		this.oldChecked = oldChecked;
	}

	public UISimpleCheckTreeNode getChangedNode() {
		return changedNode;
	}

	public void setChangedNode(UISimpleCheckTreeNode changedNode) {
		this.changedNode = changedNode;
	}

	public UISimpleCheckTreeNode.CheckType getOldCheckType() {
		return oldCheckType;
	}

	public void setOldCheckType(UISimpleCheckTreeNode.CheckType oldCheckType) {
		this.oldCheckType = oldCheckType;
	}

	public boolean isOldChecked() {
		return oldChecked;
	}

	public void setOldChecked(boolean oldChecked) {
		this.oldChecked = oldChecked;
	}
}
