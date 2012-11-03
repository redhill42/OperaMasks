package org.operamasks.faces.component.widget;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.tree.TraverseCallback;
import org.operamasks.faces.component.widget.tree.event.TreeCheckEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.state.TreeNodeCheck;

@SuppressWarnings("serial")
public class UISimpleCheckTreeNode extends UITreeNode {
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.SimpleCheckTreeNode";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.CheckTreeNode";
    
    public enum CheckType {
    	CHECKED {
    		@Override
    		public String toString() {
    			return "checked";
    		}
    	},
    	UNCHECKED {
    		@Override
    		public String toString() {
    			return "unchecked";
    		}
    	},
    	PARTLY_CHECKED {
    		@Override
    		public String toString() {
    			return "partlyChecked";
    		}
    	}
    }
    
    public static transient final TreeEventType CHECK = new TreeEventType() {
		@Override
		public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeCheckEvent((UITree)params.get("source"),
                    (UITreeNode)params.get("affectedNode"),
                    (Boolean)params.get("check"));
		}

		@Override
		public String getTypeString() {
			return "check";
		}
	};
	
    protected Boolean checked;
    protected Boolean oldChecked;
    protected CheckType checkType;
    
    protected Object[] states;
    
    public UISimpleCheckTreeNode() {
    	super();
    	
        setRendererType(DEFAULT_RENDERER_TYPE);
        checked = Boolean.FALSE;
        oldChecked = Boolean.FALSE;
    }
    
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		if (checked)
			check();
		else
			uncheck();
	}
	
	public Boolean isChecked() {
		return getChecked();
	}
	
	public CheckType getCheckType() {
		return convertCheckedToCheckType();
	}

	protected CheckType convertCheckedToCheckType() {
		if (checked)
			return CheckType.CHECKED;
		else
			return CheckType.UNCHECKED;
	}

	public void setCheckType(CheckType checkType) {
		if (checkType == CheckType.PARTLY_CHECKED)
			checkType = CheckType.CHECKED;
		
		this.checkType = checkType;
	}

	public boolean isCheckCall() {
		return checked != oldChecked;
	}
	
	public void check() {
		setChecked(this, true);

		if (isInvokeApplicationPhase()) {
			recordChange();
		}
	}

	protected void recordChange() {
		removeChange(TreeNodeCheck.class);
		if (isCheckCall()) {
			getChanges().add(new TreeNodeCheck(checked));
		}
	}
	
	protected void setChecked(UISimpleCheckTreeNode treeNode, boolean checked) {
		this.checked = checked;
		this.checkType = convertCheckedToCheckType();
	}
	
	public void uncheck() {
		setChecked(this, false);
		
		if (isInvokeApplicationPhase()) {
			recordChange();
		}
	}
	
	@Override
    public Object saveState(FacesContext context) {
       if (states == null) {
            states = new Object[3];
       }
       
       states[0] = super.saveState(context);
       states[1] = saveAttachedState(context, getChecked());
       states[2] = saveAttachedState(context, getCheckType());
       
       return states;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
    	states = (Object[]) state;
        super.restoreState(context, states[0]);
        
        checked = (Boolean)restoreAttachedState(context, states[1]);
        checkType = (CheckType)restoreAttachedState(context, states[2]);
        oldChecked = checked;
    }
    
    public <T extends UISimpleCheckTreeNode> T[] getCheckedNodes(Class<T> clazz) {
    	CheckedNodesTraverseCallback<T> callback = new CheckedNodesTraverseCallback<T>(clazz);
    	traverse(callback);
    	
    	return callback.getResult();
    }
    
    public UISimpleCheckTreeNode[] getCheckedSimpleCheckNodes() {
    	return getCheckedNodes(UISimpleCheckTreeNode.class);
    }
    
    private class CheckedNodesTraverseCallback<T extends UISimpleCheckTreeNode> implements TraverseCallback {
    	private List<T> checkedNodes;
    	private Class<T> clazz;
    	public CheckedNodesTraverseCallback(Class<T> clazz) {
    		checkedNodes = new ArrayList<T>();
    		this.clazz = clazz;
    	}
    	
		@SuppressWarnings("unchecked")
		public boolean doForEach(UITreeNode node) {
			if (node instanceof UISimpleCheckTreeNode) {
				if (((UISimpleCheckTreeNode)node).isChecked())
						checkedNodes.add((T)node);
			}

			return true;
		}
		
		@SuppressWarnings("unchecked")
		public T[] getResult() {
			T[] result = (T[])Array.newInstance(clazz, checkedNodes.size());
			
			return checkedNodes.toArray(result);
		}
    }
    
    @Override
    protected void postCreate(FacesContext context, Object bizObject,
    			MethodExpression postCreateExpression) {
    	postCreateExpression.invoke(context.getELContext(), new Object[] {this, bizObject});
    }
}