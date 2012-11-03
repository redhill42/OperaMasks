/*
 * $Id: AjaxFormRenderer.java,v 1.25 2008/04/25 09:33:53 lishaochuan Exp $
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

import static org.operamasks.resources.Resources.JSF_VALIDATE_REQUIRED;
import static org.operamasks.resources.Resources._T;

import java.io.IOException;
import java.util.Formatter;
import java.util.Iterator;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.component.widget.FormMessageTarget;
import org.operamasks.faces.component.widget.UIForm;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.validator.ClientValidator;

public class AjaxFormRenderer extends FormRenderer
{
    private static final String CLIENT_VALIDATE_PARAM = "org.operamasks.faces.CLIENT_VALIDATE";

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (isAjaxHtmlResponse(context)) {
            // If the form is enclosed in a AjaxUpdater component, then send
            // render ID for partial update.
            String renderId = getRenderId(component);
            if (renderId != null && renderId.length() != 0) {
                ResponseWriter out = context.getResponseWriter();
                out.startElement("input", component);
                out.writeAttribute("type", "hidden", null);
                out.writeAttribute("name", AjaxUpdater.RENDER_ID_PARAM, null);
                out.writeAttribute("value", renderId, null);
                out.endElement("input");
            }

            StringBuilder buf = new StringBuilder();
            Formatter fmt = new Formatter(buf);

            // Populate client side validator scripts
            if (isClientValidateEnabled(context, component)) {
                StringBuilder validations = new StringBuilder();
                addValidatorScripts(context, component, buf, validations);
                if (validations.length() != 0) {
                    fmt.format("document.forms['%s']._validators=[%s];",
                               component.getClientId(context),
                               validations);
                }
            }

            // Some custom components use form.submit() script to simulate
            // a form submission.  Replace the form.submit() function
            // to support AJAX.  See ajax.js script for more information.
            String onsubmit = (String)component.getAttributes().get("onsubmit");
            String onbeforerequest = (String)component.getAttributes().get("onbeforerequest");
            String onsuccess = (String)component.getAttributes().get("onsuccess");
            String onfailure = (String)component.getAttributes().get("onfailure");
            String oncomplete = (String)component.getAttributes().get("oncomplete");
            String groupId = (String)component.getAttributes().get("groupId");
            String defaultButton = getDefaultSubmitButton(context, component);

            fmt.format("OM.ajax.initForm(document.forms['%s'],{",
                       component.getClientId(context));

            if (onsubmit != null)
                fmt.format("onsubmit:function(){%s},", onsubmit);
            if (onbeforerequest != null) 
                fmt.format("onbeforerequest:function(){%s},", onbeforerequest);
            if (onsuccess != null)
                fmt.format("onsuccess:function(){%s},", onsuccess);
            if (onfailure != null)
                fmt.format("onfailure:function(){%s},", onfailure);
            if (oncomplete != null)
                fmt.format("oncomplete:function(){%s},", oncomplete);
            if (defaultButton != null) {
                // include default submit button name when form submitted
                fmt.format("params:{'%s':''}", defaultButton);
            }
            if (buf.charAt(buf.length()-1) == ',')
                buf.deleteCharAt(buf.length()-1);
            buf.append("},");
            if(groupId == null){
                fmt.format("null");
            }else{
                fmt.format("'%s'",groupId);
            }
            
            buf.append(");");

            addSupportScript((UIForm)component, buf.toString());
        }

        super.encodeEnd(context, component);
        
        if (isAjaxResponse(context)) {
            UIForm form = (UIForm)component;
            if(!FormMessageTarget.none.toString().equals(form.getMessageTarget())){
                encodeServerSideValidationErrors(context, form);  
            }
        }
    }
    
    private boolean isClientValidateEnabled(FacesContext context, UIComponent component) {
        Object clientValidate = component.getAttributes().get("clientValidate");
        if (clientValidate != null) {
            return "true".equalsIgnoreCase(clientValidate.toString());
        }

        String paramValue = context.getExternalContext().getInitParameter(CLIENT_VALIDATE_PARAM);
        return (paramValue != null) && paramValue.equalsIgnoreCase("true");
    }

    private void addValidatorScripts(FacesContext context, UIComponent component,
                                     StringBuilder script, StringBuilder validations)
    {
        if (!component.isRendered()) {
            return;
        }

        if (component instanceof UIInput) {
            addValidatorScripts(context, (UIInput)component, script, validations);
        }

        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            addValidatorScripts(context, child, script, validations);
        }
    }

    private void addValidatorScripts(FacesContext context, UIInput component,
                                     StringBuilder script, StringBuilder validations)
    {
        // Client side converters
        Converter converter = getConverterWithType(context, component);
        if (converter instanceof ClientValidator) {
            addValidatorScript(context, component, (ClientValidator)converter, script, validations);
        }

        // Required attribute
        if (component.isRequired()) {
            if (validations.length() != 0) {
                validations.append(",");
            }

            validations.append("new RequiredValidator('");
            validations.append(component.getClientId(context));
            validations.append("',");

            String requiredMessage = component.getRequiredMessage();
            if (requiredMessage == null) {
                requiredMessage = _T(JSF_VALIDATE_REQUIRED, FacesUtils.getLabel(context, component));
            }
            validations.append(HtmlEncoder.enquote(requiredMessage));

            String display = FacesUtils.getMessageComponentId(context, component);
            if (display != null) {
                validations.append(",");
                validations.append(HtmlEncoder.enquote(display));
            } else {
                validations.append(",null");
            }

            // trim attribute value for number types
            Class valueType = null;
            ValueExpression ve = component.getValueExpression("value");
            if (ve != null) {
                try {
                    valueType = ve.getType(context.getELContext());
                } catch (ELException ex) {/*ignored*/}
            }
            if (valueType != null && Number.class.isAssignableFrom(valueType)) {
                validations.append(",true");
            }

            validations.append(")");
        }

        // Validators
        for (Validator v : component.getValidators()) {
            if (v instanceof ClientValidator) {
                addValidatorScript(context, component, (ClientValidator)v, script, validations);
            }
        }
    }

    private void addValidatorScript(FacesContext context, UIComponent component,
                                    ClientValidator validator,
                                    StringBuilder script, StringBuilder validations)
    {
        String vs = validator.getValidatorScript(context, component);
        String vis = validator.getValidatorInstanceScript(context, component);

        if (vs != null) {
            script.append(vs);
        }
        if (vis != null) {
            if (validations.length() != 0)
                validations.append(",");
            validations.append(vis);
        }
    }

    private Converter getConverterWithType(FacesContext context, UIInput component) {
        Converter converter = component.getConverter();
        if (converter != null) {
            return converter;
        }

        ValueExpression ve = component.getValueExpression("value");
        if (ve == null) {
            return null;
        }

        Class converterType = null;
        try {
            converterType = ve.getType(context.getELContext());
        } catch (ELException ex) {/*ignored*/}

        // if converterType is null, String, or Object, assume
        // no conversion is needed
        if (converterType == null ||
            converterType == String.class ||
            converterType == Object.class) {
            return null;
        }

        // if getType returns a type for which we supports a default
        // conversion, acquire an approripate converter instance.
        try {
            Application application = context.getApplication();
            return application.createConverter(converterType);
        } catch (Exception ex) {
            return null;
        }
    }

    private String getDefaultSubmitButton(FacesContext context, UIComponent component) {
        Iterator<UIComponent> kids = FacesUtils.createChildrenIterator(component, false);
        while (kids.hasNext()) {
            UIComponent current = kids.next();
            if (current.isRendered() && (current instanceof HtmlCommandButton)) {
                String type = (String)current.getAttributes().get("type");
                if ("submit".equals(type)) {
                    return current.getClientId(context);
                }
            }
        }
        return null;
    }

    private String getRenderId(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof AjaxUpdater && !((AjaxUpdater)parent).getGlobalAction()) {
                return ((AjaxUpdater)parent).getRenderId();
            }
            parent = parent.getParent();
        }
        return null;
    }
    
    @Override
    public void provideResource(final ResourceManager rm, final UIComponent component) {
        super.provideResource(rm, component);

        FacesContext context = FacesContext.getCurrentInstance();
        if (!isClientValidateEnabled(context, component)) {
            return;
        }

        rm.registerResource(new AbstractResource("urn:client-validate:" + component.getClientId(context)) {
            public int getPriority() {
                return HIGH_PRIORITY;
            }

            public void encodeEnd(FacesContext context) throws IOException {
                YuiExtResource resource = (YuiExtResource)rm.getRegisteredResource(YuiExtResource.RESOURCE_ID);
                if (resource != null) {
                    String script = encodeExtStyleValidationScripts(context, (UIForm)component);
                    if (script != null && script.length() != 0) {
                        resource.addInitScript(script);
                    }
                }
            }
        });
    }

    private boolean isExtField(FacesContext context, UIComponent component) {
        if(component instanceof UIField){
            return true;
        }
        Renderer renderer = FacesUtils.getRenderer(context, component);
        if (renderer == null)
            return false;
        return renderer.getClass().getPackage().getName().endsWith("yuiext") ||
        			renderer.getClass().getPackage().getName().endsWith("ext");
    }

    private void encodeServerSideValidationErrors(FacesContext context, UIForm form) {
        Iterator<UIComponent> kids = FacesUtils.createFacetsAndChildrenIterator(form, false);
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            if (child instanceof UIInput) {
                UIInput input = (UIInput)child;
                if (!input.isValid() && isExtField(context, input)) {
                    encodeServerSideValidationErrors(context, input);
                }
            }
        }
    }

    private void encodeServerSideValidationErrors(FacesContext context, UIInput input) {
        Iterator<FacesMessage> iter = context.getMessages(input.getClientId(context));
        if (null == iter || !iter.hasNext()) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        while (iter.hasNext()) {
            FacesMessage message = iter.next();
            String summary = message.getSummary();
            String detail = message.getDetail();
            if (buf.length() > 0) {
                buf.append("<br/>");
            }
            if (summary != null && summary.length() > 0) {
                buf.append(summary);
            }
            if (detail != null && detail.length() > 0 && !detail.equals(summary)) {
                buf.append("<br/>").append(detail);
            }
        }

        AjaxResponseWriter writer = (AjaxResponseWriter)context.getResponseWriter();
        writer.writeActionScript(FacesUtils.getJsvar(context, input) +
            ".markInvalid(" + HtmlEncoder.enquote(buf.toString()) + ");");
    }

    private String encodeExtStyleValidationScripts(FacesContext context, UIForm form) {
        Iterator<UIComponent> kids = FacesUtils.createFacetsAndChildrenIterator(form, false);
        StringBuilder buf = new StringBuilder();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            if (child instanceof UIInput && isExtField(context, child)) {
                buf.append(encodeExtStyleValidationScripts(context, form, (UIInput)child));
            }
        }
        return buf.toString();
    }

    private String encodeExtStyleValidationScripts(FacesContext context, UIForm form, UIInput component) {
        Boolean validateOnBlur = (Boolean)form.getAttributes().get("validateOnBlur");
        String validationEvent = (String)form.getAttributes().get("validationEvent");

        if (validateOnBlur == null) {
            validateOnBlur = true;
        }

        if (validationEvent == null) {
            validationEvent = "'keyup'";
        } else if (validationEvent.equalsIgnoreCase("false")) {
            validationEvent = "false";
        } else {
            validationEvent = "'" + validationEvent + "'";
        }

        return String.format("OM.ajax.initValidation('%s','%s',%s,%b,%s);\n",
                             form.getClientId(context),
                             component.getClientId(context),
                             FacesUtils.getJsvar(context, component),
                             validateOnBlur, validationEvent);
    }
}
