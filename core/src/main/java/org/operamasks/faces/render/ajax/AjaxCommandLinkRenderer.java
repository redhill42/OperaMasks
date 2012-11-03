/*
 * $Id: AjaxCommandLinkRenderer.java,v 1.13 2008/04/16 08:08:42 patrick Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIParameter;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.el.ValueExpression;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.render.html.CommandLinkRenderer;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxCommandLinkRenderer extends CommandLinkRenderer
{
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isDisabledDynamicly(component)) {
            if (isAjaxResponse(context)) {
                renderAjaxBegin(context, component);
            } else if (isAjaxHtmlResponse(context)) {
                renderAjaxHtmlBegin(context, component);
            }
        }
        super.encodeBegin(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        super.encodeEnd(context, component);

        ResponseWriter writer = context.getResponseWriter();
        if (writer instanceof AjaxResponseWriterWrapper) {
            renderAjaxEnd((AjaxResponseWriterWrapper)writer, context, component);
        } else if (isDisabledDynamicly(component) && isAjaxHtmlResponse(context)) {
            renderAjaxHtmlEnd(context);
        }
    }

    private void renderAjaxHtmlBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        // render outer span start
        ResponseWriter out = context.getResponseWriter();
        out.startElement("span", null);
        out.writeAttribute("id", getOuterClientId(context, component), null);
        out.write("");
    }

    private void renderAjaxHtmlEnd(FacesContext context)
        throws IOException
    {
        // render outer span end
        ResponseWriter out = context.getResponseWriter();
        out.endElement("span");
    }

    private static class AjaxResponseWriterWrapper extends ResponseWriterWrapper {
        ResponseWriter wrapped;
        ResponseWriter original;
        StringWriter buf;

        public AjaxResponseWriterWrapper(ResponseWriter wrapped, ResponseWriter old, StringWriter buf) {
            this.wrapped = wrapped;
            this.original = old;
            this.buf = buf;
        }

        public ResponseWriter getWrapped() {
            return wrapped;
        }
    }

    private void renderAjaxBegin(FacesContext context, UIComponent component) {
        // create a temporary ResponseWriter to get inner HTML
        StringWriter buf = new StringWriter();
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        ResponseWriter inner = out.cloneWithHtmlWriter(buf);
        ResponseWriter wrapper = new AjaxResponseWriterWrapper(inner, out, buf);
        context.setResponseWriter(wrapper);
    }

    private void renderAjaxEnd(AjaxResponseWriterWrapper wrapper, FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxResponseWriter out = (AjaxResponseWriter)wrapper.original;
        String innerHTML = wrapper.buf.toString();
        out.writeInnerHtmlScript(getOuterClientId(context, component), innerHTML);
        context.setResponseWriter(out);
    }

    private String getOuterClientId(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        return clientId + NamingContainer.SEPARATOR_CHAR + "_outer";
    }

    private boolean isDisabledDynamicly(UIComponent component) {
        ValueExpression ve = component.getValueExpression("disabled");
        if (ve != null) {
            return !ve.isLiteralText();
        } else {
            return component.getValueExpression("binding") != null;
        }
    }

    @Override
    protected void renderSingleLink(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxHtmlResponseWriter) {
            ((AjaxHtmlResponseWriter)out).setViewStateChanged(false);
        } else if (out instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)out).setViewStateChanged(false);
        }

        renderActiveLink(context, null, component);
    }
    
    @Override
    protected void renderActiveLink(FacesContext context, UIForm form, UIComponent component)
    throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        String target = (String) component.getAttributes().get("target");
        String href = (String) component.getAttributes().get("href");
        String type = (String)component.getAttributes().get("type");
        boolean doReturn = true;
        out.startElement("a", component);
        writeIdAttributeIfNecessary(context, out, component);
        if (target != null && href != null) {
            out.writeAttribute("href", href, "href");
            out.writeAttribute("target", target, "target");
            doReturn = false;
        } else {
            out.writeAttribute("href", "#", null);
        }
        
        String onclick = getOnclickScript(context, form, component, doReturn);
        out.writeAttribute("onclick", onclick, "onclick");
        if (type != null)
            out.writeAttribute("type", type, "type");
        renderPassThruAttributes(out, component, "disabled,onclick,target");
    }

    @Override
    protected String getOnclickScript(FacesContext context,
                                      UIForm form,
                                      UIComponent component,
                                      boolean doReturn)
    {
        String onclick = (String)component.getAttributes().get("onclick");
        if (onclick != null) {
            onclick = onclick.trim();
            if (onclick.length() == 0) {
                onclick = null;
            }
        }

        List<String> paramList = new ArrayList<String>();
        paramList.add(HtmlEncoder.enquote(component.getClientId(context)));
        paramList.add("''");
        for (UIParameter param : getParameters(component)) {
            if (param.getName() != null && param.getValue() != null) {
                paramList.add(HtmlEncoder.enquote(param.getName()));
                paramList.add(HtmlEncoder.enquote(param.getValue().toString()));
            }
        }

        String[] params = paramList.toArray(new String[paramList.size()]);
        String submit = encodeAjaxSubmit(context, component, params);
        if (doReturn) {
            submit = "return " + submit;
        }

        if (onclick == null) {
            return submit;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append("var a=function(){");
            buf.append(onclick);
            buf.append("};var b=function(){");
            buf.append(submit);
            buf.append("};");
            if (doReturn) {
                buf.append("return (a.apply(this)==false)?false:b();");
            } else {
                buf.append("a.apply(this)==false||b();");
            }
            return buf.toString();
        }
    }
}
