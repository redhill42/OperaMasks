/*
 * $Id: FacesCompositeELResolver.java,v 1.4 2007/07/02 07:38:16 jacky Exp $
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

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.faces.context.FacesContext;

/**
 * This class is provided so that when a JSP is running in a non-JSF environment
 * then no JSF-related ELResolvers are contacted.
 */
public class FacesCompositeELResolver extends CompositeELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
        context.setPropertyResolved(false);
        if (FacesContext.getCurrentInstance() == null)
            return null;
        return super.getValue(context, base, property);
    }

    public Class getType(ELContext context, Object base, Object property)
        throws ELException
    {
        context.setPropertyResolved(false);
        if (FacesContext.getCurrentInstance() == null)
            return null;
        return super.getType(context, base, property);
    }

    public void setValue(ELContext context, Object base, Object property, Object val)
        throws ELException
    {
        context.setPropertyResolved(false);
        if (FacesContext.getCurrentInstance() == null)
            return;
        super.setValue(context, base, property, val);
    }

    public boolean isReadOnly(ELContext context, Object base, Object property)
        throws ELException
    {
        context.setPropertyResolved(false);
        if (FacesContext.getCurrentInstance() == null)
            return false;
        return super.isReadOnly(context, base, property);
    }
}
