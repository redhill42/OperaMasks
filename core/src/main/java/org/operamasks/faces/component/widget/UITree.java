/*
 * $Id: UITree.java,v 1.24 2008/01/30 08:52:40 yangdong Exp $
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.operamasks.faces.component.widget.tree.TraverseCallback;
import org.operamasks.faces.component.widget.tree.TreeNodeModelFactory;
import org.operamasks.faces.component.widget.tree.TreeNodeSeeker;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.event.TreeEventListener;
import org.operamasks.faces.util.FacesUtils;

public class UITree extends UIComponentBase {
	public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Tree";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Tree";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Tree";
    
    public static final String KEY_ROOT_NODE_POSTFIX = "_root_node";
    
    public UITree() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UITree(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
    private Boolean animate;
    private Boolean enableDrag;
    private Boolean enableDrop;
    private Boolean containerScroll;
    private Boolean lines;
    private String style;
    private String styleClass;
    private Boolean escapeNodeText;
    
    private boolean immediate = false;
    private boolean immediateSet = false;

    private Map<ValueExpression, Set<TreeEventType>> registeredListeners;
    private Set<TreeEventType> registeredEventTypes;
    
    private MethodExpression asyncData;
    private MethodExpression nodeText;
    private MethodExpression nodeImage;
    private MethodExpression nodeUserData;
    private MethodExpression nodeHasChildren;
    private MethodExpression nodeClass;
    private MethodExpression initAction;
    private MethodExpression postCreate;
    
    private Boolean saveState;
    private Boolean rootVisible;
    
    private Object[] states;
    
    private String jsvar;
	
	private UITreeNode rootNode;

    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setEscapeNodeText(Boolean escapeNodeText) {
        this.escapeNodeText = escapeNodeText;
    }
    
    public Boolean getEscapeNodeText() {
        if (this.escapeNodeText != null) {
            return this.escapeNodeText;
        }
        ValueExpression ve = getValueExpression("escapeNodeText");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return Boolean.TRUE;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }
    
	public Boolean getLines() {
        if (this.lines != null) {
            return this.lines;
        }
        ValueExpression ve = getValueExpression("lines");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLines(Boolean lines) {
        this.lines = lines;
    }

    public Boolean getContainerScroll() {
    	if (this.containerScroll != null) {
    		return this.containerScroll;
    	}
    	ValueExpression ve = getValueExpression("containerScroll");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setContainerScroll(Boolean containerScroll) {
        this.containerScroll = containerScroll;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getFamily()
     */
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public UITreeNode getRootNode() {
    	if (rootNode != null)
    		return rootNode;
    	
    	if (getChildCount() > 0) {
    		for (UIComponent child : getChildren()) {
    			if (child instanceof UITreeNode) {
    				rootNode = (UITreeNode)child;
    				break;
    			}
    		}
    	}
    	
        return rootNode;
    }

    public void setRootNode(UITreeNode rootNode) {
   		getChildren().clear();
   		
    	if (rootNode != null) {
    		if (rootNode.getId() == null)
    			rootNode.setId(generateRootNodeId());
    		
    		getChildren().add(rootNode);
    	}
    	
    	this.rootNode = rootNode;
    }
    
    public Boolean getAnimate() {
    	if (this.animate != null) {
    		return this.animate;
    	}
    	ValueExpression ve = getValueExpression("animate");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setAnimate(Boolean animate) {
        this.animate = animate;
    }

    public Boolean getEnableDrag() {
    	if (this.enableDrag != null) {
    		return this.enableDrag;
    	}
    	ValueExpression ve = getValueExpression("enableDrag");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setEnableDrag(Boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public Boolean getEnableDrop() {
    	if (this.enableDrop != null) {
    		return this.enableDrop;
    	}
    	ValueExpression ve = getValueExpression("enableDrop");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setEnableDrop(Boolean enableDrop) {
        this.enableDrop = enableDrop;
    }

    public String getStyle() {
    	if (this.style != null) {
    		return this.style;
    	}
    	ValueExpression ve = getValueExpression("style");
    	if (ve != null) {
    		return (String)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
    	if (this.styleClass != null) {
    		return this.styleClass;
    	}
    	ValueExpression ve = getValueExpression("styleClass");
    	if (ve != null) {
    		return (String)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public Boolean getSaveState() {
    	if (this.saveState != null) {
    		return this.saveState;
    	}
    	ValueExpression ve = getValueExpression("saveState");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setSaveState(Boolean saveState) {
        this.saveState = saveState;
    }
    
    public Boolean getRootVisible() {
    	if (this.rootVisible != null) {
    		return this.rootVisible;
    	}
    	ValueExpression ve = getValueExpression("rootVisible");
    	if (ve != null) {
    		return (Boolean)ve.getValue(getFacesContext().getELContext());
    	} else {
    		return null;
    	}
    }

    public void setRootVisible(Boolean rootVisible) {
        this.rootVisible = rootVisible;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#isImmediate()
     */
    public boolean isImmediate() {
        if (this.immediateSet) {
            return (this.immediate);
        }
        
        ValueExpression ve = getValueExpression("immediate");
        if (ve != null) {
            try {
                return (Boolean.TRUE.equals(ve.getValue(getFacesContext().getELContext())));
            } catch (ELException e) {
                throw new FacesException(e);
            }
        } else {
            return (this.immediate);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#setImmediate(boolean)
     */
    public void setImmediate(boolean immediate) {
        if (immediate != this.immediate) {
            this.immediate = immediate;
        }
        
        this.immediateSet = true;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#broadcast(javax.faces.event.FacesEvent)
     */
    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (registeredListeners == null)
    		return;

        if (event instanceof TreeEvent) {
            for (Map.Entry<ValueExpression, Set<TreeEventType>> listenerAndEventTypes :
                        registeredListeners.entrySet()) {
                if (listenerAndEventTypes.getValue().contains(((TreeEvent)event).getEventType())) {
                	ValueExpression ve = listenerAndEventTypes.getKey();
                	Object listener = ve.getValue(FacesContext.getCurrentInstance().getELContext());
                	
                	if (listener == null)
                		throw new FacesException("Null TreeEventListener");
                	
                	if (!(listener instanceof TreeEventListener))
                		throw new FacesException("Registered EL " +
                				ve.getExpressionString() + " isn't a TreeEventListener");
                	
                    ((TreeEventListener)listener).processEvent((TreeEvent)event);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#queueEvent(javax.faces.event.FacesEvent)
     */
    @Override
    public void queueEvent(FacesEvent event) {
        if (event instanceof TreeEvent) {
            if (isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
        }
        
        super.queueEvent(event);
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        if (states == null) {
             states = new Object[19];
        }
      
        states[0] = super.saveState(context);
        states[1] = immediate ? Boolean.TRUE : Boolean.FALSE;
        states[2] = immediateSet ? Boolean.TRUE : Boolean.FALSE;
        states[3] = saveAttachedState(context, asyncData);
        states[4] = saveAttachedState(context, nodeText);
        states[5] = saveAttachedState(context, nodeImage);
        states[6] = saveAttachedState(context, nodeUserData);
        states[7] = saveAttachedState(context, nodeHasChildren);
        states[8] = saveAttachedState(context, registeredListeners);
        states[9] = saveAttachedState(context, registeredEventTypes);
        states[10] = saveAttachedState(context, saveState);
        states[11] = saveAttachedState(context, jsvar);
        states[12] = saveAttachedState(context, style);
        states[13] = saveAttachedState(context, styleClass);
        states[14] = saveAttachedState(context, nodeClass);
        states[15] = saveAttachedState(context, initAction);
        states[16] = saveAttachedState(context, escapeNodeText);
        states[17] = saveAttachedState(context, postCreate);
        states[18] = saveAttachedState(context, rootVisible);
        
        return (states);
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void restoreState(FacesContext context, Object state) {
        states = (Object[]) state;
        super.restoreState(context, states[0]);
        immediate = ((Boolean) states[1]).booleanValue();
        immediateSet = ((Boolean) states[2]).booleanValue();
        asyncData = (MethodExpression)restoreAttachedState(context, states[3]);
        nodeText = (MethodExpression)restoreAttachedState(context, states[4]);
        nodeImage = (MethodExpression)restoreAttachedState(context, states[5]);
        nodeUserData = (MethodExpression)restoreAttachedState(context, states[6]);
        nodeHasChildren = (MethodExpression)restoreAttachedState(context, states[7]);
        registeredListeners = (Map<ValueExpression, Set<TreeEventType>>)restoreAttachedState(context, states[8]);
        registeredEventTypes = (Set<TreeEventType>)restoreAttachedState(context, states[9]);
        saveState = (Boolean)restoreAttachedState(context, states[10]);
        jsvar = (String)restoreAttachedState(context, states[11]);
        style = (String)restoreAttachedState(context, states[12]);
        styleClass = (String)restoreAttachedState(context, states[13]);
        nodeClass = (MethodExpression)restoreAttachedState(context, states[14]);
        initAction = (MethodExpression)restoreAttachedState(context, states[15]);
        escapeNodeText = (Boolean)restoreAttachedState(context, states[16]);
        postCreate = (MethodExpression)restoreAttachedState(context, states[17]);
        rootVisible = (Boolean)restoreAttachedState(context, states[18]);
    }

    public UITreeNode[] loadAsyncNodes(String parentNodeId) {
    	if (asyncData == null)
    		return null;

    	UITreeNode parent = findTreeNodeById(parentNodeId);
    	
        if (parent == null)
        	throw new IllegalArgumentException("Illegal tree node id " + parentNodeId);
        
    	return loadAsyncNodes(parent);
    }
    
    @SuppressWarnings("unchecked")
	public UITreeNode[] loadAsyncNodes(UITreeNode parent) {
    	if (asyncData == null)
    		return null;
        
    	if (parent.isLeaf() || !parent.isAllowsChildren() ||
    			parent.getChildCount() > 0 || isAsyncDataLoaded(parent))
    		return null;
        
    	FacesContext context = getFacesContext();
    	List bizObjects = (List)asyncData.invoke(context.getELContext(),
    			new Object[] {parent.getUserData()});
        
        if (bizObjects == null || bizObjects.size() == 0)
            return new UITreeNode[0];

        MethodExpression postCreateMethod = parent.getTree().getPostCreate();
        for (Object bizObject : bizObjects) {
            UITreeNode node = createTreeNodeByBizObject(bizObject);
            parent.getChildren().add(node);

            node.postCreate(context, bizObject, postCreateMethod);
        }
        
        parent.setAsyncDataLoaded(true);
        
        return parent.getChildren().toArray(new UITreeNode[parent.getChildren().size()]);
    }

	private UITreeNode createTreeNodeByBizObject(Object bizObject) {
		UITreeNode node = createTreeNode(getTreeNodeClass(bizObject));
		
		//node.setExpand(false);
		node.setUserData(getTreeNodeUserData(bizObject));
		node.setText(getTreeNodeText(bizObject, node.getUserData()), false);
		node.setImage(getTreeNodeImage(bizObject), false);
		node.setLeaf(getTreeNodeIsLeaf(bizObject));
		node.setAllowsChildren(getTreeNodeAllowsChildren(bizObject));
		
		return node;
	}
    
    @SuppressWarnings("unchecked")
	private Class<? extends UITreeNode> getTreeNodeClass(Object bizObject) {
		Class<? extends UITreeNode> nodeClazz = (Class<? extends UITreeNode>)invokeTreeAsyncFeatureMethod(
				nodeClass, bizObject);
		
		return nodeClazz != null ? nodeClazz : UITreeNode.class;
	}

	private boolean isAsyncDataLoaded(UIComponent component) {
    	return ((UITreeNode)component).isAsyncDataLoaded();
	}

	public UITreeNode createTreeNode() {
       	return createTreeNode(UITreeNode.class);
	}
	
	public <T extends UITreeNode> T createTreeNode(Class<T> nodeClass) {
		return TreeNodeModelFactory.getInstance().createTreeNode(nodeClass);
	}
    
	public String generateRootNodeId() {
   		return UIViewRoot.UNIQUE_ID_PREFIX + "_" + getId() + KEY_ROOT_NODE_POSTFIX;
	}

    private Boolean getTreeNodeIsLeaf(Object bizObject) {
   		Boolean hasChild = (Boolean)invokeTreeAsyncFeatureMethod(nodeHasChildren, bizObject);
    	
   		return (hasChild != null) ? !hasChild : Boolean.FALSE;
	}

	private Object invokeTreeAsyncFeatureMethod(MethodExpression expression, Object bizObject) {
		if (expression == null)
			return null;
		
		return expression.invoke(FacesContext.getCurrentInstance(
				).getELContext(), new Object[] {bizObject});
	}

	private Object getTreeNodeUserData(Object bizObject) {
		Object userData = invokeTreeAsyncFeatureMethod(nodeUserData, bizObject);
		
		return userData != null ? userData : bizObject;
	}

	private String getTreeNodeText(Object bizObject, Object userData) {
		String text = (String)invokeTreeAsyncFeatureMethod(nodeText, bizObject);
        
		return text != null ? text : userData.toString();
    }

	private String getTreeNodeImage(Object bizObject) {
		return (String)invokeTreeAsyncFeatureMethod(nodeImage, bizObject);
	}

    private boolean getTreeNodeAllowsChildren(Object data) {
        return true;
    }
    
    public UITreeNode findTreeNodeById(String nodeId) {
    	return findTreeNodeById(getRootNode(), nodeId, false);
    }

    public UITreeNode findTreeNodeById(UITreeNode base, String nodeId) {
        return findTreeNodeById(base, nodeId, false);
    }
    
    public UITreeNode findTreeNodeById(UITreeNode base, String nodeId, boolean loadAsync) {
        return findTreeNode(base, nodeId, new TreeNodeSeeker() {
    		public boolean isMatch(UITreeNode treeNode, Object identifier) {
    			return identifier != null && treeNode != null && identifier.equals(treeNode.getId());
    		}
    	}, loadAsync);
    }
    
    public UITreeNode findTreeNodeByUserData(Object userData) {
    	return findTreeNodeByUserData(getRootNode(), userData, false);
    }
    
    public UITreeNode findTreeNodeByUserData(UITreeNode base, Object userData) {
    	return findTreeNodeByUserData(base, userData, false);
    }
    
    public UITreeNode findTreeNodeByUserData(UITreeNode base, Object userData, boolean loadAsync) {
    	return findTreeNode(base, userData, new TreeNodeSeeker() {
    		public boolean isMatch(UITreeNode treeNode, Object identifier) {
    			return identifier != null && treeNode != null && identifier.equals(treeNode.getUserData());
    		}
    	}, loadAsync);
    }
    
    public UITreeNode findTreeNode(UITreeNode base, Object identifier,
    		TreeNodeSeeker seeker) {
    	return findTreeNode(base, identifier, seeker, false);
    }
    
    public UITreeNode findTreeNode(UITreeNode base, Object identifier,
    		TreeNodeSeeker seeker, boolean loadAsync) {
    	if (identifier == null)
    		return null;

    	SeekingNodeTraverseCallback callback = new SeekingNodeTraverseCallback(seeker, identifier);
    	traverse(callback, loadAsync);
    	
    	return callback.getResult();
    }
    
	private class SeekingNodeTraverseCallback implements TraverseCallback {
		private TreeNodeSeeker seeker;
		private Object identifier;
		private UITreeNode result;
		
		public SeekingNodeTraverseCallback(TreeNodeSeeker seeker, Object identifier) {
			this.seeker = seeker;
			this.identifier = identifier;
		}
		
		public UITreeNode getResult() {
			return result;
		}

		public boolean doForEach(UITreeNode node) {
			if (seeker.isMatch(node, identifier)) {
				result = node;
				return false;
			}
			
			return true;
		}
	}

	public MethodExpression getAsyncData() {
		return asyncData;
	}

	public void setAsyncData(MethodExpression asyncData) {
		this.asyncData = asyncData;
	}

	public MethodExpression getNodeImage() {
		return nodeImage;
	}

	public void setNodeImage(MethodExpression nodeImage) {
		this.nodeImage = nodeImage;
	}

	public MethodExpression getNodeHasChildren() {
		return nodeHasChildren;
	}

	public void setNodeHasChildren(MethodExpression nodeIsLeaf) {
		this.nodeHasChildren = nodeIsLeaf;
	}

	public MethodExpression getNodeText() {
		return nodeText;
	}

	public void setNodeText(MethodExpression nodeText) {
		this.nodeText = nodeText;
	}

	public MethodExpression getNodeUserData() {
		return nodeUserData;
	}

	public void setNodeUserData(MethodExpression nodeUserData) {
		this.nodeUserData = nodeUserData;
	}
	
	public MethodExpression getNodeClass() {
		return nodeClass;
	}

	public void setNodeClass(MethodExpression nodeClass) {
		this.nodeClass = nodeClass;
	}
	
	public void expandTo(UITreeNode treeNode) {
		if (treeNode == null)
			return;
		
		List<UITreeNode> expandChains = getNeedExpandChains(treeNode, true);
		
		for (UITreeNode needExpand : expandChains) {
			needExpand.expand();
		}
	}
	
	private List<UITreeNode> getNeedExpandChains(UITreeNode treeNode, boolean expand) {
		List<UITreeNode> needExpands = new ArrayList<UITreeNode>();
		
		UITreeNode parent = treeNode;
		while (true) {
			if (expand != parent.isExpand())
				needExpands.add(0, parent);
		
			if (parent.getParent() instanceof UITree ||
					parent.getParent() == null)
				break;
			
			parent = (UITreeNode)parent.getParent();
		}
		
		return needExpands;
	}
	
	public void loadAllAsyncNodes() {
		loadAsyncRootNode();
		
		loadAsyncNodesRecursively(getRootNode());
	}

	private void loadAsyncNodesRecursively(UITreeNode parent) {
		loadAsyncNodes(parent);
		
		if (parent.getChildCount() == 0)
			return;
		
		for (UIComponent child : parent.getChildren()) {
			if (child instanceof UITreeNode) {
				loadAsyncNodesRecursively((UITreeNode)child);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadAsyncRootNode() {
		if (getRootNode() != null)
			return;
		
    	if (asyncData == null)
    		throw new FacesException("Null asyncData, can't get tree root node.");
    	
    	List bizObjects = (List)asyncData.invoke(FacesContext.getCurrentInstance().getELContext(),
    			new Object[] {null});
        
        if (bizObjects == null || bizObjects.size() == 0)
        	throw new FacesException("Error asyncData method, it gets null tree root node.");

        Object bizObject = bizObjects.get(0);
        UITreeNode rootNode = createTreeNodeByBizObject(bizObject);

        setRootNode(rootNode);
	}

	public void addEventListener(ValueExpression listener, Set<TreeEventType> registeredEventTypes) {
		getRegisteredListeners().put(listener, registeredEventTypes);
		getRegisteredEventTypes().addAll(registeredEventTypes);
	}
	
	public Map<ValueExpression, Set<TreeEventType>> getRegisteredListeners() {
		if (registeredListeners == null) {
			registeredListeners = new HashMap<ValueExpression, Set<TreeEventType>>();
		}
		
		return registeredListeners;
	}
	
	public Set<TreeEventType> getRegisteredEventTypes() {
		if (registeredEventTypes == null) {
			registeredEventTypes = new HashSet<TreeEventType>();
		}
		
		return registeredEventTypes;
	}
	
	public UITreeNode getSelectedNode() {
		return findFirstSelectedNode(getRootNode());
	}

	private UITreeNode findFirstSelectedNode(UITreeNode base) {
		if (base.isSelected())
			return base;
		
		for (UIComponent child : base.getChildren()) {
			if (!(child instanceof UITreeNode))
				continue;
			
			UITreeNode foundInChildren = findFirstSelectedNode((UITreeNode)child);
			
			if (foundInChildren != null)
				return foundInChildren;
		}
		
		return null;
	}

	public void setInitAction(MethodExpression initAction) {
		this.initAction = initAction;
	}
	
	public MethodExpression getInitAction() {
		return initAction;
	}
	
	public void setPostCreate(MethodExpression postCreate) {
		this.postCreate = postCreate;
	}
	
	public MethodExpression getPostCreate() {
		return postCreate;
	}
	
	public void expandAll() {
		expandNodesRecursively(getRootNode());
	}
	
	private void expandNodesRecursively(UITreeNode treeNode) {
		if (treeNode.isLeaf())
			return;
		
		treeNode.expand();
		
		if (treeNode.getChildCount() > 0) {
			for (int i = 0; i < treeNode.getChildCount(); i++) {
				if (treeNode.getChildren().get(i) instanceof UITreeNode)
					expandNodesRecursively((UITreeNode)treeNode.getChildren().get(i));
			}
		}
	}

	public void collapseAll() {
		collapseNodesRecursively(getRootNode());
	}

	private void collapseNodesRecursively(UITreeNode treeNode) {
		if (treeNode.getChildCount() > 0) {
			for (int i = 0; i < treeNode.getChildCount(); i++) {
				if (treeNode.getChildren().get(i) instanceof UITreeNode)
					collapseNodesRecursively((UITreeNode)treeNode.getChildren().get(i));
			}
			
			treeNode.collapse();
		}
	}
	
	public void traverse(TraverseCallback callback) {
		traverse(callback, false);
	}
	
	public void traverse(TraverseCallback callback, boolean loadAsync) {
		if (getRootNode() == null)
			return;
		
		getRootNode().traverse(callback, loadAsync);
	}
}