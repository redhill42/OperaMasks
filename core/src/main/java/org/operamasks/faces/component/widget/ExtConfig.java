/*
 * $Id: ExtConfig.java,v 1.10 2008/03/17 17:32:24 jacky Exp $
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

package org.operamasks.faces.component.widget;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;

/**
 * 这个类存储一些配置用于构造Ext的组件的对象参数，比如{a:'avalue', b:true, c: 200}
 */
public class ExtConfig implements Serializable, Cloneable {

    private static final long serialVersionUID = -1065034877736460926L;

    protected Map<String,Object> properties = new HashMap<String, Object>();

    protected Set<String> vars = new HashSet<String>();
    
    public ExtConfig() {}

    public ExtConfig(UIComponent component) {
        populateConfig(component);
    }

    public void populateConfig(UIComponent component) {
        Class<?> compClass = component.getClass();
        Map<BeanProperty, ExtConfigOption> configFields = new HashMap<BeanProperty, ExtConfigOption>();
        for(Class<?> clz = compClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
        	
            Field[] fields = clz.getDeclaredFields();
            for(Field f : fields) {
            	ExtConfigOption meta = f.getAnnotation(ExtConfigOption.class);
                if (meta != null) {
                	try {
                		configFields.put(BeanUtils.getProperty(compClass, f.getName()), meta);
					} catch (IntrospectionException e) {
						e.printStackTrace();
						// ignore;
					}
                }
            }
        }
        for(BeanProperty prop : configFields.keySet()) {
            try {
            	ExtConfigOption meta = configFields.get(prop);
                Object value = prop.getReadMethod().invoke(component);
                String key = meta.value();
                if (key == null || key.length() == 0) {
                    key = prop.getName();
                }
                this.set(key, value);
            } catch (Throwable e) {
                // don't add to config when error happend
            }
        }
        
    }

    protected Object get(String name) {
        return get(name, null);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String name, T def) {
        T result = (T)properties.get(name);
        if (result == null) {
            result = def;
        } else {
            if (result instanceof ValueExpression) {
                result = (T)((ValueExpression)result).getValue(FacesContext.getCurrentInstance().getELContext());
            }
        }
        return result;
    }

    public void set(String name, Object value) {
        set(name, value, false);
    }

    public void set(String name, Object value, boolean isVar) {
        if (value == null) {
            properties.remove(name);
            if (isVar) {
                vars.remove(name);
            }
        } else {
            properties.put(name, value);
            if (isVar) {
                vars.add(name);
            }
        }
    }

    public boolean isSet(String name) {
        return properties.containsKey(name);
    }
    
    public void remove(String name) {
        properties.remove(name);
        vars.remove(name);
    }


    public void reset() {
        properties.clear();
        vars.clear();
    }
    
    /**
     * 与另一个ExtConfig "other" 合并，该ExtConfig中的属性优先，如有重复，会覆盖另一个ExtConfig中的属性.
     * @param other 另一个ExtConfig
     */
    public void merge(ExtConfig other) {        
        for (Map.Entry<String, Object> entry : other.properties.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }
    public String toScript() {
        return toScript(null);
    }
    /**
     * @param exclusion 用逗号隔开的属性名，这些属性不被写入最终的脚本。
     */
    public String toScript(String exclusion) {
        String[] excludes = null;
        if (exclusion != null) 
            excludes = exclusion.split(",");
        StringBuilder buf = new StringBuilder();
        
        props:
        for (String name : properties.keySet()) {
            if (excludes != null) {
                for (int j = 0; j < excludes.length; j++) {
                    if (name.equals(excludes[j])) {
                        continue props;
                    }
                }
            }
            Object value = properties.get(name);
            if (value != null) {
                if (value instanceof ValueExpression) {
                    value = ((ValueExpression)value).getValue(FacesContext.getCurrentInstance().getELContext());
                }
                if (buf.length() > 0)
                    buf.append(",\n");
                buf.append(getReplacedName(name)).append(":");
                if (value instanceof String) {                                     
                    scriptOnStr(buf, name, (String)value);
                } else {
                    buf.append(value);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * 给子类一个机会，改变某个属性的名字
     */
    protected String getReplacedName(String name) {
        return name;
    }
    
    /**
     * 缺省实现：把字符串的值用双引号(")括起来
     * 子类可以继承这个方法，用来处理一些特殊的属性，比如某些属性值可能不需要用引号括起来。
     */
    protected void scriptOnStr(StringBuilder buf, String propName, String propValue) {
        if (!vars.contains(propName)) {
            buf.append(HtmlEncoder.enquote(propValue, '"'));
        } else {
            buf.append(propValue);
        }
    }
    
    public ExtConfig clone() {
        try {
            ExtConfig ec = (ExtConfig)super.clone();
            ec.properties = new HashMap<String, Object>();
            ec.properties.putAll(this.properties);
            return ec;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
}
