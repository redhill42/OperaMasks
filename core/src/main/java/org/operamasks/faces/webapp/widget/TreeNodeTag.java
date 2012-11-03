/*
 * $Id: TreeNodeTag.java,v 1.8 2007/12/11 04:20:12 jacky Exp $
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

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name = "treeNode" body-content = "JSP"
 */
public class TreeNodeTag extends HtmlBasicELTag {
	public static final String RENDERER_TYPE = "org.operamasks.faces.widget.TreeNode";
	
    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getComponentType()
     */
    @Override
    public String getComponentType() {
        return UITreeNode.COMPONENT_TYPE;
    }

    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * @jsp.attribute name="userData" required = "true" type = "java.lang.String"
     */
    public void setUserData(ValueExpression userData) {
        setValueExpression("userData", userData);
    }
    
    /**
     * @jsp.attribute name="allowsChildren" required = "false" type = "java.lang.Boolean"
     */
    public void setAllowsChildren(ValueExpression allowsChildren) {
        setValueExpression("allowsChildren", allowsChildren);
    }
    
    /**
     * @jsp.attribute name="text" required = "false" type = "java.lang.String"
     */
    public void setText(ValueExpression text) {
        setValueExpression("text", text);
    }
    
    /**
     * @jsp.attribute name="image" required = "false" type = "java.lang.String"
     */
    public void setImage(ValueExpression image) {
        setValueExpression("image", image);
    }
    
    /**
     * @jsp.attribute name="expand" required = "false" type = "java.lang.Boolean"
     */
    public void setExpand(ValueExpression expand) {
        setValueExpression("expand", expand);
    }
    
    /**
     * @jsp.attribute name="async" required = "false" type = "java.lang.Boolean"
     */
    public void setAsync(ValueExpression async) {
        setValueExpression("async", async);
    }
    
    @Override
    public int doEndTag() throws JspException {
    	UITreeNode node = (UITreeNode)getComponentInstance();
    	if (node.getAsync()) {
    		setNodeAllowsChildren(node);
    	}
    	
    	if (node.getParent() instanceof UITreeNode) {
    		setNodeAllowsChildren((UITreeNode)node.getParent());
    	}
    	
    	return super.doEndTag();
    }
    
    @Override
    protected void setProperties(UIComponent component) {
        try {
            super.setProperties(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void setNodeAllowsChildren(UITreeNode node) {
		node.setAllowsChildren(true);
		node.setLeaf(false);
	}
}
