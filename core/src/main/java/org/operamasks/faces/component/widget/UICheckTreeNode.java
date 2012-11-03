package org.operamasks.faces.component.widget;

import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.component.widget.tree.event.CheckStateChange;
import org.operamasks.faces.component.widget.tree.event.CheckStateChangedEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.util.FacesUtils;

@SuppressWarnings("serial")
public class UICheckTreeNode extends UISimpleCheckTreeNode {
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.CheckTreeNode";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.CheckTreeNode";
    
    public static transient final TreeEventType CHECK_STATE_CHANGED = new TreeEventType() {
		@SuppressWarnings("unchecked")
		@Override
		public TreeEvent createEvent(Map<String, Object> params) {
            return new CheckStateChangedEvent((UITree)params.get("source"),
                    (UITreeNode)params.get("affectedNode"));
		}

		@Override
		public String getTypeString() {
			return "checkStateChanged";
		}
	};
	
	protected boolean adjustAncestorStatus;
    
    public UICheckTreeNode() {
    	super();
        adjustAncestorStatus = true;
    }
    
	public CheckType getCheckType() {
		if (checkType != null)
			return checkType;
		
		if (getChildCount() == 0) {
			return convertCheckedToCheckType();
		}
		
		CheckType type = null;
		UICheckTreeNode current = null;
		for (UIComponent child : getChildren()) {
			if (child instanceof UICheckTreeNode) {
				current = (UICheckTreeNode)child;
				
				if (type == null) {
					type = current.getCheckType();
				} else {
					CheckType currentType = current.getCheckType();
					
					if (currentType == CheckType.PARTLY_CHECKED ||
							currentType != type)
						return CheckType.PARTLY_CHECKED;
				}
			}
		}
		
		if (type != null) {
			checkType = type;
		} else {
			checkType = convertCheckedToCheckType();
		}
		
		
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public void setAdjustAncestorStatus(boolean b) {
		adjustAncestorStatus = b;
	}
	
	public void check() {
		CheckStateChangedEvent changedEvent = createCheckStateChangedEvent();
		
		setChecked(this, true, changedEvent);
		adjustAncestorsStatus(this, changedEvent);
		
		if (needProcessChangedEvent())
			queueEvent(changedEvent);
		
		if (isInvokeApplicationPhase()) {
			recordChange();
		}
	}

	private boolean needProcessChangedEvent() {
		return isApplyRequestValuesPhase() && FacesUtils.isOwnEventOfTreeNode(
					FacesContext.getCurrentInstance(), getTree(), this);
	}

	private CheckStateChangedEvent createCheckStateChangedEvent() {
		CheckStateChangedEvent changedEvent = null;
		if (needProcessChangedEvent()) {
			changedEvent = new CheckStateChangedEvent(getTree(), this);
		}
		
		return changedEvent;
	}

	private void adjustAncestorsStatus(UICheckTreeNode treeNode, CheckStateChangedEvent changedEvent) {
		if (!adjustAncestorStatus)
			return;
		
		if (treeNode.getParent() != null && (treeNode.getParent() instanceof UICheckTreeNode)) {
			UICheckTreeNode parent = (UICheckTreeNode)treeNode.getParent();
			
			boolean initiatorChecked = treeNode.isChecked();
			boolean oldChecked = parent.getChecked();
			CheckType oldCheckType = parent.getCheckType();
			
			boolean hasSet = false;
			for (UIComponent child : parent.getChildren()) {
				if (!(child instanceof UICheckTreeNode)) {
					continue;
				}
				
				UICheckTreeNode checkChild = (UICheckTreeNode)child;
				if (checkChild.getCheckType() == CheckType.PARTLY_CHECKED) {
					parent.setCheckType(CheckType.PARTLY_CHECKED);
					parent.checked = false;
					hasSet = true;
				} else if (checkChild.isChecked() == initiatorChecked) {
					continue;					
				} else {
					parent.setCheckType(CheckType.PARTLY_CHECKED);
					parent.checked = false;
					hasSet = true;
				}
			}
			
			if (!hasSet) {
				parent.checked = initiatorChecked;
				parent.checkType = treeNode.getCheckType();
			}
			
			if (changedEvent != null && parent.getCheckType() != oldCheckType) {
				changedEvent.getCheckStateChanges().add(new CheckStateChange(parent, oldCheckType, oldChecked));
			}
			
			adjustAncestorsStatus(parent, changedEvent);
		}
	}

	private void setChecked(UICheckTreeNode treeNode, boolean checked, CheckStateChangedEvent changedEvent) {
		if (changedEvent != null) {
			CheckStateChange change = new CheckStateChange(this, this.checkType, this.checked);
			changedEvent.getCheckStateChanges().add(change);
		}
		
		this.checked = checked;
		this.checkType = convertCheckedToCheckType();

		if (treeNode.getChildCount() == 0)
			return;
		
		for (UIComponent child : treeNode.getChildren()) {
			if (!(child instanceof UICheckTreeNode))
				continue;
			
			UICheckTreeNode checkChild = (UICheckTreeNode)child;
			checkChild.setChecked(checkChild, checked, changedEvent);
		}
	}
	
	public void uncheck() {
		CheckStateChangedEvent changedEvent = createCheckStateChangedEvent();
		setChecked(this, false, changedEvent);
		adjustAncestorsStatus(this, changedEvent);
		
		if (changedEvent != null)
			queueEvent(changedEvent);
		
		if (isInvokeApplicationPhase()) {
			recordChange();
		}
	}
	
    private boolean isApplyRequestValuesPhase() {
		return FacesUtils.currentPhase() == PhaseId.APPLY_REQUEST_VALUES;
	}

    @Override
    protected void postCreate(FacesContext context, Object bizObject,
    			MethodExpression postCreateExpression) {
    	if (getParent() != null && getParent() instanceof UICheckTreeNode) {
    		UICheckTreeNode parent = (UICheckTreeNode)getParent();
    		CheckType checkType = parent.getCheckType();
    		
    		if (checkType != UICheckTreeNode.CheckType.PARTLY_CHECKED) {
    			checked = parent.isChecked();
    			checkType = parent.getCheckType();
   			
    			return;
    		}
    	}
    	
    	setAdjustAncestorStatus(false);
    	postCreateExpression.invoke(context.getELContext(), new Object[] {this, bizObject});
    	setAdjustAncestorStatus(true);
    }
    
    public UICheckTreeNode[] getCheckedNodes() {
    	return getCheckedNodes(UICheckTreeNode.class);
    }
}
