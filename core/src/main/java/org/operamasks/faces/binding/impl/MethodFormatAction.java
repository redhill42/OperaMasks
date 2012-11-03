/*
 * $Id: MethodFormatAction.java,v 1.1 2007/12/17 23:24:12 daniel Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import org.operamasks.faces.binding.ModelBean;
import org.operamasks.el.eval.Coercion;

public class MethodFormatAction implements FormatAction
{
    private ModelBean target;
    private Method method;

    public MethodFormatAction(ModelBean target, Method method) {
        this.target = target;
        this.method = method;
    }

    public ModelBean getTarget() {
        return target;
    }

    public String format(FacesContext context, UIComponent component, Object value)
        throws Exception
    {
        target.inject(context);

        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 1) {
            // single argument method
            value = coerce(value, paramTypes[0]);
            return (String)target.invoke(method, value);
        } else if (paramTypes.length == 3) {
            // standard converter method
            value = coerce(value, paramTypes[2]);
            return (String)target.invoke(method, context, component, value);
        } else {
            // should not happen
            return (value == null) ? "" : value.toString();
        }
    }

    private static Object coerce(Object value, Class type) {
        if ((value != null && type != value.getClass()) ||
            (value == null && type.isPrimitive())) {
            value = Coercion.coerce(value, type);
        }
        return value;
    }
}
