/*
 * $Id: AccordionLayoutRenderer.java,v 1.9 2008/03/11 03:21:00 lishaochuan Exp $
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

import static org.operamasks.faces.render.layout.LayoutUtils.getFaceletAttribute;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.layout.AccordionLayout;
import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已经被org.operamasks.faces.render.layout.AccordionLayoutRenderHandler代替
 */
@Deprecated
public class AccordionLayoutRenderer extends HtmlRenderer
    implements ResourceProvider
{
    private static final String ACCORDION_CSS = "/yuiext/css/accordion.css";

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter();

        // accordion layout container div
        out.startElement("div", component);
        out.writeAttribute("id", clientId, "clientId");
        renderPassThruAttributes(out, component);

        // accordion content div
        out.startElement("div", component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        // end of accordion content div
        out.endElement("div");

        // end of accordion layout container div
        out.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        AccordionLayout layout = (AccordionLayout)component;
        String clientId = component.getClientId(context);
        List<Facelet> facelets = layout.getFacelets();
        ResponseWriter out = context.getResponseWriter();

        // encode faclets
        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);
            UIComponent panel;
            String panelId;

            if (facelet instanceof UIComponent) {
                panel = (UIComponent)facelet;
                panelId = panel.getClientId(context);
            } else {
                panel = null;
                panelId = clientId + NamingContainer.SEPARATOR_CHAR + i;
            }

            // accordion panel container div
            out.startElement("div", panel);
            out.writeAttribute("id", panelId, "clientId");
            if (panel != null) {
                renderPassThruAttributes(out, panel, "title");
            }

            // accordion panel title div
            out.startElement("div", panel);
            out.writeText(getFaceletAttribute(facelet, "title", String.class), panel, "title");
            out.endElement("div");

            // accordion panel content div
            out.startElement("div", panel);
            facelet.encodeAll(context);
            out.endElement("div");

            // end of accordion panel container div
            out.endElement("div");
            out.write("\n");
        }
    }

    private static final String[] LAYOUT_CONFIGS = {
        "draggable", "fitHeight", "initialHeight", "desktop", "fitToFrame", "fitContainer"
    };
    
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.ux.Accordion", "Ext.ux.InfoPanel");
        rm.registerSkinCssResource(ACCORDION_CSS);

        FacesContext context = FacesContext.getCurrentInstance();
        AccordionLayout layout = (AccordionLayout)component;
        String clientId = layout.getClientId(context);
        List<Facelet> facelets = layout.getFacelets();

        // encode script
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        String jsvar = resource.allocVariable(layout);
        
        String containerId = layout.getContainer() ;
        

        containerId = containerId == null ? clientId : containerId ;
        
        // Create and configure Ext.ux.Accordion object
        fmt.format("%s=new Ext.ux.Accordion('%s',{", jsvar, containerId);
        for (String config : LAYOUT_CONFIGS) {
            Object value = layout.getAttributes().get(config);
            if (value != null) {
                if (value instanceof String) {
                    value = HtmlEncoder.enquote((String)value);
                }
                fmt.format("%s:%s,", config, value);
            }
        }
        if (buf.charAt(buf.length()-1) == ',') {
            buf.setLength(buf.length() - 1);
        }
        buf.append("});\n");

        // Create individual accordion panel and add them to the layout
        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);
            String id, var;

            if (facelet instanceof UIComponent) {
                id = ((UIComponent)facelet).getClientId(context);
                var = resource.allocVariable((UIComponent)facelet);
            } else {
                id = clientId + NamingContainer.SEPARATOR_CHAR + i;
                var = resource.allocTempVariable();
            }

            fmt.format("%s=new Ext.ux.InfoPanel('%s',{", var, id);
            for (NameAndType nat : AccordionPanelRenderer.PANEL_CONFIGS) {
                Object value = getFaceletAttribute(facelet, nat.name, nat.type);
                if (value != null) {
                    if (value instanceof String) {
                        value = HtmlEncoder.enquote((String)value);
                    }
                    fmt.format("%s:%s,", nat.name, value);
                }
            }
            if (buf.charAt(buf.length()-1) == ',') {
                buf.setLength(buf.length() - 1);
            }
            buf.append("});\n");

            fmt.format("%s.add(%s);\n", jsvar, var);
            resource.releaseVariable(var);
        }

        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
    }
}