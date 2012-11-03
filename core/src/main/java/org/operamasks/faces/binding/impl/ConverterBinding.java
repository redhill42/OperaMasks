/*
 * $Id: ConverterBinding.java,v 1.4 2007/12/17 23:24:12 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.UIData;
import javax.faces.convert.Converter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

final class ConverterBinding extends Binding
{
    private String[] ids;
    private Method convert;
    private Method format;

    ConverterBinding(String viewId, String[] ids) {
        super(viewId);
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }

    public Method getConvertMethod() {
        return convert;
    }

    public void setConvertMethod(Method method) {
        method.setAccessible(true);
        this.convert = BindingUtils.getInterfaceMethod(method);
    }

    public Method getFormatMethod() {
        return format;
    }

    public void setFormatMethod(Method method) {
        method.setAccessible(true);
        this.format = BindingUtils.getInterfaceMethod(method);
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean bean = mbc.getModelBean();

        for (String id : this.ids) {
            UIComponent comp = mbc.getComponent(id);
            if ((comp == null) || !(comp instanceof ValueHolder)) {
                continue;
            }

            ValueHolder vh = (ValueHolder)comp;
            Converter previous = vh.getConverter();

            ConverterAdapter adapter;
            if (previous == null) {
                adapter = new ConverterAdapter();
                vh.setConverter(adapter);
            } else if (previous instanceof ConverterAdapter) {
                adapter = (ConverterAdapter)previous;
            } else {
                continue;
            }

            if (this.convert != null)
                adapter.setConvertAction(new MethodConvertAction(bean, this.convert));
            if (this.format != null)
                adapter.setFormatAction(new MethodFormatAction(bean, this.format));
        }
    }
    
    public void applyDataItem(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        // Only static converter and formatter method is supported
        if ((this.convert == null || !Modifier.isStatic(this.convert.getModifiers())) &&
            (this.format == null || !Modifier.isStatic(this.format.getModifiers()))) {
            return;
        }

        for (String id : this.ids) {
            UIComponent comp = mbc.getComponent(id);
            if ((comp == null) || !(comp instanceof ValueHolder)) {
                continue;
            }

            ValueHolder vh = (ValueHolder)comp;
            Converter previous = vh.getConverter();

            ConverterAdapter adapter;
            if (previous == null) {
                adapter = new ConverterAdapter();
                vh.setConverter(adapter);
            } else if (previous instanceof ConverterAdapter) {
                adapter = (ConverterAdapter)previous;
            } else {
                continue;
            }

            if (this.convert != null && Modifier.isStatic(this.convert.getModifiers()))
                adapter.setConvertAction(new MethodConvertAction(ModelBean.NULL_MODEL_BEAN, this.convert));
            if (this.format != null && Modifier.isStatic(this.format.getModifiers()))
                adapter.setFormatAction(new MethodFormatAction(ModelBean.NULL_MODEL_BEAN, this.format));
        }
    }
}
