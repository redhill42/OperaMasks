/*
 * $Id 
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
package org.operamasks.faces.render.form;

import static org.operamasks.resources.Resources.JSF_NO_SUCH_CONVERTER_TYPE;
import static org.operamasks.resources.Resources._T;

import java.io.IOException;
import java.util.Formatter;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.EncodeResourceEnd;
import org.operamasks.faces.annotation.component.ProcessDecodes;
import org.operamasks.faces.component.AjaxActionEventHanlder;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.render.ext.AbstractRenderHandler;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.ToolBarUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;

@DependPackages({"Ext.form","Ext.QuickTips"})
public abstract class AbstractFieldRenderHandler extends AbstractRenderHandler {
	@ProcessDecodes
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        UIField field = (UIField)component;
        if (Boolean.TRUE.equals(field.getReadOnly()) || Boolean.TRUE.equals(field.getDisabled()))
            return;

        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String newValue = requestMap.get(clientId);
        setSubmittedValue(component, newValue);
    }
	
	protected void setSubmittedValue(UIComponent component, Object newValue) {
        if (component instanceof EditableValueHolder) {
            ((EditableValueHolder)component).setSubmittedValue(newValue);
        }
    }
	
	protected String getCurrentValue(FacesContext context, UIComponent component) {
        if (component instanceof EditableValueHolder) {
            Object submittedValue = ((EditableValueHolder)component).getSubmittedValue();
            if (submittedValue != null)
                return (String)submittedValue;
        }
        Object currentValue = getValue(component);
        if (currentValue != null)
            return getFormattedValue(context, component, currentValue);
        return null;
    }

    protected Object getValue(UIComponent component) {
        if (component instanceof ValueHolder)
            return ((ValueHolder)component).getValue();
        return null;
    }

    protected String getFormattedValue(FacesContext context, UIComponent component, Object currentValue)
        throws ConverterException
    {
        return FacesUtils.getFormattedValue(context, component, currentValue);
    }


    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        String newValue = (String)submittedValue;
        ValueExpression binding = component.getValueExpression("value");
        Converter converter = null;

        if (component instanceof ValueHolder)
            converter = ((ValueHolder)component).getConverter();

        if (converter == null) {
            if (binding == null)
                return newValue;

            Class valueType = binding.getType(context.getELContext());
            if (valueType == null || valueType == String.class || valueType == Object.class) {
                return newValue;
            } else {
                converter = context.getApplication().createConverter(valueType);
                if (converter == null) {
                    throw new ConverterException(_T(JSF_NO_SUCH_CONVERTER_TYPE, valueType.getName()));
                }
            }
        }

        return converter.getAsObject(context, component, newValue);
    }
	
    @EncodeHtmlBegin
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
    	if(component.getParent() instanceof UIToolBar){
    	    return;
    	}
        UIField field = (UIField)component;
        beforeHtmlBegin(context, component);
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        String clientId = component.getClientId(context);
        out.startElement("div", component);
        out.writeAttribute("class", "x-form-item", null);
        out.startElement("div", component);
        out.writeAttribute("class", "x-form-element", null);
        if(field.getFieldLabel() != null){
            out.startElement("label", component);
            if(field.getFieldClass() != null){
                out.writeAttribute("class", field.getFieldClass(), null); 
            }
            out.writeText(field.getFieldLabel(), field, null);
            out.endElement("label");
        }
        out.startElement(getHtmlMarkup(), component);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("name", clientId, "clientId");
        String inputType = ((UIField)component).getInputType();
        if(!"password".equalsIgnoreCase(inputType)){
        	inputType = "text";
        }
        out.writeAttribute("type", inputType, null);

        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        String defaultStyle = "";
        if (isContainer(component)) {
            defaultStyle = getContainerDefaultStyle();
        }
        if (style != null && style.length() > 0) {
            style = defaultStyle + style;
        } else {
            style = defaultStyle;
        }
        out.writeAttribute("style", style, "style");
        if (styleClass != null && styleClass.length() > 0) {
            out.writeAttribute("class", styleClass, "class");
        }
        out.write("\n");
    }

	protected void beforeHtmlBegin(FacesContext context, UIComponent component) {
	}

	@EncodeHtmlEnd
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
	    if(component.getParent() instanceof UIToolBar){
            return;
        }
	    HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.endElement(getHtmlMarkup());
        out.endElement("div");
        out.endElement("div");
        out.write("\n");
    }

    @EncodeResourceBegin
    public void resourceBegin(FacesContext context,final UIComponent component,final ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        final String jsvar = resource.allocVariable(component);
        String clientId = component.getClientId(context);
        Formatter fmt = new Formatter(new StringBuffer());
        fmt.format("%s = new %s({", jsvar, getExtClass(component));
        ExtConfig config = new ExtConfig(component);
        UIField field = (UIField)component;
        config.set("value", field.getValue());
        processExtConfig(context, component, config);
        config.set("validator", String.format("function(){return OM.ajax.validateField('%s', %s);}", field.getClientId(context), jsvar), true);
        if((component.getParent() instanceof UIToolBar)){
            config.set("name", clientId);
        }
        String configStr = config.toScript();
        fmt.format(configStr);
        fmt.format("});\n");
        if((component.getParent() instanceof UIToolBar)){
            rm.registerResource(new AbstractResource(getResourceId(component)) {
                @Override
                public int getPriority() {
                    return LOW_PRIORITY - 300;
                }
                
                @Override
                public void encodeBegin(FacesContext context) throws IOException {
                    YuiExtResource resource = YuiExtResource.register(rm);
                    String toolbarJsvar = FacesUtils.getJsvar(context, component.getParent());
                    resource.addInitScript(String.format("%s.addField(%s);\n", toolbarJsvar, jsvar));
                }
            });
        } else{
            fmt.format("if(Ext.get('%s')) {%s.applyToMarkup('%s');}\n", clientId, jsvar, clientId);
        }
        resource.addInitScript(fmt.toString());
    }
    private static String getResourceId(UIComponent component) {
        return "urn:toolBarField:" + component.getClientId(FacesContext.getCurrentInstance());
    }
    
    @EncodeResourceEnd
    public void resourceEnd(FacesContext context, UIComponent component, ResourceManager rm) throws IOException {
        YuiExtResource resource = getResourceInstance(rm);
        AjaxActionEventHanlder handler = new AjaxActionEventHanlder(component);
        resource.addInitScript(handler.toScript());
    }
    
	abstract protected String getHtmlMarkup();
    
    protected String getContainerDefaultStyle() {
        return "";
    }

    protected void processExtConfig(FacesContext context, UIComponent component, ExtConfig config) {
        // do nothing, sub class can overwrite it
    }
    

}
