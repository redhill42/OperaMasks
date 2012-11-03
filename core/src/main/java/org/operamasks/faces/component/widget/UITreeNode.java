/*
 * $Id: UITreeNode.java,v 1.13 2008/01/16 02:45:40 yangdong Exp $
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
 *
 */
package org.operamasks.faces.component.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.component.widget.tree.TraverseCallback;
import org.operamasks.faces.component.widget.tree.event.TreeClickEvent;
import org.operamasks.faces.component.widget.tree.event.TreeCollapseEvent;
import org.operamasks.faces.component.widget.tree.event.TreeDblClickEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.event.TreeExpandEvent;
import org.operamasks.faces.component.widget.tree.event.TreeSelectEvent;
import org.operamasks.faces.component.widget.tree.state.TreeNodeAddChild;
import org.operamasks.faces.component.widget.tree.state.TreeNodeChangeImage;
import org.operamasks.faces.component.widget.tree.state.TreeNodeChangeText;
import org.operamasks.faces.component.widget.tree.state.TreeNodeClearChildren;
import org.operamasks.faces.component.widget.tree.state.TreeNodeCollapse;
import org.operamasks.faces.component.widget.tree.state.TreeNodeExpand;
import org.operamasks.faces.component.widget.tree.state.TreeNodeExpandTo;
import org.operamasks.faces.component.widget.tree.state.TreeNodeRemoveChild;
import org.operamasks.faces.component.widget.tree.state.TreeNodeSelect;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

@SuppressWarnings("serial")
public class UITreeNode extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.TreeNode";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.TreeNode";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.TreeNode"; 

    private String text;
    private String image;
    private Boolean expand;
    private Boolean async;
    private Object userData;
    private Boolean leaf;
    private Boolean allowsChildren;
    private Boolean selected;
    private Boolean asyncDataLoaded;
    
    private Boolean oldExpandValue;
    private Boolean oldSelectedValue;
    
    private String oldText;
    private String oldImage;
    
    private Object[] states;
    
    private ChildrenListWrapper childrenListWrapper;
    
    private List<TreeStateChange> changes;
    
    public static transient final TreeEventType SELECT = new TreeEventType() {
        public String getTypeString() {
            return "select";
        }
        
        public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeSelectEvent((UITree)params.get("source"),
                    (UITreeNode)params.get("affectedNode"));
        }
	};
	
    public static transient final TreeEventType CLICK = new TreeEventType() {
        public String getTypeString() {
            return "click";
        }
        
        public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeClickEvent((UITree)params.get(KEY_EVENT_SOURCE),
                    (UITreeNode)params.get(KEY_EVENT_AFFECTED_NODE));
        }
	};
	
    public static transient final TreeEventType DOUBLE_CLICK = new TreeEventType() {
        public String getTypeString() {
            return "dblclick";
        }
        
        public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeDblClickEvent((UITree)params.get("source"),
                    (UITreeNode)params.get("affectedNode"));
        }
	};
	
    public static transient final TreeEventType EXPAND = new TreeEventType() {
        public String getTypeString() {
            return "expand";
        }
        
        public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeExpandEvent((UITree)params.get(KEY_EVENT_SOURCE),
                    (UITreeNode)params.get(KEY_EVENT_AFFECTED_NODE));
        }
	};
	
    public static transient final TreeEventType COLLAPSE = new TreeEventType() {
        public String getTypeString() {
            return "collapse";
        }
        
        public TreeEvent createEvent(Map<String, Object> params) {
            return new TreeCollapseEvent((UITree)params.get("source"),
                    (UITreeNode)params.get("affectedNode"));
        }
	};
	
	public UITreeNode() {
        setRendererType(DEFAULT_RENDERER_TYPE);
        expand = Boolean.FALSE;
        async = Boolean.FALSE;
        allowsChildren = Boolean.FALSE;
        leaf = Boolean.TRUE;
        selected = Boolean.FALSE;
        asyncDataLoaded = Boolean.FALSE;
        
        oldExpandValue = Boolean.FALSE;
        oldSelectedValue = Boolean.FALSE;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getFamily()
     */
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        if (states == null) {
             states = new Object[11];
        }
      
        states[0] = super.saveState(context);
        states[1] = saveAttachedState(context, getId());
        states[2] = saveAttachedState(context, text);
        states[3] = saveAttachedState(context, image);
        states[4] = saveAttachedState(context, expand);
        states[5] = saveAttachedState(context, async);
        states[6] = saveAttachedState(context, userData);
        states[7] = saveAttachedState(context, leaf);
        states[8] = saveAttachedState(context, allowsChildren);
        states[9] = saveAttachedState(context, selected);
        states[10] = saveAttachedState(context, asyncDataLoaded);
        
        return (states);
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        states = (Object[]) state;
        super.restoreState(context, states[0]);
        setId((String)restoreAttachedState(context, states[1]));
        text = (String)restoreAttachedState(context, states[2]);
        image = (String)restoreAttachedState(context, states[3]);
        expand = (Boolean)restoreAttachedState(context, states[4]);
        async = (Boolean)restoreAttachedState(context, states[5]);
        userData = (Object)restoreAttachedState(context, states[6]);
        leaf = (Boolean)restoreAttachedState(context, states[7]);
        allowsChildren = (Boolean)restoreAttachedState(context, states[8]);
        selected = (Boolean)restoreAttachedState(context, states[9]);
        asyncDataLoaded = (Boolean)restoreAttachedState(context, states[10]);
        
        oldSelectedValue = selected;
        oldExpandValue = expand;
        oldText = text;
        oldImage = image;
    }

    public Boolean getExpand() {
        if (this.expand != null) {
            return this.expand;
        }
        ValueExpression ve = getValueExpression("expend");
        if (ve != null) {
            try {
                Object value = ve.getValue(getFacesContext().getELContext());
                if (value != null) {
                    return (Boolean)value;
                }
            } catch (ELException e) {
                throw new FacesException(e);
            }
        }
        return Boolean.FALSE;
    }
    
    public Boolean isExpand() {
    	return getExpand();
    }

    public void setExpand(Boolean expand) {
        if (expand)
        	expand();
        else
        	collapse();
    }
    
    public void setUserData(Object userData) {
        this.userData = userData;
    }
    
    public Object getUserData() {
        if (this.userData != null) {
            return (userData);
        }
        ValueExpression ve = getValueExpression("userData");
        if (ve != null) {
            try {
                return (ve.getValue(getFacesContext().getELContext()));
            } catch (ELException e) {
                throw new FacesException(e);
            }
        } else {
            return (null);
        }
    }
    
    public Boolean getAsync() {
        if (this.async != null) {
            return (this.async);
        }
        ValueExpression ve = getValueExpression("async");
        if (ve != null) {
            try {
                Object value = ve.getValue(getFacesContext().getELContext());
                if (value != null) {
                    return (Boolean)value;
                }
            } catch (ELException e) {
                throw new FacesException(e);
            }
        }
        return Boolean.FALSE;
    }
    
    public void setAsync(Boolean async) {
        this.async = async;
    }
    
    public String getImage() {
        if (this.image != null) {
            return (this.image);
        }
        ValueExpression ve = getValueExpression("image");
        if (ve != null) {
            try {
                return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException e) {
                throw new FacesException(e);
            }
        } else {
            return (null);
        }
    }
    
    public void setImage(String image) {
        setImage(image, true);
    }
    
    public void setImage(String image, boolean recordChange) {
        this.image = image;
        
		if (isInvokeApplicationPhase() && isChangeImageCall() && recordChange) {
			removeChange(TreeNodeChangeImage.class);
			getChanges().add(new TreeNodeChangeImage(oldImage, image));
		} else if (!isChangeImageCall()) {
			removeChange(TreeNodeChangeImage.class);
		}
    }
    
    public String getText() {
    	UITree tree = getTree();
    	
        if (this.text != null) {
       		return (this.text);
        }
        ValueExpression ve = getValueExpression("text");
        if (ve != null) {
            try {
            	if (tree!= null && tree.getEscapeNodeText())
            		return HtmlEncoder.encode((String)ve.getValue(getFacesContext().getELContext()));
            	else
            		return (String)ve.getValue(getFacesContext().getELContext());
            } catch (ELException e) {
                throw new FacesException(e);
            }
        } else {
            return (null);
        }
    }
    
    public void setText(String text) {
    	setText(text, true);
    }
    
    public void setText(String text, boolean recordChange) {
        this.text = text;
        
		if (isInvokeApplicationPhase() && isChangeTextCall() && recordChange) {
			removeChange(TreeNodeChangeText.class);
			getChanges().add(new TreeNodeChangeText(oldText, text));
		} else if (!isChangeTextCall()) {
			removeChange(TreeNodeChangeText.class);
		}
    }

	private boolean isChangeTextCall() {
		return notEqual(oldText, text);
	}
	
	private boolean isChangeImageCall() {
		return notEqual(oldImage, image);
	}

	private boolean notEqual(String string1, String string2) {
		if (string1 == null && string2 == null)
			return false;
		
		if (string1 != null)
			return !string1.equals(string2);
		else
			return true;
	}

	public Boolean getAllowsChildren() {
		return allowsChildren;
	}

	public void setAllowsChildren(Boolean allowsChildren) {
		this.allowsChildren = allowsChildren;
	}



	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	
	public void collapse() {
		expand = false;
		
		if (isInvokeApplicationPhase() && isCollapseCall()) {
			removeChange(TreeNodeExpand.class);
			getChanges().add(new TreeNodeCollapse());
		}
	}
	
	public void expand() {
		expand(false);
	}
	
	public void expand(boolean recursive) {
		expand = true;
		
		if (this.isAllowsChildren() && this.getChildCount() ==0 &&
				!this.isLeaf())
			getTree().loadAsyncNodes(this);
		
		if (isInvokeApplicationPhase() && isExpandCall()) {
			removeChange(TreeNodeCollapse.class);
			getChanges().add(new TreeNodeExpand(recursive));
		}
	}

	protected void removeChange(Class<? extends TreeStateChange> changeClass) {
		for (TreeStateChange change : getChanges()) {
			if (change.getClass().equals(changeClass)) {
				getChanges().remove(change);
				
				return;
			}
		}
	}

	public void select() {
		unselectAll();
		
		selected = true;
		
		if (isInvokeApplicationPhase() && isSelectCall())
			getChanges().add(new TreeNodeSelect());
	}
	
	private void unselectAll() {
		UITree tree = getTree();
		
		unselectTreeNodes(tree.getRootNode());
	}

	private void unselectTreeNodes(UITreeNode node) {
		if (node.isSelected()) {
			node.unselect();
		}
		
		List<UIComponent> children = node.getChildren();
		
		for (UIComponent child : children) {
			unselectTreeNodes((UITreeNode)child);
		}
	}

	public void unselect() {
		if (isInvokeApplicationPhase() && !isSelectCall()) {
			removeChange(TreeNodeSelect.class);
		}
		
		selected = false;
	}
	
	public Boolean isSelected() {
		return selected;
	}
	
	public Boolean isLeaf() {
		return leaf;
	}

	public UITree getTree() {
		return findTree(this);
	}
	
	private UITree findTree(UIComponent component) {
		if (component.getParent() == null)
			return null;
		
		if (component.getParent() instanceof UITree)
			return (UITree)component.getParent();
		
		return findTree(component.getParent());		
	}

	public Boolean getAsyncDataLoaded() {
		return asyncDataLoaded;
	}

	public void setAsyncDataLoaded(Boolean asyncDataLoaded) {
		this.asyncDataLoaded = asyncDataLoaded;
	}
	
	public Boolean isAsyncDataLoaded() {
		return asyncDataLoaded;
	}
	
	public boolean isSelectCall() {
		return selected && oldSelectedValue != selected;
	}
	
	public boolean isUnelectCall() {
		return !selected && oldSelectedValue != selected;
	}
	
	public boolean isExpandCall() {
		return expand && oldExpandValue != expand;
	}
	
	public boolean isCollapseCall() {
		return !expand && oldExpandValue != expand;
	}
	
	@Override
	public List<UIComponent> getChildren() {
		if (childrenListWrapper == null)
			childrenListWrapper = new ChildrenListWrapper(super.getChildren());
		
		return childrenListWrapper;
	}
	
    private String generateChildrenId() {
    	int serialNumber = generateSubNodeSerialNumber(this);
    	
		return getId() + "_" + serialNumber;
	}
    
	private static int generateSubNodeSerialNumber(UIComponent parent) {
		if (parent.getChildCount() == 0)
			return 0;
		
		int serialNumber = parent.getChildCount();
		
		while (isUsedSerialNumber(parent, serialNumber)) {
			serialNumber++;
		}
		
		return serialNumber;
	}
	
	private static boolean isUsedSerialNumber(UIComponent parent, int serialNumber) {
		for (UIComponent component : parent.getChildren()) {
			if (component.getId().endsWith("_" + serialNumber))
				return true;
		}
		
		return false;
	}
	
	private class ChildrenListWrapper implements List<UIComponent> {
		private List<UIComponent> original;
		
		public ChildrenListWrapper(List<UIComponent> original) {
			this.original = original;
		}

		public boolean add(UIComponent o) {
			if (o instanceof UITreeNode) {
				if (o.getId() == null)
					o.setId(generateChildrenId());
				
				int size = original.size();
				boolean result = original.add(o);
				
				if (result && isInvokeApplicationPhase())
					getChanges().add(new TreeNodeAddChild(size, (UITreeNode)o));
				
				adjustNodeFlag();
				
				return result;
			}
			
			return false;
		}

		public void add(int index, UIComponent o) {
			if (o instanceof UITreeNode) {
				if (o.getId() == null)
					o.setId(generateChildrenId());
				
				original.add(o);
				
				if (isInvokeApplicationPhase())
					getChanges().add(new TreeNodeAddChild(getChildCount(), (UITreeNode)o));
				
				adjustNodeFlag();
			}
		}

		public boolean addAll(Collection<? extends UIComponent> c) {
			return addAll(getChildCount(), c);
		}

		public boolean addAll(int index, Collection<? extends UIComponent> c) {
			List<UITreeNode> onlyTreeNodes = getOnlyTreeNodes(c);
			
			for (UITreeNode node : onlyTreeNodes) {
				if (node.getId() == null)
					node.setId(generateChildrenId());
			}
			
			boolean result = original.addAll(index, onlyTreeNodes);
			
			if (isInvokeApplicationPhase()) {
				Iterator<UITreeNode> iter = onlyTreeNodes.iterator();
				while (iter.hasNext()) {
					UITreeNode o = iter.next();
				
					if (o instanceof UITreeNode) {
						getChanges().add(new TreeNodeAddChild(index++, (UITreeNode)o));
					}
				}
			}
			
			adjustNodeFlag();
			
			return result;
		}

		@SuppressWarnings("unchecked")
		private List<UITreeNode> getOnlyTreeNodes(Collection c) {
			Iterator iter = c.iterator();
			List<UITreeNode> treeNodeOnly = new ArrayList<UITreeNode>();
			
			while (iter.hasNext()) {
				Object o = iter.next();
				
				if (o instanceof UITreeNode)
					treeNodeOnly.add((UITreeNode)o);
			}
			return treeNodeOnly;
		}
		
		public void clear() {
			original.clear();
			
			if (isInvokeApplicationPhase())
				getChanges().add(new TreeNodeClearChildren());
			
			adjustNodeFlag();
		}

		public boolean contains(Object o) {
			return original.contains(o);
		}

		public boolean containsAll(Collection<?> c) {
			return original.containsAll(c);
		}

		public UIComponent get(int index) {
			return original.get(index);
		}

		public int indexOf(Object o) {
			return original.indexOf(o);
		}

		public boolean isEmpty() {
			return original.isEmpty();
		}

		public Iterator<UIComponent> iterator() {
			return original.iterator();
		}

		public int lastIndexOf(Object o) {
			return original.lastIndexOf(o);
		}

		public ListIterator<UIComponent> listIterator() {
			return original.listIterator();
		}

		public ListIterator<UIComponent> listIterator(int index) {
			return original.listIterator();
		}

		public boolean remove(Object o) {
			int index = original.indexOf(o);
			
			if (index == -1)
				return false;
			
			remove(index);
			
			return true;
		}

		public UIComponent remove(int index) {
			UIComponent result = original.remove(index);
			
			if (result != null && isInvokeApplicationPhase())
				getChanges().add(new TreeNodeRemoveChild(index));

			adjustNodeFlag();
			
			return result;
		}

		public boolean removeAll(Collection<?> c) {
			Collection<UITreeNode> onlyTreeNodes = getOnlyTreeNodes(c);
			
			Iterator<UITreeNode> iter = onlyTreeNodes.iterator();
			boolean modified = false;
			while (iter.hasNext()) {
				UITreeNode o = iter.next();
				int index = indexOf(o);
					
				if (index == -1){
					continue;
				} else {
					modified = true;
				}
				
				remove(o);
				
				if (isInvokeApplicationPhase())
					getChanges().add(new TreeNodeRemoveChild(index));
				
				adjustNodeFlag();
			}
			
			return modified;
		}

		public boolean retainAll(Collection<?> c) {
			return original.retainAll(c);
		}

		public UIComponent set(int index, UIComponent element) {
			if (!(element instanceof UITreeNode))
				return null;
				
			UIComponent result = original.set(index, element);
			
			if (result != null && isInvokeApplicationPhase()) {
				getChanges().add(new TreeNodeRemoveChild(index));
				getChanges().add(new TreeNodeAddChild(index, (UITreeNode)element));
			}
			
			adjustNodeFlag();
			
			return result;
		}

		public int size() {
			return original.size();
		}

		public List<UIComponent> subList(int fromIndex, int toIndex) {
			return original.subList(fromIndex, toIndex);
		}

		public Object[] toArray() {
			return original.toArray();
		}

		public <T> T[] toArray(T[] a) {
			return original.toArray(a);
		}
	}

	private void adjustNodeFlag() {
		if (getChildCount() == 0) {
			leaf = true;
			expand = false;
		} else {
			allowsChildren = true;
			leaf = false;
		}
	}
	
	public List<TreeStateChange> getChanges() {
		if (changes == null)
			changes = new ArrayList<TreeStateChange>();
		
		return changes;
	}
	
	public boolean isRootNode() {
		return this.equals(getTree().getRootNode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UITreeNode) {
			UITreeNode other = (UITreeNode)obj;
			
			return other.getId() != null && other.getId().equals(this.getId());
		}
		
		return false;
	}

	public UITreeNode findChildById(String id) {
		if (getChildCount() == 0)
			return null;
		
		return getTree().findTreeNodeById(this, id);
	}
	
	public UITreeNode findChildByUserData(Object userData) {
		if (getChildCount() == 0)
			return null;
		
		return getTree().findTreeNodeByUserData(this, userData);
	}
	
	protected boolean isInvokeApplicationPhase() {
		return FacesUtils.currentPhase() == PhaseId.INVOKE_APPLICATION;
	}
	
	public Boolean isAllowsChildren() {
		return getAllowsChildren();
	}

	protected void postCreate(FacesContext context, Object bizObject,
				MethodExpression postCreateMethod) {
		// Subclasses can ajdust it's status here after being created.
	}
	
	public void expandTo() {
		if (!(getParent() instanceof UITreeNode))
			return;
		
		UIComponent ancestor = this.getParent();
		
		while (ancestor instanceof UITreeNode) {
			((UITreeNode)(ancestor)).setExpand(true);
			ancestor = ancestor.getParent();
		}
		
		if (isInvokeApplicationPhase()) {
			((UITreeNode)getParent()).getChanges().add(new TreeNodeExpandTo());
		}
	}
	
	public void traverse(TraverseCallback callback) {
		traverse(callback, false);
	}
	
	public void traverse(TraverseCallback callback, boolean loadAsync) {
		boolean continueTraverse = callback.doForEach(this);
		
		if (!continueTraverse)
			return;
		
		traverseChildren(this, callback, loadAsync);
	}
	
	private void traverseChildren(UITreeNode node, TraverseCallback callback, boolean loadAsync) {
		if (node.getChildCount() == 0 && loadAsync) {
			node.getTree().loadAsyncNodes(node);
		}
		
		if (node.getChildCount() == 0)
			return;
		
		for (UIComponent child : node.getChildren()) {
			if (child instanceof UIComponent) {
				if (!callback.doForEach((UITreeNode)child))
					break;
				
				traverseChildren(((UITreeNode)child), callback, loadAsync);
			}
		}
	}
}