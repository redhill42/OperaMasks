/*
 * $Id: TreeDelegate.java,v 1.3 2008/04/16 08:48:56 lishaochuan Exp $
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.component.tree.base.TreeDataProvider;
import org.operamasks.faces.component.tree.impl.UITree;
import org.operamasks.faces.component.tree.impl.UITreeNode;
import org.operamasks.faces.render.delegate.ViewDelegate;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;

public class TreeDelegate implements ViewDelegate {
    public void delegate(FacesContext context) throws IOException {
        ExternalContext ectx = context.getExternalContext();
        Map<String,String> paramMap = ectx.getRequestParameterMap();
        String treeId = paramMap.get(TreeRenderHandler.TREE_PARAM_KEY);
        if (treeId == null) {
            return;
        }
        
        UITree tree = TreeRenderHelper.loadComponentTree(treeId);
        String nodeId = paramMap.get("node");
        UIComponent node = tree.findComponent(nodeId);
        //刷新节点
        UITreeNode parentNode = (UITreeNode)node;
        boolean loadAllNodes = tree.getLoadAllNodes() == null ? false : tree.getLoadAllNodes();
        TreeDataProvider dataProvider =  tree.getValue();
        if(dataProvider != null){
            List<UIComponent> chidren = parentNode.getChildren();
            //先把需要更新的节点移除掉
            if(chidren.size()>0){
                int index = 0;
                while(index < chidren.size()){
                    if(((UITreeNode)chidren.get(index)).getUpdateAble()){
                        chidren.remove(index);
                    }else{
                        index++;
                    }
                }
            }
            
            //从TreeDataProvider那拿到新的数据，再构造组件树 
            TreeRenderHelper.buildNodesByCondition(tree, dataProvider, parentNode, ((UITreeNode)parentNode).getUserData(), loadAllNodes);
        }
        
        JSONArray dataArray = new JSONArray();
        buildTreeNodeJsonData((UITreeNode)node, dataArray, loadAllNodes, tree);
        tree.setLoadAllNodes(false);
        
        //保存组件树状态
        TreeRenderHelper.saveComponentTree(tree);
        
        HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setLocale(context.getViewRoot().getLocale());
        response.setContentType("text/html");
        response.setCharacterEncoding(ectx.getRequestCharacterEncoding());
        response.getWriter().write(dataArray.toString());
        context.responseComplete();
    }
    
    //根据层次递归生成节点的JSON数据
    @SuppressWarnings("unchecked")
    private void buildTreeNodeJsonData(UITreeNode node, JSONArray dataArray, boolean loadAllNodes,  UITree tree){
        for (UIComponent child : node.getChildren()) {
            UITreeNode newTreeNode = (UITreeNode)child;
            JSONObject object = new JSONObject();
            if(newTreeNode.getId() == null){
                newTreeNode.setId(TreeRenderHelper.createUniqueTreeNodeId(tree));
            }
            object.put("id", newTreeNode.getId()); 
                
            if (newTreeNode.getText() != null)
                object.put("text", newTreeNode.getText());
            if (newTreeNode.getIcon() != null){
                object.put("icon", newTreeNode.getIcon());  
            }else{
                object.put("iconCls", TreeRenderHelper.TREE_NODE_BLANK_IMG_CLASS); 
            }
            if (newTreeNode.getExpanded() != null)
                object.put("expanded", newTreeNode.getExpanded());
            if (newTreeNode.getHref() != null)
                object.put("href", newTreeNode.getHref());
            if (newTreeNode.getHrefTarget() != null)
                object.put("hrefTarget", newTreeNode.getHrefTarget());
            if (newTreeNode.getLeaf() != null)
                object.put("leaf", newTreeNode.getLeaf());
            if (newTreeNode.getQtip() != null)
                object.put("qtip", newTreeNode.getQtip());
            if (newTreeNode.getChecked() != null) {
                if(Boolean.TRUE.equals(newTreeNode.getChecked())){
                    object.put("check", "checked"); 
                }else{
                    object.put("check", "unchecked"); 
                }
                
                if (Boolean.TRUE.equals(newTreeNode.getCascade())) {
                    object.put("uiProvider", "Ext.tree.CheckboxNodeUI");
                } else {
                    object.put("uiProvider", "Ext.tree.SimpleCheckboxNodeUI");
                }
            }
            if(loadAllNodes){
                JSONArray childrenArray = new JSONArray();
                buildTreeNodeJsonData((UITreeNode)child, childrenArray, loadAllNodes, tree);
                object.put("children", childrenArray);
            }
            dataArray.add(object);
        }
    }

}
