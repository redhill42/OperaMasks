/*
 * $Id: ModelBean.java,v 1.17 2008/03/14 00:57:20 patrick Exp $
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

package org.operamasks.faces.binding;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.VariableMapper;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.ELException;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;

import elite.lang.Closure;
import org.operamasks.el.eval.ELEngine;
import org.operamasks.el.eval.PropertyDelegate;
import org.operamasks.el.eval.MethodResolvable;
import org.operamasks.el.eval.MethodDelegate;
import org.operamasks.faces.application.impl.DefaultNavigationHandler;
import org.operamasks.faces.binding.impl.DefaultModelBean;
import org.operamasks.faces.binding.impl.ValueExpressionModelBean;
import org.operamasks.faces.binding.impl.ValueWrapper;
import org.operamasks.faces.event.EventBroadcaster;
import org.operamasks.faces.annotation.RaiseEvent;
import org.operamasks.faces.util.FacesUtils;

/**
 * Encapsulate a model bean.
 */
public abstract class ModelBean implements Cloneable, PropertyDelegate, MethodDelegate
{
    /**
     * Wraps a model bean from a POJO.
     */
    public static ModelBean wrap(Object target) {
        if (target == null) {
            return NULL_MODEL_BEAN;
        }

        if (target instanceof ValueExpression) {
            return new ValueExpressionModelBean((ValueExpression)target);
        }

        ModelBeanCreator creator = ModelBindingFactory.instance().getModelBeanCreator();
        if (creator != null) {
            return creator.createModelBean(target);
        }

        return new DefaultModelBean(target);
    }

    private String name;
    private List<String> names;
    private ModelBinder binder;

    /**
     * Returns the names of the model bean.
     */
    public String[] getNames() {
        int size = 0;
        if (this.name != null)
            size++;
        if (this.names != null)
            size += this.names.size();

        String[] result = new String[size];
        int i = 0;
        if (this.name != null) {
            result[i++] = this.name;
        }
        if (this.names != null) {
            for (String n : this.names) {
                result[i++] = n;
            }
        }
        return result;
    }
    
    /**
     * return the string representation of names of the model bean
     * @reutrn The first name returned by {@link #getNames()} followed 
     *         by a list in parenthesis for all other names. Returns empty 
     *         string if there is not any name has been set for the model bean.
     */
    public String getNamesString() {
        String[] names = this.getNames();
        int size = names.length;
        if (size == 0) 
            return "";
        StringBuffer bf = new StringBuffer();
        bf.append(names[0]);
        if (size > 1) {
            bf.append("(");
            bf.append(names[1]);
            for (int i = 2; i < size; i++) {
                bf.append(",");
                bf.append(names[i]);
            }
            bf.append(")");
        }
        return bf.toString();
    }

    /**
     * Associate a name to the model bean.
     */
    public void addName(String name) {
        if (this.name == null) {
            this.name = name;
            return;
        } else if (this.name.equals(name)) {
            return;
        }

        if (this.names == null) {
            this.names = new ArrayList<String>();
            this.names.add(name);
        } else if (!this.names.contains(name)) {
            this.names.add(name);
        }
    }

    public void addNames(String[] names) {
        for (String n : names) {
            addName(n);
        }
    }

    public boolean isMatchingPrefix(String prefix) {
        if (this.name != null) {
            if (this.name.equals(prefix)) {
                return true;
            }
        }
        if (this.names != null) {
            for (String n : this.names) {
                if (n.equals(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the class of model bean target.
     */
    public abstract Class<?> getTargetClass();

    /**
     * Returns the ModelBinder instance for the model bean.
     */
    public ModelBinder getModelBinder() {
        if (this.binder == null) {
            Class<?> type = getTargetClass();
            if (type != null) {
                this.binder = ModelBindingFactory.instance().createBinder(type);
            }
        }
        return this.binder;
    }

    /**
     * Inject the underlying model bean.
     */
    public void inject(FacesContext context) {
        ModelBinder binder = getModelBinder();
        if (binder != null) {
            binder.inject(context, this);
        }
    }

    /**
     * Outject the underlying model bean.
     */
    public void outject(FacesContext context) {
        ModelBinder binder = getModelBinder();
        if (binder != null) {
            binder.outject(context, this);
        }
    }

    /**
     * Returns the underlying model bean target object.
     *
     * @param method the invoking method.
     * @return the underlying model bean target object.
     */
    public abstract Object preInvoke(Method method)
        throws Exception;

    /**
     * Release the underlying model bean target object.
     *
     * @param target the target to be released.
     * @param exception the exception occured during invocation.
     * @return the exception
     */
    public abstract Throwable postInvoke(Object target, Throwable exception);

    /**
     * Start transaction demarcation before invoking underlying method.
     *
     * @param method the invoking method.
     * @return true if transaction demarcation is begin; false otherwise.
     */
    public boolean preInvokeTx(Method method) throws Exception {
        return false; // default is do nothing
    }

    /**
     * End of transaction demarcation after invoking underlying method.
     *
     * @param transacted true if the transction demarcation is begin; false otherwise.
     * @param exception exception occurred during invocation.
     * @return the exception
     */
    public Throwable postInvokeTx(boolean transacted, Throwable exception) {
        return exception; // default is do nothing
    }

    public void rethrow(Throwable exception)
        throws Exception
    {
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            } else if (exception instanceof Error) {
                throw (Error)exception;
            } else {
                throw (Exception)exception;
            }
        }
    }

    public void rethrowUnchecked(Throwable exception) {
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            } else if (exception instanceof Error) {
                throw (Error)exception;
            } else {
                throw new FacesException(exception);
            }
        }
    }

    /**
     * Get a field value of target model bean.
     */
    public Object getField(Field field) {
        Object target = null;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            result = field.get(target);
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }

        return result;
    }

    /**
     * Set a field value of target model bean.
     */
    public void setField(Field field, Object value) {
        Object target = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            field.set(target, value);
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }
    }

    /**
     * Invoke a method on the target model bean.
     */
    protected Object invokeMethod(Object target, Method method, Object[] args)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        return method.invoke(target, args);
    }

    /**
     * Invoke a dynamic method on the target model bean.
     */
    protected Object invokeDynamicMethod(ELContext ctx, Object target, Method method, Closure[] args) {
        return ELEngine.invokeMethod(ctx, target, method, args);
    }

    /**
     * Invoke a method on the target model bean.
     */
    public Object invoke(Method method, Object... args)
        throws Exception
    {
        ELContext ctx = FacesContext.getCurrentInstance().getELContext();

        Object target = null;
        boolean transacted = false;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(method);
            transacted = preInvokeTx(method);

            // set context variables
            Object vars = setContextVariables(ctx, target, args);

            // invoke the method
            result = invokeMethod(target, method, args);

            // raise event if necessary
            RaiseEvent raise = method.getAnnotation(RaiseEvent.class);
            if (raise != null) {
                decodeEvent(target, raise.value());
            }

            // restore context variables
            restoreContextVariables(ctx, vars);

        } catch (InvocationTargetException ex) {
            exception = ex.getTargetException();
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            exception = postInvokeTx(transacted, exception);
            rethrow(postInvoke(target, exception));
        }

        return result;
    }

    /**
     * Invoke an action method on the target model bean.
     */
    public Object invokeAction(Method method, Object... args)
        throws Exception
    {
        ELContext ctx = FacesContext.getCurrentInstance().getELContext();

        Object target = null;
        boolean transacted = false;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(method);
            transacted = preInvokeTx(method);

            // set context variable
            Object vars = setContextVariables(ctx, target, args);

            // invoke the method
            result = invokeMethod(target, method, args);

            // raise event if necessary
            RaiseEvent raise = method.getAnnotation(RaiseEvent.class);
            if (raise != null) {
                decodeEvent(target, raise.value());
            }

            // decode outcome value
            if (result != null) {
                result = decodeOutcome(target, result);
            }

            // restore context variables
            restoreContextVariables(ctx, vars);

        } catch (InvocationTargetException ex) {
            exception = ex.getTargetException();
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            exception = postInvokeTx(transacted, exception);
            rethrow(postInvoke(target, exception));
        }

        return result;
    }

    private static final Class[] BROADCAST_PARAM_TYPES = { Object.class, String.class };

    private void decodeEvent(Object target, String spec) {
        String name, params;

        // decode parameters
        int paren = spec.indexOf('(');
        if (paren != -1 && spec.endsWith(")")) {
            name = spec.substring(0, paren).trim();
            params = spec.substring(paren+1, spec.length()-1).trim();
        } else {
            name = spec;
            params = null;
        }

        if (name.length() != 0) {
            if (params == null || params.length() == 0) {
                // directly broadcast event if no arguments
                EventBroadcaster.getInstance().broadcast(target, name);
            } else {
                // create a method expression bridge to broadcast event with arguments
                ELContext elContext = FacesContext.getCurrentInstance().getELContext();
                String expression = "#{_$events.broadcast(" + params + ")}";

                if (elContext.getVariableMapper().resolveVariable("_$events") == null) {
                    ValueExpression var = new ValueWrapper(EventBroadcaster.getInstance(), Object.class);
                    elContext.getVariableMapper().setVariable("_$events", var);
                }

                MethodExpression bridge = ELEngine.getExpressionFactory()
                    .createMethodExpression(elContext, expression, Void.TYPE, BROADCAST_PARAM_TYPES);
                bridge.invoke(elContext, new Object[] { target, name });
            }
        }
    }

    private Object decodeOutcome(Object target, Object result) {
        String outcome = result.toString();
        String events = null;

        if (outcome.startsWith(DefaultNavigationHandler.VIEW_SCHEME)) {
            int sep = outcome.indexOf('#');
            if (sep != -1) {
                events = outcome.substring(sep+1);
                result = outcome.substring(0, sep);
            }
        } else if (outcome.startsWith("#")) {
            events = outcome.substring(1);
            result = null;
        }

        if (events != null && events.length() != 0) {
            decodeEvent(target, events);
        }

        return result;
    }

    public Object setContextVariables(ELContext elctx, Object target, Object[] args) {
        VariableMapper vm = elctx.getVariableMapper();
        ValueExpression[] vars = new ValueExpression[2 + args.length];

        vars[0] = vm.setVariable("this", new ValueWrapper(target, Object.class));
        vars[1] = vm.setVariable("arg", new ValueWrapper(args, Object[].class));
        for (int i = 0; i < args.length; i++) {
            vars[i+2] = vm.setVariable("$"+(i+1), new ValueWrapper(args[i], Object.class));
        }

        return vars;
    }

    public void restoreContextVariables(ELContext elctx, Object opaque) {
        ValueExpression[] vars = (ValueExpression[])opaque;
        VariableMapper vm = elctx.getVariableMapper();

        vm.setVariable("this", vars[0]);
        vm.setVariable("arg", vars[1]);
        for (int i = 0; i < vars.length-2; i++) {
            vm.setVariable("$"+(i+1), vars[i+2]);
        }
    }

    // Implement PropertyResolvable

    public Object getValue(ELContext context, Object property) {
        Object target = null;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            if (target != null) {
                result = context.getELResolver().getValue(context, target, property);
            }
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }

        return result;
    }

    public Class<?> getType(ELContext context, Object property) {
        Object target = null;
        Class<?> result = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            if (target != null) {
                result = context.getELResolver().getType(context, target, property);
            }
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }

        return result;
    }

    public void setValue(ELContext context, Object property, Object value) {
        Object target = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            if (target != null) {
                context.getELResolver().setValue(context, target, property, value);
            }
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }
    }

    public boolean isReadOnly(ELContext context, Object property) {
        Object target = null;
        boolean result = false;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            if (target != null) {
                result = context.getELResolver().isReadOnly(context, target, property);
            }
        } catch (Throwable ex) {
            exception = ex;
        } finally {
            rethrowUnchecked(postInvoke(target, exception));
        }

        return result;
    }

    // Implement MethodResolvable

    public MethodInfo getMethodInfo(ELContext context, String name)
        throws MethodNotFoundException
    {
        Class<?> targetClass = getTargetClass();
        if (targetClass == null) {
            throw new MethodNotFoundException(name);
        }

        // resolve static method...
        if (!(MethodDelegate.class.isAssignableFrom(targetClass))) {
            for (Method method : targetClass.getMethods()) {
                if (name.equals(method.getName())) {
                    return new MethodInfo(name, method.getReturnType(), method.getParameterTypes());
                }
            }
        }

        // resolve dynamic method...
        if (MethodResolvable.class.isAssignableFrom(targetClass)) {
            Object target = null;
            MethodInfo result = null;
            Throwable exception = null;

            try {
                target = preInvoke(null);
                result = ((MethodResolvable)target).getMethodInfo(context, name);
            } catch (Throwable ex) {
                exception = ex;
            } finally {
                rethrowUnchecked(postInvoke(target, exception));
            }

            return result;
        }

        throw new MethodNotFoundException(name);
    }

    public Object invoke(ELContext context, String name, Closure[] args)
        throws MethodNotFoundException
    {
        Class<?> targetClass = getTargetClass();
        if (targetClass == null) {
            throw new MethodNotFoundException(name);
        }

        // invoke static method...
        if (!(MethodDelegate.class.isAssignableFrom(targetClass))) {
            Method method = ELEngine.resolveMethod(context, targetClass, name, args);
            if (method != null) {
                try {
                    return invokeDynamic(context, method, args);
                } catch (Exception ex) {
                    throw new ELException(ex);
                }
            }
        }

        // invoke dynamic method...
        if (MethodResolvable.class.isAssignableFrom(targetClass)) {
            Object target = null;
            Object result = null;
            Throwable exception = null;

            try {
                target = preInvoke(null);
                result = ((MethodResolvable)target).invoke(context, name, args);
            } catch (Throwable ex) {
                exception = ex;
            } finally {
                rethrowUnchecked(postInvoke(target, exception));
            }

            return result;
        }

        throw new MethodNotFoundException(name);
    }

    protected Object invokeDynamic(ELContext ctx, Method method, Closure[] args)
        throws Exception
    {
        Object target = null;
        boolean transacted = false;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(method);
            transacted = preInvokeTx(method);

            // set context variables
            Object vars = setContextVariables(ctx, target, ELEngine.getArgValues(ctx, args));

            // invoke the method
            result = this.invokeDynamicMethod(ctx, target, method, args);

            // raise event if necessary
            RaiseEvent raise = method.getAnnotation(RaiseEvent.class);
            if (raise != null) {
                decodeEvent(target, raise.value());
            }
            
            // restore context variables
            restoreContextVariables(ctx, vars);

        } catch (Throwable ex) {
            exception = ex;
        } finally {
            exception = postInvokeTx(transacted, exception);
            rethrow(postInvoke(target, exception));
        }

        return result;
    }

    /**
     * Evaluate the expression that is scoped with the target model bean.
     */
    public <T> T evaluateExpression(String expression, Class<T> expectedType) {
        return FacesUtils.evaluateExpressionGet(this, expression, expectedType);
    }

    public abstract boolean equals(Object obj);
    public abstract int hashCode();

    public ModelBean clone() {
        try {
            ModelBean copy = (ModelBean)super.clone();
            if (this.names != null) {
                copy.names = new ArrayList<String>(this.names);
            }
            return copy;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    public static final ModelBean NULL_MODEL_BEAN = new ModelBean() {
        public Class<?> getTargetClass() {
            return null;
        }

        public Object preInvoke(Method method) {
            return null;
        }

        public Throwable postInvoke(Object target, Throwable exception) {
            return exception;
        }

        @Override
        public Object getField(Field field) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    return field.get(null);
                } catch (IllegalAccessException ex) {
                    throw new FacesException(ex);
                }
            } else {
                throw new NullPointerException();
            }
        }

        @Override
        public void setField(Field field, Object value) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    field.set(null, value);
                } catch (IllegalAccessException ex) {
                    throw new FacesException(ex);
                }
            } else {
                throw new NullPointerException();
            }
        }

        public boolean equals(Object obj) {
            return obj == NULL_MODEL_BEAN;
        }

        public int hashCode() {
            return 0;
        }
    };
}
