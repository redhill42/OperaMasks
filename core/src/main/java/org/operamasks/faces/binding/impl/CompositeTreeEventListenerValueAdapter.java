/*
 * $Id: CompositeTreeEventListenerValueAdapter.java,v 1.2 2008/02/23 06:38:46 yangdong Exp $
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

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@SuppressWarnings("serial")
public class CompositeTreeEventListenerValueAdapter extends AbstractValueAdapter
				implements TreeEventListener, Serializable {
	private TreeEventListener listener;

	public void processEvent(TreeEvent event) throws AbortProcessingException {
		if (listener != null) {
			listener.processEvent(event);
		}
	}
	
	public void setTreeEventListener(TreeEventListener listener) {
		this.listener = listener;
	}

	@Override
	public Class<?> getExpectedType() {
		return TreeEventListener.class;
	}

	@Override
	public Class<?> getType(ELContext arg0) {
		return TreeEventListener.class;
	}

	@Override
	public Object getValue(ELContext arg0) {
		return this;
	}

	@Override
	public boolean isReadOnly(ELContext arg0) {
		return true;
	}

	@Override
	public void setValue(ELContext arg0, Object arg1) {}
}

@SuppressWarnings("serial")
class MethodTreeEventListener implements TreeEventListener {
	private ModelBean bean;
	private Method method;
	
	MethodTreeEventListener(ModelBean bean, Method method) {
		this.bean = bean;
		this.method = method;
	}

	public void processEvent(TreeEvent event) throws AbortProcessingException {
       	try {
			this.bean.invoke(this.method, event);
		} catch (Exception e) {
			throw new FacesException(e);
		}
	}
}
