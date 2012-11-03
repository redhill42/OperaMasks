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
package org.operamasks.faces.render.ext;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.Container;
import org.operamasks.faces.annotation.component.ContainerItem;
import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public abstract class AbstractRenderHandler
{
    protected YuiExtResource getResourceInstance(ResourceManager rm) {
        return YuiExtResource.register(rm, getDependPackages());
    }
    
    protected String[] getDependPackages(){
        DependPackages meta = this.getClass().getAnnotation(DependPackages.class);
        return meta != null ? meta.value() : new String[0];
    }
    
    protected String getExtClass(UIComponent component){
        ExtClass meta = this.getClass().getAnnotation(ExtClass.class);
        return meta != null ? meta.value() : null;
    }
    
    protected boolean isContainer(UIComponent component) {
        return component.getClass().getAnnotation(Container.class) != null;
    }

    protected boolean isContainerItem(UIComponent component) {
        return component.getClass().getAnnotation(ContainerItem.class) != null;
    }
    
    @OperationListener
    protected void invokeAjaxOperation(UIComponent component, String opName, Map<String, Object> attrs) {
        FacesContext context = FacesContext.getCurrentInstance(); 
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        if (attrs != null) {
            cm.getAttributes().putAll(attrs);
        }
        cm.invoke(context, opName, component);
    }
    
    protected void addOperationScript(String script){
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager.getInstance(context).addOperationScript(script);
    }
}
