/*
 * $Id: VariableResolverAdapter.java,v 1.4 2007/07/02 07:38:16 jacky Exp $
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

package org.operamasks.faces.el;

import javax.faces.context.FacesContext;
import javax.el.ELResolver;

@SuppressWarnings("deprecation")
public class VariableResolverAdapter extends javax.faces.el.VariableResolver
{
    private ELResolver elResolver;

    public VariableResolverAdapter(ELResolver elResolver) {
        this.elResolver = elResolver;
    }

    public Object resolveVariable(FacesContext context, String name)
        throws javax.faces.el.EvaluationException
    {
        try {
            return elResolver.getValue(context.getELContext(), null, name);
        } catch (javax.el.ELException ex) {
            throw new javax.faces.el.EvaluationException(ex);
        }
    }
}
