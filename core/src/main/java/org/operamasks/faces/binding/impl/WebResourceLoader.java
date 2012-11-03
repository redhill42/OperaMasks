/*
 * $Id: WebResourceLoader.java,v 1.2 2007/12/22 13:35:40 daniel Exp $
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

import java.net.URL;
import java.net.MalformedURLException;
import javax.faces.context.FacesContext;
import org.operamasks.faces.application.ApplicationAssociate;

final class WebResourceLoader extends ClassLoader
{
    public static WebResourceLoader getInstance() {
        return ApplicationAssociate.getInstance().getSingleton(WebResourceLoader.class);
    }

    private WebResourceLoader() {}

    protected URL findResource(String name) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            return ctx.getExternalContext().getResource(name);
        } catch (MalformedURLException ex) {
            return null;
        }
    }
}
