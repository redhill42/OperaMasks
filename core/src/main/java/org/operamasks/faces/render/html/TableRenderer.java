/*
 * $Id: TableRenderer.java,v 1.4 2007/07/02 07:37:47 jacky Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TableRenderer extends HtmlRenderer
{
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIData data = (UIData)component;
        data.setRowIndex(-1);

        ResponseWriter out = context.getResponseWriter();
        out.startElement("table", component);
        writeIdAttributeIfNecessary(context, out, component);
        renderPassThruAttributes(out, component, "rows");
        out.writeText("\n", null);

        renderCaption(context, component);
        renderHeader(context, component);
        renderFooter(context, component);
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        renderBody(context, component);
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIData data = (UIData)component;
        data.setRowIndex(-1);

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
        int columnHeaderCount = getFacetCount(component, "header");
        if ((header == null || !header.isRendered()) && columnHeaderCount == 0)
            return;

        ResponseWriter out = context.getResponseWriter();
        String headerClass = (String)component.getAttributes().get("headerClass");

        out.startElement("thead", component);
        out.writeText("\n", null);

        if (header != null && header.isRendered()) {
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
        }

        if (columnHeaderCount != 0) {
            out.startElement("tr", component);
            for (UIColumn column : getColumns(component)) {
                String columnHeaderClass = (String)column.getAttributes().get("headerClass");
                UIComponent columnHeader = column.getFacet("header");

                out.startElement("th", column);
                if (columnHeaderClass != null)
                    out.writeAttribute("class", columnHeaderClass, "columnHeaderClass");
                else if (headerClass != null)
                    out.writeAttribute("class", headerClass, "headerClass");
                out.writeAttribute("scope", "col", null);
                if (columnHeader != null && columnHeader.isRendered())
                    columnHeader.encodeAll(context);
                out.endElement("th");
            }
            out.endElement("tr");
            out.writeText("\n", null);
        }

        out.endElement("thead");
        out.writeText("\n", null);
    }

    private void renderFooter(FacesContext context, UIComponent component)
        throws IOException
    {
        UIComponent footer = component.getFacet("footer");
        int columnFooterCount = getFacetCount(component, "footer");
        if ((footer == null || !footer.isRendered()) && columnFooterCount == 0)
            return;

        ResponseWriter out = context.getResponseWriter();
        String footerClass = (String)component.getAttributes().get("footerClass");

        out.startElement("tfoot", component);
        out.writeText("\n", null);

        if (columnFooterCount != 0) {
            out.startElement("tr", component);
            for (UIColumn column : getColumns(component)) {
                String columnFooterClass = (String)column.getAttributes().get("footerClass");
                UIComponent columnFooter = column.getFacet("footer");

                out.startElement("td", column);
                if (columnFooterClass != null)
                    out.writeAttribute("class", columnFooterClass, "columnFooterClass");
                else if (footerClass != null)
                    out.writeAttribute("class", footerClass, "footerClass");
                if (columnFooter != null && columnFooter.isRendered())
                    columnFooter.encodeAll(context);
                out.endElement("td");
            }
            out.endElement("tr");
            out.writeText("\n", null);
        }

        if (footer != null && footer.isRendered()) {
            out.startElement("tr", footer);
            out.startElement("td", footer);
            if (footerClass != null)
                out.writeAttribute("class", footerClass, "footerClass");
            out.writeAttribute("colspan", getColumnCount(component), null);
            footer.encodeAll(context);
            out.endElement("td");
            out.endElement("tr");
            out.writeText("\n", null);
        }

        out.endElement("tfoot");
        out.writeText("\n", null);
    }

    private void renderBody(FacesContext context, UIComponent component)
        throws IOException
    {
        List<UIColumn> columns = getColumns(component);
        String[] rowClasses = getStyleClasses(component, "rowClasses");
        String[] columnClasses = getStyleClasses(component, "columnClasses");
        int rowStyles = rowClasses.length;
        int columnStyles = columnClasses.length;

        UIData data = (UIData)component;
        int rows = data.getRows();
        int rowIndex = data.getFirst();
        int rowStyle = 0;

        ResponseWriter out = context.getResponseWriter();

        out.startElement("tbody", component);
        out.writeText("\n", null);

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++) {
            data.setRowIndex(rowIndex++);
            if (!data.isRowAvailable())
                break;

            out.startElement("tr", component);
            if (rowStyles > 0) {
                out.writeAttribute("class", rowClasses[rowStyle++], "rowClasses");
                if (rowStyle >= rowStyles)
                    rowStyle = 0;
            }
            out.writeText("\n", null);

            int columnStyle = 0;
            for (UIColumn column : columns) {
                out.startElement("td", column);
                if (columnStyles > 0) {
                    out.writeAttribute("class", columnClasses[columnStyle++], "columnClasses");
                    if (columnStyle >= columnStyles)
                        columnStyle = 0;
                }

                for (UIComponent kid : column.getChildren()) {
                    if (kid.isRendered()) {
                        kid.encodeAll(context);
                    }
                }

                out.endElement("td");
                out.writeText("\n", null);
            }

            out.endElement("tr");
            out.writeText("\n", null);
        }

        out.endElement("tbody");
        out.writeText("\n", null);
        data.setRowIndex(-1);
    }

    private List<UIColumn> getColumns(UIComponent component) {
        List<UIColumn> result = new ArrayList<UIColumn>();
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIColumn) && kid.isRendered())
                result.add((UIColumn)kid);
        }
        return result;
    }

    private int getColumnCount(UIComponent component) {
        int n = 0;
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIColumn) && kid.isRendered())
                n++;
        }
        return n;
    }

    private int getFacetCount(UIComponent component, String name) {
        int n = 0;
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIColumn) && kid.isRendered()) {
                UIComponent facet = kid.getFacet(name);
                if (facet != null && facet.isRendered())
                    n++;
            }
        }
        return n;
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
}