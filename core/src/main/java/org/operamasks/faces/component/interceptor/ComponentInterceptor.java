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
package org.operamasks.faces.component.interceptor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.EncodeAjaxChildren;
import org.operamasks.faces.annotation.component.EncodeAjaxEnd;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlChildren;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.annotation.component.ProcessDecodes;
import org.operamasks.faces.annotation.component.ProcessUpdates;
import org.operamasks.faces.annotation.component.ProcessValidators;
import org.operamasks.faces.component.SensitivePropertyChecker;
import org.operamasks.faces.interceptor.AbstractInterceptor;
import org.operamasks.faces.interceptor.InvokeContext;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

/**
 * 组件调用拦截
 * @author root
 *
 */
public class ComponentInterceptor extends AbstractInterceptor
{
    private RenderHandlerInvokor invoker;
    private Map<String, Class<? extends Annotation>> htmlEncodeMapping;
    private Map<String, Class<? extends Annotation>> ajaxEncodeMapping;
    private Map<String, Class<? extends Annotation>> lifecycleMapping;
    private Map<Method, PropertyDescriptor> setters;
    private Map<Method, String> operations;
    private PhaseId phaseId;

    public ComponentInterceptor(Class<? extends UIComponent> componentClass, RenderHandlerInvokor invoker) {
        this.invoker = invoker;
        setters = new HashMap<Method, PropertyDescriptor>();
        operations = new HashMap<Method, String>();
        phaseId = PhaseId.ANY_PHASE;
        scanComponenetClass(componentClass);
        initMapping();
    }
    
    private void scanComponenetClass(Class<? extends UIComponent> componentClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(componentClass);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                if (property.getWriteMethod() != null) {
                    setters.put(property.getWriteMethod(), property);
                }
            }
        } catch (IntrospectionException e) {
            //ignore
        }

        for(Class<?> clz = componentClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            Method[] methods = clz.getDeclaredMethods();
            for(Method m : methods) {
                Operation meta = m.getAnnotation(Operation.class);
                if (meta != null) {
                    operations.put(m, meta.value());
                }
            }
        }

    }

    private void initMapping() {
        htmlEncodeMapping = new HashMap<String, Class<? extends Annotation>>();        
        ajaxEncodeMapping = new HashMap<String, Class<? extends Annotation>>();
        lifecycleMapping = new HashMap<String, Class<? extends Annotation>>();

        htmlEncodeMapping.put("encodeBegin", EncodeHtmlBegin.class);
        htmlEncodeMapping.put("encodeChildren", EncodeHtmlChildren.class);
        htmlEncodeMapping.put("encodeEnd", EncodeHtmlEnd.class);
        
        ajaxEncodeMapping.put("encodeBegin", EncodeAjaxBegin.class);
        ajaxEncodeMapping.put("encodeChildren", EncodeAjaxChildren.class);
        ajaxEncodeMapping.put("encodeEnd", EncodeAjaxEnd.class);
        
        lifecycleMapping.put("processDecodes", ProcessDecodes.class);
        lifecycleMapping.put("processValidators", ProcessValidators.class);
        lifecycleMapping.put("processUpdates", ProcessUpdates.class);
    }

    @Override
    public void afterInvoke(InvokeContext context) throws Throwable {
    }

    @Override
    public void beforeInvoke(InvokeContext context) throws Throwable {
        Object oldValue = null;
        Method m = context.getMethod();
        PropertyDescriptor p = setters.get(m);
        if (p != null) {
            Method readMethod = p.getReadMethod();
            if (readMethod != null) {
                oldValue = p.getReadMethod().invoke(context.getTarget());
            }
        }
        Class<? extends Annotation> annotation = findAction(context);
        if (annotation != null) {
            if (annotation == EncodeAjaxEnd.class) {
                if (context.getTarget() instanceof UIComponent) {
                    UIComponent comp = (UIComponent) context.getTarget();
                    SensitivePropertyChecker.processSensitiveProperties(comp);
                }
            }
            Object[] oldArgs = context.getArgs();
            Object[] newArgs = new Object[oldArgs.length + 1];
            System.arraycopy(oldArgs, 0, newArgs, 0, oldArgs.length);
            newArgs[newArgs.length-1] = context.getTarget();
            boolean invoked = invoker.invoke(annotation, newArgs);
            if (!invoked) {
                super.doInvoke(context);
            }
        } else if ("getRendersChildren".equals(context.getMethod().getName()) && invoker.encodeChildren()) {
                context.setReturnValue(Boolean.TRUE);
        } else {
            super.doInvoke(context);
        }
        firePropertyChanged(context, oldValue);
        invokeComponentOperation(context);
        context.setComplete(true);
    }
    
    


    private void invokeComponentOperation(InvokeContext context) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m = context.getMethod();
        if (!this.operations.keySet().contains(m)) {
            return;
        }
        String opName = this.operations.get(m);
        opName = (opName == null || opName.length() == 0) ? m.getName() : opName;
        invoker.invokeComponentOperation(opName, context.getTarget(), context.getArgs());
    }

    private void firePropertyChanged(InvokeContext context, Object oldValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m = context.getMethod();
        PropertyDescriptor p = setters.get(m);
        if (p != null) {
            Object newValue = null;
            Object[] args = context.getArgs();
            if (args.length == 1) {
                newValue = args[0];
            }
            
            boolean changed = false;
            if (oldValue == null) {
                if (newValue != null) {
                    changed = true;
                }
            } else {
                if (!oldValue.equals(newValue)) {
                    changed = true;
                }
            }
            
            if (changed) {
                PropertyChangeEvent event = new PropertyChangeEvent(context.getTarget(), p.getName(), oldValue, newValue);
                event.setPropagationId(FacesUtils.getCurrentPhaseId(FacesContext.getCurrentInstance()));
                invoker.firePropertyChanged(event);
            }
        }
    }

    @Override
    public void doInvoke(InvokeContext context) throws Throwable {
    }
    
    private Class<? extends Annotation> findAction(InvokeContext ic) {
        Method m = ic.getMethod();
        String methodName = m.getName();
        Class<? extends Annotation> target = lifecycleMapping.get(methodName);
        if (target != null) {
            return target;
        }
        
        FacesContext context = null;
        if (ic.getArgs().length > 0 && (ic.getArgs()[0] instanceof FacesContext)) {
            context = (FacesContext)ic.getArgs()[0];
            if (FacesUtils.isAjaxResponse(context)) {
                target = ajaxEncodeMapping.get(methodName);
            } else if (FacesUtils.isHtmlResponse(context)) {
                target = htmlEncodeMapping.get(methodName);
            }
        }
        return target;
    }
    

    private static final String[] matchedMethods = new String[]{
        "encodeBegin",
        "encodeChildren",
        "encodeEnd",
        "encodeResourceBegin",
        "encodeResourceChildren",
        "encodeResourceEnd",
        //"getRendererType",
        "getRendersChildren",
        "processDecodes",
        "processValidators",
        "processUpdates"
    };

    public boolean isMatch(InvokeContext context) {
        String methodName = context.getMethod().getName();
        for (String matchedMethod : matchedMethods) {
            if (matchedMethod.equals(methodName))
                return true;
        }
        Method m = context.getMethod();
        if (m.getAnnotations().length > 0) {
            return true;
        }
        
        if (setters.keySet().contains(m)) {
            return true;
        }
        return false;
    }


}
