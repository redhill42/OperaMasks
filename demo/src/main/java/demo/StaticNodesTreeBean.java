/*
 * $Id: StaticNodesTreeBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo;

import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@ManagedBean(name="StaticNodesTree", scope=ManagedBeanScope.REQUEST)
public class StaticNodesTreeBean implements TreeEventListener {
	private static final long serialVersionUID = -6277818724685197031L;
	private String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void processEvent(TreeEvent event) throws AbortProcessingException {
		response = (event.getAffectedNode()).getUserData() + ", " + event.getEventType();
	}
}