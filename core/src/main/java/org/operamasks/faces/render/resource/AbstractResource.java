/*
 * $Id: AbstractResource.java,v 1.4 2007/07/02 07:38:04 jacky Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * {@link Resource}接口的抽象实现，具体的实现类可以扩展该类而不必重写所有方法。
 */
public abstract class AbstractResource implements Resource
{
    private String id;

    protected AbstractResource(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return NORMAL_PRIORITY;
    }

    public void encodeBegin(FacesContext context)
        throws IOException
    {
    }

    public void encodeEnd(FacesContext context)
        throws IOException
    {
    }

    public String getLoadScript(FacesContext context) {
        return null;
    }

    public String getUnloadScript(FacesContext context) {
        return null;
    }
}
