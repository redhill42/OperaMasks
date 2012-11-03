package org.operamasks.faces.component.tree.base;

import javax.faces.component.UIComponentBase;

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.tree.impl.UITreeNode;
import org.operamasks.faces.render.tree.TreeNodeRenderHandler;
import org.operamasks.faces.render.tree.TreeRenderHelper;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName = "myTreeNode")
@Component(renderHandler=TreeNodeRenderHandler.class)
public abstract class UITreeNodeBase extends UIComponentBase{
    @ExtConfigOption protected String text;
    @ExtConfigOption protected String icon;
    @ExtConfigOption protected String id;
    @ExtConfigOption protected Boolean checked;
    @ExtConfigOption protected Boolean allowChildren;
    @ExtConfigOption protected Boolean disabled;
    @ExtConfigOption protected Boolean expandable;
    @ExtConfigOption protected Boolean expanded;
    @ExtConfigOption protected String href;
    @ExtConfigOption protected String hrefTarget;
    @ExtConfigOption protected Boolean leaf;
    @ExtConfigOption protected String qtip;
    @ExtConfigOption protected Boolean singleClickExpand;
    @ExtConfigOption protected String cls;
    @ExtConfigOption protected String iconCls;
    
    protected Boolean cascade;
    protected Object userData;
    
    //节点分为两类，一类是通过TreeDataProvider,因此每次都需要更新，
    //另外一类是静态的还有通过api加进来的，不需要每次都更新
    protected boolean updateAble;
    
    /**
     * 刷新树节点的一级子节点
     */
    @Operation
    public void reloadChildren(){}
    
    /**
     * 移除树节点
     */
    @Operation
    public void remove(){
    }
    
    /**
     * 加入子节点
     */
    public void add(UITreeNode node){
        UITreeNode proxyNode = TreeRenderHelper.getProxyNode(node);
        this.getChildren().add(proxyNode);
    }
    
    /**
     * 设置树节点的勾中状态,并发送脚本到客户端，改变客户端的Ext组件
     */
    @Operation
    public void setChecked(Boolean checked) {
        setCheckedWithoutScript(checked);
    }
    
    /**
     * 设置树节点的勾中状态,不发送脚本到客户端
     */
    public void setCheckedWithoutScript(Boolean checked) {
        this.checked = checked;
    }
    public Boolean getChecked() {
        if (this.checked != null) {
            return this.checked;
        }
        javax.el.ValueExpression ve = this.getValueExpression("checked");
        if (ve != null) {
            try {
                return (Boolean) ve.getValue(this.getFacesContext().getELContext());
            } catch (javax.el.ELException e) {
                throw new javax.faces.FacesException(e);
            }
        }
        return null;
    }
    
    /**
     * 设置树节点的图标，并发送脚本到客户端，改变客户端的Ext组件
     */
    @Operation
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public java.lang.String getIcon() {
        if (this.icon != null) {
            return this.icon;
        }
        javax.el.ValueExpression ve = this.getValueExpression("icon");
        if (ve != null) {
            try {
                return (java.lang.String) ve.getValue(this.getFacesContext().getELContext());
            } catch (javax.el.ELException e) {
                throw new javax.faces.FacesException(e);
            }
        }
        return null;
    }
    
    /**
     * 设置树节点的文本，并发送脚本到客户端，改变客户端的Ext组件
     */
    @Operation
    public void setText(java.lang.String value) {
        this.text = value;
    }
    
    public java.lang.String getText() {
        if (this.text != null) {
            return this.text;
        }
        javax.el.ValueExpression ve = this.getValueExpression("text");
        if (ve != null) {
            try {
                return (java.lang.String) ve.getValue(this.getFacesContext().getELContext());
            } catch (javax.el.ELException e) {
                throw new javax.faces.FacesException(e);
            }
        }
        return null;
    }
}
