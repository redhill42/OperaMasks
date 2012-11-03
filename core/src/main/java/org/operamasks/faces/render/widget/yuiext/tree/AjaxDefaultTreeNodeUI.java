/*
 * $Id: AjaxDefaultTreeNodeUI.java,v 1.3 2007/12/29 10:04:57 yangdong Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.component.widget.tree.state.TreeNodeAddChild;
import org.operamasks.faces.component.widget.tree.state.TreeNodeChangeImage;
import org.operamasks.faces.component.widget.tree.state.TreeNodeChangeText;
import org.operamasks.faces.component.widget.tree.state.TreeNodeClearChildren;
import org.operamasks.faces.component.widget.tree.state.TreeNodeCollapse;
import org.operamasks.faces.component.widget.tree.state.TreeNodeExpand;
import org.operamasks.faces.component.widget.tree.state.TreeNodeExpandTo;
import org.operamasks.faces.component.widget.tree.state.TreeNodeRemoveChild;
import org.operamasks.faces.component.widget.tree.state.TreeNodeSelect;
import org.operamasks.faces.component.widget.tree.state.TreeNodeUnselect;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxDefaultTreeNodeUI extends DefaultTreeNodeUI {
	@Override
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.TreePanel2", "Ext.tree.FacesTreeLoader"};
	}
	
	@Override
    protected String getNormalEventHandler(FacesContext context,
    		UITree tree, TreeEventType eventType) {
        StringBuffer buf = new StringBuffer();
        new Formatter(buf).format(
        		"\nfunction(node) {" +
                	"\nOM.ajax.submit(%s, %s, ['%s=%s', '%s='+node.id], true);" +
                "\n}",
            ExtJsUtils.getComponentSource(context, tree),
            HtmlEncoder.enquote(HtmlRenderer.getActionURL(context), '\''),
            TreeRenderUtils.getEventTypeKey(context, tree),
            eventType.getTypeString(),
            TreeRenderUtils.getNodeIdKey(context, tree)
        );
        
        return buf.toString();
    }
    
	@Override
    protected String getSelectionChangeEventHandler(FacesContext context,
    		UITree tree, TreeEventType eventType) {
		StringBuffer buf = new StringBuffer();
        new Formatter(buf).format(
        		"\nfunction(selMode, node) {" +
                	"\nOM.ajax.submit(%s, %s, ['%s=%s', '%s='+(node==null?'':node.id)], true);" +
                "\n}",
            ExtJsUtils.getComponentSource(context, tree),
            HtmlEncoder.enquote(HtmlRenderer.getActionURL(context), '\''),
            TreeRenderUtils.getEventTypeKey(context, tree),
            eventType.getTypeString(),
            TreeRenderUtils.getNodeIdKey(context, tree)
        );
        
        return buf.toString();
    }
    
    protected String getOnEventByEventType(TreeEventType eventType) {
    	if (eventType == UITreeNode.SELECT) {
    		return "selectionchange";
    	} else if (eventType.equals(UITreeNode.EXPAND) ||
    			eventType.equals(UITreeNode.COLLAPSE)) {
    		return "before" + eventType.getTypeString() + "node";
    	} else {
    		return eventType.getTypeString();
    	}
	}
    
    @Override
    public String getRenderKitId() {
    	return "AJAX";
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Class[] getStateChangeClasses() {
		return new Class[] {
				TreeNodeSelect.class,
				TreeNodeUnselect.class,
				TreeNodeExpand.class,
				TreeNodeCollapse.class,
				TreeNodeAddChild.class,
				TreeNodeRemoveChild.class,
				TreeNodeClearChildren.class,
				TreeNodeChangeText.class,
				TreeNodeChangeImage.class,
				TreeNodeExpandTo.class
		};
    }
    
    @Override
    public String getChangeScript(TreeStateChange change, UITree tree, UITreeNode treeNode) {
    	if (TreeNodeClearChildren.class.equals(change.getClass())) {
    		return getClearChildrenScript(tree, treeNode);
    	} else if (TreeNodeSelect.class.equals(change.getClass())) {
    		return getSimpleMethodCallScript(tree, treeNode, "select2");
    	} else if (TreeNodeUnselect.class.equals(change.getClass())) {
    		return getSimpleMethodCallScript(tree, treeNode, "unselect2");
    	} else if (TreeNodeExpand.class.equals(change.getClass())) {
    		return getTreeNodeExpandScript((TreeNodeExpand)change, tree, treeNode);
    	} else if (TreeNodeCollapse.class.equals(change.getClass())) {
    		return getSimpleMethodCallScript(tree, treeNode, "collapse2");
    	} else if (TreeNodeAddChild.class.equals(change.getClass())) {
    		return getTreeNodeAddChildScript((TreeNodeAddChild)change, tree, treeNode);
    	} else if (TreeNodeRemoveChild.class.equals(change.getClass())) {
    		return getTreeNodeRemoveChildScript((TreeNodeRemoveChild)change, tree, treeNode);
    	} else if (TreeNodeChangeText.class.equals(change.getClass())) {
    		TreeNodeChangeText changeText = (TreeNodeChangeText)change;
    		return getSetTextScript(tree, treeNode, changeText.getNewText());
    	} else if (TreeNodeChangeImage.class.equals(change.getClass())) {
    		TreeNodeChangeImage changeImage = (TreeNodeChangeImage)change;
    		return getSetImageScript(tree, treeNode, changeImage.getNewImage());
    	} else if (TreeNodeExpandTo.class.equals(change.getClass())) {
    		return getExpandToScript(tree, treeNode);
    	}
    	
    	return null;
    }
    
    private String getTreeNodeExpandScript(TreeNodeExpand change, UITree tree, UITreeNode treeNode) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\n%s.getNodeById('%s').expand2(%s);",
				FacesUtils.getJsvar(getFacesContext(), tree),
				treeNode.getId(),
				change.isRecursive() ? "true" : "false"
		);
		
		return buf.toString();
	}

	private String getExpandToScript(UITree tree, UITreeNode treeNode) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\n%1$s.expandPath2(%1$s.getNodeById('%2$s').getPath());",
				FacesUtils.getJsvar(getFacesContext(), tree),
				treeNode.getId()
		);
		
		return buf.toString();
	}

	private String getClearChildrenScript(UITree tree, UITreeNode treeNode) {
    	if (treeNode.getChildCount() == 0)
    		return null;
    	
    	StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		for (UIComponent child : treeNode.getChildren()) {
			fmt.format("\n%1$s.getNodeById('%2$s').removeChild(%1$s.getNodeById('%3$s'));",
					FacesUtils.getJsvar(getFacesContext(), tree),
					treeNode.getId(),
					((UITreeNode)child).getId());
		}
		
		return buf.toString();
	}

	public String getSetTextScript(UITree tree, UITreeNode treeNode, String text) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\n%s.getNodeById('%s').setText('%s');",
				FacesUtils.getJsvar(getFacesContext(), tree),
				treeNode.getId(),
				text
		);
		
		return buf.toString();
    }
    
    public String getSetImageScript(UITree tree, UITreeNode treeNode, String image) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format(
				"\nvar iconEl = %s.getNodeById('%s').getUI().getIconEl();" +
				"\niconEl.className = 'x-tree-node-icon';" +
				"\niconEl.src = '%s';",
				FacesUtils.getJsvar(getFacesContext(), tree),
				treeNode.getId(),
				TreeRenderUtils.getImagePath(image)
		);
		
		return buf.toString();
    }
    
    private String getTreeNodeAddChildScript(TreeNodeAddChild change, UITree tree, UITreeNode treeNode) {
    	UITreeNode child = change.getChild();
    	int index = change.getIndex();
    	
		FacesContext context = FacesContext.getCurrentInstance();
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		String jsvar = FacesUtils.getJsvar(context, child);
		fmt.format(TreeRenderUtils.getTreeNodeDefinition(tree, child, jsvar));
		
		fmt.format(
				"\n%1$s.getNodeById('%2$s').insertBefore(%3$s, %1$s.getNodeById('%2$s').item(%4$d));" +
				"\nif (%1$s.getNodeById('%2$s').childNodes.length > 0) {" +
					"\n%1$s.getNodeById('%2$s').leaf = false;" +
					"\n%1$s.getNodeById('%2$s').allowsChildren = true;" +
					"\nif (typeof %1$s.getNodeById('%2$s').loaded != 'undefined')" +
						"%1$s.getNodeById('%2$s').loaded = true" +
				"\n}",
				FacesUtils.getJsvar(context, tree),
				treeNode.getId(),
				jsvar,
				index
		);
		
		return buf.toString();
	}
    
	public String getTreeNodeRemoveChildScript(TreeNodeRemoveChild change, UITree tree, UITreeNode treeNode) {
		int index = change.getIndex();
		
		FacesContext context = FacesContext.getCurrentInstance();
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format(
				"\n%1$s.getNodeById('%2$s').removeChild2(%1$s.getNodeById('%2$s').item(%3$d));" +
				"\nif (%1$s.getNodeById('%2$s').childNodes.length == 0) {" +
					"\n%1$s.getNodeById('%2$s').leaf = true;" +
				"\n}",
				FacesUtils.getJsvar(context, tree),
				treeNode.getId(),
				index
		);
		
		return buf.toString();
	}


	private String getSimpleMethodCallScript(UITree tree, UITreeNode treeNode, String clientCommand) {
		StringBuilder buf = new StringBuilder();
		new Formatter(buf).format(
				"\n%s.getNodeById('%s').%s();",
				FacesUtils.getJsvar(FacesContext.getCurrentInstance(), tree),
				treeNode.getId(),
				clientCommand
		);
		
		return buf.toString();
    }
}
