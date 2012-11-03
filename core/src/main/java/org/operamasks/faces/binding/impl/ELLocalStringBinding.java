/*
 * $Id: ELLocalStringBinding.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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

import java.util.ResourceBundle;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Enumeration;
import java.lang.reflect.Array;
import java.io.Serializable;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;
import javax.el.MethodInfo;
import javax.el.ELContext;
import javax.el.MethodNotFoundException;

import elite.lang.Closure;
import org.operamasks.el.eval.Coercion;
import org.operamasks.el.eval.MethodResolvable;
import org.operamasks.el.eval.ELEngine;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.application.impl.ResourceBundleWrapper;
import static org.operamasks.resources.Resources.*;

class ELLocalStringBinding extends Binding implements Injector
{
    private Closure closure;
    private String key;
    private Class<?> type;

    ELLocalStringBinding(Closure closure, String key, Class<?> type) {
        super(null);
        this.closure = closure;
        this.key = key;
        this.type = type;
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        inject(ctx, mbc.getModelBean());
    }

    public void inject(FacesContext ctx, ModelBean bean) {
        if (closure != null) {
            Object value = getValue((ELiteBean)bean);
            if (value != null) {
                closure.setValue(ctx.getELContext(), value);
            }
        }
    }

    public void outject(FacesContext context, ModelBean bean) {
        // do nothing
    }

    public Object getValue(ELiteBean bean) {
        if (this.type.isArray()) {
            return loadArray(bean);
        } else if (this.type == List.class) {
            return loadList(bean);
        } else if (this.type == Map.class) {
            return loadMap(bean);
        } else if (this.type == ResourceBundle.class) {
            return loadResourceBundle(bean);
        } else {
            return loadSingle(bean);
        }
    }

    private Object loadSingle(ELiteBean bean) {
        String text = bean.getLocalString(key);
        return text == null ? null : Coercion.coerce(text, type);
    }

    private Object loadArray(ELiteBean bean) {
        Class<?> elemType = type.getComponentType();
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; ; i++) {
            String text = bean.getLocalString(key + "["+i+"]");
            if (text == null) {
                break;
            }
            list.add(Coercion.coerce(text, elemType));
        }

        Object[] result = (Object[]) Array.newInstance(elemType, list.size());
        list.toArray(result);
        return result;
    }

    private Object loadList(ELiteBean bean) {
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; ; i++) {
            String text = bean.getLocalString(key + "["+i+"]");
            if (text == null) {
                break;
            }
            list.add(text);
        }

        return list;
    }

    private Object loadMap(ELiteBean bean) {
        return new LocalStringMap(bean.getPath());
    }

    private Object loadResourceBundle(ELiteBean bean) {
        ResourceBundle bundle = bean.getLocalStringBundle();
        if (bundle != null) {
            bundle = new ResourceBundleWrapper(bundle);
        }
        return bundle;
    }

    private static final class LocalStringMap extends AbstractMap<String,String>
        implements MethodResolvable, Serializable
    {
        private final String path;

        LocalStringMap(String path) {
            this.path = path;
        }

        // The normal Map implementation

        private String getString(String key) {
            return ELiteBean.getLocalString(path, key);
        }

        public boolean containsKey(Object key) {
            return (key != null) && (getString(key.toString()) != null);
        }

        public String get(Object key) {
            return getString(key.toString());
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

            ResourceBundle bundle = ELiteBean.getLocalStringBundle(path);
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

        public String format(String key, Object... args) {
            String msgtext = getString(key);
            if (msgtext == null || args == null || args.length == 0) {
                return msgtext;
            }
            return MessageFormat.format(msgtext, args);
        }

        // Implement MethodResolvable to perform convenient message formatting.

        public MethodInfo getMethodInfo(ELContext ctx, String name)
            throws MethodNotFoundException
        {
            if (getString(name) == null) {
                throw new MethodNotFoundException(_T(MVB_LOCAL_STRING_KEY_NOT_FOUND, name));
            }
            return new MethodInfo(name, String.class, new Class[0]);
        }

        public Object invoke(ELContext ctx, String name, Closure[] args)
            throws MethodNotFoundException
        {
            String msgtext = getString(name);
            if (msgtext == null) {
                throw new MethodNotFoundException(_T(MVB_LOCAL_STRING_KEY_NOT_FOUND, name));
            }

            if (args.length != 0) {
                msgtext = MessageFormat.format(msgtext, ELEngine.getArgValues(ctx, args));
            }
            return msgtext;
        }
    }
}
