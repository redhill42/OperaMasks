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

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.EncodeAjaxBegin;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

@ExtClass("Ext.form.Checkbox")
public class CheckboxRenderHandler extends AbstractFieldRenderHandler {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        UIField field = (UIField)component;
        if (Boolean.TRUE.equals(field.getReadOnly()) || Boolean.TRUE.equals(field.getDisabled()))
            return;

        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        Boolean newValue = "on".equals(requestMap.get(clientId)) ? true : false;
        setSubmittedValue(component, newValue);
    }
	
	@Override
	protected String getHtmlMarkup(){
		return "input";
	}
	
	@Override
	protected void beforeHtmlBegin(FacesContext context, UIComponent component) {
		((UIField)component).setInputType("checkbox");
	}
	
    @EncodeAjaxBegin
	public void encodeAjax(FacesContext context, UIComponent component)
			throws IOException {
		AjaxResponseWriter out = (AjaxResponseWriter) context.getResponseWriter();
		String jsvar = FacesUtils.getJsvar(context, component);
		String script = "";
		String value = getCurrentValue(FacesContext.getCurrentInstance(), component);
		if (value != null && value.length() != 0) {
			script = String.format("%s.setValue(%s);\n", jsvar, value);
		}else{
		    script = String.format("%s.setValue(false);\n", jsvar);
		}
		out.writeScript(script);
	}
}
