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

import java.beans.PropertyChangeEvent;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.PropertyListener;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.form.impl.UITextField;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

@ExtClass("Ext.form.TextField")
public class TextFieldRenderHandler extends AbstractFieldRenderHandler {
	@Override
	protected String getHtmlMarkup(){
		return "input";
	}
	
	
    @EncodeAjaxBegin
	public void encodeAjax(FacesContext context, UIComponent component)
			throws IOException {
		AjaxResponseWriter out = (AjaxResponseWriter) context.getResponseWriter();
		String jsvar = FacesUtils.getJsvar(context, component);
		String script = "";
		String value = getCurrentValue(FacesContext.getCurrentInstance(), component);
		if (value == null) {
			value = "";
		}
		value = value.replaceAll("\\n", "\\\\n");
		value = value.replaceAll("\\r", "");
		script = String.format("%s.setValue('%s');\n", jsvar, value);
		out.writeScript(script);
//        if (FacesUtils.isDynamicValue(component, "disabled") || component.getAttributes().get("actionBinding") != null) {
//            Boolean disabled = (Boolean)component.getAttributes().get("disabled");
//            if (disabled != null) {
//                if (disabled) {
//                    out.writeScript(jsvar + ".disable();");
//                } else {
//                    out.writeScript(jsvar + ".enable();");
//                }
//            }
//        }
	}
    
    @Override
    protected void processExtConfig(FacesContext context,
    		UIComponent component, ExtConfig config) {
        UITextField field = (UITextField)component;
    	Object value = field.getValue();
    	config.set("value", value == null ? "" : value.toString());
    	if(field.getRegex() != null){
    	    config.remove("regex");
    	    config.set("regex", field.getRegex(), true);
    	}
    }
}
