/*
 * $Id: AsyncTreeMethodBinding.java,v 1.3 2008/02/23 06:38:46 yangdong Exp $
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

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.resources.Resources;
import org.operamasks.util.BeanUtils;

public class AsyncTreeMethodBinding extends Binding {
	private String id;
	private String value;
	private Method method;
	private Method reader;
	private Method writer;
	
	private static MethodInfo asyncDataMethodInfo =
				new MethodInfo("asyncData", List.class, new Class[] {Object.class});
	private static MethodInfo nodeTextMethodInfo =
		new MethodInfo("nodeText", String.class, new Class[] {Object.class});
	private static MethodInfo nodeImageMethodInfo =
		new MethodInfo("nodeImage", List.class, new Class[] {Object.class});
	private static MethodInfo nodeHasChildrenMethodInfo =
		new MethodInfo("nodeHasChildren", boolean.class, new Class[] {Object.class});
	private static MethodInfo nodeClassMethodInfo =
		new MethodInfo("nodeClass", Class.class, new Class[] {Object.class});
	private static MethodInfo initActionMethodInfo =
		new MethodInfo("initAction", void.class, new Class[] {FacesContext.class, UITree.class});
	private static MethodInfo postCreateMethodInfo =
		new MethodInfo("postCreate", void.class, new Class[] {UITreeNode.class, Object.class});
	
	protected AsyncTreeMethodBinding(String viewId, String id, String value, Method method) {
		super(viewId);
		this.id = id;
		this.value = value;
		this.method = BindingUtils.getInterfaceMethod(method);
		this.reader = getReader();
		this.writer = getWriter();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	@Override
	public void apply(FacesContext ctx, ModelBindingContext mbc) {
		if (id.equals(""))
			throw new FacesException(Resources._T(Resources.MVB_ASYNC_TREE_NULL_TREE_ID, value));
		
		if (!(mbc.getComponent() instanceof UITree))
			return;

		UITree tree = (UITree)mbc.getComponent(id);
		MethodExpression previous = null;
		if (tree != null) {
			try {
				previous = (MethodExpression)reader.invoke(tree, new Object[] {});
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			
			if ((previous != null) && !(previous instanceof CompositeMethodAdapter)) {
	            return;
	        }
			
			CompositeMethodAdapter adapter;
	        if (previous == null) {
	            adapter = new CompositeMethodAdapter(getMethodInfo());
	            adapter.setPhaseId(PhaseId.RESTORE_VIEW);
	            try {
					writer.invoke(tree, new Object[] {adapter});
				} catch (Exception e) {
					throw new FacesException(e);
				}
	        } else {
	            adapter = (CompositeMethodAdapter)previous;
	        }

	        adapter.addMethodBinding(new ActionMethodAdapter(mbc.getModelBean(), method));
		}
	}

	private Method getWriter() {
		try {
			return BeanUtils.getWriteMethod(UITree.class, value);
		} catch (IntrospectionException e) {
			return null;
		}
	}
	
	private Method getReader() {
		try {
			return BeanUtils.getReadMethod(UITree.class, value);
		} catch (IntrospectionException e) {
			return null;
		}
	}

	private MethodInfo getMethodInfo() {
		if (value.equals("asyncData")) {
			return asyncDataMethodInfo;
		}
		
		if (value.equals("nodeText")) {
			return nodeTextMethodInfo;
		}
		
		if (value.equals("nodeImage")) {
			return nodeImageMethodInfo;
		}
		
		if (value.equals("nodeHasChildren")) {
			return nodeHasChildrenMethodInfo;
		}
		
		if (value.equals("nodeClass")) {
			return nodeClassMethodInfo;
		}
		
		if (value.equals("initAction")) {
			return initActionMethodInfo;
		}
		
		if (value.equals("postCreate")) {
			return postCreateMethodInfo;
		}
		
		return null;
	}
}
