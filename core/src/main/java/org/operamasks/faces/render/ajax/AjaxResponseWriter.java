/*
 * $Id: AjaxResponseWriter.java,v 1.16 2008/01/30 07:58:15 yangdong Exp $
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

package org.operamasks.faces.render.ajax;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.ajax.AjaxLogger;
import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxResponseWriter extends ResponseWriter
{
    // Flag to indicate a property is outjected
    public static final String OUTJECTED_KEY = "__outjected__";

    private Writer out;
    private String contentType;
    private String encoding;

    private static class Attribute {
        String       name;
        Object       value;
        String       property;
    }

    private static class Element {
        String          name;
        String          id;
        UIComponent     component;
        List<Attribute> attributes;
        StringBuffer    textBuf;
        boolean         dynamicText;

        public Element(String name, UIComponent component) {
            this.name = name;
            this.component = component;
        }

        public void addAttribute(String name, Object value, String property) {
            Attribute att = new Attribute();
            att.name = name;
            att.value = value;
            att.property = property;

            if (attributes == null)
                attributes = new ArrayList<Attribute>();
            attributes.add(att);
        }

        public void addText(String text, boolean dynamic) {
            if (textBuf == null)
                textBuf = new StringBuffer();
            textBuf.append(text);
            if (dynamic) dynamicText = true;
        }

        public void addText(char[] text, int off, int len) {
            if (textBuf == null)
                textBuf = new StringBuffer();
            textBuf.append(text, off, len);
        }

        public void addText(int c) {
            if (textBuf == null)
                textBuf = new StringBuffer();
            textBuf.append((char)c);
        }

        public List<Attribute> getAttributes() {
            if (attributes == null) {
                return Collections.emptyList();
            } else {
                return attributes;
            }
        }

        public String getText() {
            return (textBuf == null) ? "" : textBuf.toString();
        }
    }

    private Stack<Element> stack = new Stack<Element>();
    private Element current;
    private boolean viewStateChanged;
    private boolean writeFullState;
    private String actionScript;

    private static final String EMPTY_STRING = "''";

    AjaxResponseWriter(Writer out, String contentType, String encoding) {
        this.out = out;
        this.contentType = contentType;
        this.encoding = encoding;
        if (!FacesUtils.isTransientStateSupported(FacesContext.getCurrentInstance())) {
            this.writeFullState = true;
        }
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharacterEncoding() {
        return encoding;
    }

    public void setViewStateChanged() {
        this.viewStateChanged = true;
        if (!this.writeFullState && !FacesUtils.isMarkedForTransientState(FacesContext.getCurrentInstance())) {
            this.writeFullState = true;
        }
    }

    public void setViewStateChanged(boolean writeFullState) {
        this.viewStateChanged = true;
        if (writeFullState) {
            this.writeFullState = true;
        }
    }

    public boolean isViewStateChanged() {
        return this.viewStateChanged;
    }

    public boolean isWriteFullState() {
        return this.writeFullState;
    }

    public void addRequestParameter(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }

        try {
            if (value != null) {
                writeScript(String.format("OM.ajax.addRequestParameter(%s,%s);",
                                          HtmlEncoder.enquote(key), HtmlEncoder.enquote(value)));
            } else {
                writeScript(String.format("OM.ajax.removeRequestParameter(%s);",
                                          HtmlEncoder.enquote(key)));
            }
            setViewStateChanged(false);
        } catch (IOException ex) {
            throw new FacesException(ex);
        }
    }
    
    public void startDocument() throws IOException {
    }

    public void endDocument() throws IOException {
    	FacesContext context = FacesContext.getCurrentInstance();
        // Write view state at end of AJAX response.
        if (viewStateChanged) {
            String[] state = FacesUtils.getViewState(context);

            if (writeFullState) {
                out.write("OM.ajax.viewId=null;\n");
            } else {
                out.write("OM.ajax.viewId='" + context.getViewRoot().getViewId() + "';\n");
            }

            if (state[0] != null)
                out.write("OM.ajax.viewState='" + state[0] + "';\n");
            if (state[1] != null)
                out.write("OM.ajax.renderKitId='" + state[1] + "';\n");
        }
        
        // Write action scripts that must perform after non-action scripts and view state.
        if (actionScript != null) {
            out.write(actionScript);
        }
        
        // Write server debug log to client If an AjaxLogger exists and
        // it's attribute 'serverLog' is true,
        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot != null) {
        	AjaxLogger logger = findLogger(viewRoot);
        	
        	if (logger != null && logger.getServerLog()) {
        		String serverLog = Debug.getServerLog();
        		if (serverLog != null && !serverLog.equals("")) {
        			out.write("!@#$%" + serverLog);
        		}
        	}
        }
    }

    private AjaxLogger findLogger(UIComponent component) {
    	if (component instanceof AjaxLogger)
    		return (AjaxLogger)component;
    	
    	for (UIComponent kid : component.getChildren()) {
    		AjaxLogger logger = findLogger(kid);
    		
    		if (logger != null)
    			return logger;
    	}
    	
    	return null;
	}

	public void flush() throws IOException {
        // do nothing
    }

    public void startElement(String name, UIComponent component) {
        if (name == null)
            throw new NullPointerException();

        // TODO: close empty HTML elements such as BR

        if (current != null)
            stack.push(current);
        current = new Element(name, component);
    }

    public void writeAttribute(String name, Object value, String property) {
        if (name == null)
            throw new NullPointerException();

        if (current == null)
            throw new IllegalStateException();
        if (current.component == null)
            return;
        if (name.equals("id")) {
            if (value != null) current.id = value.toString();
        } else {
            current.addAttribute(name, value, property);
        }
    }

    public void writeURIAttribute(String name, Object value, String property)
        throws IOException
    {
        String valueStr = (value == null) ? "" : value.toString();
        String uri = HtmlEncoder.encodeURI(valueStr, encoding);
        writeAttribute(name, uri, property);
    }

    public void writeText(Object text, String property)
        throws IOException
    {
        if (current != null && current.component != null && current.id != null) {
            String str = (text == null) ? "" : text.toString();
            boolean dynamic = isDynamicValue(current.component, property);
            current.addText(str, dynamic);
        }
    }

    public void writeText(char[] text, int off, int len) {
        if (current != null && current.component != null && current.id != null) {
            current.addText(text, off, len);
        }
    }

    public void write(char[] cbuf, int off, int len) {
        if (current != null && current.component != null && current.id != null) {
            current.addText(cbuf, off, len);
        }
    }

    public void write(int c) {
        if (current != null && current.component != null && current.id != null) {
            current.addText(c);
        }
    }

    public void write(String str, int off, int len) {
        if (current != null && current.component != null && current.id != null) {
            current.addText(str.substring(off, off+len), false);
        }
    }

    public void endElement(String name)
        throws IOException
    {
        if (name == null)
            throw new NullPointerException();
        if (current == null || !current.name.equals(name))
            throw new IllegalStateException();

        if (current.component != null && current.id != null) {
            for (Attribute att : current.getAttributes()) {
                if (isDynamicValue(current.component, att.property)) {
                    writeAttributeScript(current.id, att.name, att.value);
                }
            }

            if (current.dynamicText) {
                writeInnerHtmlScript(current.id, current.getText());
            }
        }

        current = null;
        if (!stack.isEmpty())
            current = stack.pop();
    }

    public void writeAttributeScript(String id, String name, Object value)
        throws IOException
    {
        if (name.equals("style")) {
            String strValue = (value == null) ? EMPTY_STRING : HtmlEncoder.enquote(value.toString(), '\'');
            out.write("OM.S('" + id + "'," + strValue + ");\n");
        } else {
            String strValue;
            if (name.equals("class"))
                name = "className";
            if (value == null) {
                strValue = EMPTY_STRING;
            } else if (value instanceof Boolean) {
                strValue = value.toString();
            } else {
                strValue = HtmlEncoder.enquote(value.toString(), '\'');
            }
            out.write("OM.F('" + id + "','" + name + "'," + strValue + ");\n");
        }
    }

    public void writeInnerHtmlScript(String id, String text)
        throws IOException
    {
        out.write("OM.T('" + id + "'," + HtmlEncoder.enquote(text, '\'') + ");\n");
    }

    public void writeScript(String text)
        throws IOException
    {
        out.write(text);
    }

    public void writeActionScript(String script) {
        if (actionScript == null) {
            actionScript = script;
        } else {
            actionScript += script;
        }
    }

    private boolean isDynamicValue(UIComponent component, String property) {
        if (property == null) {
            return false;
        } else if (property.equals("clientId")) {
            return false;
        } else {
            ValueExpression value = component.getValueExpression(property);
            if (value != null) {
                return !value.isLiteralText();
            } else if (component.getValueExpression("binding") != null) {
                return true;
            } else if (component.getAttributes().containsKey(OUTJECTED_KEY)) {
                return true; // see ViewELResolver for more details
            } else {
                return false;
            }
        }
    }

    public void writeComment(Object data) {
        // do nothing
    }

    public ResponseWriter cloneWithWriter(Writer writer) {
        return new AjaxResponseWriter(writer, getContentType(), getCharacterEncoding());
    }

    public AjaxHtmlResponseWriter cloneWithHtmlWriter(Writer writer) {
        return new AjaxHtmlResponseWriter(writer, this);
    }

    public void close() {
        // do nothing
    }
}
