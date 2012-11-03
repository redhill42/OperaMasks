/*
 * $Id: DefaultTreeNodeUI.java,v 1.5 2008/01/16 02:45:40 yangdong Exp $
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

package org.operamasks.faces.render.widget.yuiext.tree;

import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.org.json.simple.JSONObject;

public class DefaultTreeNodeUI implements TreeNodeUI {
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.TreePanel2", "Ext.tree.FacesTreeLoader"};
	}

	public void beginEncodeResource(YuiExtResource resource, Formatter formatter, UITree tree,
			UITreeNode parent, UITreeNode node) {
		String jsvar = FacesUtils.getJsvar(getFacesContext(), node);
		resource.addVariable(jsvar);
		formatter.format(TreeRenderUtils.getTreeNodeDefinition(tree, node, jsvar));
		
		if (parent == null) {
	        formatter.format(
	        		"\n%1$s.setRootNode(%2$s);" +
	        		"\n%1$s.render('%3$s');",
	        		FacesUtils.getJsvar(getFacesContext(), tree),
	        		FacesUtils.getJsvar(getFacesContext(), node),
	        		tree.getClientId(FacesContext.getCurrentInstance())
	        );
		} else {
			String parentJsvar = FacesUtils.getJsvar(getFacesContext(), parent);
			
			formatter.format("\n%s.appendChild(%s);", parentJsvar, jsvar);
		}
	}

	public void endEncodeResource(YuiExtResource resource, Formatter formatter, UITree tree,
			UITreeNode parent, UITreeNode node) {
		String jsvar = FacesUtils.getJsvar(getFacesContext(), node);
		
		if (node.isSelected()) {
			formatter.format("\n%s.select();", jsvar);
		}
		
		resource.releaseVariable(jsvar);		
	}
	
	public String encodeEventScript(UITree tree, TreeEventType eventType) {
		FacesContext context = getFacesContext();
		StringBuilder buf = new StringBuilder();
		
		String eventOwner = null;
		String eventHandler = null;
		String eventName = null;
			
		if (UITreeNode.SELECT.equals(eventType)) {
			eventOwner = FacesUtils.getJsvar(context, tree) + ".getSelectionModel()";
			eventHandler = getSelectionChangeEventHandler(context, tree, eventType);
		} else {
			eventOwner = FacesUtils.getJsvar(context, tree);
			eventHandler = getNormalEventHandler(context, tree, eventType);
		}

		eventName = getOnEventByEventType(eventType);
			
		buf.append(String.format("%s.on('%s', %s);\n", eventOwner, eventName, eventHandler));
		
		return buf.toString();
	}

	protected String getOnEventByEventType(TreeEventType eventType) {
    	if (eventType.equals(UITreeNode.SELECT)) {
    		return "selectionchange";
    	} else if (eventType.equals(UITreeNode.EXPAND) ||
    			eventType.equals(UITreeNode.COLLAPSE)) {
    		return "before" + eventType.getTypeString() + "node";
    	} else {
    		return eventType.getTypeString();
    	}
	}

	protected String getNormalEventHandler(FacesContext context, UITree tree, TreeEventType event) {
        StringBuffer buf = new StringBuffer();
        new Formatter(buf).format(
            "\nfunction(node) {" +
                "\n%s" +
                "\nreturn false;" +
            "\n}",
            getSubmitScript(tree, event)
        );
        return buf.toString();
    }
	
	protected String getSelectionChangeEventHandler(FacesContext context, UITree tree, TreeEventType eventType) {
		StringBuilder buf = new StringBuilder();
        new Formatter(buf).format(
            "\nfunction(selMode, node) {" +
                "%s" +
            "\n}",
            getSubmitScript(tree, eventType)
        );
        return buf.toString();
	}
	
	protected String getSubmitScript(UITree tree, TreeEventType eventType) {
		FacesContext context = getFacesContext();
		return HtmlRenderer.encodeSubmit(context, HtmlRenderer.getParentForm(tree), null,
				HtmlEncoder.enquote(TreeRenderUtils.getEventTypeKey(context, tree)),
				HtmlEncoder.enquote(eventType.getTypeString()),
				HtmlEncoder.enquote(TreeRenderUtils.getNodeIdKey(context, tree)),
				"node.id");
	}
	
    protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	private boolean isSelectOperationRegistered(Set<TreeEventType> registeredEventTypes) {
		if (registeredEventTypes.contains(UITreeNode.SELECT) ||
				registeredEventTypes.contains(UITreeNode.CLICK) ||
						registeredEventTypes.contains(UITreeNode.DOUBLE_CLICK))
			return true;
				
		return false;
	}

	public Class<? extends UITreeNode> getNodeClass() {
		return UITreeNode.class;
	}

	public String getRenderKitId() {
		return "HTML_BASIC";
	}

	public TreeEventType[] getEventTypesForRegistration(UITree tree) {
    	Set<TreeEventType> registeredEventTypes = tree.getRegisteredEventTypes();
    	
    	Set<TreeEventType> finalEventTypes = new HashSet<TreeEventType>();
    	finalEventTypes.addAll(registeredEventTypes);
    	
		if (!isSelectOperationRegistered(registeredEventTypes))
			finalEventTypes.add(UITreeNode.SELECT);
    	
		if (!registeredEventTypes.contains(UITreeNode.EXPAND))
			finalEventTypes.add(UITreeNode.EXPAND);
			
		if (!registeredEventTypes.contains(UITreeNode.COLLAPSE))
			finalEventTypes.add(UITreeNode.COLLAPSE);
		
		return finalEventTypes.toArray(new TreeEventType[finalEventTypes.size()]);
	}

	public String getChangeScript(TreeStateChange change, UITree tree, UITreeNode treeNode) {
		// Only in Ajax mode, we need process tree state changes.
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getStateChangeClasses() {
		// Only in Ajax mode, we need process tree state changes.
		return null;
	}

	public Map<String, Object> decode(FacesContext context, UITree tree,
					UITreeNode treeNode, TreeEventType eventType) {
		// No special decoding need to do
		return null;
	}

	@SuppressWarnings("unchecked")
	public JSONObject treeNodeToJSON(UITree tree, UITreeNode treeNode) {
		JSONObject data = new JSONObject();
		data.put("id", treeNode.getId());
		data.put("text", getTreeNodeText(tree, treeNode));
		data.put("icon", TreeRenderUtils.getImagePath(treeNode.getImage()));
		data.put("allowsChildren", treeNode.getAllowsChildren());
		data.put("leaf", treeNode.isLeaf());

		return data;
	}
	
	protected String getTreeNodeText(UITree tree, UITreeNode treeNode) {
		return TreeRenderUtils.getTreeNodeText(tree, treeNode);
	}
	
	protected String getTreeNodeImage(String image) {
    	return TreeRenderUtils.getImagePath(image);
	}

	public Map<String, Object> getTreeNodeConfig(UITree tree, UITreeNode treeNode) {
	    Map<String, Object> nodeConfig = new HashMap<String, Object>();
	    nodeConfig.put("id", treeNode.getId());
	    nodeConfig.put("text", treeNode.getText());
	    nodeConfig.put("icon", TreeRenderUtils.getImagePath(treeNode.getImage()));
	    nodeConfig.put("text", getTreeNodeText(tree, treeNode));
	    nodeConfig.put("allowsChildren", treeNode.getAllowsChildren());
	    nodeConfig.put("leaf", treeNode.getLeaf());
	    nodeConfig.put("expanded", treeNode.getExpand());
	    
	    return nodeConfig;
	}
}
