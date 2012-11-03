/*
 * $Id: CheckTreeNodeUI.java,v 1.3 2008/01/16 02:45:40 yangdong Exp $
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

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;


public class CheckTreeNodeUI extends SimpleCheckTreeNodeUI implements TreeNodeUI {
	@Override
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.CheckTreeNode"};
	}
	
	@Override
	public Class<? extends UITreeNode> getNodeClass() {
		return UICheckTreeNode.class;
	}
	
	@Override
	public String encodeEventScript(UITree tree, TreeEventType eventType) {
		if (UICheckTreeNode.CHECK_STATE_CHANGED.equals(eventType))
			return "";
		
		return super.encodeEventScript(tree, eventType);
	}
}
