/*
 * $Id: TreeEventListenerTag.java,v 1.9 2008/01/23 05:33:07 yangdong Exp $
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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.faces.webapp.UIComponentELTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.TreeEventListenerRegister;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;

/**
 * @jsp.tag name = "treeEventListener" body-content = "empty"
 */
public class TreeEventListenerTag extends TagSupport {

    private static final long serialVersionUID = 5615490464766204109L;

    private ValueExpression binding;
    private ValueExpression click;
    private ValueExpression dblClick;
    private ValueExpression expand;
    private ValueExpression collapse;
    private ValueExpression select;
    private ValueExpression events;

    /**
     * @jsp.attribute name="binding" required = "true" type = "org.operamasks.faces.event.TreeEventListener"
     */
    public void setBinding(ValueExpression binding) {
        this.binding = binding;
    }
    
    /**
     * @jsp.attribute name="click" required = "false" type = "java.lang.Boolean"
     */
    public void setClick(ValueExpression click) {
        this.click = click;
    }
    
    /**
     * @jsp.attribute name="dblClick" required = "false" type = "java.lang.Boolean"
     */
    public void setDblClick(ValueExpression dblClick) {
        this.dblClick = dblClick;
    }
    
    /**
     * @jsp.attribute name="expand" required = "false" type = "java.lang.Boolean"
     */
    public void setExpand(ValueExpression expand) {
        this.expand = expand;
    }
    
    /**
     * @jsp.attribute name="collapse" required = "false" type = "java.lang.Boolean"
     */
    public void setCollapse(ValueExpression collapse) {
        this.collapse = collapse;
    }
    
    /**
     * @jsp.attribute name="select" required = "false" type = "java.lang.Boolean"
     */
    public void setSelect(ValueExpression select) {
        this.select = select;
    }
    
    /**
     * @jsp.attribute name="events" required = "false" type = "java.lang.String"
     */
    public void setEvents(ValueExpression events) {
        this.events = events;
    }

    public int doStartTag() throws JspException {
        UIComponentClassicTagBase tag = UIComponentELTag
                .getParentUIComponentClassicTagBase(pageContext);
        if (tag == null || !(tag instanceof TreeTag))
            throw new JspException("treeEventListenerTag must be nested within treeTag.");
        
        if (!tag.getCreated())
            return SKIP_BODY;

        UITree tree = (UITree)tag.getComponentInstance();

        if (binding != null) {
        	TreeEventListenerRegister register = new TreeEventListenerRegister(tree);
        	register.setClick(click);
        	register.setClick(dblClick);
        	register.setClick(expand);
        	register.setClick(collapse);
        	register.setClick(select);
        	register.registerListener(binding);
        } else {
            throw new FacesException("Can't resolve treeEventListener binding: " +
                    binding.getExpressionString());
        }
        
        return SKIP_BODY;
    }
    


	public void release() {
        super.release();
        binding = null;
    }
}
