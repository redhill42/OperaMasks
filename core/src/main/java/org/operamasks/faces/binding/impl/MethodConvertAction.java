/*
 * $Id: MethodConvertAction.java,v 1.1 2007/12/17 23:24:12 daniel Exp $
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.operamasks.faces.binding.ModelBean;

public class MethodConvertAction implements ConvertAction
{
    private ModelBean target;
    private Method method;

    public MethodConvertAction(ModelBean target, Method method) {
        this.target = target;
        this.method = method;
    }

    public ModelBean getTarget() {
        return target;
    }

    public Object convert(FacesContext context, UIComponent component, String value)
        throws Exception
    {
        target.inject(context);

        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 1) {
            // single argument method
            return target.invoke(method, value);
        } else if (paramTypes.length == 3) {
            // standard converter method
            return target.invoke(method, context, component, value);
        } else {
            // should not happen
            return value;
        }
    }
}
