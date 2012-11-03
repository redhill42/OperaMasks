/*
 * $Id: CheckTreeNodeUtils.java,v 1.5 2008/04/03 01:58:27 lishaochuan Exp $
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
import java.util.Map;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;

public class CheckTreeNodeUtils {
	public static final String KEY_CHECK_TYPE = "_check_type";

	public static String createCheckTreeNodeConfigArray(UITree tree, UICheckTreeNode treeNode) {
	    Map<String, Object> nodeConfig = new HashMap<String, Object>();
	    nodeConfig.put("id", treeNode.getId());
	    nodeConfig.put("text", treeNode.getText());
	    nodeConfig.put("icon", TreeRenderUtils.getImagePath(treeNode.getImage()));
	    nodeConfig.put("text", TreeRenderUtils.getTreeNodeText(tree, treeNode));
	    nodeConfig.put("allowsChildren", treeNode.getAllowsChildren());
	    nodeConfig.put("leaf", treeNode.getLeaf());
	    nodeConfig.put("expanded", treeNode.getExpand());
	    
	    if(Boolean.FALSE.equals(tree.getRootVisible()) && treeNode.getParent() instanceof UITree){
	        nodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.RootCheckboxNodeUI"));
	    }else{
	        nodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.CheckboxNodeUI"));
	    }
	    

	    if (treeNode.getCheckType() == UICheckTreeNode.CheckType.CHECKED) {
	    	nodeConfig.put("check", "checked");
	    } else if (treeNode.getCheckType() == UICheckTreeNode.CheckType.PARTLY_CHECKED) {
	    	nodeConfig.put("check", "partlyChecked");
	    } else {
	    	nodeConfig.put("check", "unchecked");
	    }
	    
	    return ExtJsUtils.createJsArray(nodeConfig);
	}

	public static String getCheckTreeNodeDefinition(UITree tree, UICheckTreeNode treeNode, String jsvar) {
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\nvar %s = new %s(%s);",
				jsvar,
	    		TreeRenderUtils.getTreeNodeClassName(tree, treeNode),
	    		createCheckTreeNodeConfigArray(tree, treeNode)
	    );
		
		return buf.toString();
	}

	public static String getCheckTypeKey(FacesContext context, UITree tree) {
		return FacesUtils.getJsvar(context, tree) + CheckTreeNodeUtils.KEY_CHECK_TYPE;
	}

	public static String getCheckEventHandler(FacesContext context, UITree tree, String submitScript) {
		StringBuilder buf = new StringBuilder();
	    new Formatter(buf).format(
	        "\nfunction(node, state) {" +
	            "\n%s" +
	        "\n}",
	        submitScript
	    );
	    return buf.toString();
	}

	public static String encodeCheckEventRegistration(FacesContext context, UITree tree,
			String checkSubmitScript) {
		StringBuilder buf = new StringBuilder();
		
		String eventOwner = FacesUtils.getJsvar(context, tree);
		String eventHandler = checkSubmitScript;
		String eventName = "check";
			
		buf.append(String.format("%s.on('%s', %s);\n", eventOwner, eventName, eventHandler));
		
		return buf.toString();
	}

	public static void beforeEncodeResource(YuiExtResource resource, Formatter formatter, UITree tree, UITreeNode parent, UITreeNode treeNode) {
		String jsvar = FacesUtils.getJsvar(getFacesContext(), treeNode);
		resource.addVariable(jsvar);
		formatter.format(getCheckTreeNodeDefinition(tree, (UICheckTreeNode)treeNode, jsvar));
		
		if (parent == null) {
	        formatter.format(
	        		"\n%1$s.setRootNode(%2$s);" +
	        		"\n%1$s.render('%3$s');",
	        		FacesUtils.getJsvar(getFacesContext(), tree),
	        		FacesUtils.getJsvar(getFacesContext(), treeNode),
	        		tree.getClientId(FacesContext.getCurrentInstance())
	        );
		} else {
			String parentJsvar = FacesUtils.getJsvar(getFacesContext(), parent);
			
			formatter.format("\n%s.appendChild(%s);", parentJsvar, jsvar);
		}
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
}
