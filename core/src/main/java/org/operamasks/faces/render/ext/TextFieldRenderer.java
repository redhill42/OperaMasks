/*
 * $Id: TextFieldRenderer.java,v 1.9 2008/01/26 03:22:57 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITextField;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class TextFieldRenderer extends FieldRenderer {
    private final static String EXT_CLASS = "Ext.form.TextField";
    private static final String[] options = new String[] {
    	"grow",
    	"growMin",
    	"growMax",
    	"vtype",
    	"disableKeyFilter",
    	"allowBlank",
    	"minLength",
    	"maxLength",
    	"minLengthText",
    	"maxLengthText",
    	"selectOnFocus",
    	"blankText",
    	"extValidator",
    	"regexText",
    	"emptyText",
    	"emptyClass",
    	"width",
    	"inputType"    	
    };
    
    @Override
    protected String getExtClass(UIComponent component) {
        return EXT_CLASS;
    }

    @Override
    protected String[] getPackageDependencies() {
        return new String[]{
            "Ext.form.TextField",
            "Ext.QuickTips"
        };
    }
    
    public void encodeBegin(FacesContext context, UIComponent component,
            HtmlResponseWriter out) throws IOException {
        super.encodeBegin(context, component, out);
        
        out.startElement("input", component);
        writeIdAttributeIfNecessary(context, out, component);
        String type = "text";
        String passwordText = "password";
        HtmlInputText inputText = (HtmlInputText)component;
        if(component instanceof UITextField){
            UITextField textField = (UITextField)inputText;
            if (passwordText.equalsIgnoreCase(textField.getInputType())){
                type = passwordText;
            }
        }
        out.writeAttribute("type", type, null);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        String autocomplete = (String)component.getAttributes().get("autocomplete");
        if (autocomplete != null && "off".equals(autocomplete))
            out.writeAttribute("autocomplete", "off", "autocomplete");
        renderPassThruAttributes(out, component);
        out.endElement("input");
    }
    
    @Override
    protected String getEndScript(YuiExtResource resource, UIComponent component) {
    	Formatter fmt = createFormatter();
    	
        String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
        ExtJsUtils.applyToContainer(fmt, FacesContext.getCurrentInstance(),
        		jsvar, component);
        
        return fmt.toString();
    }

    @Override
    protected ConfigOptions getConfigOptions(FacesContext context,
            UIComponent component) {
    	ConfigOptions configOptions = createConfigOptions(component);
    	for (String option : options) {
    		configOptions.add(option);
    	}
    	
    	if (component instanceof UITextField) {
    		UITextField textfield = (UITextField)component;
    		
    		configOptions.addItem("validator", textfield.getExtValidator());
    		configOptions.addItem("regex", textfield.getRegex());
    		configOptions.addItem("maskRe", textfield.getMaskRe());
    	}

    	return configOptions;
    }
    
    @Override
    protected boolean encodeMarkup() {
    	return false;
    }
    
	protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }
	
	@Override
	protected String getInitScript(UIComponent component, String jsvar) {
        return encodeValue(component, jsvar);
	}

    protected String encodeValue(UIComponent component, String jsvar) {
        String value = getCurrentValue(getContext(), component);
        if (value != null && value.length() != 0) {
        	return String.format("%s.setValue(%s);\n", jsvar, HtmlEncoder.enquote(value));
        } else {
            return jsvar + ".reset();\n";
        }
    }
}
