/*
 * $Id: WidgetResource.java,v 1.3 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.render.resource.SkinDescriptor;
import org.operamasks.faces.render.resource.AbstractResource;

public class WidgetResource extends AbstractResource
{
    public static final String RESOURCE_ID = "urn:widget";

    public WidgetResource() {
        super(RESOURCE_ID);
    }

    @Override
    public void encodeBegin(FacesContext context)
        throws IOException
    {
        String skin = SkinManager.getCurrentSkin(context);

        ResourceManager manager = ResourceManager.getInstance(context);
        ResponseWriter out = context.getResponseWriter();

        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.writeAttribute("src", manager.getResourceURL("widget.js"), null);
        out.endElement("script");
        out.write("\n");

        // add ExplorerCanvas for Microsoft Internet Explorer
        String ua = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        if (ua != null) {
            ua = ua.toLowerCase();
            if (ua.indexOf("msie") != -1 && ua.indexOf("opera") == -1) {
                out.startElement("script", null);
                out.writeAttribute("type", "text/javascript", null);
                out.writeAttribute("src", manager.getResourceURL("excanvas.js"), null);
                out.endElement("script");
                out.write("\n");
            }
        }

        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.write("UIApplication.path='" + manager.getResourceURL("/") + "';");
        out.write("UIApplication.skinPath='" + manager.getSkinResourceURL(skin, "widget/") + "';");
        out.endElement("script");
        out.write("\n");

        // must import stylesheet before script that style rules may modified
        out.startElement("link", null);
        out.writeAttribute("rel", "stylesheet", null);
        out.writeAttribute("type", "text/css", null);
        out.writeAttribute("href", manager.getSkinResourceURL(skin, "widget/skin.css"), null);
        out.endElement("link");
        out.write("\n");

        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.writeAttribute("src", manager.getSkinResourceURL(skin, "widget/skin.js"), null);
        out.endElement("script");
        out.write("\n");

        // write extra files
        SkinDescriptor skinDesc = SkinManager.getInstance(context).getSkin(skin);
        if (skinDesc != null) {
            String extraFiles = skinDesc.getProperty("widget.files");
            if (extraFiles != null) {
                for (String file : extraFiles.split(",")) {
                    file = file.trim();
                    if (file.endsWith(".js")) {
                        out.startElement("script", null);
                        out.writeAttribute("type", "text/javascript", null);
                        out.writeAttribute("src", manager.getSkinResourceURL(skin, file), null);
                        out.endElement("script");
                        out.write("\n");
                    } else if (file.endsWith("css")) {
                        out.startElement("link", null);
                        out.writeAttribute("rel", "stylesheet", null);
                        out.writeAttribute("type", "text/css", null);
                        out.writeAttribute("href", manager.getSkinResourceURL(skin, file), null);
                        out.endElement("link");
                        out.write("\n");
                    }
                }
            }
        }
    }

    @Override
    public String getLoadScript(FacesContext context) {
        return "UIApplication.initialize();";
    }

    @Override
    public String getUnloadScript(FacesContext context) {
        return "UIApplication.cleanup();";
    }

    public static void register(ResourceManager manager) {
        if (!manager.isResourceRegistered(RESOURCE_ID)) {
            manager.registerResource(new WidgetResource());
        }
    }
}
