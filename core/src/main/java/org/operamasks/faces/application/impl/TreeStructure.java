/*
 * $Id: TreeStructure.java,v 1.4 2008/02/18 14:02:02 jacky Exp $
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

package org.operamasks.faces.application.impl;

import static org.operamasks.resources.Resources.JSF_CREATE_COMPONENT_ERROR;
import static org.operamasks.resources.Resources._T;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.ComponentContainer;
import org.operamasks.faces.component.ComponentFactory;
import org.operamasks.util.Utils;

public class TreeStructure implements java.io.Serializable
{
    private String id;
    private String className;
    private ArrayList<TreeStructure> children;
    private HashMap<String,TreeStructure> facets;

    private static final long serialVersionUID = -8679279788535268484L;

    public TreeStructure() {}

    public TreeStructure(UIComponent component) {
        id = component.getId();
        className = component.getClass().getName();
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Collection<TreeStructure> getChildren() {
        if (children == null)
            return Collections.emptyList();
        return children;
    }

    public Map<String,TreeStructure> getFacets() {
        if (facets == null)
            return Collections.emptyMap();
        return facets;
    }

    public void addChild(TreeStructure treeStruct) {
        if (children == null)
            children = new ArrayList<TreeStructure>();
        children.add(treeStruct);
    }

    public void addFacet(String facetName, TreeStructure treeStruct) {
        if (facets == null)
            facets = new HashMap<String,TreeStructure>();
        facets.put(facetName, treeStruct);
    }

    @SuppressWarnings("unchecked")
	public UIComponent createComponent() {
        try {
        	Class<? extends UIComponent> c = Utils.findClass(className);
        	ComponentContainer container = ComponentContainer.getInstance();
        	ComponentFactory fac = container.getComponentFactoryByName(c.getName());
        	if (fac == null) {
        	    throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, c.getName()));
        	}
            return fac.createComponent();
        } catch (Throwable ex) {
            throw new FacesException(ex);
        }
    }
}
