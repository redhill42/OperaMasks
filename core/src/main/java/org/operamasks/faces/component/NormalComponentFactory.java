/*
 * $Id 
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
package org.operamasks.faces.component;

import static org.operamasks.resources.Resources.JSF_CREATE_COMPONENT_ERROR;
import static org.operamasks.resources.Resources._T;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.util.Utils;

public class NormalComponentFactory implements ComponentFactory {
    protected ComponentConfig config;
    protected ClassLoader loader;
    protected Class<? extends UIComponent> componentClass;


    public NormalComponentFactory(ComponentConfig config) {
        this.config = config;
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        this.loader = assoc.getClassLoader();
    }

    public ComponentConfig getConfig() {
        return this.config;
    }

    public UIComponent createComponent() {
        loadClassIfNeeded();
        UIComponent component = null;
        try {
            component = createComponentInstance();
            return component;
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, componentClass.getName()), ex);
        }           
    }

    protected UIComponent createComponentInstance() throws Exception {
        return (UIComponent)componentClass.newInstance();
    }

    public void destroyComponent(UIComponent component) {
        
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    public Class<? extends UIComponent> getComponentClass() {
        return this.componentClass;
    }
    
    private void loadClassIfNeeded() {
        if (this.componentClass == null) {
            try {
                this.componentClass = Utils.findClass(this.config.getComponentClass(), this.loader);
            } catch (ClassNotFoundException ex) {
                throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, this.config.getComponentType()), ex);
            }
        }
    }

}
