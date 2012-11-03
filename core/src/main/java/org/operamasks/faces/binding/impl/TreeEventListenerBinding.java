/*
 * $Id: TreeEventListenerBinding.java,v 1.3 2008/02/23 06:38:46 yangdong Exp $
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

package org.operamasks.faces.binding.impl;

import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.tree.TreeEventListenerRegister;
import org.operamasks.faces.event.TreeEventListener;
import org.operamasks.resources.Resources;

public class TreeEventListenerBinding extends Binding {
	private String id;
	private boolean click;
	private boolean dblClick;
	private boolean expand;
	private boolean collapse;
	private boolean select;
	private String[] events;
	private Method treeEventListenerMethod;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isClick() {
		return click;
	}

	public void setClick(boolean click) {
		this.click = click;
	}

	public boolean isDblClick() {
		return dblClick;
	}

	public void setDblClick(boolean dblClick) {
		this.dblClick = dblClick;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean isCollapse() {
		return collapse;
	}

	public void setCollapse(boolean collapse) {
		this.collapse = collapse;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String[] getEvents() {
		return events;
	}

	public void setEvents(String[] events) {
		this.events = events;
	}

	protected TreeEventListenerBinding(String viewId, String id) {
		super(viewId);
		this.id = id;
	}
	
	public Method getTreeEventListenerMethod() {
		return treeEventListenerMethod;
	}

	public void setTreeEventListenerMethod(Method treeEventListenerMethod) {
		this.treeEventListenerMethod = treeEventListenerMethod;
	}

	@Override
	public void apply(FacesContext ctx, ModelBindingContext mbc) {
		if (id.equals(""))
			throw new FacesException(Resources._T(Resources.MVB_TREE_EVENT_LISTENER_NULL_TREE_ID));
		
		if (!(mbc.getComponent() instanceof UITree))
			return;

		UITree tree = (UITree)mbc.getComponent(id);
		if (tree == null)
			return;
		
		CompositeTreeEventListenerValueAdapter adapter = null;
		for (ValueExpression listenerVE : tree.getRegisteredListeners().keySet()) {
			if (listenerVE == null) {
				tree.getRegisteredListeners().remove(null);
				break;
			}
			
			TreeEventListener listener = (TreeEventListener)listenerVE.getValue(ctx.getELContext());
			if (listener instanceof CompositeTreeEventListenerValueAdapter) {
				adapter = (CompositeTreeEventListenerValueAdapter)listener;
				break;
			}
		}
		
		boolean newAdapter = (adapter == null);
		if (newAdapter) {
			adapter = new CompositeTreeEventListenerValueAdapter();
			TreeEventListenerRegister register = new TreeEventListenerRegister(tree);
			register.setClick(new SimpleObjectValueAdapter(click));
			register.setDblClick(new SimpleObjectValueAdapter(dblClick));
			register.setExpand(new SimpleObjectValueAdapter(expand));
			register.setCollapse(new SimpleObjectValueAdapter(collapse));
			register.setSelect(new SimpleObjectValueAdapter(select));
			register.setEvents(new SimpleObjectValueAdapter(getEventsString()));
			register.registerListener(adapter);
		}
		
		if (newAdapter || !mbc.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
			adapter.setTreeEventListener(new MethodTreeEventListener(mbc.getModelBean(),
					treeEventListenerMethod));
		}
	}
	
	public String getEventsString() {
		StringBuilder buf = new StringBuilder();
		if (events != null && events.length > 0) {
			for (String event : events) {
				buf.append(event).append(",");
			}			
		}
		
		if (buf.length() > 0)
			buf.deleteCharAt(buf.length() - 1);
		
		if (buf.length() > 0)
			return buf.toString();
		
		return null;
	}
	
	@SuppressWarnings("serial")
	private class SimpleObjectValueAdapter extends AbstractValueAdapter {
		private Object obj;
		
		SimpleObjectValueAdapter(Object obj) {
			this.obj = obj;
		}
		@Override
		public Class<?> getExpectedType() {
			return obj.getClass();
		}

		@Override
		public Class<?> getType(ELContext arg0) {
			return obj.getClass();
		}

		@Override
		public Object getValue(ELContext arg0) {
			return obj;
		}

		@Override
		public boolean isReadOnly(ELContext arg0) {
			return false;
		}

		@Override
		public void setValue(ELContext arg0, Object arg1) {
			this.obj = arg1;
		}
		
	} 
}
