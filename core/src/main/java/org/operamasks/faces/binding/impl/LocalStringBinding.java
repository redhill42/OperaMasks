/*
 * $Id: LocalStringBinding.java,v 1.12 2008/01/31 04:12:24 daniel Exp $
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

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Set;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.el.eval.MethodResolvable;
import org.operamasks.el.eval.Coercion;
import org.operamasks.el.eval.ELEngine;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.application.impl.ResourceBundleWrapper;
import static org.operamasks.resources.Resources.*;

class LocalStringBinding extends PropertyBinding implements Injector
{
    private String basename;
    private String key;

    private Map<Locale,Object> cache = new ConcurrentHashMap<Locale, Object>();

    LocalStringBinding(String viewId) {
        super(viewId);
    }

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        inject(ctx, mbc.getModelBean());
    }

    public void outject(FacesContext ctx, ModelBean bean) {
        // do nothing
    }

    public void inject(FacesContext ctx, ModelBean bean) {
        Object value = getValue(bean);
        if (value != null) {
            setModelValue(bean, value);
        }
    }

    public Object getValue(ModelBean bean) {
        Locale locale = FacesUtils.getCurrentLocale();

        Object value = this.cache.get(locale);
        if (value != null) {
            return value;
        }

        if (this.type.isArray()) {
            return this.loadArray(bean, locale);
        } else if (this.type == List.class) {
            return this.loadList(bean, locale);
        } else if (this.type == Map.class) {
            return this.loadMap(bean, locale);
        } else if (this.type == ResourceBundle.class) {
            return this.loadResourceBundle(locale);
        } else {
            return this.loadSingle(bean, locale);
        }
    }

    private String getString(String key, Locale locale) {
        return FacesUtils.getLocalString(getDeclaringClass(), this.basename, key, locale);
    }

    private Object loadSingle(ModelBean bean, Locale locale) {
        String text = this.getString(this.key, locale);

        if (text == null) {
            return null;
        } else if (FacesUtils.isValueExpression(text)) {
            return bean.evaluateExpression(text, this.type);
        } else {
            Object value = Coercion.coerce(text, this.type);
            this.cache.put(locale, value);
            return value;
        }
    }

    private Object loadArray(ModelBean bean, Locale locale) {
        Class<?> elemType = this.type.getComponentType();
        List<Object> list = new ArrayList<Object>();
        boolean cacheable = true;

        for (int i = 0; ; i++) {
            String text = getString(this.key + "["+i+"]", locale);
            if (text == null) {
                break;
            }

            if (FacesUtils.isValueExpression(text)) {
                cacheable = false;
                list.add(bean.evaluateExpression(text, elemType));
            } else {
                list.add(Coercion.coerce(text, elemType));
            }
        }

        Object[] result = (Object[])Array.newInstance(elemType, list.size());
        list.toArray(result);

        if (cacheable) {
            this.cache.put(locale, result);
        }
        return result;
    }

    private Object loadList(ModelBean bean, Locale locale) {
        Class<?> elemType = getActualTypeArgument(0);
        List<Object> list = new ArrayList<Object>();
        boolean cacheable = true;

        for (int i = 0; ; i++) {
            String text = getString(this.key + "["+i+"]", locale);
            if (text == null) {
                break;
            }

            if (FacesUtils.isValueExpression(text)) {
                cacheable = false;
                list.add(bean.evaluateExpression(text, elemType));
            } else {
                list.add(Coercion.coerce(text, elemType));
            }
        }

        if (cacheable) {
            this.cache.put(locale, list);
        }
        return list;
    }

    private Class<?> getActualTypeArgument(int ordinal) {
        Type type = null;

        Field field = this.getField();
        if (field != null) {
            type = field.getGenericType();
        } else {
            Method method = this.getWriteMethod();
            if (method != null) {
                type = method.getGenericReturnType();
            }
        }

        if ((type != null) && (type instanceof ParameterizedType)) {
            Type[] args = ((ParameterizedType)type).getActualTypeArguments();
            if ((args.length > ordinal) && (args[ordinal] instanceof Class)) {
                return (Class<?>)args[ordinal];
            }
        }

        return Object.class;
    }

    private Object loadMap(ModelBean bean, Locale locale) {
        return new LocalStringMap(bean, getDeclaringClass(), this.basename, locale);
    }

    private Object loadResourceBundle(Locale locale) {
        ResourceBundle bundle = FacesUtils.getLocalStringBundle(getDeclaringClass(), this.basename, locale);
        if (bundle != null) {
            bundle = new ResourceBundleWrapper(bundle);
        }
        return bundle;
    }

    private static final class LocalStringMap extends AbstractMap<String,String>
        implements MethodResolvable, Serializable
    {
        private final ModelBean bean;
        private final Class<?> declaringClass;
        private final String basename;
        private final Locale locale;

        LocalStringMap(ModelBean bean, Class<?> declaringClass, String basename, Locale locale) {
            this.bean = bean;
            this.declaringClass = declaringClass;
            this.basename = basename;
            this.locale = locale;
        }

        private String getString(String key) {
            return FacesUtils.getLocalString(declaringClass, basename, key, locale);
        }

        // The normal Map implementation.

        public boolean containsKey(Object key) {
            return (key != null) && (this.getString(key.toString()) != null);
        }

        public String get(Object key) {
            String msgtext = this.getString(key.toString());

            if (msgtext != null) {
                msgtext = bean.evaluateExpression(msgtext, String.class);
            }

            return msgtext;
        }

        public Set<Entry<String,String>> entrySet() {
            return stringMap().entrySet();
        }

        public Set<String> keySet() {
            return stringMap().keySet();
        }

        public Collection<String> values() {
            return stringMap().values();
        }

        private Map<String,String> stringMap = null;

        private Map<String,String> stringMap() {
            if (this.stringMap != null) {
                return this.stringMap;
            }

            ResourceBundle bundle = FacesUtils.getLocalStringBundle(declaringClass, basename, locale);
            if (bundle == null) {
                return (this.stringMap = Collections.emptyMap());
            }

            Map<String,String> map = new HashMap<String, String>();
            for (Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements();) {
                String key = keys.nextElement();
                map.put(key, bundle.getString(key));
            }

            return (this.stringMap = map);
        }

        // Load and format local string. This is a static binding method.

        public String format(String key, Object... params) {
            String msgtext = this.getString(key);
            if (msgtext == null || params == null || params.length == 0) {
                return msgtext;
            }
            return MessageFormat.format(msgtext, params);
        }

        // Implement MethodResolvable to perform convenient message formatting.

        public MethodInfo getMethodInfo(ELContext ctx, String name)
            throws MethodNotFoundException
        {
            if (this.getString(name) == null) {
                throw new MethodNotFoundException(_T(MVB_LOCAL_STRING_KEY_NOT_FOUND, name));
            }

            return new MethodInfo(name, String.class, new Class[0]);
        }

        public Object invoke(ELContext ctx, String name, Closure[] args)
            throws MethodNotFoundException
        {
            String msgtext = this.getString(name);
            if (msgtext == null) {
                throw new MethodNotFoundException(_T(MVB_LOCAL_STRING_KEY_NOT_FOUND, name));
            }

            if (args.length != 0) {
                msgtext = MessageFormat.format(msgtext, ELEngine.getArgValues(ctx, args));
            }

            return msgtext;
        }

        private Object writeReplace() {
            return null;
        }
    }
}
