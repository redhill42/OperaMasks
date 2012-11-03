/*
 * $Id: DefaultELResolverRegistry.java,v 1.9 2008/01/01 16:42:59 daniel Exp $
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

import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.el.ELResolver;
import javax.el.CompositeELResolver;
import javax.el.ResourceBundleELResolver;

import javax.faces.el.*;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.operamasks.faces.application.ELResolverRegistry;
import org.operamasks.faces.el.ImplicitObjectELResolverForFaces;
import org.operamasks.faces.el.VariableResolverChainWrapper;
import org.operamasks.faces.el.PropertyResolverChainWrapper;
import org.operamasks.faces.el.ManagedBeanELResolver;
import org.operamasks.faces.el.FacesResourceBundleELResolver;
import org.operamasks.faces.el.ScopedAttributeELResolver;
import org.operamasks.faces.el.FacesCompositeELResolver;
import org.operamasks.faces.el.ImplicitObjectELResolverForJsp;
import org.operamasks.faces.el.VariableResolverAdapter;
import org.operamasks.faces.el.PropertyResolverAdapter;
import org.operamasks.faces.el.ManagedBeanPropertyELResolver;
import org.operamasks.el.eval.ELEngine;

@SuppressWarnings("deprecation")
public class DefaultELResolverRegistry extends ELResolverRegistry
{
    // ELResolvers from configuration and application
    private List<ELResolver> elResolversFromConfig;
    private List<ELResolver> elResolversFromApp;

    // legacy Variable/Property Resolvers from configuration and application
    private VariableResolver variableResolverFromConfig;
    private VariableResolver variableResolverFromApp;
    private PropertyResolver propertyResolverFromConfig;
    private PropertyResolver propertyResolverFromApp;

    // final composite ELResolver for Faces and JSP
    private volatile CompositeELResolver elResolverForFaces;
    private volatile CompositeELResolver elResolverForJsp;

    // legacy Variable/Property resolver adapters
    private volatile VariableResolver variableResolverAdapter;
    private volatile PropertyResolver propertyResolverAdapter;

    // The default VariableResolver that resolves nothing
    private static VariableResolver DEFAULT_VARIABLE_RESOLVER = new VariableResolver() {
        public Object resolveVariable(FacesContext context, String name) { return null; }
    };

    // The default PropertyResolver that resolves nothing
    private static PropertyResolver DEFAULT_PROPERTY_RESOLVER = new PropertyResolver() {
        public Object getValue(Object base, Object property) { return null; }
        public Object getValue(Object base, int index) { return null; }
        public void setValue(Object base, Object property, Object value) {}
        public void setValue(Object base, int index, Object value) {}
        public boolean isReadOnly(Object base, Object property) { return false; }
        public boolean isReadOnly(Object base, int index) { return false; }
        public Class getType(Object base, Object property) { return null; }
        public Class getType(Object base, int index) { return null; }
    };

    protected DefaultELResolverRegistry() {
        this.elResolversFromConfig = new CopyOnWriteArrayList<ELResolver>();
        this.elResolversFromApp = new CopyOnWriteArrayList<ELResolver>();
    }

    public List<ELResolver> getELResolversFromConfig() {
        return Collections.unmodifiableList(this.elResolversFromConfig);
    }

    public void addELResolverFromConfig(ELResolver resolver) {
        this.elResolversFromConfig.add(resolver);
        this.setDirty();
    }

    public List<ELResolver> getELResolversFromApp() {
        return Collections.unmodifiableList(this.elResolversFromApp);
    }

    public void addELResolverFromApp(ELResolver resolver) {
        this.elResolversFromApp.add(resolver);
        this.setDirty();
    }

    public VariableResolver getDefaultVariableResolver() {
        return DEFAULT_VARIABLE_RESOLVER;
    }

    public PropertyResolver getDefaultPropertyResolver() {
        return DEFAULT_PROPERTY_RESOLVER;
    }

    public VariableResolver getVariableResolverFromConfig() {
        return this.variableResolverFromConfig;
    }

    public void setVariableResolverFromConfig(VariableResolver resolver) {
        this.variableResolverFromConfig = resolver;
        this.setDirty();
    }

    public VariableResolver getVariableResolverFromApp() {
        return this.variableResolverFromApp;
    }

    public void setVariableResolverFromApp(VariableResolver resolver) {
        this.variableResolverFromApp = resolver;
        this.setDirty();
    }

    public PropertyResolver getPropertyResolverFromConfig() {
        return this.propertyResolverFromConfig;
    }

    public void setPropertyResolverFromConfig(PropertyResolver resolver) {
        this.propertyResolverFromConfig = resolver;
        this.setDirty();
    }

    public PropertyResolver getPropertyResolverFromApp() {
        return this.propertyResolverFromApp;
    }

    public void setPropertyResolverFromApp(PropertyResolver resolver) {
        this.propertyResolverFromApp = resolver;
        this.setDirty();
    }

    public VariableResolver getVariableResolverAdapter() {
        if (this.variableResolverAdapter == null) {
            this.variableResolverAdapter = new VariableResolverAdapter(this.getELResolverForFaces());
        }
        return this.variableResolverAdapter;
    }

    public PropertyResolver getPropertyResolverAdapter() {
        if (this.propertyResolverAdapter == null) {
            this.propertyResolverAdapter = new PropertyResolverAdapter(this.getELResolverForFaces());
        }
        return this.propertyResolverAdapter;
    }

    public ELResolver getELResolverForFaces() {
        CompositeELResolver composite = this.elResolverForFaces;

        if (composite == null) {
            composite = new CompositeELResolver();

            composite.add(new ImplicitObjectELResolverForFaces());

            for (ELResolver r : this.elResolversFromConfig) {
                composite.add(r);
            }

            if (this.variableResolverFromApp != null)
                composite.add(new VariableResolverChainWrapper(this.variableResolverFromApp));
            if (this.variableResolverFromConfig != null)
                composite.add(new VariableResolverChainWrapper(this.variableResolverFromConfig));
            if (this.propertyResolverFromApp != null)
                composite.add(new PropertyResolverChainWrapper(this.propertyResolverFromApp));
            if (this.propertyResolverFromConfig != null)
                composite.add(new PropertyResolverChainWrapper(this.propertyResolverFromConfig));

            for (ELResolver r : this.elResolversFromApp) {
                composite.add(r);
            }

            composite.add(new ManagedBeanELResolver());
            composite.add(new ResourceBundleELResolver());
            composite.add(new FacesResourceBundleELResolver());
            ELEngine.addDefaultELResolvers(composite);
            composite.add(new ManagedBeanPropertyELResolver());
            composite.add(new ScopedAttributeELResolver());

            this.elResolverForFaces = composite;
        }

        return composite;
    }

    public ELResolver getELResolverForJsp() {
        CompositeELResolver composite = this.elResolverForJsp;

        if (composite == null) {
            composite = new FacesCompositeELResolver();

            composite.add(new ImplicitObjectELResolverForJsp());
            composite.add(new ManagedBeanELResolver());
            composite.add(new FacesResourceBundleELResolver());
            ELEngine.addDefaultELResolvers(composite);
            composite.add(new ManagedBeanPropertyELResolver());
            
            for (ELResolver r : this.elResolversFromConfig) {
                composite.add(r);
            }

            if (this.variableResolverFromApp != null)
                composite.add(new VariableResolverChainWrapper(this.variableResolverFromApp));
            if (this.variableResolverFromConfig != null)
                composite.add(new VariableResolverChainWrapper(this.variableResolverFromConfig));
            if (this.propertyResolverFromApp != null)
                composite.add(new PropertyResolverChainWrapper(this.propertyResolverFromApp));
            if (this.propertyResolverFromConfig != null)
                composite.add(new PropertyResolverChainWrapper(this.propertyResolverFromConfig));

            for (ELResolver r : this.elResolversFromApp) {
                composite.add(r);
            }

            this.elResolverForJsp = composite;
        }

        return composite;
    }

    public void registerELResolverWithJsp(ServletContext context) {
        JspApplicationContext jspContext = JspFactory.getDefaultFactory().getJspApplicationContext(context);
        jspContext.addELResolver(this.getELResolverForJsp());
        jspContext.addELContextListener(new ELContextListenerImpl());
    }

    private void setDirty() {
        this.elResolverForFaces = null;
        this.elResolverForJsp = null;
    }
}
