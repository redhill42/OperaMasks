/*
 * $Id:
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

package org.operamasks.faces.component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

/**
 * 此类管理需要由服务器端引擎主动检查是否出现变动的属性。
 * 当组件属性绑定了一个Bean属性，而应用程序修改了Bean属性的值而没有通知引擎，
 * 则需要由引擎主动对这些属性值进行检查。
 * @author patrick
 */
public class SensitivePropertyChecker {
    
    protected static final String SENSITIVE_PROERTIES_CHECKSUM = "org.operamasks.faces.SENSITIVE_PROPERTIES_CHECKSUM";
    protected static final String SENSITIVE_PROERTIES = "org.operamasks.faces.SENSITIVE_PROPERTIES";
    
    private static Logger logger = Logger.getLogger("org.operamasks.faces.iovc");
    
    public static void makeClientPropertiesSet(UIComponent comp) {
        Set<String> properties= new HashSet<String>();
        for(Class<?> clz = comp.getClass(); clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            SensitiveProperties clzmeta = clz.getAnnotation(SensitiveProperties.class);
            if (clzmeta != null && clzmeta.value() != null && clzmeta.value().length > 0) {
                properties.addAll(Arrays.asList(clzmeta.value()));
            }
            Field[] fields = clz.getDeclaredFields();
            for(Field f : fields) {
                Sensitive meta = f.getAnnotation(Sensitive.class);
                if (meta != null) {
                    properties.add(f.getName());
                }
            }
        }
        comp.getAttributes().put(SENSITIVE_PROERTIES, properties);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Long> computePropertiesChecksum(UIComponent comp) {
        Map<String, Long> chkSumMap = new HashMap<String, Long>();
        Set<String> properties = (Set<String>) comp.getAttributes().get(SENSITIVE_PROERTIES);
        if (properties == null) {
            //second pass
            makeClientPropertiesSet(comp);
            properties = (Set<String>) comp.getAttributes().get(SENSITIVE_PROERTIES);
        }
        for (String pname : properties) {
            String value;
            try {
                 value = FacesUtils.getFieldValueString(comp, pname);
            } catch (NoSuchFieldException ex) {
                continue;
            }
            if (value == null) {
                continue;
            }
            InputStream is = new ByteArrayInputStream(value.getBytes());
            try {
                CheckedInputStream checksum = new CheckedInputStream(is, new Adler32());
                while (checksum.read() >= 0);
                long chksum = checksum.getChecksum().getValue();
                chkSumMap.put(pname, chksum);
            } catch (IOException ex) {
                logger.warning("SensitivePropertyChecker : IOException is thrown when calculating checksum for field '" 
                        + pname + "' of " + FacesUtils.getComponentDesc(comp) + ". This field will be ignored in change-checking.");
            }
        }
        return chkSumMap;
    }
    
    public static void updateChecksum(UIComponent comp) {
        comp.getAttributes().put(SENSITIVE_PROERTIES_CHECKSUM, computePropertiesChecksum(comp));
    }
    
    /**
     * 通过对比组件中保存的checksum数据与当前状态生成的checksum数据，判断敏感属性是否已发生改变。
     * @param comp
     * @return <p>若组件中未保存上一次的checksum状态，说明为首次调用，返回true；
     *         <p>若组件中保存的checksum为空，说明没有需要判断的敏感属性，返回false
     *         <p>若组件中保存的checksum与当前状态生成的checksum有任何不同，返回true，否则返回false。
     */
    @SuppressWarnings("unchecked")
    public static boolean isChanged(UIComponent comp) {
        Map<String, Long> chkSumMap = (Map<String, Long>) comp.getAttributes().get(SENSITIVE_PROERTIES_CHECKSUM);
        if (chkSumMap == null) 
            return true;
        if (chkSumMap.size() == 0)
            return false;
        Map<String, Long> newChkSumMap = computePropertiesChecksum(comp);
        for (Map.Entry<String, Long> entry : chkSumMap.entrySet()) {
            if (!entry.getValue().equals(newChkSumMap.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
        
    }

    /**
     * 通过对比组件中保存的checksum数据与当前状态生成的checksum数据，返回已发生改变的敏感属性集合。
     * 若checksum记录不存在或为空，则认为所有属性均发生改变（首次调用）。
     * @param comp
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getChangedProperty(UIComponent comp) {
        Set<String> changed = new HashSet<String>();
        Map<String, Long> chkSumMap = (Map<String, Long>) comp.getAttributes().get(SENSITIVE_PROERTIES_CHECKSUM);
        if (chkSumMap != null && chkSumMap.size() > 0) {
            Map<String, Long> newChkSumMap = computePropertiesChecksum(comp);
            for (Map.Entry<String, Long> entry : chkSumMap.entrySet()) {
                if (!entry.getValue().equals(newChkSumMap.get(entry.getKey()))) {
                    changed.add(entry.getKey());
                }
            }
        } else {
            changed.addAll((Set<String>) comp.getAttributes().get(SENSITIVE_PROERTIES));
        }
        return changed;
    }
    
    /**
     * 通过对比组件中保存的checksum数据与当前状态生成的checksum数据，判断一个指定属性是否已发生改变。
     * @param comp
     * @throws IllegalArgumentException 若传入的propertyName不是敏感属性。
     * @return 若checksum记录不存在或传入的属性名是敏感属性但在checksum记录中不存在，返回true。
     */
    @SuppressWarnings("unchecked")
    public static boolean isChanged(UIComponent comp, String propertyName) {
        Map<String, Long> chkSumMap = (Map<String, Long>) comp.getAttributes().get(SENSITIVE_PROERTIES_CHECKSUM);
        if (chkSumMap == null)
            return true;
        Long oldSum = chkSumMap.get(propertyName);
        if (oldSum == null) {
            if (isSensitvieProperties(comp, propertyName)) {
                return true;
            } else {
                throw new IllegalArgumentException("Name '" + propertyName + "' is not a sensitive property of " + FacesUtils.getComponentDesc(comp));
            }
        }
        Map<String, Long> newChkSumMap = computePropertiesChecksum(comp);
        Long newSum = newChkSumMap.get(propertyName);
        return !oldSum.equals(newSum);
    }
    
    public static boolean isSensitvieProperties(UIComponent comp, String propertyName) {
        Set<String> properties = (Set<String>) comp.getAttributes().get(SENSITIVE_PROERTIES);
        if (properties != null) {
            return properties.contains(propertyName);
        } else {
            return false;
        }
    }
    
    public static void processSensitiveProperties(UIComponent comp) {
        Set<String> properties = SensitivePropertyChecker.getChangedProperty(comp);
        FacesContext fctxt = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(fctxt);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(fctxt, comp));
        for (String pname : properties) {
            if (!"value".equals(pname))
            cm.invoke(fctxt, pname, comp);
        }
        SensitivePropertyChecker.updateChecksum(comp);
    }
    
    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public static @interface Sensitive {
        
    }
    
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface SensitiveProperties {
        public String[] value() default {};
    }
}

