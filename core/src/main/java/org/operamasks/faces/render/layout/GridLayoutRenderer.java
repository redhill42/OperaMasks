/*
 * $Id: GridLayoutRenderer.java,v 1.5 2007/07/02 07:38:13 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

import org.operamasks.faces.component.layout.GridLayout;
import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.render.html.HtmlRenderer;

public class GridLayoutRenderer extends HtmlRenderer
{
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("table", component);
        writeIdAttributeIfNecessary(context, out, component);
        renderPassThruAttributes(out, component);
        out.writeText("\n", null);

        renderCaption(context, component);
        renderHeader(context, component);
        renderFooter(context, component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        renderBody(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.endElement("table");
        out.writeText("\n", null);
    }

    private void renderCaption(FacesContext context, UIComponent component)
        throws IOException
    {
        UIComponent caption = component.getFacet("caption");
        if (caption == null || !caption.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String captionClass = (String)component.getAttributes().get("captionClass");
        String captionStyle = (String)component.getAttributes().get("captionStyle");

        out.startElement("caption", caption);
        if (captionClass != null)
            out.writeAttribute("class", captionClass, "captionClass");
        if (captionStyle != null)
            out.writeAttribute("style", captionStyle, "captionStyle");
        caption.encodeAll(context);
        out.endElement("caption");
    }

    private void renderHeader(FacesContext context, UIComponent component)
        throws IOException
    {
        UIComponent header = component.getFacet("header");
        if (header == null || !header.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String headerClass = (String)component.getAttributes().get("headerClass");

        out.startElement("thead", component);
        out.writeText("\n", null);
        out.startElement("tr", header);
        out.startElement("th", header);
        if (headerClass != null)
            out.writeAttribute("class", headerClass, "headerClass");
        out.writeAttribute("colspan", getColumnCount(component), null);
        out.writeAttribute("scope", "colgroup", null);
        header.encodeAll(context);
        out.endElement("th");
        out.endElement("tr");
        out.writeText("\n", null);
        out.endElement("thead");
        out.writeText("\n", null);
    }

    private void renderFooter(FacesContext context, UIComponent component)
        throws IOException
    {
        UIComponent footer = component.getFacet("footer");
        if (footer == null || !footer.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        String footerClass = (String)component.getAttributes().get("footerClass");

        out.startElement("tfoot", component);
        out.writeText("\n", null);
        out.startElement("tr", footer);
        out.startElement("td", footer);
        if (footerClass != null)
            out.writeAttribute("class", footerClass, "footerClass");
        out.writeAttribute("colspan", getColumnCount(component), null);
        footer.encodeAll(context);
        out.endElement("td");
        out.endElement("tr");
        out.writeText("\n", null);
        out.endElement("tfoot");
        out.writeText("\n", null);
    }

    private void renderBody(FacesContext context, UIComponent component)
        throws IOException
    {
        int columns = getColumnCount(component);
        String[] rowClasses = getStyleClasses(component, "rowClasses");
        String[] columnClasses = getStyleClasses(component, "columnClasses");
        int rowStyles = rowClasses.length;
        int columnStyles = columnClasses.length;

        int rowStyle = 0;
        int columnStyle = 0;
        int cellidx = 0;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("tbody", component);
        out.writeText("\n", null);

        GridLayout layout = (GridLayout)component;
        if (layout.getFacelets().size() > 0) {
            for (Facelet facelet : layout.getFacelets()) {
                if (cellidx % columns == 0) {
                    if (cellidx != 0) {
                        // close previous "<tr>"
                        out.endElement("tr");
                        out.writeText("\n", null);
                    }

                    out.startElement("tr", component);
                    if (rowStyles > 0) {
                        out.writeAttribute("class", rowClasses[rowStyle++], "rowClasses");
                        if (rowStyle >= rowStyles)
                            rowStyle = 0;
                    }
                    columnStyle = 0;
                }

                out.startElement("td", component);
                if (columnStyles > 0) {
                    out.writeAttribute("class", columnClasses[columnStyle++], "columnClasses");
                    if (columnStyle >= columnStyles)
                        columnStyle = 0;
                }
                facelet.encodeAll(context);
                out.endElement("td");
                out.writeText("\n", null);
                cellidx++;
            }

            // fill with empty cells
            while (cellidx % columns != 0) {
                out.startElement("td", null);
                if (columnStyles > 0) {
                    out.writeAttribute("class", columnClasses[columnStyle++], null);
                    if (columnStyle >= columnStyles)
                        columnStyle = 0;
                }
                out.endElement("td");
                cellidx++;
            }

            // close last "<tr>"
            out.endElement("tr");
            out.writeText("\n", null);
        }

        out.endElement("tbody");
        out.writeText("\n", null);
    }


    private String[] getStyleClasses(UIComponent component, String name) {
        String values = (String)component.getAttributes().get(name);
        if (values == null)
            return new String[0];
        String[] result = values.split(",");
        for (int i = 0; i < result.length; i++)
            result[i] = result[i].trim();
        return result;
    }

    private int getColumnCount(UIComponent component) {
        Integer count = (Integer)component.getAttributes().get("columns");
        if (count == null) {
            return 2;
        } else if (count < 1) {
            return 1;
        } else {
            return count;
        }
    }
}
