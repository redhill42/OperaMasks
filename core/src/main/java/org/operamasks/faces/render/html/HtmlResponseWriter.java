/*
 * $Id: HtmlResponseWriter.java,v 1.6 2007/07/02 07:37:45 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ResponseWriter;
import org.operamasks.faces.component.html.HtmlPage;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;

import java.io.Writer;
import java.io.IOException;

public class HtmlResponseWriter extends ResponseWriter
{
    protected Writer out;
    private String contentType;
    private String encoding;

    private boolean dontEscape;
    private int state = TEXT;

    private static final int START_ELEMENT = 0;
    private static final int END_ELEMENT   = 1;
    private static final int TEXT          = 2;

    public HtmlResponseWriter(Writer out, String contentType, String encoding) {
        this.out = out;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharacterEncoding() {
        return encoding;
    }

    public void startDocument() throws IOException {
        // find the HtmlPage component in the view tree, if it's found then
        // the HtmlPage component is responsible to render the page begin
        // elements, otherwise,  must render page begin elements now.
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = context.getViewRoot();
        HtmlPage page = FacesUtils.getHtmlPage(viewRoot);
        if (page == null || !page.isRendered()) {
            HtmlPageRenderer renderer = getHtmlPageRenderer(context);
            if (renderer != null) {
                renderer.encodePageBegin(context, viewRoot);
            }
        }
    }

    public void endDocument() throws IOException {
        closeStart();

        // find the HtmlPage component in the view tree, if it's found then
        // the HtmlPage component is responsible to render the page end
        // elements, otherwise, must render page end elements now.
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = context.getViewRoot();
        HtmlPage page = FacesUtils.getHtmlPage(viewRoot);
        if (page == null || !page.isRendered()) {
            HtmlPageRenderer renderer = getHtmlPageRenderer(context);
            if (renderer != null) {
                renderer.encodePageEnd(context, viewRoot);
            }
        }
    }

    private HtmlPageRenderer getHtmlPageRenderer(FacesContext context) {
        return (HtmlPageRenderer)context.getRenderKit().getRenderer(
            "org.operamasks.faces.HtmlDocument", "org.operamasks.faces.HtmlPage");
    }

    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Write the start tag of an element.
     */
    public void startElement(String name, UIComponent component)
        throws IOException
    {
        if (name == null) {
            throw new NullPointerException();
        }
        if (state == START_ELEMENT) {
            out.write('>');
        }
        out.write('<');
        out.write(name);
        state = START_ELEMENT;

        if (name.equalsIgnoreCase("script") || name.equalsIgnoreCase("style")) {
            dontEscape = true;
        }
    }

    /**
     * Write the end tag of an element.
     */
    public void endElement(String name)
        throws IOException
    {
        if (name == null) {
            throw new NullPointerException();
        }
        if (state == START_ELEMENT) {
            if (HtmlRenderer.isEmptyElement(name)) {
                out.write("/>");
            } else {
                out.write("></");
                out.write(name);
                out.write(">");
            }
        } else {
            out.write("</");
            out.write(name);
            out.write('>');
        }
        state = END_ELEMENT;
        dontEscape = false;
    }

    public void writeAttribute(String name, Object value, String property)
        throws IOException
    {
        if (name == null)
            throw new NullPointerException();
        if (state != START_ELEMENT)
            throw new IllegalStateException("No current open element.");

        if (value instanceof Boolean) {
            // The boolean attributes that appearence in the start tag an element
            // implies that the value of the attribute is "true". Their absence
            // implies a value of "false".
            if (((Boolean)value).booleanValue() == true) {
                out.write(' ');
                out.write(name);
                out.write("=\"");
                out.write(name);
                out.write('"');
            }
        } else {
            if (value == null)
                value = "";
            out.write(' ');
            out.write(name);
            out.write("=\"");
            HtmlEncoder.encode(out, value.toString());
            out.write('"');
        }
    }

    public void writeURIAttribute(String name, Object value, String property)
        throws IOException
    {
        if (name == null)
            throw new NullPointerException();
        if (state != START_ELEMENT)
            throw new IllegalStateException("No current open element.");

        String valueStr = (value == null) ? "" : value.toString();
        out.write(' ');
        out.write(name);
        out.write("=\"");
        HtmlEncoder.encodeURI(out, valueStr, encoding);
        out.write('"');
    }

    /**
     * Writes out the comment.
     */
    public void writeComment(Object data)
        throws IOException
    {
        closeStart();

        out.write("<!--");
        if (data != null) {
            String comment = data.toString();
            int length = comment.length();
            boolean sawDash = false;

            // "--" illegal in comments, expand it
            for (int i = 0; i < length; i++) {
                char c = comment.charAt(i);
                if (c == '-') {
                    if (sawDash) {
                        out.write(' ');
                    } else {
                        sawDash = true;
                    }
                } else {
                    sawDash = false;
                }
                out.write(c);
            }
            if (sawDash)
                out.write(' ');
        }
        out.write("-->");
    }

    private void closeStart() throws IOException {
        if (state == START_ELEMENT) {
            out.write('>');
            state = TEXT;
        }
    }

    /**
     * Writes the text, escaping HTML metacharacters as needed.
     */
    public void writeText(Object textObj, String property)
        throws IOException
    {
        closeStart();

        String text = (textObj == null) ? "" : textObj.toString();
        if (dontEscape) {
            out.write(text);
        } else {
            HtmlEncoder.encode(out, text);
        }
    }

    public void writeText(char text[])
        throws IOException
    {
        writeText(text, 0, text.length);
    }

    public void writeText(char text[], int off, int len)
        throws IOException
    {
        closeStart();

        if (dontEscape) {
            out.write(text, off, len);
        } else {
            HtmlEncoder.encode(out, text, off, len);
        }
    }

    public ResponseWriter cloneWithWriter(Writer writer) {
        return new HtmlResponseWriter(writer, getContentType(), getCharacterEncoding());
    }

    public void close()
        throws IOException
    {
        closeStart();
        out.close();
    }

    public void write(char cbuf[], int off, int len)
        throws IOException
    {
        closeStart();
        out.write(cbuf, off, len);
    }

    public void write(int c)
        throws IOException
    {
        closeStart();
        out.write(c);
    }

    public void write(String str)
        throws IOException
    {
        closeStart();
        out.write(str);
    }

    public void write(String str, int off, int len)
        throws IOException
    {
        closeStart();
        out.write(str, off, len);
    }
}
