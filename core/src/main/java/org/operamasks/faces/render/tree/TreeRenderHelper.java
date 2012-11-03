/*
 * $Id: TreeRenderHelper.java,v 1.7 2008/04/28 05:39:25 lishaochuan Exp $
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

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.ComponentContainer;
import org.operamasks.faces.component.tree.base.TreeDataProvider;
import org.operamasks.faces.component.tree.base.UITreeNodeBase;
import org.operamasks.faces.component.tree.impl.UITree;
import org.operamasks.faces.component.tree.impl.UITreeNode;

public class TreeRenderHelper {
    public static final String NODE_INDEX_KEY = "_nodeIndex";
    public static final String DELEGATE_KEY = "_delegate";
    public static final String TREE_NODE_BLANK_IMG_CLASS = "blank-image";
    
    //根据树节点得到树组件
    public static UITree getTree(UITreeNodeBase node){
        UIComponent tree = node;
        while(tree.getParent() != null){
            tree = tree.getParent();
            if(tree instanceof UITree){
                return (UITree)tree;
            }
        }
        return null;
    }
    
    //创建树组件上唯一的id
    public static String createUniqueTreeNodeId(UITree tree){
        FacesContext context = FacesContext.getCurrentInstance();
        return tree.getId() + "_" + getIndexParam(context, tree).toString();
    }
    private static Integer getIndexParam(FacesContext context, UITree tree) {
        Map<String, Object> sessionmap = context.getExternalContext().getSessionMap();
        String nodeIndexParam = tree.getClientId(context) + "_" + NODE_INDEX_KEY;
        if (sessionmap.get(nodeIndexParam) == null) {
            sessionmap.put(nodeIndexParam, 1);
            return 0;
        } else {
            int index = Integer.parseInt(sessionmap.get(nodeIndexParam).toString());
            sessionmap.put(nodeIndexParam, index + 1);
            return index;
        }
    }
    
    //保存树组件的状态
    public static void saveComponentTree(UITree tree) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> sessionmap = context.getExternalContext().getSessionMap();
        sessionmap.put(tree.getClientId(context) + DELEGATE_KEY, tree);
    }
    
    //根据树的id来得到树的组件对象
    public static UITree loadComponentTree(String clientId){
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ectx = context.getExternalContext();
        Map<String, Object> sessionmap = ectx.getSessionMap();
        return (UITree) sessionmap.get(clientId + DELEGATE_KEY);
    }
    
    //根据用户绑定的TreeDataProvider以及tree的一些属性来生成组件树
    public static void buildNodesByCondition(UITree tree, TreeDataProvider dataProvider, UIComponent parent, Object parentUserData, Boolean expandAll) {
        Object[] children = dataProvider.getChildren(parentUserData);
        if (children == null) {
            return;
        }
        for (Object child : children) {
            UITreeNode newNode = new UITreeNode();
            newNode.setUpdateAble(true);
            newNode.setId(TreeRenderHelper.createUniqueTreeNodeId(tree));
            newNode.setText(dataProvider.getText(child));
            String icon = dataProvider.getIcon(child);
            if( icon != null ){
                newNode.setIcon(icon); 
            }else{
                newNode.setIconCls(TREE_NODE_BLANK_IMG_CLASS);
            }
            newNode.setChecked(dataProvider.isChecked(child));
            newNode.setExpanded(dataProvider.isExpanded(child));
            newNode.setCascade(dataProvider.isCascade(child));
            newNode.setHref(dataProvider.getHref(child));
            newNode.setHrefTarget(dataProvider.getHrefTarget(child));
            newNode.setUserData(child);
            Object[] childChildren = dataProvider.getChildren(child);
            if(childChildren == null || childChildren.length == 0){
                newNode.setLeaf(true);
            }else{
                newNode.setLeaf(false);
            }
            if(parent instanceof UITree){
                ((UITree)parent).add(newNode);
            }
            if(parent instanceof UITreeNode){
                ((UITreeNode)parent).add(newNode);
            }
            if (expandAll) {
                buildNodesByCondition(tree,dataProvider, newNode, child, expandAll);
            }
        }
    }
    
    //用新树的根节点替换旧树的根节点
    public static UITree replaceRootTreeNode(UITree newTree, UITree oldTree) {
        UITreeNode newRootNode = null;
        for (UIComponent child : newTree.getChildren()) {
            if (child instanceof UITreeNode) {
                newRootNode = (UITreeNode) child;
                break;
            }
        }
        if (newRootNode != null) {
        	for (UIComponent child : oldTree.getChildren()) {
        		if (child instanceof UITreeNode) {
        			oldTree.getChildren().remove(child);
        			break;
        		}
        	}
        	oldTree.getChildren().add(newRootNode);
        }
        return oldTree;
    }
    
    //把一个节点转换为动态代理的节点
    public static UITreeNode getProxyNode(UITreeNode node){
        UITreeNode proxyNode = (UITreeNode)ComponentContainer.getInstance().getComponentFactory(UITreeNode.COMPONENT_TYPE).createComponent();
        proxyNode.setText(node.getText());
        proxyNode.setIcon(node.getIcon());
        proxyNode.setId(node.getId());
        proxyNode.setChecked(node.getChecked());
        proxyNode.setHref(node.getHref());
        proxyNode.setHrefTarget(node.getHrefTarget());
        proxyNode.setDisabled(node.getDisabled());
        proxyNode.setExpanded(node.getExpanded());
        proxyNode.setUserData(node.getUserData());
        proxyNode.setAllowChildren(node.getAllowChildren());
        proxyNode.setCascade(node.getCascade());
        proxyNode.setCls(node.getCls());
        proxyNode.setLeaf(node.getLeaf());
        proxyNode.setQtip(node.getQtip());
        proxyNode.getAttributes().putAll(node.getAttributes());
        return proxyNode;
    }
}
