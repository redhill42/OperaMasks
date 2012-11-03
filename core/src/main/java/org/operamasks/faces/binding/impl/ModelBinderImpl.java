/*
 * $Id: ModelBinderImpl.java,v 1.13 2008/04/11 03:16:06 patrick Exp $
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

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.PhaseId;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.util.FacesUtils;

import static org.operamasks.resources.Resources._T;
import static org.operamasks.resources.Resources.IOVC_MISSING_GETTER;

final class ModelBinderImpl extends ModelBinder
{
    private Class<?> targetClass;
    private final List<Binding> bindings;
    private final List<Injector> injectors;
    
    private Logger logger = Logger.getLogger("org.operamasks.iovc");

    ModelBinderImpl(Class<?> targetClass) {
        BindingBuilder builder = new BindingBuilder();
        builder.build(targetClass);

        this.targetClass = targetClass;
        this.bindings = builder.getBindings();
        this.injectors = builder.getInjectors();

        // Add implicit component attribute bindings that the attribute
        // value is loaded from resource bundle.
        addLocalStringAttributeBindings(targetClass);
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    private void addLocalStringAttributeBindings(Class<?> targetClass) {
        for (String key : getLocalStringKeys(targetClass)) {
            int dot = key.indexOf('.');
            if (dot != -1) {
                String id = key.substring(0, dot);
                String att = key.substring(dot+1);
                if (isValidAttribute(att)) {
                    addLocalStringAttributeBinding(targetClass, key, id, att);
                }
            }
        }

        Class[] interfaces = targetClass.getInterfaces();
        if (interfaces != null) {
            for (Class c : interfaces) {
                addLocalStringAttributeBindings(c);
            }
        }

        Class<?> superclass = targetClass.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            addLocalStringAttributeBindings(superclass);
        }
    }

    private void addLocalStringAttributeBinding(Class<?> targetClass, String key, String id, String att) {
        String viewId = null;

        // find a ValueBinding with the same id and attribute
        for (Binding b : this.bindings) {
            if (b instanceof ValueBinding) {
                ValueBinding vb = (ValueBinding)b;
                if (id.equals(vb.getId()) && att.equals(vb.getAttribute())) {
                    // if found then set the local string key for the
                    // value binding instead of create separate binding.
                    if (vb.getLocalString() == null) {
                        LocalStringBinding ls = new LocalStringBinding(vb.getViewId());
                        ls.setName(vb.getName());
                        ls.setType(vb.getType());
                        ls.setDeclaringClass(targetClass);
                        ls.setKey(key);
                        vb.setLocalString(ls);
                    }
                    return;
                } else if (id.equals(vb.getId())) {
                    // use the view identifier from the value binding
                    // that have the same id.
                    viewId = vb.getViewId();
                }
            }
        }

        LocalStringAttributeBinding binding = new LocalStringAttributeBinding(viewId);
        binding.setDeclaringClass(targetClass);
        binding.setId(id);
        binding.setAttribute(att);
        this.bindings.add(binding);
    }

    private Set<String> getLocalStringKeys(Class<?> declaringClass) {
        ResourceBundle bundle = FacesUtils.getLocalStringBundle(declaringClass);
        if (bundle == null) {
            return Collections.emptySet();
        }

        String classname = declaringClass.getName();
        String basename = classname.substring(classname.lastIndexOf('.')+1);
        classname += "."; basename += "."; // for prefix matching

        Set<String> keys = new HashSet<String>();
        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
            String key = e.nextElement();
            if (key.startsWith(classname)) {
                keys.add(key.substring(classname.length()));
            } else if (key.startsWith(basename)) {
                keys.add(key.substring(basename.length()));
            }
        }
        return keys;
    }

    private boolean isValidAttribute(String att) {
        return att.matches("[a-zA-Z][a-zA-Z0-9_]*")
            && !"label".equals(att)
            && !"description".equals(att);
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        String currentViewId = mbc.getViewId();
        for (Binding b : this.bindings) {
            if (b.isMatchingView(currentViewId)) {
                b.applyGlobal(ctx, mbc);
            }
        }
    }
    
    public void applyModel(FacesContext ctx, ModelBindingContext mbc) {
        String currentViewId = mbc.getViewId();
        // 当存在binding绑定(此绑定信息记录在ValueBinding实例中)时，ManagedBean将
        // 持有一个绑定的组件类实例，此实例的Scope与ManagedBean相同（通常为session），
        // 而在恢复组件树时，也会在组件树上创建一个与其语义相同的实例(Scope为request)。
        // 在进行绑定动作之前，需要先把ManagedBean中组件实例的状态复制到组件树实例中。
        // 只在RestoreView的时候，才将绑定的值恢复到新从组件树上创建的组件上;
        if (mbc.getPhaseId().equals(PhaseId.RESTORE_VIEW)) 
            copyComponentState(ctx, mbc);
        for (Binding b : this.bindings) {
            if (b.isMatchingView(currentViewId)) {
                b.apply(ctx, mbc);
            }
        }
    }
    
    private void copyComponentState(FacesContext ctx, ModelBindingContext mbc) {
        String currentViewId = mbc.getViewId();
        Set<UIComponent> copied = new HashSet<UIComponent>();
        for (Binding binding : this.bindings) {
            if ((binding.isMatchingView(currentViewId)) && (binding instanceof ValueBinding)) {
                ValueBinding b = (ValueBinding) binding;
                if ("binding".equals(b.getAttribute())) {        
                    UIComponent comp = mbc.getComponent(b.getId());
                    if (comp != null) {
                        if (copied.contains(comp)) break;
                        copied.add(comp);
                        ModelBean bean = mbc.getModelBean();
                        ValueExpression bindingVE = new PropertyValueAdapter(b, bean);
                        ELContext elctx = ctx.getELContext();

                        UIComponent oldValue = (UIComponent)bindingVE.getValue(elctx);
                        // 当组件没有在view的tag中指明binding属性，而是通过IoVC绑定的，
                        // 那么在首次访问页面时不应将binding的值恢复到新创建出来的组件上。
                        // 在IoVC方式下，beforeRender是基于view的PhaseListener，执行在此之后，
                        // 因此在beforeRender中做的初始化代码将最终生效。
                        if (oldValue != null && oldValue != comp) {
                            comp.restoreState(ctx, oldValue.saveState(ctx));
                        }
                    }
                }
            }
        }
    }

    public void applyDataModel(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        String currentViewId = mbc.getViewId();
        
        if (this.bindings.size() == 0) {
            // use bean property as default bindings
            addBindingsFromBean(currentViewId);
        }
        for (Binding b : this.bindings) {
            if (b.isMatchingView(currentViewId)) {
                b.applyDataItem(ctx, mbc, data);
            }
        }
    }

    private void addBindingsFromBean(String currentViewId) {
        PropertyDescriptor[] pds = null;
        try {
            pds = Introspector.getBeanInfo(targetClass).
                getPropertyDescriptors();
        } catch (IntrospectionException e) {
            // do nothing
        }
        
        if (pds == null) {
            return;
        }
        
        for (PropertyDescriptor pd : pds) {
        	//skip write-only properties
        	if (pd.getReadMethod() == null) {
        		if (logger.isLoggable(Level.FINE)) {
        			logger.fine(_T(IOVC_MISSING_GETTER, targetClass.getSimpleName(), pd.getName()));
        		}
        		continue;
        	}
            ValueBinding b = new ValueBinding(currentViewId);
            b.init(Bind.class, targetClass, pd.getReadMethod());
            b.setId(pd.getName());
            b.setAttribute("value");
            this.bindings.add(b);
        }
    }

    public void inject(FacesContext context, ModelBean bean) {
        if (!this.injectors.isEmpty()) {
            for (Injector b : this.injectors) {
                b.inject(context, bean);
            }
        }
    }

    public void outject(FacesContext context, ModelBean bean) {
        if (!this.injectors.isEmpty()) {
            for (Injector b : this.injectors) {
                b.outject(context, bean);
            }
        }
    }
}
