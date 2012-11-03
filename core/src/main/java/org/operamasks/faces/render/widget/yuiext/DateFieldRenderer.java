/*
 * $Id: DateFieldRenderer.java,v 1.10 2008/03/10 08:35:18 lishaochuan Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.operamasks.faces.component.widget.DateFieldConfig;
import org.operamasks.faces.component.widget.UIDateField;
import org.operamasks.faces.render.html.TextRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.resources.Resources;

public class DateFieldRenderer extends TextRenderer implements ResourceProvider {
	private static char[] CLIENT_DATE_PATTERNS = new char[] {
		'd', 'D', 'j', 'l', 'S', 'w', 'z', 'W', 'F', 'm', 'M', 'n',
		't', 'Y', 'y', 'A', 'g', 'G', 'h', 'H', 'i', 's', 'O'
	};
	
	private static String[] SERVER_DATE_PATTERNS = new String[] {
		"dd", "EEE", "d", "EEEE", "'th'", "E", "D", "w", "MMMM", "MM", "MMM",
		"M", "d", "yyyy", "yy", "a", "h", "k", "hh", "kk", "mm", "ss", "Z"
	};
	
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.form.DateField", "Ext.QuickTips");
        
        String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);       
        resource.addVariable(jsvar);        
               
        StringBuilder buf = new StringBuilder();
        encodeDateField(buf, component, jsvar);        
        
        resource.addInitScript(buf.toString());        
        resource.releaseVariable(jsvar);
    }
    private void encodeDateField(StringBuilder buf, UIComponent component, String jsvar) {
        buf.append(jsvar).append("=new Ext.form.DateField({\n");
        
        if (component instanceof UIDateField) {
            UIDateField dateField = (UIDateField)component;
            DateFieldConfig dateConfig = dateField.getDateConfig();
            String script = dateConfig.toScript();
            if(script.endsWith(",")){
            	script = script.substring(0,script.length()-1);
            }
            buf.append(script);           
        } else {
            buf.append("format:'" + new DateFieldConfig().getFormat() + "'");
        }
        
        buf.append("});\n");
        
        ExtJsUtils.applyToContainer(buf, FacesContext.getCurrentInstance(), jsvar, component);    
    }

    protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }
    
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component,
    		Object submittedValue) throws ConverterException {
    	if (submittedValue == null || "".equals(submittedValue))
    		return null;
    	
    	SimpleDateFormat format = new SimpleDateFormat(getServerDateFormat(component));
    	
    	try {
			return format.parse((String)submittedValue);
		} catch (ParseException e) {
			UIDateField dateField = (UIDateField)component;
			dateField.setConverterMessage(Resources._T(
					Resources.JSF_DATETIME_CONVERTER,
					new Object[] {
							component.getClientId(context),
							getServerDateFormat(dateField)
					}
				)
			);
			
			throw new ConverterException(e.getMessage(), e);
		} 
    }
    
	@Override
    protected String getFormattedValue(FacesContext context, UIComponent component,
    		Object currentValue) throws ConverterException {
		if (currentValue == null)
			return null;
		
    	SimpleDateFormat format = new SimpleDateFormat(getServerDateFormat(component));
    	
    	return format.format((Date)currentValue);
    }
    
    private String getFormat(UIComponent component) {
    	String dateFormat;
        if (component instanceof UIDateField) {
            UIDateField dateField = (UIDateField)component;
            dateFormat = dateField.getDateConfig().getFormat();
        } else {
            dateFormat = new DateFieldConfig().getFormat();
        }
        
        return dateFormat;
	}
    
    private String getServerDateFormat(UIComponent component) {
    	return convertToServerFormat(getFormat(component));
    }
    
    /*
     * Convert client date format(PHP style date pattern) to server date
     * format(Java style date pattern).
     */
	private String convertToServerFormat(String clientFormat) {
		StringBuffer severDateFormatBuf = new StringBuffer();

    	int escapeCharCount = 0;
    	StringBuffer normalCharsBuf = new StringBuffer();
    	
    	for (int i = 0; i < clientFormat.length(); i++) {
    		char c = clientFormat.charAt(i);

    		// It's an PHP escape char
   			if (c == '\\') {
   				if (escapeCharCount++ > 2) {
   					// Really want a '\' char
   					normalCharsBuf.append("\\");
   					escapeCharCount = 0;
   				}
   				
				continue;
    		}

   			// char ''' is escape char of Java style date pattern, so
   			// convert it to "''" and save it
			if (c == '\'') {
       			normalCharsBuf.append("\'\'");
       			escapeCharCount = 0;
       			
       			continue;
			}
			
   			// Previous char is escape char, so the char is escaped.
   			if (escapeCharCount > 0) {
   				normalCharsBuf.append(c);
   				escapeCharCount = 0;
   				
   				continue;
   			}

       		String serverPattern = convertToServerDatePattern(c);

       		if (serverPattern != null) {
       			// Append normal chars which precede server pattern string
       			if (normalCharsBuf.length() > 0) {
           			severDateFormatBuf.append('\'').append(normalCharsBuf).append('\'');
           			// clear normal chars
           			normalCharsBuf.delete(0, normalCharsBuf.length());
       			}
       			
           		severDateFormatBuf.append(serverPattern);
           	} else {
           		// Save other normal char
       			normalCharsBuf.append(c);
   			}
    	}
    	
    	// Append last part of normal chars
		if (normalCharsBuf.length() > 0) {
			severDateFormatBuf.append('\'').append(normalCharsBuf).append('\'');
		}
    	
    	return severDateFormatBuf.toString();
	}
    
    // Convert PHP style date pattern char to Java style date pattern string.
    // If the char isn't a PHP style date pattern char, return null
	private String convertToServerDatePattern(char c) {
		for (int i = 0; i < CLIENT_DATE_PATTERNS.length; i++) {
			if (c == CLIENT_DATE_PATTERNS[i])
				return SERVER_DATE_PATTERNS[i];
		}
		
		return null;
	}

}
