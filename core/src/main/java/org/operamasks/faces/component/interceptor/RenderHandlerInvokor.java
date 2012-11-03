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

import java.beans.PropertyChangeEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;

import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.EncodeAjaxChildren;
import org.operamasks.faces.annotation.component.EncodeAjaxEnd;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlChildren;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.EncodeInitScript;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.EncodeResourceChildren;
import org.operamasks.faces.annotation.component.EncodeResourceEnd;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.annotation.component.ProcessDecodes;
import org.operamasks.faces.annotation.component.ProcessUpdates;
import org.operamasks.faces.annotation.component.ProcessValidators;
import org.operamasks.faces.annotation.component.PropertyListener;

@SuppressWarnings("unchecked")
public class RenderHandlerInvokor
{
    private List<Action> actions;
    private List<Action> propertyListeners;
    private List<Action> operationListeners;
    private Object handler;
    private boolean encodeChildren;
    private boolean encodeResourceChildren;
    
    private static Class<? extends Annotation>[] SUPPORT_ACTIONS = new Class[]{
        ProcessDecodes.class,
        ProcessValidators.class,
        ProcessUpdates.class,
        EncodeHtmlBegin.class,
        EncodeHtmlChildren.class,
        EncodeHtmlEnd.class,
        EncodeAjaxBegin.class,
        EncodeAjaxChildren.class,
        EncodeAjaxEnd.class,
        EncodeResourceBegin.class,
        EncodeResourceChildren.class,
        EncodeResourceEnd.class,
        EncodeInitScript.class
    };

    public RenderHandlerInvokor(Class<?> handlerClass) {
        actions = new ArrayList<Action>();
        propertyListeners = new ArrayList<Action>();
        operationListeners = new ArrayList<Action>();
        scanHandlerClass(handlerClass);
        
    }
    
    public boolean invoke(Class<? extends Annotation> annotation, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<Action> matchedActions = findActions(annotation, actions);
        for (Action action : matchedActions) {
            invokeAction(action, args);
        }
        return matchedActions.size() > 0;
    }
    
    public boolean encodeChildren() {
        return this.encodeChildren;
    }
    
    public boolean encodeResourceChildren() {
        return this.encodeResourceChildren;
    }
    
    public void invokeComponentOperation(String opName, Object target, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<Action> matchedActions = findActions(OperationListener.class, operationListeners);
        List<Action> matchedOp = new ArrayList<Action>(); 
        List<Action> commonOp = new ArrayList<Action>(); 
        for (Action action : matchedActions) {
            OperationListener meta = (OperationListener)action.meta; 
            if (opName.equals(meta.value())) {
                matchedOp.add(action);
            } else if (meta.value() == null || meta.value().length() == 0) {
                commonOp.add(action);
            }
        }
        if (matchedOp.size() > 0 ) {
            for (Action action : matchedOp) {
                Object[] newArgs = new Object[args.length + 1];
                newArgs[0] = target;
                System.arraycopy(args, 0, newArgs, 1, args.length);
                invokeAction(action, newArgs);
            }
        } else {
            for (Action action : commonOp) {
                Object[] newArgs = new Object[3];
                newArgs[0] = target;
                newArgs[1] = opName;
                newArgs[2] = null;
                invokeAction(action, newArgs);
            }
        }
    }

    public void firePropertyChanged(PropertyChangeEvent event) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<Action> matchedActions = findActions(PropertyListener.class, propertyListeners);
        for (Action action : matchedActions) {
            invokeAction(action, event);
        }
    }
    
    private void invokeAction(Action action, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        action.m.invoke(handler, args);
    }

    private List<Action> findActions(Class<? extends Annotation> annotation, List<Action> fromActions) {
        List<Action> matchedActions = new ArrayList<Action>();
        for (Action action : fromActions) {
            if (action.meta.annotationType().equals(annotation)) {
                matchedActions.add(action);
            }
        }
        return matchedActions;
    }

    private void scanHandlerClass(Class<?> handlerClass) {
        try {
            this.handler = handlerClass.newInstance();
        } catch (Exception e) {
            throw new FacesException(e);
        }

        for(Class<?> clz = handlerClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            Method[] methods = clz.getDeclaredMethods();
            for(Method m : methods) {
                PropertyListener p = m.getAnnotation(PropertyListener.class);
                if (p != null) {
                    m.setAccessible(true);
                    propertyListeners.add(new Action(p, m));
                }
                OperationListener op = m.getAnnotation(OperationListener.class);
                if (op != null) {
                    m.setAccessible(true);
                    operationListeners.add(new Action(op, m));
                }
                addAction(m);
            }
        }
    }
    
    private void addAction(Method m) {
        for (Class<? extends Annotation> clazz : SUPPORT_ACTIONS) {
            Annotation meta = m.getAnnotation(clazz); 
            if (meta != null){
                if (!encodeResourceChildren) {
                    if (EncodeResourceChildren.class.equals(meta.annotationType())) {
                        encodeResourceChildren = true;
                    }
                }
                if (!encodeChildren) {
                    // if exist encode children annotation, set encodeChildren to true.
                    if (EncodeHtmlChildren.class.equals(meta.annotationType()) || EncodeAjaxChildren.class.equals(meta.annotationType())) {
                            encodeChildren = true;
                    }
                }
                //checkMethodSignature(m);
                m.setAccessible(true);
                Action action = new Action(meta, m);
                actions.add(action);
            }
        }
    }

//    private void checkMethodSignature(Method m) {
//        if (m.getParameterTypes().length != 2 || 
//                !(FacesContext.class.isAssignableFrom(m.getParameterTypes()[0])
//                && UIComponent.class.isAssignableFrom(m.getParameterTypes()[1]) 
//                && m.getReturnType().equals(void.class))) {
//            throw new FacesException("the method["+ m.toString()+"] must have signature like public void method(FacesContext context, UIComponent component)" );
//        }
//    }
    
    class Action {
        Annotation meta;
        Method m;
        Action(Annotation meta, Method m) {
            this.meta = meta;
            this.m = m;
        }
    }

}
