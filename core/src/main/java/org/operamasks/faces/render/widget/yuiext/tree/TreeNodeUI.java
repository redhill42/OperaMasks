/*
 * $Id: TreeNodeUI.java,v 1.3 2008/01/16 02:45:40 yangdong Exp $
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
import java.util.Map;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.org.json.simple.JSONObject;


public interface TreeNodeUI {
	public String[] getResourceIds();
	public void beginEncodeResource(YuiExtResource resource, Formatter formatter,
			UITree tree, UITreeNode parent, UITreeNode node);
	public void endEncodeResource(YuiExtResource resource, Formatter formatter,
			UITree tree, UITreeNode parent, UITreeNode node);
	public String encodeEventScript(UITree tree, TreeEventType eventType);
	
	public Map<String, Object> decode(FacesContext context, UITree tree,
			UITreeNode treeNode, TreeEventType eventType);
	
	public TreeEventType[] getEventTypesForRegistration(UITree tree);
	
	public String getChangeScript(TreeStateChange change, UITree tree, UITreeNode treeNode);
	
	public Class[] getStateChangeClasses();
	
	public Class<? extends UITreeNode> getNodeClass();
	public String getRenderKitId();
	
	public JSONObject treeNodeToJSON(UITree tree, UITreeNode treeNode);
	
	public Map<String, Object> getTreeNodeConfig(UITree tree, UITreeNode treeNode);
}
