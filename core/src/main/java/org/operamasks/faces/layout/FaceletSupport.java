/*
 * $Id: FaceletSupport.java,v 1.3 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.layout;

import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.Serializable;

/**
 * The abstract base class used to simplify Facelet implementation.
 */
public abstract class FaceletSupport implements Facelet, Serializable
{
    private static final long serialVersionUID = 8093881131628434473L;

    protected String name;
    protected Object constraints;
    protected Map<String,Object> attributes = new HashMap<String,Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getConstraints() {
        return this.constraints;
    }

    public void setConstraints(Object constraints) {
        this.constraints = constraints;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract void encodeAll(FacesContext context)
        throws IOException;
}
