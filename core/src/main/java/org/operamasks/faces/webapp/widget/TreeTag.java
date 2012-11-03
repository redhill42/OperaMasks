/*
 * $Id: TreeTag.java,v 1.12 2007/12/21 09:37:40 yangdong Exp $
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
package org.operamasks.faces.webapp.widget;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name = "tree" body-content = "JSP"
 */
public class TreeTag extends HtmlBasicELTag {
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Tree";
    
    private MethodExpression asyncData;
    private MethodExpression nodeText;
    private MethodExpression nodeImage;
    private MethodExpression nodeUserData;
    private MethodExpression nodeHasChildren;
    private MethodExpression nodeClass;
    private MethodExpression initAction;
    private MethodExpression postCreate;
    
    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getComponentType()
     */
    @Override
    public String getComponentType() {
        return UITree.COMPONENT_TYPE;
    }

    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return RENDERER_TYPE;
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }
    
    /**
     * @jsp.attribute type="java.lang.Boolean"
     */
    public void setEscapeNodeText(ValueExpression escapeNodeText) {
        setValueExpression("escapeNodeText", escapeNodeText);
    }
    
    /**
     * @jsp.attribute name="lines" required = "false" type = "java.lang.Boolean"
     */
    public void setLines(ValueExpression lines) {
        setValueExpression("lines", lines);
    }
    
    /**
     * @jsp.attribute name="containerScroll" required = "false" type = "java.lang.Boolean"
     */
    public void setContainerScroll(ValueExpression containerScroll) {
        setValueExpression("containerScroll", containerScroll);
    }
    
    /**
     * @jsp.attribute name="enableDrop" required = "false" type = "java.lang.Boolean"
     */
    public void setEnableDrop(ValueExpression enableDrop) {
        setValueExpression("enableDrop", enableDrop);
    }
    
    /**
     * @jsp.attribute name="enableDrag" required = "false" type = "java.lang.Boolean"
     */
    public void setEnableDrag(ValueExpression enableDrag) {
        setValueExpression("enableDrag", enableDrag);
    }
    
    /**
     * @jsp.attribute name="animate" required = "false" type = "java.lang.Boolean"
     */
    public void setAnimate(ValueExpression animate) {
        setValueExpression("animate", animate);
    }

    /**
     * @jsp.attribute name="style" required = "false" type = "java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }
    
    /**
     * @jsp.attribute name="styleClass" required = "false" type = "java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }    
    
    /**
     * @jsp.attribute method-signature="java.util.List loadAsyncData(java.lang.Object)"
     */
	public void setAsyncData(MethodExpression asyncData) {
		this.asyncData = asyncData;
	}

    /**
     * @jsp.attribute method-signature="java.lang.String getImage(java.lang.Object)"
     */
	public void setNodeImage(MethodExpression nodeImage) {
		this.nodeImage = nodeImage;
	}
	
    /**
     * @jsp.attribute method-signature="java.lang.Boolean hasChildren(java.lang.Object)"
     */
	public void setNodeHasChildren(MethodExpression nodeHasChildren) {
		this.nodeHasChildren = nodeHasChildren;
	}

    /**
     * @jsp.attribute method-signature="java.lang.String getText(java.lang.Object)"
     */
	public void setNodeText(MethodExpression nodeText) {
		this.nodeText = nodeText;
	}

    /**
     * @jsp.attribute method-signature="java.lang.Object getUserData(java.lang.Object)"
     */
	public void setNodeUserData(MethodExpression nodeUserData) {
		this.nodeUserData = nodeUserData;
	}
	
    /**
     * @jsp.attribute method-signature="java.lang.Class getNodeClass(java.lang.Object)"
     */
	public void setNodeClass(MethodExpression nodeClass) {
		this.nodeClass = nodeClass;
	}
	
    /* (non-Javadoc)
     * @see org.operamasks.faces.webapp.html.HtmlBasicELTag#setProperties(javax.faces.component.UIComponent)
     */
    @Override
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        
        UITree tree = (UITree)component;
        if (asyncData != null)
        	tree.setAsyncData(asyncData);
        
        if (nodeText != null)
        	tree.setNodeText(nodeText);
        
        if (nodeImage != null)
        	tree.setNodeImage(nodeImage);
        
        if (nodeUserData != null)
        	tree.setNodeUserData(nodeUserData);
        
        if (nodeHasChildren != null)
        	tree.setNodeHasChildren(nodeHasChildren);
        
        if (nodeClass != null)
        	tree.setNodeClass(nodeClass);
        	
        if (initAction != null)
        	tree.setInitAction(initAction);
        
        if (postCreate != null)
        	tree.setPostCreate(postCreate);
    }
    
    /**
     * @jsp.attribute name="saveState" required = "false" type = "java.lang.Boolean"
     */
    public void setSaveState(ValueExpression saveState) {
        setValueExpression("saveState", saveState);
    }
    
    /**
     * @jsp.attribute method-signature="void postCreate(org.operamasks.faces.component.widget.UITreeNode, java.lang.Object)"
     */
    public void setPostCreate(MethodExpression postCreate) {
        this.postCreate = postCreate;
    }
    
    /**
     * @jsp.attribute method-signature="void initAction(javax.faces.context.FacesContext, org.operamasks.faces.component.widget.UITree)"
     */
    public void setInitAction(MethodExpression initAction) {
        this.initAction = initAction;
    }
    
    /**
     * @jsp.attribute name="rootVisible" required = "false" type = "java.lang.Boolean"
     */
    public void setRootVisible(ValueExpression rootVisible) {
        setValueExpression("rootVisible", rootVisible);
    }
}
