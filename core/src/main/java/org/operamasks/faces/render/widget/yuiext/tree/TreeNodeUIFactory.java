/*
 * $Id: TreeNodeUIFactory.java,v 1.4 2008/01/28 14:00:25 yangdong Exp $
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.TreeNodeModelFactory;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;

public class TreeNodeUIFactory {
	private Map<UIType, TreeNodeUI> uis;
	private static TreeNodeUIFactory instance;
	
	private TreeNodeUIFactory() {}
	
	public static TreeNodeUIFactory getInstance() {
		if (instance == null) {
			instance = new TreeNodeUIFactory();
			instance.registerUI(new DefaultTreeNodeUI());
			instance.registerUI(new AjaxDefaultTreeNodeUI());
			instance.registerUI(new CheckTreeNodeUI());
			instance.registerUI(new AjaxCheckTreeNodeUI());
			instance.registerUI(new AjaxSimpleCheckTreeNodeUI());
			instance.registerUI(new SimpleCheckTreeNodeUI());
		}
		
		return instance;
	}
	
	public void registerUI(TreeNodeUI ui) {
		if (uis == null)
			uis = new HashMap<UIType, TreeNodeUI>();
		
		uis.put(new UIType(ui.getNodeClass(), ui.getRenderKitId()), ui);
	}
	
	public TreeNodeUI unregisterUI(Class<? extends UITreeNode> nodeClass,
			String renderKitId) {
		if (uis == null)
			return null;
		
		if (renderKitId == null)
			renderKitId = "HTML_BASIC";
		
		return uis.remove(new UIType(nodeClass, renderKitId));
	}
	
	public TreeNodeUI unregisterUI(TreeNodeUI ui) {
		if (uis == null)
			return null;
		
		return uis.remove(new UIType(ui.getNodeClass(), ui.getRenderKitId()));
	}
	
	private static final class UIType {
		private Class<? extends UITreeNode> nodeClass;
		private String renderKitId;
		
		@SuppressWarnings("unchecked")
		public UIType(Class<? extends UITreeNode> nodeClass,
				String renderKitId) {
			if (nodeClass.getName().indexOf("$$") != -1)
				nodeClass = (Class<? extends UITreeNode>)nodeClass.getSuperclass();
			
			this.nodeClass = nodeClass;
			this.renderKitId = renderKitId;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof UIType) {
				UIType other = (UIType)obj;
				
				if (other.nodeClass == null ||
						other.renderKitId == null)
					return false;
				
				return other.nodeClass.equals(this.nodeClass) &&
					other.renderKitId.equals(this.renderKitId);
			}

			return false;
		}
		
		@Override
		public int hashCode() {
			return nodeClass.hashCode() + renderKitId.hashCode();
		}
	}
	
	public TreeNodeUI getUI(Class<? extends UITreeNode> nodeClass,
			String renderKitId) {
		if (uis == null)
			return null;
		
		if (renderKitId == null)
			renderKitId = "HTML_BASIC";
		
		return uis.get(new UIType(nodeClass, renderKitId));
	}

	public String encodeEventsScript(UITree tree, Set<TreeEventType> registeredEventTypes, String renderKitId) {
		StringBuilder buf = new StringBuilder("");
		
		for (TreeEventType eventType : registeredEventTypes) {
			TreeNodeUI ui = findUI(eventType, renderKitId);
			String script = ui.encodeEventScript(tree, eventType);
			if (script != null)
				buf.append(script);
		}
		
		return buf.toString();
	}
	
	private TreeNodeUI findUI(TreeEventType eventType, String renderKitId) {
		Class<? extends UITreeNode> nodeClass = TreeNodeModelFactory.getInstance(
				).getNodeClassByEventType(eventType);
		
		return getUI(nodeClass, renderKitId);
	}
	
	public Set<TreeNodeUI> getUIs(String renderKitId) {
		if (uis == null)
			return null;
		
		Set<TreeNodeUI> uisByRenderKitId = new HashSet<TreeNodeUI>();
		
		for (TreeNodeUI ui : uis.values()) {
			if (ui.getRenderKitId().equals(renderKitId)) {
				uisByRenderKitId.add(ui);
			}
		}
		
		return uisByRenderKitId;
	}

	public String getChangeScript(TreeStateChange change, UITree tree,
			UITreeNode treeNode, String renderKitId) {
		Set<TreeNodeUI> uis = getUIs(renderKitId);
		
		for (TreeNodeUI ui : uis) {
			if (isChangeProcessor(change, ui)) {
				return ui.getChangeScript(change, tree, treeNode);
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean isChangeProcessor(TreeStateChange change, TreeNodeUI ui) {
		for (Class changeClass : ui.getStateChangeClasses()) {
			if (changeClass.equals(change.getClass()))
				return true;
		}
		
		return false;
	}

	public Set<TreeEventType> getEventTypesForRegistration(UITree tree, String renderKitId) {
		Set<TreeNodeUI> uis = getUIs(renderKitId);
		
		Set<TreeEventType> eventTypes = new HashSet<TreeEventType>();
		
		for (TreeNodeUI ui : uis) {
			for (TreeEventType eventType : ui.getEventTypesForRegistration(tree))
			eventTypes.add(eventType);
		}
		
		return eventTypes;
	}

	public static Map<String, Object> decode(FacesContext context, UITree tree, UITreeNode treeNode,
				TreeEventType eventType, String renderKitId) {
		TreeNodeUI ui = TreeNodeUIFactory.getInstance().getUI(treeNode.getClass(), renderKitId);

		return ui.decode(context, tree, treeNode, eventType);
	}

	@SuppressWarnings("unchecked")
	public JSONArray treeNodesToJSON(UITree tree, UITreeNode[] treeNodes,
			String renderKitId) {
		JSONArray dataArray = new JSONArray();
		
		for (UITreeNode treeNode : treeNodes) {
			JSONObject data = getUI(treeNode.getClass(),
					renderKitId).treeNodeToJSON(tree, treeNode);
			
			dataArray.add(data);
		}

		return dataArray;
	}

	public Map<String, Object> getTreeNodeConfig(UITree tree, UITreeNode treeNode, String renderKitId) {
		TreeNodeUI ui = getUI(treeNode.getClass(), renderKitId);

		return ui.getTreeNodeConfig(tree, treeNode);
	}
}
