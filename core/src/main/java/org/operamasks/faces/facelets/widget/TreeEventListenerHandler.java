/*
 * $Id: TreeEventListenerHandler.java,v 1.5 2008/01/16 02:45:40 yangdong Exp $
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
package org.operamasks.faces.facelets.widget;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.event.TreeEventListener;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class TreeEventListenerHandler extends TagHandler
{
    private final TagAttribute binding;

    public TreeEventListenerHandler(TagConfig config) {
        super(config);
        this.binding = this.getAttribute("binding");
    }

    public void apply(FaceletContext ctx, UIComponent parent) {
        if (!(parent instanceof UITree)) {
            throw new TagException(this.tag, "The treeEventListener must be nested within tree tag.");
        }

        if (ComponentSupport.isNew(parent) && binding != null) {
            UITree tree = (UITree)parent;
            tree.addEventListener(this.binding.getValueExpression(ctx, TreeEventListener.class),
            			getRegisteredEventTypes(ctx));
        }
    }
    
    private Set<TreeEventType> getRegisteredEventTypes(FaceletContext ctx) {
    	Set<TreeEventType> eventTypes = new HashSet<TreeEventType>();
    	
    	addIfRegister(eventTypes, ctx, this.getAttribute("click"), UITreeNode.CLICK);
    	addIfRegister(eventTypes, ctx, this.getAttribute("dblClick"), UITreeNode.DOUBLE_CLICK);
    	addIfRegister(eventTypes, ctx, this.getAttribute("expand"), UITreeNode.EXPAND);
    	addIfRegister(eventTypes, ctx, this.getAttribute("collapse"), UITreeNode.COLLAPSE);
    	addIfRegister(eventTypes, ctx, this.getAttribute("select"), UITreeNode.SELECT);
    	
    	TagAttribute events = this.getAttribute("events");
    	if (events != null) {
    		String strEvents = events.getValue();
    		registerFromEvents(eventTypes, strEvents);
    	}
    	
    	return eventTypes;
    }
    
    private void registerFromEvents(Set<TreeEventType> eventTypes, String events) {
    	if (events == null)
    		return;
    	
		StringTokenizer tokenizer = new StringTokenizer(events, ",");
		while (tokenizer.hasMoreElements()) {
			String typeString = tokenizer.nextToken();
			
			TreeEventType eventType = TreeRenderUtils.getEventType(typeString);
			if (eventType != null)
				eventTypes.add(eventType);
		}
	}
    
    private void addIfRegister(Set<TreeEventType> eventTypes, FaceletContext ctx,
    		TagAttribute attr, TreeEventType eventType) {
    	
        if (attr == null) {
            return;
        }

        Boolean b = (Boolean)attr.getObject(ctx, Boolean.class);
        if ((b != null) && Boolean.TRUE.equals(b)) {
            eventTypes.add(eventType);
        }
	}
}
