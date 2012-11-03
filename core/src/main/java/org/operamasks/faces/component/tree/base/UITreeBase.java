package org.operamasks.faces.component.tree.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.tree.impl.UITreeNode;
import org.operamasks.faces.render.tree.TreeRenderHandler;
import org.operamasks.faces.render.tree.TreeRenderHelper;
import org.operamasks.faces.tools.annotation.ComponentMeta;
import org.operamasks.faces.util.FacesUtils;

@ComponentMeta(tagName="myTree")
@Component(renderHandler=TreeRenderHandler.class)
public abstract class UITreeBase extends UIComponentBase{
    
    //ext config
    @ExtConfigOption protected Boolean animCollapse;
    @ExtConfigOption protected Boolean autoHeight;
    @ExtConfigOption protected Boolean autoScroll;
    @ExtConfigOption protected Boolean autoShow;
    @ExtConfigOption protected Boolean autoWidth;
    @ExtConfigOption protected Boolean bodyBorder;
    @ExtConfigOption protected String bodyStyle;
    @ExtConfigOption protected Boolean border;
    @ExtConfigOption protected Boolean collapseFirst;
    @ExtConfigOption protected Boolean collapsed;
    @ExtConfigOption protected Boolean collapsible;
    @ExtConfigOption protected Boolean draggable;
    @ExtConfigOption protected Boolean floating;
    @ExtConfigOption protected Boolean footer;
    @ExtConfigOption protected Boolean frame;
    @ExtConfigOption protected Boolean header;
    @ExtConfigOption protected Boolean headerAsText;
    @ExtConfigOption protected Integer height;
    @ExtConfigOption protected Boolean hideBorders;
    @ExtConfigOption protected Boolean hideCollapseTool;
    @ExtConfigOption protected Boolean hideParent;
    @ExtConfigOption protected String layout;
    @ExtConfigOption protected Boolean maskDisabled;
    @ExtConfigOption protected Boolean shadow;
    @ExtConfigOption protected Integer shadowOffset;
    @ExtConfigOption protected Boolean shim;
    @ExtConfigOption protected String title;
    @ExtConfigOption protected Boolean titleCollapse;
    @ExtConfigOption protected Integer width;
    
    @ExtConfigOption protected Boolean animate;
    @ExtConfigOption protected Boolean containerScroll;
    @ExtConfigOption protected String hlColor;
    @ExtConfigOption protected Boolean rootVisible;
    
    //data
    protected TreeDataProvider value;
    
    //event
    protected String oncheck;
    protected String onclick;
    protected String oncollapsenode;
    protected String onexpandnode;
    
    //other
    protected Boolean saveState;
    protected String expandedNodes;
    protected Boolean loadAllNodes;
    protected Boolean expandAll;
    protected String eventName;
    protected UITreeNode eventNode;
    protected UITreeNode selectedNode;
    
    /**
     * 展开树的所有节点
     */
    @Operation
    public void expandAll(){
        this.loadAllNodes = true;
    };
    
    /**
     * 收缩树的所有节点
     */
    @Operation
    public void collapseAll(){};
    
    /**
     * 加载树上所有的节点
     */
    public void loadAllNodes(){
        this.loadAllNodes = true;
        getRootNode().reloadChildren();
    };
    
    /**
     * 得到树上所有勾中的节点
     */
    public List<UITreeNode> getCheckedNodes(){
        List<UITreeNode> checkedNodes = new ArrayList<UITreeNode>();
        Iterator<UIComponent> kids = FacesUtils.createChildrenIterator(this, false);
        while (kids.hasNext()) {
            UIComponent current = kids.next();
            if(current instanceof UITreeNode){
                UITreeNode node = ((UITreeNode)current);
                if(Boolean.TRUE.equals(node.getChecked())){
                    checkedNodes.add(node);
                }
            }
        }
        return checkedNodes;
    }
    
    /**
     * 给树加上根节点
     */
    public void add(UITreeNode node){
        UITreeNode proxyNode = TreeRenderHelper.getProxyNode(node);
        this.getChildren().add(proxyNode);
    }
    
    /**
     * 得到树的根节点
     */
    public UITreeNode getRootNode(){
        for(UIComponent c : this.getChildren()){
            if(c instanceof UITreeNode){
                return (UITreeNode)c;
            }
        }
        return null;
    }
}
