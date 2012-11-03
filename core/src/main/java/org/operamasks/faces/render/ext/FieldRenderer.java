/*
 * $Id: FieldRenderer.java,v 1.2 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import static org.operamasks.resources.Resources.JSF_NO_SUCH_CONVERTER_TYPE;
import static org.operamasks.resources.Resources._T;

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.operamasks.faces.util.FacesUtils;

public abstract class FieldRenderer extends ComponentRenderer {
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String newValue = requestMap.get(clientId);
        setSubmittedValue(component, newValue);
    }

    protected String getCurrentValue(FacesContext context, UIComponent component) {
        if (component instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder)component).getSubmittedValue();
            if (submittedValue != null)
                return (String)submittedValue;
        }
        Object currentValue = getValue(component);
        if (currentValue != null)
            return getFormattedValue(context, component, currentValue);
        return null;
    }

    protected Object getValue(UIComponent component) {
        if (component instanceof ValueHolder)
            return ((ValueHolder)component).getValue();
        return null;
    }

    protected String getFormattedValue(FacesContext context, UIComponent component, Object currentValue)
        throws ConverterException
    {
        return FacesUtils.getFormattedValue(context, component, currentValue);
    }

    public void setSubmittedValue(UIComponent component, Object newValue) {
        if (component instanceof EditableValueHolder) {
            ((EditableValueHolder)component).setSubmittedValue(newValue);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        String newValue = (String)submittedValue;
        ValueExpression binding = component.getValueExpression("value");
        Converter converter = null;

        if (component instanceof ValueHolder)
            converter = ((ValueHolder)component).getConverter();

        if (converter == null) {
            if (binding == null)
                return newValue;

            Class valueType = binding.getType(context.getELContext());
            if (valueType == null || valueType == String.class || valueType == Object.class) {
                return newValue;
            } else {
                converter = context.getApplication().createConverter(valueType);
                if (converter == null) {
                    throw new ConverterException(_T(JSF_NO_SUCH_CONVERTER_TYPE, valueType.getName()));
                }
            }
        }

        return converter.getAsObject(context, component, newValue);
    }
}
