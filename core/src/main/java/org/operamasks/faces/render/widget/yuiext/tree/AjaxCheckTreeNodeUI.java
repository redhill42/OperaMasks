/*
 * $Id: AjaxCheckTreeNodeUI.java,v 1.4 2008/04/03 01:58:27 lishaochuan Exp $
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

import java.util.Map;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.org.json.simple.JSONObject;


public class AjaxCheckTreeNodeUI extends AjaxSimpleCheckTreeNodeUI implements TreeNodeUI {
	@Override
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.CheckTreeNode"};
	}
	
	@Override
	public Class<? extends UITreeNode> getNodeClass() {
		return UICheckTreeNode.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject treeNodeToJSON(UITree tree, UITreeNode treeNode) {
		JSONObject data = super.treeNodeToJSON(tree, treeNode);
		if(Boolean.FALSE.equals(tree.getRootVisible()) && treeNode.getParent() instanceof UITree){
            data.put("uiProvider", "Ext.tree.RooteCheckboxNodeUI");
        }else{
            data.put("uiProvider", "Ext.tree.CheckboxNodeUI");
        }
		
		return data;
	}
	
	@Override
	public Map<String, Object> getTreeNodeConfig(UITree tree, UITreeNode treeNode) {
		Map<String, Object> treeNodeConfig = super.getTreeNodeConfig(tree, treeNode);
		if(Boolean.FALSE.equals(tree.getRootVisible()) && treeNode.getParent() instanceof UITree){
            treeNodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.RootCheckboxNodeUI"));
        }else{
            treeNodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.CheckboxNodeUI"));
        }

		return treeNodeConfig;
	}
	
	@Override
	public String encodeEventScript(UITree tree, TreeEventType eventType) {
		if (UICheckTreeNode.CHECK_STATE_CHANGED.equals(eventType))
			return "";
		
		return super.encodeEventScript(tree, eventType);
	}
}
