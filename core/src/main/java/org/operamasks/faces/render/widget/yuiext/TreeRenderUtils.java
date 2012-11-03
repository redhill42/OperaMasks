/*
 * $Id: TreeRenderUtils.java,v 1.4 2008/01/16 02:45:40 yangdong Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.TreeNodeModelFactory;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUIFactory;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class TreeRenderUtils {
	public static final String KEY_TREE_EVENT_TYPE_POSTFIX = "_tree_eventType";
	public static final String KEY_TREE_NODE_ID_POSTFIX = "_tree_node";
	public static final String TREE_NODE_RENDERER_TYPE = "org.operamasks.faces.widget.TreeNode";
	public static final String RENDERER_TYPE = "org.operamasks.faces.widget.TreeNode";
	private static Map<String, TreeEventType> eventTypes;
	
	public static String getNodeIdKey(FacesContext context, UITree tree) {
		return FacesUtils.getJsvar(context, tree) + KEY_TREE_NODE_ID_POSTFIX;
	}
	public static String getEventTypeKey(FacesContext context, UITree tree) {
		return FacesUtils.getJsvar(context, tree) + KEY_TREE_EVENT_TYPE_POSTFIX;
	}

	public static String getTreeNodeDefinition(UITree tree, UITreeNode treeNode, String jsvar) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\nvar %s = new %s(%s);",
				jsvar,
	    		getTreeNodeClassName(tree, treeNode),
	    		createTreeNodeConfigArray(tree, treeNode)
	    );
		
		return buf.toString();
	}
	
	public static String getTreeNodeClassName(UITree tree, UITreeNode node) {
		if (isAsyncTreeNode(tree, node))
			return "Ext.tree.AsyncTreeNode";
		else
			return "Ext.tree.TreeNode";
	}
	
	public static boolean isAsyncTreeNode(UITree tree, UITreeNode node) {
		if (node.isLeaf() || node.getChildCount() > 0)
			return false;
		
		return Boolean.TRUE.equals(node.getAsync()) || (tree.getAsyncData() != null);
	}
	
	public static String createTreeNodeConfigArray(UITree tree, UITreeNode treeNode) {
	    Map<String, Object> treeNodeConfig = TreeNodeUIFactory.getInstance().getTreeNodeConfig(
	    		tree, treeNode, FacesContext.getCurrentInstance().getViewRoot().getRenderKitId());
	    
	    return ExtJsUtils.createJsArray(treeNodeConfig);
	}
	
	public static String getTreeNodeDefinition(UITree tree, UITreeNode treeNode) {
		String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), treeNode);
		
		return getTreeNodeDefinition(tree, treeNode, jsvar);
	}
	
	public static String getImagePath(String image) {
		if (image == null)
			return null;
		
		if (image.startsWith("/"))
			return ((ServletContext)FacesContext.getCurrentInstance(
						).getExternalContext().getContext()).getContextPath() + image;
		
		String uri = ((HttpServletRequest)FacesContext.getCurrentInstance(
				).getExternalContext().getRequest()).getRequestURI();

		return uri.substring(0, uri.lastIndexOf("/") + 1) + image;
	}
	
	public static String getTreeNodeText(UITree tree, UITreeNode treeNode) {
		String nodeText = treeNode.getText() == null ?
				treeNode.getUserData().toString() : treeNode.getText();
				
		boolean escapeText = (tree != null && tree.getEscapeNodeText()) ? true : false;
		if (nodeText != null && escapeText)
			return HtmlEncoder.encode(nodeText);
		else
			return nodeText;
	}
	
	public static TreeEventType getEventType(String eventTypeString) {
		eventTypeString = eventTypeString.trim();
		
		if (TreeRenderUtils.eventTypes == null)
			TreeRenderUtils.eventTypes = new HashMap<String, TreeEventType>();
		
		if (TreeRenderUtils.eventTypes.containsKey(eventTypeString))
			return TreeRenderUtils.eventTypes.get(eventTypeString);
		
		Set<TreeEventType> registeredEventTypes = TreeNodeModelFactory.getInstance().getRegisteredEventTypes();
		
		for (TreeEventType eventType : registeredEventTypes) {
			if (eventType.getTypeString().equals(eventTypeString)) {
				TreeRenderUtils.eventTypes.put(eventTypeString, eventType);
				
				return eventType;
			}
		}
		
		return null;
	}
}
