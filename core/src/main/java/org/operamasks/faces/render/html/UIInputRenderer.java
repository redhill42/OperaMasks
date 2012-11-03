/*
 * $Id: UIInputRenderer.java,v 1.4 2007/07/02 07:37:48 jacky Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.Converter;
import javax.el.ValueExpression;
import java.util.Map;
import static org.operamasks.resources.Resources.*;

public abstract class UIInputRenderer extends UIOutputRenderer
{
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

    @Override protected String getCurrentValue(FacesContext context, UIComponent component) {
        if (component instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder)component).getSubmittedValue();
            if (submittedValue != null)
                return (String)submittedValue;
        }
        return super.getCurrentValue(context, component);
    }

    public void setSubmittedValue(UIComponent component, Object newValue) {
        if (component instanceof EditableValueHolder) {
            ((EditableValueHolder)component).setSubmittedValue(newValue);
        }
    }

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
