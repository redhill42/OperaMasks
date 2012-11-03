/*
 * $Id: ModelBindingInterceptor.java,v 1.2 2007/12/26 21:13:21 daniel Exp $
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

package org.operamasks.faces.binding.ejb;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.faces.context.FacesContext;
import org.operamasks.faces.binding.ModelBean;

public class ModelBindingInterceptor
{
    @AroundInvoke
    public Object intercept(InvocationContext inv)
        throws Exception
    {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (ctx != null) {
            Object target = inv.getTarget();
            ModelBean bean = ModelBean.wrap(target);
            Object result;

            bean.setContextVariables(ctx.getELContext(), target, inv.getParameters());

            bean.inject(ctx);
            result = inv.proceed();
            bean.outject(ctx);

            return result;
        } else {
            return inv.proceed();
        }
    }
}
