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
import javax.faces.component.UIViewRoot;
import org.operamasks.faces.component.interceptor.ComponentInterceptor;
import org.operamasks.faces.component.interceptor.RenderHandlerInvokor;

import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.debug.DebugInterceptor;
import org.operamasks.faces.interceptor.CompositeInterceptor;
import org.operamasks.cglib.proxy.Enhancer;
import org.operamasks.util.Utils;

public class MetaComponentFactory extends NormalComponentFactory
{

    private RenderHandlerInvokor renderInvoker;

    public MetaComponentFactory(ComponentConfig config) {
        super(config);
        if (config.getHandlerClass() != null) {
            this.renderInvoker = new RenderHandlerInvokor(config.getHandlerClass());
        }
    }
    
    @Override
    protected UIComponent createComponentInstance() throws Exception {
        UIComponent component = null;
        
        CompositeInterceptor compositeInterceptor = new CompositeInterceptor();
        if (Enhancer.isEnhanced(componentClass)) {
            reloadComponentClass();
        }

        compositeInterceptor.addInterceptor(new ComponentInterceptor(componentClass, renderInvoker));

        if (Debug.isDebugComponent(componentClass) && !UIViewRoot.class.equals(componentClass)) {
            compositeInterceptor.addInterceptor(new DebugInterceptor());
        }
        
        try {
            Enhancer en = new Enhancer();
            en.setSuperclass(componentClass);
            en.setCallback(compositeInterceptor);
            component = (UIComponent) en.create();
            this.componentClass = component.getClass();
        } catch (Throwable e) {}
        
        if (component == null)
            component = super.createComponentInstance();
        
        SensitivePropertyChecker.makeClientPropertiesSet(component);
        
        return component;
    }

    private void reloadComponentClass() {
        try {
            this.componentClass = Utils.findClass(this.config.getComponentClass(), this.loader);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, this.config.getComponentType()), ex);
        }
   }

    public RenderHandlerInvokor getRenderInvoker() {
        return renderInvoker;
    }
}
