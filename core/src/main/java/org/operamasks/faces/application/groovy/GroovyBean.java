/*
 * $Id: GroovyBean.java,v 1.6 2008/01/31 04:12:24 daniel Exp $
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

package org.operamasks.faces.application.groovy;

import groovy.lang.GroovyObject;
import groovy.lang.MissingMethodException;
import javax.el.MethodNotFoundException;
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.el.eval.ELEngine;
import org.operamasks.faces.binding.impl.DefaultModelBean;

class GroovyBean extends DefaultModelBean
{
    public GroovyBean(GroovyObject target) {
        super(target);
    }

    @Override
    public Object invoke(ELContext context, String name, Closure[] args) {
        try {
            return super.invoke(context, name, args);
        } catch (MethodNotFoundException ex) {
            return invokeDynamic(context, name, args);
        }
    }

    private Object invokeDynamic(ELContext ctx, String name, Closure[] args) {
        Object target = null;
        Object result = null;
        Throwable exception = null;

        try {
            target = preInvoke(null);
            result = ((GroovyObject)target).invokeMethod(name, ELEngine.getArgValues(ctx, args));
        } catch (Throwable t) {
            exception = t;
        }

        exception = postInvoke(target, exception);
        if (exception != null) {
            if (exception instanceof MissingMethodException) {
                throw new MethodNotFoundException(exception);
            } else {
                rethrowUnchecked(exception);
            }
        }

        return result;
    }
}
