/*
 * $Id: ClosureFormatAction.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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
import javax.el.ELContext;

import elite.lang.Closure;
import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.binding.ModelBean;

public class ClosureFormatAction implements FormatAction
{
    private Closure closure;

    public ClosureFormatAction(Closure closure) {
        this.closure = closure;
    }

    public ModelBean getTarget() {
        return ModelBean.NULL_MODEL_BEAN;
    }

    public String format(FacesContext context, UIComponent component, Object value) {
        ELContext elctx = context.getELContext();
        switch (closure.arity(elctx)) {
        case 1:
            value = closure.call(elctx, value);
            break;
        case 2:
            value = closure.call(elctx, component, value);
            break;
        case 3:
            value = closure.call(elctx, context, component, value);
            break;
        }
        return Coercion.coerceToString(value);
    }
}
