/*
 * $Id: ELiteBean.java,v 1.9 2008/03/05 12:50:40 jacky Exp $
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Enumeration;

import javax.el.MethodNotFoundException;
import javax.el.ELContext;
import javax.el.MethodInfo;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.el.eval.VariableMapperImpl;

/**
 * The ELite script model bean.
 */
class ELiteBean extends ModelBean
{
    /**
     * Create ELiteBean from embeded elite script.
     */
    static ELiteBean make(ELContext elctx) {
        VariableMapper vm = elctx.getVariableMapper();
        if (vm instanceof VariableMapperImpl) {
            return make(elctx, null, ((VariableMapperImpl)vm).getVariableMap());
        } else {
            return null;
        }
    }

    /**
     * Create ELiteBean from external elite script.
     */
    static ELiteBean make(ELContext elctx, String path, Map<String,ValueExpression> vmap) {
        ELBindingBuilder builder = new ELBindingBuilder();
        ModelBinder binder = builder.build(elctx, path, vmap);
        return binder == null ? null : new ELiteBean(path, binder);
    }

    public static ResourceBundle getLocalStringBundle(String path) {
        if (path == null) {
            return null;
        }

        int i = path.lastIndexOf('/');
        String basename = path.substring(0, i+1);
        basename = basename.replace('/', '.') + "LocalStrings";

        try {
            Locale locale = FacesUtils.getCurrentLocale();
            ClassLoader loader = WebResourceLoader.getInstance();
            return ResourceBundle.getBundle(basename, locale, loader);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    public static String getLocalString(String path, String key) {
        ResourceBundle bundle = getLocalStringBundle(path);
        if (bundle == null) {
            return null;
        }

        int i = path.lastIndexOf('/');
        String prefix = path.substring(i+1);
        if ((i = prefix.lastIndexOf('.')) != -1) {
            prefix = prefix.substring(0, i);
        }

        String msgtext;
        try {
            // search string with the message prefix
            msgtext = bundle.getString(prefix + "." + key);
        } catch (MissingResourceException ex) {
            try {
                // search string without any prefix
                msgtext = bundle.getString(key);
            } catch (MissingResourceException ex2) {
                return null;
            }
        }

        // resolve relative reference
        if (msgtext != null && msgtext.startsWith("%") && msgtext.endsWith("%")) {
            try {
                String ref = msgtext.substring(1, msgtext.length()-1);
                msgtext = bundle.getString(ref);
            } catch (MissingResourceException ex) {}
        }

        return msgtext;
    }

    public static Set<String> getLocalStringKeys(String path) {
        ResourceBundle bundle = getLocalStringBundle(path);
        if (bundle == null) {
            return Collections.emptySet();
        }

        int i = path.lastIndexOf('/');
        String prefix = path.substring(i+1);
        if ((i = prefix.lastIndexOf('.')) != -1) {
            prefix = prefix.substring(0, i+1);
        } else {
            prefix += ".";
        }

        Set<String> keys = new HashSet<String>();
        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            if (key.startsWith(prefix)) {
                keys.add(key.substring(prefix.length()));
            }
        }
        return keys;
    }

    // Implementation----------------------------------

    private String path;
    private ModelBinder binder;

    ELiteBean(String path, ModelBinder binder) {
        this.path = path;
        this.binder = binder;
    }

    public String getPath() {
        return path;
    }
    
    public ModelBinder getModelBinder() {
        return binder;
    }

    public ResourceBundle getLocalStringBundle() {
        return getLocalStringBundle(path);
    }

    public String getLocalString(String key) {
        return getLocalString(path, key);
    }

    public Set<String> getLocalStringKeys() {
        return getLocalStringKeys(path);
    }

    public Class<?> getTargetClass() {
        return null;
    }

    public Object getValue(ELContext context, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object property, Object value) {

    }

    public Class<?> getType(ELContext context, Object property) {
        return null;
    }

    public boolean isReadOnly(ELContext context, Object property) {
        return false;
    }

    public Object preInvoke(Method method) throws Exception {
        return null;
    }

    public Throwable postInvoke(Object target, Throwable exception) {
        return exception;
    }

    public Object invoke(Method method, Object... args) {
        throw new MethodNotFoundException();
    }

    public Object invokeAction(Method method, Object... args) {
        throw new MethodNotFoundException();
    }

    public MethodInfo getMethodInfo(ELContext context, String name) {
        throw new MethodNotFoundException();
    }

    public Object invoke(ELContext context, String name, Closure[] args) {
        throw new MethodNotFoundException();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ELiteBean) {
            ELiteBean other = (ELiteBean)obj;
            return path == null ? other.path == null : path.equals(other.path);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return path == null ? 0 : path.hashCode();
    }
}
