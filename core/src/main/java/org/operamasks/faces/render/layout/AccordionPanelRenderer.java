/*
 * $Id:
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
package org.operamasks.faces.render.layout;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.layout.AccordionPanel;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已废弃
 */
@Deprecated
public class AccordionPanelRenderer extends HtmlRenderer implements ResourceProvider
{
    private static final String ACCORDION_CSS = "/yuiext/css/accordion.css";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component.getParent() instanceof LayoutManager) {
            // encoded by parent layout
            return;
        }

        AccordionPanel panel = (AccordionPanel)component;
        String clientId = panel.getClientId(context);
        ResponseWriter out = context.getResponseWriter();

        // accordion div
        out.startElement("div", component);
        out.writeAttribute("id", clientId, "clientId");
        renderPassThruAttributes(out, component , "title");

        // title div
        out.startElement("div", component) ;
        out.writeText(panel.getTitle(), panel, "title");
        out.endElement("div");

        // content div
        out.startElement("div", component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (component.getParent() instanceof LayoutManager) {
            // encoded by parent layout
            return;
        }

        ResponseWriter out = context.getResponseWriter();

        // end of content div
        out.endElement("div");

        // end of accordion div
        out.endElement("div");
    }

    static final NameAndType[] PANEL_CONFIGS = {
        new NameAndType("animate", Boolean.class),
        new NameAndType("bodyClass", String.class),
        new NameAndType("collapsed", Boolean.class),
        new NameAndType("collapseOnUnpin", Boolean.class),
        new NameAndType("collapsible", Boolean.class),
        new NameAndType("draggable", Boolean.class),
        new NameAndType("duration", String.class),
        new NameAndType("easingCollapse", String.class),
        new NameAndType("easingExpand", String.class),
        new NameAndType("icon", String.class),
        new NameAndType("minWidth", String.class),
        new NameAndType("maxWidth", String.class),
        new NameAndType("minHeight", String.class),
        new NameAndType("maxHeight", String.class),
        new NameAndType("panelClass", String.class),
        new NameAndType("pinned", Boolean.class),
        new NameAndType("resizable", Boolean.class),
        new NameAndType("shadowMode", String.class),
        new NameAndType("showPin", Boolean.class),
        new NameAndType("trigger", String.class),
        new NameAndType("useShadow", Boolean.class),
        new NameAndType("autoScroll", Boolean.class)
    };

    public void provideResource(ResourceManager rm, UIComponent component) {
        if (component.getParent() instanceof LayoutManager) {
            // encoded by parent layout
            return;
        }

        YuiExtResource resource = YuiExtResource.register(rm, "Ext.ux.InfoPanel");
        rm.registerSkinCssResource(ACCORDION_CSS) ;

        AccordionPanel panel = (AccordionPanel)component;
        String clientId = panel.getClientId(FacesContext.getCurrentInstance());
        String jsvar = resource.allocVariable(panel);

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        fmt.format("%s = new Ext.ux.InfoPanel('%s',{", jsvar, clientId);
        for (NameAndType nat : PANEL_CONFIGS) {
            Object value = panel.getAttributes().get(nat.name);
            if (value != null) {
                if (value instanceof String) {
                    value = HtmlEncoder.enquote((String)value);
                }
                fmt.format("%s:%s,", nat.name, value);
            }
        }
        if (buf.charAt(buf.length()-1) == ',') {
            buf.setLength(buf.length()-1);
        }
        buf.append("});\n");

        resource.releaseVariable(jsvar);
        resource.addInitScript(buf.toString());
    }
}
