/*
 * $Id: ApplicationImpl.java,v 1.15 2008/02/18 14:02:02 jacky Exp $
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

import static org.operamasks.resources.Resources.JSF_CLASS_NOT_FOUND;
import static org.operamasks.resources.Resources.JSF_CREATE_COMPONENT_ERROR;
import static org.operamasks.resources.Resources.JSF_ILLEGAL_ADD_ELRESOLVER;
import static org.operamasks.resources.Resources.JSF_ILLEGAL_SETTING_VIEWHANDLER;
import static org.operamasks.resources.Resources.JSF_NO_SUCH_COMPONENT_TYPE;
import static org.operamasks.resources.Resources._T;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import org.operamasks.el.eval.ELEngine;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ConverterRegistry;
import org.operamasks.faces.application.ELResolverRegistry;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.ValidatorRegistry;
import org.operamasks.faces.component.ComponentConfig;
import org.operamasks.faces.component.ComponentContainer;
import org.operamasks.faces.component.ComponentFactory;
import org.operamasks.faces.component.NormalComponentFactory;
import org.operamasks.faces.el.MethodBindingMethodExpressionAdapter;
import org.operamasks.faces.el.ValueBindingValueExpressionAdapter;
import org.operamasks.faces.interceptor.ProxyFactory;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.util.Utils;

@SuppressWarnings("deprecation")
public class ApplicationImpl extends Application
{
    private ApplicationAssociate associate;

    private Locale defaultLocale;
    private Collection<Locale> supportedLocales;
    private String defaultRenderKitId;
    private String messageBundle;

    private ActionListener actionListener;
    private NavigationHandler navigationHandler;
    private ViewHandler viewHandler;
    private StateManager stateManager;

    private ConverterRegistry converterRegistry;
    private ValidatorRegistry validatorRegistry;

    private ELResolverRegistry elResolverRegistry;
    private List<ELContextListener> elContextListeners = new CopyOnWriteArrayList<ELContextListener>();

    protected ApplicationImpl(ApplicationAssociate associate) {
        this.associate = associate;
        this.converterRegistry = DefaultConverterRegistry.getInstance();
        this.validatorRegistry = DefaultValidatorRegistry.getInstance();
        this.elResolverRegistry = ELResolverRegistry.getInstance();
    }

    public ActionListener getActionListener() {
        return this.actionListener;
    }

    public void setActionListener(ActionListener listener) {
        assertNotNull(listener);
        this.actionListener = listener;
    }

    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setDefaultLocale(Locale locale) {
        assertNotNull(locale);
        this.defaultLocale = locale;
    }

    public Iterator<Locale> getSupportedLocales() {
        Collection<Locale> locales = this.supportedLocales;
        if (locales == null) {
            locales = Collections.emptyList();
        }
        return locales.iterator();
    }

    public void setSupportedLocales(Collection<Locale> locales) {
        assertNotNull(locales);
        this.supportedLocales = new ArrayList<Locale>(locales);
    }

    public String getDefaultRenderKitId() {
        return this.defaultRenderKitId;
    }

    public void setDefaultRenderKitId(String renderKitId) {
        assertNotNull(renderKitId);
        this.defaultRenderKitId = renderKitId;
    }

    public String getMessageBundle() {
        return this.messageBundle;
    }

    public void setMessageBundle(String bundle) {
        assertNotNull(bundle);
        this.messageBundle = bundle;
    }

    public NavigationHandler getNavigationHandler() {
        return this.navigationHandler;
    }

    public void setNavigationHandler(NavigationHandler handler) {
        assertNotNull(handler);
        this.navigationHandler = handler;
    }

    public PropertyResolver getPropertyResolver() {
        return this.elResolverRegistry.getPropertyResolverAdapter();
    }

    public void setPropertyResolver(PropertyResolver resolver) {
        assertNotNull(resolver);
        assertNotRequestMade();
        this.elResolverRegistry.setPropertyResolverFromApp(resolver);
    }

    public VariableResolver getVariableResolver() {
        return this.elResolverRegistry.getVariableResolverAdapter();
    }

    public void setVariableResolver(VariableResolver resolver) {
        assertNotNull(resolver);
        assertNotRequestMade();
        this.elResolverRegistry.setVariableResolverFromApp(resolver);
    }

    public ResourceBundle getResourceBundle(FacesContext context, String var) {
        assertNotNull(context);
        assertNotNull(var);

        Locale         locale   = FacesUtils.getCurrentLocale();
        ClassLoader    loader   = associate.getClassLoader();
        String         basename = associate.getResourceBundleBaseName(var);
        ResourceBundle bundle   = null;

        if (basename != null) {
            try {
                bundle = ResourceBundle.getBundle(basename, locale, loader);
            } catch (java.util.MissingResourceException ex) {
                // fall back to en/US
                bundle = ResourceBundle.getBundle(basename, Locale.ENGLISH, loader);
            }
        }

        if (bundle == null) {
            return null;
        } else {
            return new ResourceBundleWrapper(bundle);
        }
    }

    public ViewHandler getViewHandler() {
        return this.viewHandler;
    }

    public void setViewHandler(ViewHandler handler) {
        assertNotNull(handler);
        assertNotResponseRendered();
        this.viewHandler = handler;
    }

    public StateManager getStateManager() {
        return this.stateManager;
    }

    public void setStateManager(StateManager manager) {
        assertNotNull(manager);
        assertNotRequestMade();
        this.stateManager = manager;
    }

    public void addComponent(String componentType, String componentClass) {
        assertNotNull(componentType);
        assertNotNull(componentClass);
        ComponentContainer container = ComponentContainer.getInstance();
        if (!container.containsComponentFactory(componentType)) {
            container.addComponentFactory(new NormalComponentFactory(new ComponentConfig(componentType, componentClass)));
        }
    }

    public UIComponent createComponent(String componentType)
        throws FacesException
    {
        assertNotNull(componentType);

        ComponentContainer container = ComponentContainer.getInstance();
        ComponentFactory fac = container.getComponentFactory(componentType);
        
        if (fac == null) {
            throw new FacesException(_T(JSF_NO_SUCH_COMPONENT_TYPE, componentType));
        }
        return fac.createComponent();
    }

    public UIComponent createComponent(ValueBinding binding,
                                       FacesContext context,
                                       String componentType)
        throws FacesException
    {
        assertNotNull(binding);
        assertNotNull(context);
        assertNotNull(componentType);

        try {
            Object component = binding.getValue(context);
            if (!(component instanceof UIComponent)) {
                component = createComponent(componentType);
                binding.setValue(context, component);
            } else if (component != null) {
                processChildrenComponentId(context, (UIComponent) component);
            }
            return (UIComponent)component;
        } catch (FacesException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, componentType), ex);
        }
    }


    private void processChildrenComponentId(FacesContext context, UIComponent component) {
        if (component != null) {
            List<UIComponent> children = component.getChildren();
            if (children != null) {
                Iterator kids = component.getChildren().iterator();
                while (kids.hasNext()) {
                    UIComponent kid = (UIComponent) kids.next();
                    String id = kid.getId();
                    if (id != null && id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
                        UIViewRoot viewRoot = context.getViewRoot();
                        kid.setId(viewRoot.createUniqueId());
                    }
                    processChildrenComponentId(context, kid);
                }
            }
        }
    }

    public UIComponent createComponent(ValueExpression binding,
                                       FacesContext context,
                                       String componentType)
        throws FacesException
    {
        assertNotNull(binding);
        assertNotNull(context);
        assertNotNull(componentType);

        try {
            Object component = binding.getValue(context.getELContext());
            if (!(component instanceof UIComponent)) {
                component = createComponent(componentType);
                binding.setValue(context.getELContext(), component);
            } else if (component != null) {
                processChildrenComponentId(context, (UIComponent) component);
            }
            return (UIComponent)component;
        } catch (FacesException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, componentType), ex);
        }
    }

    public Iterator<String> getComponentTypes() {
        ComponentContainer container = ComponentContainer.getInstance();
        return container.getComponentTypes().iterator();
    }

    public void addConverter(String converterId, String converterClassName) {
        try {
            Class converterClass = Utils.findClass(converterClassName, associate.getClassLoader());
            ConverterFactory factory = this.converterRegistry.createConverterFactory(converterClass);
            this.converterRegistry.addConverterFactory(converterId, factory);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CLASS_NOT_FOUND, converterClassName), ex);
        }
    }

    public void addConverter(Class targetClass, String converterClassName) {
        try {
            Class converterClass = Utils.findClass(converterClassName, associate.getClassLoader());
            ConverterFactory factory = this.converterRegistry.createConverterFactory(converterClass);
            this.converterRegistry.addConverterFactory(targetClass, factory);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CLASS_NOT_FOUND, converterClassName), ex);
        }
    }

    public Converter createConverter(String converterId) {
        return this.converterRegistry.createConverter(converterId);
    }

    public Converter createConverter(Class targetClass) {
        return this.converterRegistry.createConverter(targetClass);
    }

    public Iterator<String> getConverterIds() {
        return this.converterRegistry.getConverterIds();
    }

    public Iterator<Class> getConverterTypes() {
        return this.converterRegistry.getConverterTypes();
    }

    public void addValidator(String validatorId, String validatorClassName) {
        try {
            Class validatorClass = Utils.findClass(validatorClassName, associate.getClassLoader());
            ValidatorFactory factory = this.validatorRegistry.createValidatorFactory(validatorClass);
            this.validatorRegistry.addValidatorFactory(validatorId, factory);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CLASS_NOT_FOUND, validatorClassName), ex);
        }
    }

    public Validator createValidator(String validatorId) {
        return this.validatorRegistry.createValidator(validatorId);
    }

    public Iterator<String> getValidatorIds() {
        return this.validatorRegistry.getValidatorIds();
    }

    public MethodBinding createMethodBinding(String ref, Class params[])
        throws ReferenceSyntaxException
    {
        if (ref == null)
            throw new NullPointerException();
        if (params == null)
            params = new Class[0];

        try {
            FacesContext context = FacesContext.getCurrentInstance();
            MethodExpression expr = getExpressionFactory().createMethodExpression(
                context.getELContext(), ref, null, params);
            return new MethodBindingMethodExpressionAdapter(expr);
        } catch (ELException ex) {
            throw new ReferenceSyntaxException(ex);
        }
    }

    public ValueBinding createValueBinding(String ref)
        throws ReferenceSyntaxException 
    {
        assertNotNull(ref);

        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ValueExpression expr = getExpressionFactory().createValueExpression(
                context.getELContext(), ref, Object.class);
            return new ValueBindingValueExpressionAdapter(expr);
        } catch (ELException ex) {
            throw new ReferenceSyntaxException(ex);
        }
    }
    
    public void addELContextListener(ELContextListener listener) {
        assertNotNull(listener);
        this.elContextListeners.add(listener);
    }
    
    public void removeELContextListener(ELContextListener listener) {
        if (listener != null && this.elContextListeners != null) {
            this.elContextListeners.remove(listener);
        }
    }
    
    private static final ELContextListener[] EMPTY_EL_CONTEXT_LISTENER_ARRAY
        = new ELContextListener[0];
    
    public ELContextListener[] getELContextListeners() {
        if (this.elContextListeners == null)
            return EMPTY_EL_CONTEXT_LISTENER_ARRAY;
        return this.elContextListeners.toArray(EMPTY_EL_CONTEXT_LISTENER_ARRAY);
    }
    
    public ExpressionFactory getExpressionFactory() {
        return ELEngine.getExpressionFactory();
    }
    
    public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType) {
        return ELEngine.evaluateExpression(context.getELContext(), expression, expectedType);
    }

    public void addELResolver(ELResolver resolver) {
        assertNotNull(resolver);
        assertNotRequestMade();
        this.elResolverRegistry.addELResolverFromApp(resolver);
    }

    public ELResolver getELResolver() {
        return this.elResolverRegistry.getELResolverForFaces();
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    private void assertNotRequestMade() {
        if (this.associate.isRequestMade()) {
            throw new IllegalStateException(_T(JSF_ILLEGAL_ADD_ELRESOLVER));
        }
    }

    private void assertNotResponseRendered() {
        if (this.associate.isResponseRendered()) {
            throw new IllegalStateException(_T(JSF_ILLEGAL_SETTING_VIEWHANDLER));
        }
    }
}
