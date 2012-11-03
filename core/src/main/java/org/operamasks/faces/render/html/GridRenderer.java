/*
 * $Id: GridRenderer.java,v 1.6 2007/07/02 07:37:48 jacky Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

import org.operamasks.faces.component.html.HtmlGridCell;

public class GridRenderer extends HtmlRenderer
{
    @Override public boolean getRendersChildren() {
        return true;
    }

    @Override public void encodeBegin(FacesContext context, UIComponent component)
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
        int rowStyleIndex = 0;
        int columnStyleIndex = 0;
        int columnIndex = 0;
        int[] rowSpans = new int[columns];
        boolean open = false;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("tbody", component);
        out.write("\n");

        for (UIComponent kid : component.getChildren()) {
            if (!kid.isRendered())
                continue;

            // Start a new row.
            if (columnIndex == 0) {
                if (open) {
                    out.endElement("tr");
                    out.write("\n");
                }

                out.startElement("tr", component);
                if (rowStyles > 0) {
                    out.writeAttribute("class", rowClasses[rowStyleIndex], "rowClasses");
                    if (++rowStyleIndex >= rowStyles)
                        rowStyleIndex = 0;
                }
                columnStyleIndex = 0;
                open = true;
            }

            // Find a cell that not spanned by previous row.
            boolean foundCell = false;
            for (int i = columnIndex; i < columns; i++) {
                if (rowSpans[i] > 0) {
                    rowSpans[i]--;
                    continue;
                } else {
                    columnIndex = i;
                    foundCell = true;
                    break;
                }
            }

            // If no cell can put into current row then start a new row.
            if (!foundCell) {
                out.endElement("tr");
                out.write("\n");
                out.startElement("tr", component);
                if (rowStyles > 0) {
                    out.writeAttribute("class", rowClasses[rowStyleIndex], "rowClasses");
                    if (++rowStyleIndex >= rowStyles)
                        rowStyleIndex = 0;
                }
                for (int i = 0; i < columns; i++) {
                    rowSpans[i] = 0;
                }
                columnIndex = 0;
                columnStyleIndex = 0;
            }

            // Encode the cell.
            if (kid instanceof HtmlGridCell) {
                String styleClass = (String)kid.getAttributes().get("styleClass");
                if (styleClass == null && columnStyles > 0) {
                    styleClass = columnClasses[columnStyleIndex];
                }
                out.startElement("td", kid);
                if (styleClass != null) {
                    out.writeAttribute("class", styleClass, "styleClass");
                }
                renderPassThruAttributes(out, kid, "styleClass");
                kid.encodeAll(context);
                out.endElement("td");
                out.write("\n");

                int rowspan = ((HtmlGridCell)kid).getRowspan();
                int colspan = ((HtmlGridCell)kid).getColspan();

                if (rowspan == 0) {
                    // The value zero ("0") means that the cell spans all rows
                    // from the current row to the last row of the table section
                    // in which the cell is defined.
                    rowspan = Integer.MAX_VALUE;
                } else if (rowspan > 1) {
                    rowspan--;
                } else {
                    rowspan = 0;
                }

                if (colspan == 0) {
                    // The value zero ("0") means that the cell spans all columns
                    // from the current column to the last column of the column
                    // group in which the cell is defined.
                    colspan = columns - columnIndex;
                } else if (colspan > 1) {
                    if (columnIndex + colspan > columns) {
                        colspan = columns - columnIndex;
                    }
                } else {
                    colspan = 1;
                }

                for (int i = columnIndex; i < columnIndex + colspan; i++) {
                    rowSpans[i] = rowspan;
                }
                columnIndex += colspan;
                columnStyleIndex += colspan;
            } else {
                out.startElement("td", component);
                if (columnStyles > 0) {
                    out.writeAttribute("class", columnClasses[columnStyleIndex], "columnClasses");
                }
                kid.encodeAll(context);
                out.endElement("td");
                out.write("\n");

                columnIndex++;
                columnStyleIndex++;
            }

            if (columnIndex >= columns)
                columnIndex = 0;
            if (columnStyleIndex >= columnStyles)
                columnStyleIndex = 0;
        }

        if (open) {
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
            int childCount = 0;
            for (UIComponent kid : component.getChildren()) {
                if (kid.isRendered())
                    childCount++;
            }
            return childCount;
        } else {
            return count;
        }
    }
}
