/*
 * $Id: ScriptResource.java,v 1.5 2007/07/02 07:38:04 jacky Exp $
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
 * 表示一个脚本资源的具体类。
 */
public class ScriptResource extends AbstractResource
{
    private String path;
    private int priority;

    /**
     * 根据脚本路径构造一个脚本资源。
     *
     * @param path 脚本的本地相对路径
     */
    public ScriptResource(String path) {
        this(path, NORMAL_PRIORITY);
    }

    /**
     * 根据脚本路径及优先级构造一个脚本资源。
     *
     * @param path 脚本的本地相对路径
     * @param priority 资源优先级
     */
    public ScriptResource(String path, int priority) {
        super("urn:script:" + path);
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
            ((AjaxResponseWriter)out).writeScript("OM.ajax.loadScript('" + path + "');\n");
        } else {
            out.startElement("script", null);
            out.writeAttribute("type", "text/javascript", null);
            out.writeAttribute("src", path, null);
            out.endElement("script");
            out.write("\n");
        }
    }
}
