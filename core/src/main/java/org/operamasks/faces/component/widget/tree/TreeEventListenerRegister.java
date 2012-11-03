package org.operamasks.faces.component.widget.tree;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;

public class TreeEventListenerRegister {
	private UITree tree;
    private ValueExpression click;
    private ValueExpression dblClick;
    private ValueExpression expand;
    private ValueExpression collapse;
    private ValueExpression select;
    private ValueExpression events;
    
	public TreeEventListenerRegister(UITree tree) {
		this.tree = tree;
	}
	
	public UITree getTree() {
		return tree;
	}

	public void setTree(UITree tree) {
		this.tree = tree;
	}

	public ValueExpression getClick() {
		return click;
	}

	public void setClick(ValueExpression click) {
		this.click = click;
	}

	public ValueExpression getDblClick() {
		return dblClick;
	}

	public void setDblClick(ValueExpression dblClick) {
		this.dblClick = dblClick;
	}

	public ValueExpression getExpand() {
		return expand;
	}

	public void setExpand(ValueExpression expand) {
		this.expand = expand;
	}

	public ValueExpression getCollapse() {
		return collapse;
	}

	public void setCollapse(ValueExpression collapse) {
		this.collapse = collapse;
	}

	public ValueExpression getSelect() {
		return select;
	}

	public void setSelect(ValueExpression select) {
		this.select = select;
	}

	public ValueExpression getEvents() {
		return events;
	}

	public void setEvents(ValueExpression events) {
		this.events = events;
	}
	
	public void registerListener(ValueExpression listener) {
		tree.addEventListener(listener, getRegisteredEventTypes());
	}
	
    private Set<TreeEventType> getRegisteredEventTypes() {
    	Set<TreeEventType> eventTypes = new HashSet<TreeEventType>();
    	
    	addIfRegister(eventTypes, click, UITreeNode.CLICK);
    	addIfRegister(eventTypes, dblClick, UITreeNode.DOUBLE_CLICK);
    	addIfRegister(eventTypes, expand, UITreeNode.EXPAND);
    	addIfRegister(eventTypes, collapse, UITreeNode.COLLAPSE);
    	addIfRegister(eventTypes, select, UITreeNode.SELECT);
    	
    	registerFromEvents(eventTypes, events);
    	
    	return eventTypes;
    }

    private void registerFromEvents(Set<TreeEventType> eventTypes, ValueExpression eventsVE) {
		if (eventsVE == null)
			return;
		
		String events = (String)eventsVE.getValue(FacesContext.
				getCurrentInstance().getELContext());
		
		if (events == null || events.equals(""))
			return;
		
		StringTokenizer tokenizer = new StringTokenizer(events, ",");
		while (tokenizer.hasMoreElements()) {
			String typeString = tokenizer.nextToken();
			
			TreeEventType eventType = getEventType(typeString);
			if (eventType != null)
				eventTypes.add(eventType);
		}
	}

	private TreeEventType getEventType(String eventString) {
		return TreeRenderUtils.getEventType(eventString);
	}

	private void addIfRegister(Set<TreeEventType> eventTypes,
    		ValueExpression eventVE, TreeEventType eventType) {
		if (eventVE == null)
			return;
		
		if (Boolean.TRUE.equals(eventVE.getValue(FacesContext.getCurrentInstance(
    				).getELContext()))) {
			eventTypes.add(eventType);
    	}
	}
}
