/*
 * $Id: PropertyBinding.java,v 1.7 2008/03/14 00:57:20 patrick Exp $
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

import javax.faces.FacesException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.operamasks.resources.Resources.*;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.resources.Resources;

class PropertyBinding extends Binding
{
    protected String name;
    protected Class<?> type;

    private Field field;
    private Method read;
    private Method write;
    private Method init;
    private Class<?> declaringClass;

    private Reader reader;
    private Writer writer;
    
    static Logger log = Logger.getLogger("org.opermasks.faces.binding");

    protected PropertyBinding(String viewId) {
        super(viewId);
    }

    public void init(Class<? extends Annotation> metaType, Class<?> targetClass, AnnotatedElement f) {
        String name;
        Class  type;
        Class  decl;

        Field  field = null;
        Method read  = null;
        Method write = null;

        if (f instanceof Field) {
            field = (Field)f;
            name = field.getName();
            type = field.getType();
            decl = field.getDeclaringClass();
        } else if (f instanceof Method) {
            Method  method     = (Method)f;
            String  methodName = method.getName();
            Class   returnType = method.getReturnType();
            Class[] paramTypes = method.getParameterTypes();

            if (methodName.startsWith("set") && paramTypes.length == 1 && returnType == Void.TYPE) {
                name = methodName.substring(3);
                type = paramTypes[0];
                write = method;
            } else if (methodName.startsWith("get") && paramTypes.length == 0 && returnType != Void.TYPE) {
                name = methodName.substring(3);
                type = returnType;
                read = method;
            } else if (methodName.startsWith("is") && paramTypes.length == 0 && returnType == Boolean.TYPE) {
                name = methodName.substring(2);
                type = returnType;
                read = method;
            } else {
                throw new FacesException(_T(MVB_INVALID_ACCESSOR_METHOD, methodName));
            }

            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            decl = method.getDeclaringClass();
        } else {
            throw new IllegalArgumentException();
        }

        this.init(metaType, name, type, targetClass, field, read, write);
        this.declaringClass = decl;
    }

    public void init(Class<? extends Annotation> metaType,
                     String     name,
                     Class<?>   type,
                     Class<?>   targetClass,
                     Field      field,
                     Method     read,
                     Method     write)
    {
        if (read == null)
            read = BindingUtils.getReadMethod(targetClass, name, type);
        if (write == null)
            write = BindingUtils.getWriteMethod(targetClass, name, type);

        boolean bRead = (read != null) && BindingUtils.isBindingPresent(targetClass, read, metaType);
        boolean bWrite = (write != null) && BindingUtils.isBindingPresent(targetClass, write, metaType);

        if (field != null && (bRead || bWrite)) {
            throw new FacesException(_T(MVB_BIND_FIELD_AND_METHOD, name));
        } else if (bRead && bWrite) {
            throw new FacesException(_T(MVB_BIND_READ_AND_WRITE, name));
        }

        this.setName(name);
        this.setType(type);

        if (read != null) {
            read.setAccessible(true);
            read = BindingUtils.getInterfaceMethod(read);
            this.read = read;
            this.reader = new MethodReader(read);
        }

        if (write != null) {
            write.setAccessible(true);
            write = BindingUtils.getInterfaceMethod(write);
            this.write = write;
            this.writer = new MethodWriter(this.write);
        }

        if (field != null) {
            field.setAccessible(true);
            this.field = field;
            if (read == null) {
            	this.reader = new FieldReader(field);
            }
            if( write == null) {
                this.writer = new FieldWriter(field);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Field getField() {
        return this.field;
    }

    public Method getReadMethod() {
        return this.read;
    }

    public Method getWriteMethod() {
        return this.write;
    }

    public Method getInitMethod() {
        return this.init;
    }

    public void setInitMethod(Method method) {
        method.setAccessible(true);
        this.init = BindingUtils.getInterfaceMethod(method);
        if (this.field != null && this.reader != null) {
            this.reader = new InitReader(this.field, this.init, this.reader);
        }
    }

    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    public void setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    public boolean isReadable() {
        return this.reader != null;
    }

    public boolean isWriteable() {
        return this.writer != null;
    }

    public boolean isReadOnly() {
        return this.writer == null;
    }

    public boolean isWriteOnly() {
        return this.reader == null;
    }

    public Object getModelValue(ModelBean bean) {
        if (this.reader != null) {
            try {
                return this.reader.get(bean);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }
        return null;
    }

    public void setModelValue(ModelBean bean, Object value) {
        if (this.writer != null) {
            try {
                this.writer.set(bean, value);
            } catch (Exception ex) {
                throw new FacesException(_T(MVB_PROPERTY_INJECTION_FAILED, bean.getNamesString(), this.name), ex);
            }
        }
    }

    // ----------------------------------------------------------------------

    private static abstract class Reader {
        abstract Object get(ModelBean bean) throws Exception;
    }

    private static abstract class Writer {
        abstract void set(ModelBean bean, Object value) throws Exception;
    }

    private static class FieldReader extends Reader {
        private final Field field;

        FieldReader(Field field) {
            this.field = field;
        }

        Object get(ModelBean bean) throws Exception {
            return bean.getField(this.field);
        }
    }

    private static class FieldWriter extends Writer {
        private final Field field;

        FieldWriter(Field field) {
            this.field = field;
        }

        void set(ModelBean bean, Object value) throws Exception {
            bean.setField(this.field, value);
        }
    }

    private class InitReader extends Reader {
        private final Field field;
        private final Method init;
        private final Reader reader;

        InitReader(Field field, Method init, Reader reader) {
            this.field = field;
            this.init = init;
            this.reader = reader;
        }

        Object get(ModelBean bean) throws Exception {
            Object value = bean.getField(this.field);
            if (value == null) {
                value = bean.invoke(this.init);
                if (value != null) {
                    bean.setField(this.field, value);
                }
            }

            return this.reader.get(bean);
        }
    }

    private static class MethodReader extends Reader {
        private final Method method;

        MethodReader(Method method) {
            this.method = method;
        }

        Object get(ModelBean bean) throws Exception {
            return bean.invoke(this.method);
        }
    }

    private static class MethodWriter extends Writer {
        private final Method method;

        MethodWriter(Method method) {
            this.method = method;
        }

        void set(ModelBean bean, Object value) throws Exception {
            bean.invoke(this.method, value);
        }
    }
}
