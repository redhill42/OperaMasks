/*
 * $Id: HtmlPageRenderer.java,v 1.23 2008/04/22 14:35:19 jacky Exp $
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

import java.io.IOException;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;

public class HtmlPageRenderer extends HtmlRenderer
{
    private static final String HTML_STRICT =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" " +
        "\"http://www.w3.org/TR/html4/strict.dtd\">";
    private static final String HTML_TRANSITIONAL =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" " +
        "\"http://www.w3.org/TR/html4/loose.dtd\">";
    private static final String HTML_FRAMESET =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" " +
        "\"http://www.w3.org/TR/html4/frameset.dtd\">";

    private static final String XHTML_STRICT =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
    private static final String XHTML_TRANSITIONAL =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String XHTML_FRAMESET =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" " +
        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">";
    
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        boolean isXHTML = fixupContentType(context);

        String lang = (String)component.getAttributes().get("lang");
        if (lang == null) {
            Locale locale = context.getViewRoot().getLocale();
            lang = locale.getLanguage().replace('_', '-');
        }
        String dir = (String)component.getAttributes().get("dir");
        String title = (String)component.getAttributes().get("title");

        ResponseWriter out = context.getResponseWriter();
        writeDocType(out, component, isXHTML);
        out.startElement("html", component);
        if (isXHTML)
            out.writeAttribute("xmlns", "http://www.w3.org/1999/xhtml", null);
        if (lang != null)
            out.writeAttribute("lang", lang, "lang");
        if (isXHTML && lang != null)
            out.writeAttribute("xml:lang", lang, "lang");
        if (dir != null)
            out.writeAttribute("dir", dir, "dir");
        out.write("\n");

        out.startElement("head", component);
        out.write("\n");
        if (title != null) {
            out.startElement("title", component);
            out.writeText(title, "title");
            out.endElement("title");
            out.write("\n");
        }
        encodePageBegin(context, component);
        out.endElement("head");
        out.write("\n");

        encodeBodyBegin(context, component);
    }

    private boolean fixupContentType(FacesContext context) {
        boolean isXHTML = false;

        String contentType = context.getResponseWriter().getContentType();
        if (contentType != null) {
            isXHTML = contentType.equals("text/xml") ||
                      contentType.equals("application/xhtml+xml") ||
                      contentType.equals("application/xml");
        }

        if (isXHTML && !isAjaxResponse(context)) {
            // must set content type to "text/html" for IE to render XHTML document
            Object o = context.getExternalContext().getResponse();
            if (o instanceof HttpServletResponse) {
                try {
                    ((HttpServletResponse)o).setContentType("text/html");
                } catch (Exception ex) {/*ignored*/}
            }
        }

        return isXHTML;
    }

    private void writeDocType(ResponseWriter out, UIComponent component, boolean isXHTML)
        throws IOException
    {
        String doctype = (String)component.getAttributes().get("doctype");

        if (isXHTML) {
            if (doctype == null) {
                doctype = XHTML_STRICT;
            } else if (doctype.equals("strict")) {
                doctype = XHTML_STRICT;
            } else if (doctype.equals("transitional") || doctype.equals("loose")) {
                doctype = XHTML_TRANSITIONAL;
            } else if (doctype.equals("frameset")) {
                doctype = XHTML_FRAMESET;
            }
        } else {
            if (doctype == null) {
                doctype = HTML_TRANSITIONAL;
            } else if (doctype.equals("strict")) {
                doctype = HTML_STRICT;
            } else if (doctype.equals("transitional") || doctype.equals("loose")) {
                doctype = HTML_TRANSITIONAL;
            } else if (doctype.equals("frameset")) {
                doctype = HTML_FRAMESET;
            }
        }

        out.write(doctype);
        out.write("\n");
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        encodePageEnd(context, component);
        encodeBodyEnd(context, component);
    }

    /**
     * Subclasses can override this method to customize body encoding.
     */
    protected void encodeBodyBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        String onload = getOnload(context, component);
        String onunload = getOnunload(context, component);

        ResponseWriter out = context.getResponseWriter();
        out.startElement("body", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (onload != null)
            out.writeAttribute("onload", onload, "onunload");
        if (onunload != null)
            out.writeAttribute("onunload", onunload, "onunload");
        renderPassThruAttributes(out, component, "lang,dir,title,onload,onunload");
        out.write("\n");
    }

    protected String getOnload(FacesContext context, UIComponent component) {
        String resScript = getResourceManager(context).getLoadScript(context);
        String userScript = (String)component.getAttributes().get("onload");
        return joinScript(resScript, userScript);
    }

    protected String getOnunload(FacesContext context, UIComponent component) {
        String resScript = getResourceManager(context).getUnloadScript(context);
        String userScript = (String)component.getAttributes().get("onunload");
        return joinScript(resScript, userScript);
    }

    private String joinScript(String resScript, String userScript) {
        if (resScript != null && userScript != null) {
            if (!resScript.endsWith(";"))
                resScript += ";";
            return resScript + userScript;
        } else if (resScript != null) {
            return resScript;
        } else if (userScript != null) {
            return userScript;
        } else {
            return null;
        }
    }

    /**
     * Subclasses can override this method to customize body encoding.
     */
    protected void encodeBodyEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        out.endElement("body");
        out.endElement("html");
        out.write("\n");
    }

    public void encodePageBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        ResourceManager rm = getResourceManager(context);
        String url = rm.getResourceURL("ajax.js");
        // insert ajax javascript
        ResponseWriter out = context.getResponseWriter();
        out.write("<script type=\"text/javascript\" src=\"" + url + "\"></script>\n");
        if (!isAjaxResponse(context)) {
            rm.consumeContainerResources(context, context.getViewRoot());
            rm.consumeResources(context, context.getViewRoot());
            rm.consumeInitScriptByMeta(context, context.getViewRoot());
            //rm.consumeResourcesRenderer(context, context.getViewRoot());
            rm.encodeBegin(context);
        }
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.encodeBegin(context);
    }

    public void encodePageEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (!isAjaxResponse(context)) {
            getResourceManager(context).encodeEnd(context);
        }
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.encodeEnd(context);
        cm.reset();
    }

    private static ResourceManager getResourceManager(FacesContext context) {
        return ResourceManager.getInstance(context);
    }
}
