/*
 * $Id: FormRenderer.java,v 1.17 2008/03/17 01:18:37 patrick Exp $
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
import java.util.Map;
import java.util.HashMap;

import javax.faces.FacesException;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.el.ValueExpression;

import org.operamasks.faces.component.widget.UICalcNumberField;
import org.operamasks.faces.component.widget.UIDateField;
import org.operamasks.faces.component.widget.UITextField;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.application.ViewBuilder;

import static org.operamasks.resources.Resources.*;

public class FormRenderer extends HtmlRenderer implements ResourceProvider
{
    private static final String SUPPORT_SCRIPTS_ATTR = "org.operamasks.faces.SUPPORT_SCRIPTS";
    private static final String HIDDEN_FIELDS_ATTR = "org.operamasks.faces.HIDDEN_FIElDS";
    private static final String TRANSIENT_FORM_PARAM = "org.operamasks.faces.TRANSIENT_FORM";
    private static final String FORM_RICH_PARAM = "org.operamasks.faces.FORM_RICH";
    
    public void decode(FacesContext context, UIComponent component) {
        String postback = getPostbackFieldName(context, component);
        if (context.getExternalContext().getRequestParameterMap().containsKey(postback)) {
            ((UIForm)component).setSubmitted(true);
        } else {
            ((UIForm)component).setSubmitted(false);
        }
    }
    
    private boolean hasFormParent(UIComponent component){
        while (component != null) {
            component = component.getParent();
            if (component instanceof UIForm)
                return true;
        }
        return false;
    }

    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if(hasFormParent(component)){
            throw new FacesException(_T(JSF_NESTED_FORM));
        }

        ResponseWriter out = context.getResponseWriter();
        String clientId = component.getClientId(context);

        Object method = null;
        if (isTransientFormSupported())
            method = component.getAttributes().get("method");
        if (method == null)
            method = "post";

        out.startElement("form", component);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("name", clientId, "clientId");
        out.writeAttribute("method", method, null);
        out.writeAttribute("action", getActionURL(context), null);
        renderPassThruAttributes(out, component);
        out.writeText("\n", null);
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();

        renderHiddenFields(context, component);

        // The hidden field to indicate that this form is submitted
        out.startElement("input", component);
        out.writeAttribute("type", "hidden", null);
        out.writeAttribute("name", getPostbackFieldName(context, component), null);
        out.endElement("input");

        if (isTransientForm((UIForm)component)) {
            if (!isAjaxResponse(context) && !isAjaxHtmlResponse(context)) {
                out.startElement("input", component);
                out.writeAttribute("type", "hidden", null);
                out.writeAttribute("name", FacesUtils.VIEW_ID_PARAM, null);
                out.writeAttribute("value", context.getViewRoot().getViewId(), null);
                out.endElement("input");
            }

            try {
                FacesUtils.markForTransientState(context);
                context.getApplication().getViewHandler().writeState(context);
            } finally {
                FacesUtils.unmarkTransientState(context);
            }
        } else {
            context.getApplication().getViewHandler().writeState(context);
        }

        out.endElement("form");
        renderScripts(context, component);
    }

    public static boolean isTransientForm(UIForm form) {
        if (!isTransientFormSupported()) {
            return false;
        }

        if (form.isTransient()) {
            return true;
        }

        Object method = form.getAttributes().get("method");
        if (method != null && "get".equalsIgnoreCase(method.toString())) {
            return true;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        String param = context.getExternalContext().getInitParameter(TRANSIENT_FORM_PARAM);
        return (param != null) && "true".equalsIgnoreCase(param);
    }

    public static boolean isTransientFormSupported() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (context.getApplication().getViewHandler() instanceof ViewBuilder);
    }

    private void renderScripts(FacesContext context, UIComponent form)
        throws IOException
    {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String key = SUPPORT_SCRIPTS_ATTR + form.getClientId(context);

        String supportScripts = (String)requestMap.get(key);
        if (supportScripts != null) {
            ResponseWriter out = context.getResponseWriter();
            out.startElement("script", null);
            out.writeAttribute("type", "text/javascript", null);
            out.writeAttribute("language", "Javascript", null);
            out.write(supportScripts);
            out.endElement("script");
            requestMap.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    private void renderHiddenFields(FacesContext context, UIComponent form)
        throws IOException
    {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String key = HIDDEN_FIELDS_ATTR + form.getClientId(context);
        Map<String,String> hiddenFields = (Map<String,String>)requestMap.get(key);

        if (hiddenFields != null) {
            ResponseWriter out = context.getResponseWriter();
            for (String name : hiddenFields.keySet()) {
                String value = hiddenFields.get(name);
                out.startElement("input", null);
                out.writeAttribute("type", "hidden", null);
                out.writeAttribute("name", name, null);
                out.writeAttribute("value", value, null);
                out.endElement("input");
            }
        }
    }

    public static void addSupportScript(UIForm form, String script) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String key = SUPPORT_SCRIPTS_ATTR + form.getClientId(context);

        String supportScripts = (String)requestMap.get(key);
        supportScripts = (supportScripts == null) ? script : supportScripts + script;
        requestMap.put(key, supportScripts);
    }

    @SuppressWarnings("unchecked")
    public static void addHiddenFields(UIForm form, String name, String value) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String key = HIDDEN_FIELDS_ATTR + form.getClientId(context);

        Map<String,String> hiddenFields = (Map<String,String>)requestMap.get(key);
        if (hiddenFields == null) {
            hiddenFields = new HashMap<String, String>();
            requestMap.put(key, hiddenFields);
        }
        hiddenFields.put(name, value);
    }

    public static String getPostbackFieldName(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        return clientId + NamingContainer.SEPARATOR_CHAR + "_postback";
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (isRichEnabled(context, component)) {        
            // replace renderer for descendant components
            setRichRendererType(component, context);
        }        
    }
    
    private boolean isRichEnabled(FacesContext context, UIComponent component) {
        Object rich = component.getAttributes().get("rich");
        if (rich != null) {
            return "true".equalsIgnoreCase(rich.toString());
        }

        String paramValue = context.getExternalContext().getInitParameter(FORM_RICH_PARAM);
        return (paramValue != null) && paramValue.equalsIgnoreCase("true");
    }
    
    private void setRichRendererType(UIComponent root, FacesContext context) {
        for (UIComponent kid : root.getChildren()) {
            if (kid.isRendered()) {
                String rendererType = getRichRendererType(kid, context);
                if (rendererType != null) {
                    kid.setRendererType(rendererType);
                }
            }
            setRichRendererType(kid, context);
        }
    }
    
    private String getRichRendererType(UIComponent component, FacesContext context) {
        String family = component.getFamily();
        String rendererType = component.getRendererType();

        // Input text component rendered as a textField
        if ("javax.faces.Input".equals(family) && "javax.faces.Text".equals(rendererType)) {
            ValueExpression ve = component.getValueExpression("value");
            if (ve != null) {
                Class type = ve.getType(context.getELContext());
                if (type != null) {
                    if (java.util.Date.class.isAssignableFrom(type)) {
                        return UIDateField.DEFAULT_RENDERER_TYPE;
                    } else if (type == double.class || type == float.class ||
                               java.lang.Number.class.isAssignableFrom(type)) {
                        return UICalcNumberField.DEFAULT_RENDERER_TYPE;
                    }
                }
            }
            return UITextField.DEFAULT_RENDERER_TYPE;
        }        
        
        // Command button component rendered as a Button
        if ("javax.faces.Command".equals(family) && "javax.faces.Button".equals(rendererType)) {
            return "org.operamasks.faces.widget.Button";
        }
        
        // SelectOneMenu rendered as a ComboBox
        if ("javax.faces.SelectOne".equals(family) && "javax.faces.Menu".equals(rendererType)) {
            return "org.operamasks.faces.widget.Combo";
        }
        
        // Default renderer type
        return null;
    }
}
