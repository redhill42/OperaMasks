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

import java.util.Map;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.ext.ExtClass;
import org.operamasks.faces.component.form.impl.UIDateField;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.convert.DefaultDateTimeConverter;
import org.operamasks.faces.util.DateTimeFormatUtils;

@ExtClass("Ext.form.DateField")
@DependPackages({"Ext.form","Ext.menu.DateMenu"})
public class DateFieldRenderHandler extends TextFieldRenderHandler {
	@Override
	protected void processExtConfig(FacesContext context, UIComponent component, ExtConfig config) {
	    UIDateField field = (UIDateField)component;
	    Object currentValue = field.getValue();
		config.set("value", getFormattedValue(context,component,currentValue));
		if(field.getFormat() == null){
		    String format = DateTimeFormatUtils.DEFAUTL_DATE_FORMAT;
		    Converter c = field.getConverter(); 
		    String patten = DateTimeFormatUtils.getPattenFromConverter(c);
		    if(patten != null){
		        format = patten;
		    }
		    config.set("format", DateTimeFormatUtils.converToClientFormat(format));
		}else{
		    config.set("format", DateTimeFormatUtils.converToClientFormat(field.getFormat()));
		}
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        UIDateField field = (UIDateField)component;
        if (Boolean.TRUE.equals(field.getReadOnly()) || Boolean.TRUE.equals(field.getDisabled()))
            return;

        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String newValue = requestMap.get(clientId);
        Converter c = field.getConverter(); 
        if(c == null){
            DateTimeConverter dateTimeConverter = new DefaultDateTimeConverter();
            String format = field.getFormat();
            format = format == null ? DateTimeFormatUtils.DEFAUTL_DATE_FORMAT : format;
            dateTimeConverter.setPattern(format);
            dateTimeConverter.setTimeZone(TimeZone.getTimeZone(DateTimeFormatUtils.DEFAUTL_TIMEZONE));
            field.setConverter(dateTimeConverter);
        }else{
            field.setFormat(DateTimeFormatUtils.getPattenFromConverter(c));
        }
        setSubmittedValue(component, newValue);
    }

}


