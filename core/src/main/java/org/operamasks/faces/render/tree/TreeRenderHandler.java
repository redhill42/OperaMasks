/*
 * $Id: TreeRenderHandler.java,v 1.10 2008/04/28 05:16:45 lishaochuan Exp $
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

import static org.operamasks.faces.util.FacesUtils.createChildrenIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.EncodeResourceEnd;
import org.operamasks.faces.annotation.component.ProcessDecodes;
import org.operamasks.faces.component.tree.base.TreeDataProvider;
import org.operamasks.faces.component.tree.impl.UITree;
import org.operamasks.faces.component.tree.impl.UITreeNode;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.ext.AbstractRenderHandler;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;

@DependPackages( { "Ext.tree.TreePanel", "Ext.tree.CheckTreeNode", "Ext.tree.SimpleCheckTreeNode"})
public class TreeRenderHandler extends AbstractRenderHandler {
    public static final String TREE_PARAM_KEY = "_ajaxTree";
    private static final String TREE_EVENT_NAME_KEY = "_eventName";
    private static final String TREE_EVENT_NODE_KEY = "_eventNode";
    private static final String TREE_SELECTED_NODE_KEY = "_selectedNode";
    private static final String TREE_CHECKED_NODES_KEY = "_checkedNodes";
    private static final String TREE_EXPANDED_NODES_KEY = "_expandedNodes";
    private static final String TREE_STATE_KEY = "_state";
    public static final String TREE_LOADER_SUBFIX = "_loader";
    
    @ProcessDecodes
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();

        //先读取内存中的树节点状态，如果存在，则恢复之前的树节点状态
        UITree tree = TreeRenderHelper.loadComponentTree(component.getClientId(context));
        if (tree != null) {
            tree = TreeRenderHelper.replaceRootTreeNode(tree, (UITree) component);
        } else{
            return;
        }
        
        //在拿到客户端的信息之前，先把组件上的状态清空
        clearTreeState(tree);
        
        //从request重新构建树的状态
        buildTreeState(context, tree);
        
    }

    private void clearTreeState(UITree tree) {
        tree.setEventNode(null);
        tree.setSelectedNode(null);
        tree.setExpandedNodes("");
        
        Iterator<UIComponent> kids = createChildrenIterator(tree, false);
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if(kid instanceof UITreeNode){
                UITreeNode treeNode = (UITreeNode)kid;
                if(Boolean.TRUE.equals(treeNode.getChecked())){
                    treeNode.setCheckedWithoutScript(false);
                }
            }
        }
    }

    private void buildTreeState(FacesContext context, UITree tree) {
        String clientId = tree.getClientId(context);
        Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
        String eventName = requestMap.get(clientId + TREE_EVENT_NAME_KEY);
        //拿到客户端传上来的信息，构建树的状态
        if (eventName != null) {
            //保存事件名
            tree.setEventName(eventName);
            
            //保存事件源
            String eventNode = requestMap.get(clientId + TREE_EVENT_NODE_KEY);
            if(eventNode != null && !("".equals(eventNode))){
                tree.setEventNode((UITreeNode) tree.findComponent(eventNode));
            }
            
            //保存被选中的节点
            String selectedNode = requestMap.get(clientId + TREE_SELECTED_NODE_KEY);
            if(selectedNode != null && !("".equals(selectedNode))){
                tree.setSelectedNode((UITreeNode) tree.findComponent(selectedNode));
            }
            
            
            //设置被勾中的节点
            String checkedNodes = requestMap.get(clientId + TREE_CHECKED_NODES_KEY);
            if(checkedNodes != null && !("".equals(checkedNodes))){
                String[] nodes = checkedNodes.split(",");
                for (String node : nodes) {
                    UITreeNode checkedTreeNode = (UITreeNode) tree.findComponent(node);
                    if(checkedTreeNode != null){
                        checkedTreeNode.setCheckedWithoutScript(true);
                    }
                }
            }
            
            //保存树上展开节点的信息，格式如："id1,id2,id3,"
            String expandedNodesParam = requestMap.get(clientId + TREE_EXPANDED_NODES_KEY);
            if(expandedNodesParam != null){
                tree.setExpandedNodes(expandedNodesParam);
            }
        }
    }


    @EncodeHtmlBegin
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {

        String clientId = component.getClientId(context);
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.startElement("div", component);
        out.writeAttribute("id", clientId, null);
        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        if (style != null) {
            out.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            out.writeAttribute("class", styleClass, null);
        }

        //放几个隐藏域，用来存放树的状态,因此树的外面需要有w:form
        String[] hiddenFieldKeys = new String[]{
                TREE_EVENT_NAME_KEY,// 存放事件名
                TREE_EVENT_NODE_KEY,// 存放单个数据源，如click,expand,colapse
                TREE_CHECKED_NODES_KEY,// 存放勾中的树节点
                TREE_SELECTED_NODE_KEY, //存放选中的树节点
                TREE_EXPANDED_NODES_KEY,//存放展开的树节点
                TREE_STATE_KEY,
        };
        for(String key : hiddenFieldKeys){
            String filedName = clientId + key;
            out.startElement("input", component);
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("id", filedName, null);
            out.writeAttribute("name", filedName, null);
            if(TREE_STATE_KEY.equals(key)){
                out.writeAttribute("value", clientId, null);
            }
            out.endElement("input");
        }
    }

    @EncodeHtmlEnd
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.endElement("div");
        out.write("\n");
    }

    @EncodeResourceBegin
    public void resourceBegin(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        UITree tree = (UITree)component;
        //建立树组件
        buildChildNodes(context, tree);
        
        //如果选择了保存树状态，那么拿到之前保存的树的expandedNodes属性
        if(Boolean.TRUE.equals(tree.getSaveState())){
            UITree savedTree = TreeRenderHelper.loadComponentTree(tree.getClientId(context));
            if(savedTree != null){
                tree.setExpandedNodes(savedTree.getExpandedNodes());
                restoreTreeState(context, tree); 
            }
        }

        YuiExtResource resource = getResourceInstance(rm);
        String jsvar = resource.allocVariable(component);
        String clientId = component.getClientId(context);
        Formatter fmt = new Formatter(new StringBuffer());

        // tree loader
        String jsvar_loader = jsvar + TREE_LOADER_SUBFIX;
        resource.addVariable(jsvar_loader);
        fmt.format("%s = new Ext.tree.TreeLoader({\n", jsvar_loader);
        fmt.format("dataUrl:'%s',\n", HtmlRenderer.getActionURL(context));
        fmt.format("baseParams: {\n", TREE_PARAM_KEY, clientId);
        fmt.format("'%s': '%s'\n", TREE_PARAM_KEY, clientId);
        fmt.format("}});\n");

        // tree panel
        fmt.format("%s = new Ext.tree.TreePanel({", jsvar);
        ExtConfig config = new ExtConfig(component);
        config.set("loader", jsvar_loader, true);
        config.set("selModel", "new Ext.tree.DefaultSelectionModel", true);
        fmt.format(config.toScript());
        fmt.format("});\n");

        // tree root
        for (UIComponent child : component.getChildren()) {
            if (child instanceof UITreeNode) {
                UITreeNode node = (UITreeNode) child;
                fmt.format("%s.setRootNode(new Ext.tree.AsyncTreeNode({\n", jsvar);
                ExtConfig rootNodeconfig = new ExtConfig(node);
                rootNodeconfig.remove("checked");
                if (node.getChecked() != null) {
                    if(Boolean.TRUE.equals(node.getChecked())){
                        rootNodeconfig.set("check", "checked"); 
                    }else{
                        rootNodeconfig.set("check", "unchecked"); 
                    }
                    
                    if (Boolean.TRUE.equals(node.getCascade())) {
                        rootNodeconfig.set("uiProvider", "Ext.tree.CheckboxRootNodeUI", true);
                    } else {
                        rootNodeconfig.set("uiProvider", "Ext.tree.SimpleCheckboxRootNodeUI", true);
                    }
                }
                if(node.getIcon() == null){
                    rootNodeconfig.set("iconCls", TreeRenderHelper.TREE_NODE_BLANK_IMG_CLASS);
                }
                fmt.format(rootNodeconfig.toScript());
                fmt.format("}));\n");
                fmt.format("%s.render('%s');\n", jsvar, clientId);
                break;
            }
        }
        resource.addInitScript(fmt.toString());

        TreeRenderHelper.saveComponentTree((UITree)component);
    }
    
    private void restoreTreeState(FacesContext context, UITree tree) {
        String expandedNodesParam = tree.getExpandedNodes();
        if(expandedNodesParam != null){
            String[] expandedNodes = expandedNodesParam.split(",");
            for(String expandedNode : expandedNodes){
                UITreeNode node = (UITreeNode)tree.findComponent(expandedNode);
                if(node != null){
                    node.setExpanded(true);
                }
            }
        }
    }

    private void buildChildNodes(FacesContext context, UIComponent component) {
        UITree tree = (UITree) component;
        TreeDataProvider dataProvider = tree.getValue();
        if (dataProvider != null) {
            // 如果指定了value属性，那么删除<w:tree>标签的所有子<w:treeNode>
            for (UIComponent c : tree.getChildren()) {
                if (c instanceof UITreeNode) {
                    tree.getChildren().remove(c);
                }
            }
            // 根据UITree的loadAllNodes属性来决定是否加载所有的节点
            boolean loadAllNodes = tree.getLoadAllNodes() == null ? false : tree.getLoadAllNodes();
            TreeRenderHelper.buildNodesByCondition(tree, dataProvider, tree, null, loadAllNodes);
        }
    }

    @EncodeResourceEnd
    public void resourceEnd(FacesContext context, UITree tree, ResourceManager rm) throws IOException {
        Formatter fmt = new Formatter(new StringBuffer());
        String jsvar = FacesUtils.getJsvar(context, tree);
        String clientId = tree.getClientId(context);
        // 默认给tree注册事件处理方法，当触发事件的时候，并不会提交请求，而是把信息记录到隐藏域
        for (String event : getSupportEvents(tree)) {
            String eventName = event.replace("on", "");
            Object eventHanlder = tree.getAttributes().get(event);
            String ajaxActionScript = eventHanlder == null ? "" : eventHanlder.toString();
            //如果saveState为true的时候，当节点展开或者收缩的时候，需要把状态提交到服务器
            if(Boolean.TRUE.equals(tree.getSaveState())
               && "".equals(ajaxActionScript) 
               && ("collapsenode".equals(eventName) || "expandnode".equals(eventName))){
                UIForm form = HtmlRenderer.getParentForm(tree);
                ajaxActionScript += String.format(
                    "OM.ajax.action(%s,null,'%s',true);return true;",
                    ((form == null) ? "null" : "'" + form.getClientId(context) + "'"),
                    tree.getClientId(context)
                );
            }
            fmt.format("%s.on('%s',function(node){buildEventParams('%s', node, '%s', %s);%s});\n", 
                        jsvar, 
                        eventName, 
                        eventName, 
                        clientId, 
                        jsvar, 
                        ajaxActionScript);
        }
        //onselect的处理比较特殊
        Object selectAjaxActionHandler = tree.getAttributes().get("onselect");
        String selectHandler = "";
        if(selectAjaxActionHandler != null){
            selectHandler = selectAjaxActionHandler.toString();
        }
        fmt.format("%s.getSelectionModel().on('selectionchange',function(selectionModel,node){buildEventParams('select', node, '%s', %s);%s});\n", 
                    jsvar, 
                    clientId, 
                    jsvar, 
                    selectHandler); 
        
        
        if(Boolean.TRUE.equals(tree.getExpandAll())){
            //如果初始化的时候需要expandAll,那么设置一个标志，下面load的时候，不需要重新去计算树的状态
            fmt.format("window.%sIsExpandAll = true;\n", jsvar);
            fmt.format("%s.expandAll();\n", jsvar); 
        }
        
        //把树上默认勾中的节点信息写到隐藏域中去
        List<UITreeNode> checkedNodes = tree.getCheckedNodes();
        StringBuffer sb = new StringBuffer();
        for(UITreeNode node : checkedNodes){
            sb.append(node.getId()).append(",");
        }
        fmt.format("document.getElementById('%s').value='%s';\n", clientId + TREE_CHECKED_NODES_KEY, sb.toString()); 
        
        //当取数据(也就是触发loader的load事件)的时候，让客户端重新计算树上勾中的节点
        String jsvar_loader = jsvar + TREE_LOADER_SUBFIX;
        fmt.format("%s.on('load',function(node){var isExpandAll=window.%sIsExpandAll;if(isExpandAll){isExpandAll=false;return;}buildEventParams('check', %s.getRootNode(), '%s', %s);});\n", 
                jsvar_loader, 
                jsvar,
                jsvar,
                clientId, 
                jsvar);
        
        YuiExtResource resource = getResourceInstance(rm);
        resource.addInitScript(fmt.toString());
    }

    // tree组件支持的所有事件
    private List<String> getSupportEvents(UITree tree) {
        List<String> events = new ArrayList<String>();
        events.add("oncheck");
        if(tree.getOncollapsenode() != null || Boolean.TRUE.equals(tree.getSaveState())){
            events.add("oncollapsenode");
        }
        if(tree.getOnexpandnode() != null || Boolean.TRUE.equals(tree.getSaveState())){
            events.add("onexpandnode");
        }
        return events;
    }

    @EncodeAjaxBegin
    public void ajaxResponse(FacesContext context, UIComponent component) throws IOException {
        ExternalContext ectx = context.getExternalContext();
        Map<String,String> paramMap = ectx.getRequestParameterMap();
        String clientId = component.getClientId(context);
        String key = clientId + TREE_STATE_KEY;
        String treeId = paramMap.get(key);
        if (treeId != null && treeId.equals(component.getClientId(context))) {
            TreeRenderHelper.saveComponentTree((UITree)component);
        }
    }
}

