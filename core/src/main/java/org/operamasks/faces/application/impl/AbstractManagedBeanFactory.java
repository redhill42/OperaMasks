/*
 * $Id: AbstractManagedBeanFactory.java,v 1.9 2008/04/08 11:22:21 patrick Exp $
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

import static org.operamasks.faces.annotation.ManagedBeanScope.APPLICATION;
import static org.operamasks.faces.annotation.ManagedBeanScope.NONE;
import static org.operamasks.faces.annotation.ManagedBeanScope.REQUEST;
import static org.operamasks.faces.annotation.ManagedBeanScope.SESSION;
import static org.operamasks.faces.util.FacesUtils.createValueExpression;
import static org.operamasks.faces.util.FacesUtils.evaluateExpressionGet;
import static org.operamasks.faces.util.FacesUtils.isValueExpression;
import static org.operamasks.resources.Resources.JSF_BEAN_PROPERTY_NOT_ARRAY_OR_LIST;
import static org.operamasks.resources.Resources.JSF_BEAN_PROPERTY_NOT_MAP;
import static org.operamasks.resources.Resources.JSF_BEAN_PROPERTY_NOT_WRITEABLE;
import static org.operamasks.resources.Resources.JSF_CREATE_MANAGED_BEAN_ERROR;
import static org.operamasks.resources.Resources.JSF_CYCLIC_MANAGEDBEAN_REFERENCE;
import static org.operamasks.resources.Resources._T;
import static org.operamasks.util.BeanUtils.getProperty;

import java.beans.IntrospectionException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.operamasks.el.eval.Coercion;
import org.operamasks.el.parser.ELNode;
import org.operamasks.el.parser.Parser;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.InjectionManager;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.ManagedBeanFactory;
import org.operamasks.faces.beans.ObjectFactory;
import org.operamasks.faces.config.ManagedBeanConfig;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.Utils;

public abstract class AbstractManagedBeanFactory implements ManagedBeanFactory
{
    protected ManagedBeanConfig mbean;
    protected InjectionManager injectionManager;
    protected ClassLoader loader;

    // use a thread local to detect cyclic bean reference
    private static ThreadLocal<Map<ManagedBeanConfig,Object>> beanRefLocal =
        new ThreadLocal<Map<ManagedBeanConfig,Object>>() {
            @Override protected Map<ManagedBeanConfig,Object> initialValue() {
                return new IdentityHashMap<ManagedBeanConfig,Object>();
            }
        };

    public AbstractManagedBeanFactory(ManagedBeanConfig mbean) {
        this.mbean = mbean;

        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        this.injectionManager = assoc.getInjectionManager();
        this.loader = assoc.getClassLoader();
    }

    public ManagedBeanConfig getConfig() {
        return mbean;
    }

    public String getBeanName() {
        return mbean.getManagedBeanName();
    }

    public String getBeanClassName() {
        return mbean.getManagedBeanClass();
    }

    public ManagedBeanScope getScope() {
        return mbean.getManagedBeanScope();
    }

    /**
     * Attempt to instantiate the managed bean and set its properties.
     */
    public Object createBean(FacesContext context)
        throws FacesException
    {
        String classname = this.mbean.getManagedBeanClass();
        Object bean;

        // detect cyclic reference
        Map<ManagedBeanConfig,Object> beanRefs = beanRefLocal.get();
        if (beanRefs.containsKey(mbean)) {
            throw new FacesException(_T(JSF_CYCLIC_MANAGEDBEAN_REFERENCE, classname));
        }

        // instantiate the bean
        try {
            bean = instantiateBean(context);
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_MANAGED_BEAN_ERROR, classname), ex);
        }

        try {
            // keep the bean to detect cyclic reference
            beanRefs.put(mbean, bean);

            // Inject dependency objects
            injectBean(bean);

            // populate bean properties
            bean = populateBean(bean);

            // invoke lifecycle call back methods.
            invokePostConstruct(bean);
        } finally {
            beanRefs.remove(mbean);
        }

        // do post processing for the bean
        return postCreateBean(bean);
    }

    /**
     * Attempt to destroy the bean.
     */
    public void destroyBean(Object bean) {
        // invoke lifecycle call back methods.
        invokePreDestroy(bean);
    }

    public boolean isInstance(Object bean) {
        return this.getBeanClassName().equals(bean.getClass().getName());
    }

    protected abstract Object instantiateBean(FacesContext context)
        throws Exception;

    protected void injectBean(Object bean) {
        this.injectionManager.inject(bean);
    }

    protected void invokePostConstruct(Object bean) {
        this.injectionManager.invokePostConstruct(bean);
    }

    protected void invokePreDestroy(Object bean) {
        this.injectionManager.invokePreDestroy(bean);
    }

    protected Object postCreateBean(Object bean) {
        if (bean instanceof ObjectFactory) {
            return ((ObjectFactory)bean).getObject();
        } else {
            return bean;
        }
    }

    /**
     * Populate managed bean properties.
     */
    protected Object populateBean(Object bean) {
        try {
            if (mbean.getListEntries() != null) {
                copyListEntries((List)bean, mbean.getListEntries());
            } else if (mbean.getMapEntries() != null) {
                copyMapEntries((Map)bean, mbean.getMapEntries());
            } else {
                setBeanProperties(bean);
            }
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CREATE_MANAGED_BEAN_ERROR, mbean.getManagedBeanClass()), ex);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private void copyListEntries(List list, ManagedBeanConfig.ListEntries listEntries)
        throws ClassNotFoundException
    {
        Class valueClass =
            (listEntries.getValueClass() != null)
                ? Utils.findClass(listEntries.getValueClass(), loader)
                : String.class;

        for (String strValue : listEntries.getValues()) {
            Object value;
            if (strValue == null) {
                value = null;
            } else if (ValueExpression.class.isAssignableFrom(valueClass)) {
                value = createValueExpression(strValue, Object.class);
            } else if (isValueExpression(strValue)) {
                value = checkAndEvaluateExpression(strValue, valueClass);
            } else {
                value = Coercion.coerce(strValue, valueClass);
            }
            list.add(value);
        }
    }

    @SuppressWarnings("unchecked")
    private void copyMapEntries(Map map, ManagedBeanConfig.MapEntries mapEntries)
        throws ClassNotFoundException
    {
        Class keyClass =
            (mapEntries.getKeyClass() != null)
                ? Utils.findClass(mapEntries.getKeyClass(), loader)
                : String.class;
        Class valueClass =
            (mapEntries.getValueClass() != null)
                ? Utils.findClass(mapEntries.getValueClass(), loader)
                : String.class;

        for (ManagedBeanConfig.MapEntry entry : mapEntries.getMapEntries()) {
            String strKey = entry.getKey();
            String strValue = entry.getValue();
            Object key, value;

            if (strKey == null) {
                key = null;
            } else if (isValueExpression(strKey)) {
                key = checkAndEvaluateExpression(strKey, keyClass);
            } else {
                key = Coercion.coerce(strKey, keyClass);
            }

            if (strValue == null) {
                value = null;
            } else if (ValueExpression.class.isAssignableFrom(valueClass)) {
                value = createValueExpression(strValue, Object.class);
            } else if (isValueExpression(strValue)) {
                value = checkAndEvaluateExpression(strValue, valueClass);
            } else {
                value = Coercion.coerce(strValue, valueClass);
            }

            map.put(key, value);
        }
    }

    private void setBeanProperties(Object bean)
        throws ClassNotFoundException
    {
        for (ManagedBeanConfig.Property property : mbean.getManagedProperties()) {
            // skip properties without name
            String name = property.getPropertyName();
            if (name == null || name.length() == 0)
                continue;

            // introspect bean property
            BeanProperty bp;
            try {
                bp = getProperty(bean.getClass(), name);
            } catch (IntrospectionException ex) {
                throw new FacesException(ex);
            }

            // create property value
            Object value;
            if (property.getListEntries() != null) {
                value = getArrayOrListValue(bean, property, bp);
            } else if (property.getMapEntries() != null) {
                value = getMapValue(bean, property, bp);
            } else {
                value = getSimpleValue(bean, property, bp);
            }

            // set property value
            if (bp != null && bp.getWriteMethod() != null) {
                try {
                    bp.getWriteMethod().invoke(bean, value);
                } catch (InvocationTargetException ex) {
                    throw new FacesException(ex.getTargetException());
                } catch (Exception ex) {
                    throw new FacesException(ex);
                }
            } else if (property.checkAndGetField(bean.getClass()) != null) {
                try {
                    property.getField().set(bean, value);
                } catch (Exception ex) {
                    throw new FacesException(ex);
                }
            } else if (bean instanceof UIComponent) {
                ((UIComponent)bean).getAttributes().put(name, value);
            } else {
                throw new FacesException(_T(JSF_BEAN_PROPERTY_NOT_WRITEABLE,
                                            bean.getClass().getName(), name));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object getArrayOrListValue(Object bean, ManagedBeanConfig.Property property, BeanProperty bp)
        throws ClassNotFoundException
    {
        Class propertyClass = null;
        boolean isArray = false;
        Object oldValues = null;

        if (property.checkAndGetField(bean.getClass()) != null) {
            propertyClass = property.getField().getType();
            isArray = propertyClass.isArray();
            if (!isArray && (propertyClass != List.class)) {
                throw new FacesException(_T(JSF_BEAN_PROPERTY_NOT_ARRAY_OR_LIST,
                                            bean.getClass().getName(),
                                            property.getPropertyName()));
            }

            // get existing values
            try {
                oldValues = property.getField().get(bean);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else if (bp != null) {
            propertyClass = bp.getType();
            isArray = propertyClass.isArray();
            if (!isArray && (propertyClass != List.class)) {
                throw new FacesException(_T(JSF_BEAN_PROPERTY_NOT_ARRAY_OR_LIST,
                                            bean.getClass().getName(),
                                            property.getPropertyName()));
            }

            // get existing values
            Method readMethod = bp.getReadMethod();
            if (readMethod != null) {
                try {
                    oldValues = readMethod.invoke(bean);
                } catch (InvocationTargetException ex) {
                    throw new FacesException(ex.getTargetException());
                } catch (Exception ex) {
                    throw new FacesException(ex);
                }
            }
        }

        List newValues = new ArrayList();
        if (oldValues != null) {
            // copy list entries from existing list
            if (isArray) {
                int len = Array.getLength(oldValues);
                for (int i = 0; i < len; i++) {
                    newValues.add(Array.get(oldValues, i));
                }
            } else {
                newValues.addAll((List)oldValues);
            }
        }

        // copy list entries from configuration
        copyListEntries(newValues, property.getListEntries());

        // convert to array
        Object values;
        if (isArray) {
            values = Array.newInstance(propertyClass.getComponentType(), newValues.size());
            for (int i = 0; i < newValues.size(); i++) {
                Array.set(values, i, newValues.get(i));
            }
        } else {
            values = newValues;
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    private Object getMapValue(Object bean, ManagedBeanConfig.Property property, BeanProperty bp)
        throws ClassNotFoundException
    {
        Map result = null;

        if (property.checkAndGetField(bean.getClass()) != null) {
            if (property.getField().getType() != Map.class) {
                throw new FacesException(_T(JSF_BEAN_PROPERTY_NOT_MAP,
                                            bean.getClass().getName(),
                                            property.getPropertyName()));

            }

            // retrieve old map
            try {
                result = (Map)property.getField().get(bean);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else if (bp != null) {
            if (bp.getType() != Map.class) {
                throw new FacesException(_T(JSF_BEAN_PROPERTY_NOT_MAP,
                                            bean.getClass().getName(),
                                            property.getPropertyName()));
            }

            // retrieve old map
            Method readMethod = bp.getReadMethod();
            if (readMethod != null) {
                try {
                    result = (Map)readMethod.invoke(bean);
                } catch (InvocationTargetException ex) {
                    throw new FacesException(ex.getTargetException());
                } catch (Exception ex) {
                    throw new FacesException(ex);
                }
            }
        }

        if (result == null) {
            result = new HashMap();
        }

        // copy map entries from configuration
        copyMapEntries(result, property.getMapEntries());

        return result;
    }

    @SuppressWarnings("unchecked")
    private Object getSimpleValue(Object bean, ManagedBeanConfig.Property property, BeanProperty bp)
        throws ClassNotFoundException
    {
        Object result;

        // determine value class
        Class valueClass;
        if (property.checkAndGetField(bean.getClass()) != null) {
            valueClass = property.getField().getType();
        } else if (bp != null) {
            valueClass = bp.getType();
        } else if (property.getPropertyClass() != null) {
            valueClass = Utils.findClass(property.getPropertyClass(), loader);
        } else {
            valueClass = String.class;
        }

        // create appropriate value
        String strValue = property.getValue();
        if (strValue == null) {
            result = null;
        } else if (ValueExpression.class.isAssignableFrom(valueClass)) {
            result = createValueExpression(strValue, Object.class);
        } else if (isValueExpression(strValue)) {
            result = checkAndEvaluateExpression(strValue, valueClass);
        } else {
            result = Coercion.coerce(strValue, valueClass);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object checkAndEvaluateExpression(String expression, Class expectedType)
        throws FacesException
    {
        if (!isValidLifespan(expression)) {
            throw new FacesException();
        }

        return evaluateExpressionGet(expression, expectedType);
    }

    private boolean isValidLifespan(String expression) {
        ManagedBeanScope scope = mbean.getManagedBeanScope();
        ManagedBeanScope refscope = getExpressionScope(expression);

        // valid if the expression has no scope
        if (refscope == null) {
            return true;
        }

        // if the managed bean's scope is "none" but the scope of the
        // referenced object is not "none", scope is invalid
        if (scope == NONE) {
            return refscope == NONE;
        }

        // if the managed bean's scope is "request" it is able to refer
        // to objects in any scope
        if (scope == REQUEST) {
            return true;
        }

        // if the managed bean's scope is "session" it is able to refer
        // to objects in other "session", "application", or "none" scopes
        if (scope == SESSION) {
            return refscope != REQUEST;
        }

        // if the managed bean's scope is "application" it is able to refer
        // to objects in other "application", or "none" scopes
        if (scope == APPLICATION) {
            return refscope == APPLICATION || refscope == NONE;
        }

        return false;
    }

    private ManagedBeanScope getExpressionScope(String expression) {
        ELNode node = Parser.parse(expression);
        String identifier = null;

        // XXX non-portable code, the means of this code is to find
        // out the left-most identifier of the expression.
        if (node instanceof ELNode.IDENT) {
            identifier = ((ELNode.IDENT)node).id;
        } else if (node instanceof ELNode.ACCESS) {
            ELNode leftmost = node;
            do {
                leftmost = ((ELNode.ACCESS)leftmost).right;
            } while (leftmost instanceof ELNode.ACCESS);
            if (leftmost instanceof ELNode.IDENT) {
                identifier = ((ELNode.IDENT)leftmost).id;
            }
        }

        if (identifier == null) {
            return null;
        }

        ManagedBeanFactory refBean = ManagedBeanContainer.getInstance().getBeanFactory(identifier);
        if (refBean != null) {
            return refBean.getScope();
        }

        // determine scope from implicit objects
        if (identifier.equalsIgnoreCase("requestScope")) {
            return REQUEST;
        } else if (identifier.equalsIgnoreCase("sessionScope")) {
            return SESSION;
        } else if (identifier.equalsIgnoreCase("applicationScope")) {
            return APPLICATION;
        }

        // no scope was provided in the expression so check for the
        // expression in all of the scopes.
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();

        if (ec.getRequestMap().get(identifier) != null) {
            return REQUEST;
        } else if (ec.getSessionMap().get(identifier) != null) {
            return SESSION;
        } else if (ec.getApplicationMap().get(identifier) != null) {
            return APPLICATION;
        }

        // no scope found
        return null;
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }
}
