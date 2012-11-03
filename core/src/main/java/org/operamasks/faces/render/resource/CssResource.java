/*
 * $Id: CssResource.java,v 1.5 2007/07/02 07:38:04 jacky Exp $
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
import javax.faces.context.ResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import java.io.IOException;

/**
 * 表示一个CSS资源的具体类。
 */
public class CssResource extends AbstractResource
{
    private String path;
    private int priority;

    /**
     * 以指定的资源路径构造一个CSS资源。
     *
     * @param path 资源相对路径
     */
    public CssResource(String path) {
        this(path, NORMAL_PRIORITY);
    }

    /**
     * 以指定的资源路径及优先级构造一个CSS资源。
     *
     * @param path 资源相对路径
     * @param priority 资源优先级
     */
    public CssResource(String path, int priority) {
        super("urn:css:" + path);
        this.path = path;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void encodeBegin(FacesContext context)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)out).writeScript("OM.ajax.loadStylesheet('" + path + "');\n");
        } else {
            out.startElement("link", null);
            out.writeAttribute("rel", "stylesheet", null);
            out.writeAttribute("type", "text/css", null);
            out.writeAttribute("href", path, null);
            out.endElement("link");
            out.write("\n");
        }
    }
}
