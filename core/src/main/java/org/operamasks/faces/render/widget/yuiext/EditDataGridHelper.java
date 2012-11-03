/*
 * $Id: EditDataGridHelper.java,v 1.4 2008/04/14 11:28:09 jacky Exp $
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
package org.operamasks.faces.render.widget.yuiext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIEditDataGrid;
import org.operamasks.faces.component.widget.grid.UIOutputColumn;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;
import org.operamasks.util.Utils;

public class EditDataGridHelper {
    
    public static void applyData(FacesContext context, UIEditDataGrid grid, ChangedData data) {
        if (grid.getBindBean() != null) {
            applyBindBean(context, grid, data);
        } else {
            grid.setAddedData(data.addedData);
            grid.setModifiedData(data.modifiedData);
            grid.setRemovedData(data.removedData);
        }
    }

    private static void applyBindBean(FacesContext context, UIEditDataGrid grid,
            ChangedData data) {
        Object bindBean = grid.getBindBean();
        Class<?> bindBeanClass = null;
        Method facMethod = null;
        // find bind bean class or factory method
        if (bindBean instanceof String) {
            try {
                bindBeanClass = Utils.findClass((String)bindBean, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                try {
                    bindBeanClass = Utils.findClass((String)bindBean, AjaxEditDataGridRenderer.class.getClassLoader());
                } catch (ClassNotFoundException e1) {
                    throw new FacesException(e1);
                }
            }
        } 
        else if(bindBean instanceof Class) {
            bindBeanClass = (Class<?>)bindBean;
        } 
        else {
            try {
                facMethod = bindBean.getClass().getMethod("createBean", new Class[]{});
            } catch (Exception e) {
                bindBeanClass = bindBean.getClass();
            }
        }
        // populate bean
        if (facMethod != null) {
            populateBindBean(context, grid, data, bindBean, facMethod);
        } 
        else if (bindBeanClass != null) {
            populateBindBean(context, grid, data, bindBeanClass);
        } 
        else {
            throw new FacesException("could find bean class or method named 'createBean' from bindBean attribute");
        }
        
        
    }


    private static void populateBindBean(FacesContext context, UIEditDataGrid grid,
            ChangedData changedData, Class<?> bindBeanClass) {
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(bindBeanClass);
        } catch (IntrospectionException e) {
            throw new FacesException("could not get bind bean info", e);
        }
        
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        HashMap<String, PropertyDescriptor> pdMap = new HashMap<String, PropertyDescriptor>(pds.length, 1.0f);
        for (PropertyDescriptor aPd : pds) {
            pdMap.put(aPd.getName(), aPd);
        }
        
        int length = 0;
        Object beans = null;
        
        // added
        length = changedData.addedData.size();
        if (length > 0) {
            beans = createBeanArrayFromClass(bindBeanClass, length);
            populateData(context, grid, changedData.addedData, beans, pdMap);
            grid.setAddedData(beans);
        }

        // modified
        length = changedData.modifiedData.size();
        if (length > 0) {
            beans = createBeanArrayFromClass(bindBeanClass, length);
            populateData(context, grid, changedData.modifiedData, beans, pdMap);
            grid.setModifiedData(beans);
        }

        //removed
        length = changedData.removedData.size();
        if (length > 0) {
            beans = createBeanArrayFromClass(bindBeanClass, length);
            populateData(context, grid, changedData.removedData, beans, pdMap);
            grid.setRemovedData(beans);
        }

    }
    
    private static void populateBindBean(FacesContext context, UIEditDataGrid grid,
            ChangedData changedData, Object bindBean, Method facMethod) {
        Object bean = null;
        try {
            bean = facMethod.invoke(bindBean, new Object[]{});
        } catch (Exception e) {
            throw new FacesException("could not create bean instance", e);
        }
        
        Class bindBeanClass = bean.getClass();
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(bindBeanClass);
        } catch (IntrospectionException e) {
            throw new FacesException("could not get bind bean info", e);
        }
        
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        HashMap<String, PropertyDescriptor> pdMap = new HashMap<String, PropertyDescriptor>(pds.length, 1.0f);
        for (PropertyDescriptor aPd : pds) {
            pdMap.put(aPd.getName(), aPd);
        }

        int length = 0;
        Object beans = null;
        
        // added
        length = changedData.addedData.size();
        if (length > 0) {
            beans = createBeanArrayFromFac(bindBeanClass, bindBean, facMethod, length);
            populateData(context, grid, changedData.addedData, beans, pdMap);
            grid.setAddedData(beans);
        }

        // modified
        length = changedData.modifiedData.size();
        if (length > 0) {
            beans = createBeanArrayFromFac(bindBeanClass, bindBean, facMethod, length);
            populateData(context, grid, changedData.modifiedData, beans, pdMap);
            grid.setModifiedData(beans);
        }

        //removed
        length = changedData.removedData.size();
        if (length > 0) {
            beans = createBeanArrayFromFac(bindBeanClass, bindBean, facMethod, length);
            populateData(context, grid, changedData.removedData, beans, pdMap);
            grid.setRemovedData(beans);
        }
    }
    
    private static Object createBeanArrayFromClass(Class<?> bindBeanClass, int length) {
        Object beans = Array.newInstance(bindBeanClass, length);
        Object bean = null;
        for (int i = 0; i < length; i++) {
            try {
                bean = bindBeanClass.newInstance();
            } catch (Exception e) {
                throw new FacesException("could not create bean instance", e);
            }
            Array.set(beans, i, bean);
        }
        return beans;
    }
    
    private static Object createBeanArrayFromFac(Class bindBeanClass, Object bindBean, Method facMethod, int length) {
        Object beans = Array.newInstance(bindBeanClass, length);
        for (int i = 0; i < length; i++) {
            Object bean = null;
            try {
                bean = facMethod.invoke(bindBean, new Object[]{});
            } catch (Exception e) {
                throw new FacesException("could not create bean instance", e);
            }
            Array.set(beans, i, bean);
        }
        return beans;
    }
    
    private static void populateData(FacesContext context, UIEditDataGrid grid,
            JSONArray dataArray, Object beans, HashMap<String, PropertyDescriptor> pdMap) {
        int length = dataArray.size();
        for (int i = 0; i < length; i++) {
            JSONObject data = (JSONObject) dataArray.get(i);
            ExpressionFactory ef = context.getApplication().getExpressionFactory();
            ELContext elContext = context.getELContext();
            Map<String, Object> request = context.getExternalContext().getRequestMap();
            String beanDataKey = "_beanData";
            Object bean = Array.get(beans, i);
            request.put(beanDataKey, bean);
            for (Object key : data.keySet()) {
                Object value = data.get(key);
                String propName = key.toString();
                String expression = "#{".concat(beanDataKey).concat(".").concat(propName).concat("}");
                PropertyDescriptor pd = pdMap.get(propName);
                if (pd == null) {
                    UIOutputColumn column = DataRendererHelper.getOutputColumnById(grid, key.toString());
                    String var = grid.getVar();
                    if (column != null && value != null && var != null) {
                        ValueExpression ve = column.getValueExpression("value");
                        if (ve != null) {
                            value = FacesUtils.getObjectValue(context, column, value.toString());
                            request.put(var, bean);
                            ve.setValue(elContext, value);
                            request.remove(var);
                        }
                    }
                } else {
                    UIOutputColumn column = DataRendererHelper.getOutputColumnById(grid, key.toString());
                    if (column != null && value != null) {
                        value = FacesUtils.getObjectValue(context, column, value.toString());
                    }
                    value = Coercion.coerce(value, pd.getPropertyType());
                    ValueExpression ve = ef.createValueExpression(elContext, expression, pd.getPropertyType());
                    ve.setValue(elContext, value);
                }
            }
            request.remove(beanDataKey);
        }

    }
    
    static class ChangedData 
    {
        JSONArray addedData = new JSONArray();
        JSONArray modifiedData = new JSONArray();
        JSONArray removedData = new JSONArray();
        public ChangedData(JSONArray modifiedData, JSONArray removedData) {
            if (modifiedData != null) {
                calcModifiedData(modifiedData);
            }
            if (removedData != null) {
                this.removedData = removedData;
            }
        }
        
        @SuppressWarnings("unchecked")
        private void calcModifiedData(JSONArray modifiedData) {
            for (Object data : modifiedData) {
                Object rowIndex = ((JSONObject)data).get(UIDataGrid.SERVER_ROW_INDEX);
                if (rowIndex == null) {
                    this.addedData.add(data);
                }
                else {
                    this.modifiedData.add(data);
                }
            }
        }
    }

}
