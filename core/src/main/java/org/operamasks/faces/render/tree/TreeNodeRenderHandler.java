/*
 * $Id: TreeNodeRenderHandler.java,v 1.4 2008/04/17 06:12:33 lishaochuan Exp $
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
package org.operamasks.faces.render.tree;

import java.util.Formatter;

import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.component.tree.impl.UITree;
import org.operamasks.faces.component.tree.impl.UITreeNode;
import org.operamasks.faces.render.ext.AbstractRenderHandler;
import org.operamasks.faces.util.FacesUtils;

@DependPackages({"Ext.tree.TreePanel","Ext.tree.CheckTreeNode"})
public class TreeNodeRenderHandler extends AbstractRenderHandler {
    
    @OperationListener("reloadChildren")
    public void reloadChildren(UITreeNode node){
        UITree tree = TreeRenderHelper.getTree(node);
        if(tree != null){
            FacesContext context = FacesContext.getCurrentInstance();
            Formatter fmt = new Formatter(new StringBuffer());
            String jsvar = FacesUtils.getJsvar(context, tree);
            String jsvar_loader = jsvar + TreeRenderHandler.TREE_LOADER_SUBFIX;
            fmt.format("updateTreeNode(%s, %s, '%s');\n", jsvar, jsvar_loader, node.getId());
            
            addOperationScript(fmt.toString());
        }
    }
    
    @OperationListener("remove")
    public void remove(UITreeNode node){
        UITree tree = TreeRenderHelper.getTree(node);
        if(tree != null){
            FacesContext context = FacesContext.getCurrentInstance();
            String treeJsvar = FacesUtils.getJsvar(context, tree);
            Formatter fmt = new Formatter(new StringBuffer());
            fmt.format("var treeNode_toRemove=findNodeById(%s.getRootNode(),'%s');\n", treeJsvar, node.getId());
            fmt.format("treeNode_toRemove.remove();\n");
            fmt.format("var treeNode_toExpand=findNodeById(%s.getRootNode(),'%s');\n", treeJsvar, node.getParent().getId());
            fmt.format("treeNode_toExpand.expand();\n");
            addOperationScript(fmt.toString());
        }
        node.getParent().getChildren().remove(node);
    }
    
    @OperationListener("setChecked")
    public void setChecked(UITreeNode node, Boolean checked){
        UITree tree = TreeRenderHelper.getTree(node);
        if(tree != null && node.getChecked() != null){
            FacesContext context = FacesContext.getCurrentInstance();
            String treeJsvar = FacesUtils.getJsvar(context, tree);
            Formatter fmt = new Formatter(new StringBuffer());
            fmt.format("var treeNode_toCheck=findNodeById(%s.getRootNode(),'%s');\n", treeJsvar, node.getId());
            fmt.format("try{treeNode_toCheck.getUI().setChecked(%s);}catch(e){};\n", checked);
            addOperationScript(fmt.toString());
        }
    }
    
    @OperationListener("setIcon")
    public void setIcon(UITreeNode node, String icon){
        UITree tree = TreeRenderHelper.getTree(node);
        if(tree != null && node.getIcon() != null){
            FacesContext context = FacesContext.getCurrentInstance();
            String treeJsvar = FacesUtils.getJsvar(context, tree);
            Formatter fmt = new Formatter(new StringBuffer());
            fmt.format("var treeNode_toIcon=findNodeById(%s.getRootNode(),'%s');\n", treeJsvar, node.getId());
            fmt.format("try{treeNode_toIcon.getUI().getIconEl().src = '%s';}catch(e){};\n", node.getIcon());
            addOperationScript(fmt.toString());
        }
    }
    
    @OperationListener("setText")
    public void setText(UITreeNode node, String text){
        UITree tree = TreeRenderHelper.getTree(node);
        if(tree != null && node.getText() != null){
            FacesContext context = FacesContext.getCurrentInstance();
            String treeJsvar = FacesUtils.getJsvar(context, tree);
            Formatter fmt = new Formatter(new StringBuffer());
            fmt.format("var treeNode_toUpdate=findNodeById(%s.getRootNode(),'%s');\n", treeJsvar, node.getId());
            fmt.format("treeNode_toUpdate.setText('%s');\n", node.getText());
            addOperationScript(fmt.toString());
        }
    }
}
