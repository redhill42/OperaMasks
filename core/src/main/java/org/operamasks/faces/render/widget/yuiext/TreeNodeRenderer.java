/*
 * $Id: TreeNodeRenderer.java,v 1.3 2007/12/19 07:44:49 yangdong Exp $
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
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.TreeNodeModelFactory;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUI;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUIFactory;
import org.operamasks.faces.util.FacesUtils;

public class TreeNodeRenderer extends Renderer {
	@Override
	public void decode(FacesContext context, UIComponent component) {
		UITreeNode node = (UITreeNode)component;
		UITree tree = node.getTree();

		if (isOwnEvent(context, tree, node))
			processEvent(context, tree, node);
		
		super.decode(context, component);
	}
	
	private void processEvent(FacesContext context, UITree tree, UITreeNode node) {
		String eventTypeString = getEventType(context, tree);
		TreeEventType eventType = TreeRenderUtils.getEventType(eventTypeString);
		
		if (eventType == null)
			return;
		
		Map<String, Object> params = TreeNodeUIFactory.decode(context, tree,
				node, eventType, getRenderKitId());
		
		TreeNodeModelFactory.getInstance().processEvent(context, tree, node,
				eventType, params);
	}
	
	protected String getEventType(FacesContext context, UITree tree) {
		return context.getExternalContext().getRequestParameterMap().get(
				TreeRenderUtils.getEventTypeKey(context, tree));
	}

	protected boolean isOwnEvent(FacesContext context, UITree tree, UITreeNode node) {
		return FacesUtils.isOwnEventOfTreeNode(context, tree, node);
	}

	protected boolean isTreeEvent(FacesContext context, UITree tree) {
		return getEventType(context, tree) != null;
	}
	
	public void encodeResource(YuiExtResource resource, Formatter formatter,
			UITree tree, UITreeNode parent, UITreeNode node) {
		TreeNodeUI ui = TreeNodeUIFactory.getInstance().getUI(node.getClass(), getRenderKitId());
		ui.beginEncodeResource(resource, formatter, tree, parent, node);
		
		if (node.isExpand() && node.getChildCount() == 0)
			tree.loadAsyncNodes(node);
		
		if (node.getChildCount() > 0) {
			Iterator<UIComponent> children = node.getChildren().iterator();
			while (children.hasNext()) {
				UIComponent component = children.next();
				
				if (component instanceof UITreeNode) {
					UITreeNode child = (UITreeNode)component;
					child.setRendererType(TreeRenderUtils.RENDERER_TYPE);
					TreeNodeRenderer renderer = getTreeNodeRenderer(child);
					renderer.encodeResource(resource, formatter, tree, node, child);
				}
			}
		}
		
		ui.endEncodeResource(resource, formatter, tree, parent, node);
	}
	
	protected String getRenderKitId() {
		return "HTML_BASIC";
	}

	private TreeNodeRenderer getTreeNodeRenderer(UITreeNode node) {
		return (TreeNodeRenderer)getFacesContext().getRenderKit().getRenderer(
				node.getFamily(), node.getRendererType());
	}
	
	private FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
}
