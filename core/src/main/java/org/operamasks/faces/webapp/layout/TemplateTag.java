/*
 * $Id: TemplateTag.java,v 1.6 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.webapp.layout;

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;

import org.operamasks.faces.component.layout.TemplateLayout;
import org.operamasks.faces.component.layout.UITemplateContainer;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.layout.LayoutContext;

/**
 * @jsp.tag name="template" body-content="JSP" dynamic-attributes="true"
 * @jsp.attribute name="id" required="false" rtexprvalue="true"
 * @jsp.attribute name="rendered" required="false" type="boolean"
 * @jsp.attribute name="binding" required="false" type="javax.faces.component.UIComponent"
 */
public class TemplateTag extends UIComponentELTag
    implements DynamicAttributes
{
    protected ValueExpression src;
    private Map<String,Object> attributes = new HashMap<String,Object>();

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return TemplateLayout.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setSrc(ValueExpression src) {
        this.src = src;
    }

    public void setDynamicAttribute(String uri, String localName, Object value) {
        if (uri == null) {
            attributes.put(localName, value);
        }
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        for (Map.Entry<String,Object> e : attributes.entrySet()) {
            String name = e.getKey();
            Object value = e.getValue();
            if (value instanceof ValueExpression) {
                component.setValueExpression(name, ((ValueExpression)value));
            } else {
                component.getAttributes().put(name, value);
            }
        }
    }

    private boolean bodyRendered = false;

    /**
     * The internal tag handler class used to create UITemplateContainer component.
     */
    private static class TemplateContainerTag extends UIComponentELTag {
        public String getRendererType() {
            return null;
        }

        public String getComponentType() {
            return UITemplateContainer.COMPONENT_TYPE;
        }
    }

    public int doAfterBody() throws JspException {
        bodyRendered = true;

        if (src != null) {
            String uri = (String)src.getValue(getFacesContext().getELContext());
            String context = null;

            if (uri.startsWith("//")) {
                uri = uri.substring(1);
                context = findContextPath(uri);
                if (context == null) {
                    throw new JspTagException("Invalid context path: " + uri);
                }
                uri = uri.substring(context.length());
            }

            TemplateContainerTag tag = new TemplateContainerTag();
            tag.setPageContext(pageContext);
            tag.setParent(this);
            tag.setId("_tid_" + this.getComponentInstance().getId()); // ensure ID uniqueness
            tag.doStartTag();

            JspWriter out = pageContext.pushBody();
            tag.setBodyContent((BodyContent)out);

            // include "src" as template content
            tag.doInitBody();
            loadTemplate(uri, context);
            tag.doAfterBody();

            pageContext.popBody();
            tag.doEndTag();
            tag.release();
        }

        return super.doAfterBody();
    }

    private void loadTemplate(String uri, String context) throws JspException {
        LayoutManager layout = (LayoutManager)getComponentInstance();
        LayoutContext.pushLayoutContext(getFacesContext(), layout);

        try {
            if (context == null) {
                pageContext.include(uri, true);
            } else {
                ServletContext c = pageContext.getServletContext().getContext(context);
                RequestDispatcher rd = c.getRequestDispatcher(uri);
                TemplateResponseWrapper rw = new TemplateResponseWrapper(pageContext);
                rd.include(pageContext.getRequest(), rw);
            }
        } catch (IOException ex) {
            throw new JspException(ex);
        } catch (ServletException ex) {
            throw new JspException(ex);
        } finally {
            LayoutContext.popLayoutContext(getFacesContext());
        }
    }

    private String findContextPath(String uri) {
        ServletContext c = pageContext.getServletContext();
        String context = null;
        int start = 0;

        do {
            int slash = uri.indexOf('/', start);
            if (slash == -1)
                break;
            String path = uri.substring(0, slash);
            if (c.getContext(path) != null) {
                context = path;
            }
            start = slash + 1;
        } while (true);

        return context;
    }

    private static class TemplateResponseWrapper extends HttpServletResponseWrapper {
        // The PrintWriter writes all output to the JspWriter of the including page
        private PrintWriter writer;

        TemplateResponseWrapper(PageContext pc) throws IOException {
            super((HttpServletResponse)pc.getResponse());
            this.writer = new PrintWriter(pc.getOut());
        }

        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        public ServletOutputStream getOutputStream() {
            // FIXME
            throw new IllegalStateException("Writer used");
        }
    }
    
    public int doEndTag() throws JspException {
        if (!bodyRendered) {
            // fix for empty body
            BodyContent bodyContent = pageContext.pushBody();
            setBodyContent(bodyContent);
            doInitBody();
            doAfterBody();
            pageContext.popBody();
        }

        return super.doEndTag();
    }

    public void release() {
        super.release();
        src = null;
        attributes.clear();
        bodyRendered = false;
    }
}
