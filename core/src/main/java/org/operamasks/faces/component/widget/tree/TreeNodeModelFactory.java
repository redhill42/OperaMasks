/*
 * $Id: TreeNodeModelFactory.java,v 1.3 2008/01/16 02:45:40 yangdong Exp $
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

package org.operamasks.faces.component.widget.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;

public class TreeNodeModelFactory {
	private static TreeNodeModelFactory instance;
	private Map<Class<? extends UITreeNode>, TreeNodeModel> models;
	private Map<TreeEventType, TreeNodeModel> eventTypes;
	
	private TreeNodeModelFactory() {}
	
	public static TreeNodeModelFactory getInstance() {
		if (instance == null) {
			instance = new TreeNodeModelFactory();
			instance.registerModel(new DefaultTreeNodeModel());
			instance.registerModel(new SimpleCheckTreeNodeModel());
			instance.registerModel(new CheckTreeNodeModel());
		}
		
		return instance;
	}
	
	public synchronized void registerModel(TreeNodeModel model) {
		if (models == null)
			models = new HashMap<Class<? extends UITreeNode>, TreeNodeModel>();
		
		models.put(model.getNodeClass(), model);
		eventTypes = null;
	}
	
	public synchronized TreeNodeModel unregisterModel(Class<? extends UITreeNode> nodeClass) {
		if (models == null)
			return null;
		
		TreeNodeModel model =  models.remove(nodeClass);
		eventTypes = null;
	
		return model;
	}
	
	public synchronized TreeNodeModel unregisterModel(TreeNodeModel model) {
		if (models == null)
			return null;
		
		model =  models.remove(model.getNodeClass());
		eventTypes = null;
	
		return model;
	}
	
	public TreeNodeModel getModel(Class<? extends UITreeNode> nodeClass) {
		if (models == null)
			return null;
		
		return models.get(nodeClass);
	}
	
	public Set<TreeEventType> getRegisteredEventTypes() {
		if (models == null)
			return null;

		if (eventTypes != null)
			return eventTypes.keySet();
		
		initEventTypes();
		
		return eventTypes.keySet();
	}
	
	public Set<TreeEventType> getRegisteredEventTypes(Class<? extends UITreeNode> nodeClass) {
		TreeNodeModel model = getModel(nodeClass);
		
		if (model == null || model.getEventTypes() == null)
			return null;
		
		
		Set<TreeEventType> eventTypes = new HashSet<TreeEventType>();
		for (TreeEventType eventType : model.getEventTypes()) {
			eventTypes.add(eventType);
		}
		
		return eventTypes;
	}

	private void initEventTypes() {
		if (eventTypes != null)
			return;
		
		eventTypes = new HashMap<TreeEventType, TreeNodeModel>();
		
		for (Entry<Class<? extends UITreeNode>, TreeNodeModel> entry : models.entrySet()) {
			for (TreeEventType eventType : entry.getValue().getEventTypes()) {
				eventTypes.put(eventType, entry.getValue());				
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends UITreeNode> T createTreeNode(Class<T> nodeClass) {
		TreeNodeModel model = getModel(nodeClass);
		
		if (model != null)
			return (T)model.createTreeNode();
		else
			throw new FacesException(nodeClass + " isn't a registered tree node class.");
	}
	
	public void processEvent(FacesContext context, UITree tree,
					UITreeNode treeNode, TreeEventType eventType,
				Map<String, Object> params) {
		if (eventType == null)
			return;
		
		if (!eventTypes.containsKey(eventType))
			return;
		
		eventTypes.get(eventType).processEvent(context, tree, treeNode,
				eventType, params);
	}
	
	public Class<? extends UITreeNode> getNodeClassByEventType(TreeEventType eventType) {
		initEventTypes();
		
		return eventTypes.get(eventType).getNodeClass();
	}
}
