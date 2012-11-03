/*
 * $Id: RadioGroupRenderHandler.java,v 1.4 2008/04/29 07:40:46 lishaochuan Exp $
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

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.component.DependPackages;
import org.operamasks.faces.annotation.component.ProcessDecodes;
import org.operamasks.faces.component.form.impl.UIRadioGroup;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.util.FacesUtils;

@DependPackages("Ext.form")
public class RadioGroupRenderHandler extends AbstractSelectGroupRenderHandler {
	
	@ProcessDecodes
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String newValue = requestMap.get(clientId);
        
        UIRadioGroup group = (UIRadioGroup)component;
        group.setValue(newValue);
    }

	@Override
	protected String buildHtmls(FacesContext context, UIComponent component){
		String clientId = component.getClientId(context);
		UIRadioGroup group = (UIRadioGroup)component;
        Formatter fmt = new Formatter(new StringBuffer());
        List<SelectItem> selectItems = SelectItemsUtil.getSelectItems(component);
        int index = 1;
        for(SelectItem item : selectItems){
        	String strChecked = "";
            if(item.getValue()!= null && item.getValue().equals(group.getValue())){
            	strChecked = " checked='true'";
            }
            String radioId = generateSelectBoxId(clientId, index);
            index++;
        	fmt.format("<input type='radio' id='%s' name='%s' value='%s' %s/>", 
        			radioId, clientId, item.getValue(), strChecked);
        	if("v".equals(group.getDirection()) || "vertical".equals(group.getDirection())){
        		fmt.format("<br/>");
        	}
        }
        return fmt.toString();
	}
	
	@Override
	protected String buildScripts(FacesContext context, UIComponent component){
		String clientId = component.getClientId(context);
		String jsvar = FacesUtils.getJsvar(context, component);
        Formatter fmt = new Formatter(new StringBuffer());
        jsvarNames = new ArrayList<String>();
        List<SelectItem> selectItems = SelectItemsUtil.getSelectItems(component);
        int index = 1;
        for(SelectItem item : selectItems){
        	String clientId_radio = generateSelectBoxId(clientId, index);
        	String jsvar_radio = generateSelectBoxId(jsvar, index);
        	index++;
        	jsvarNames.add(jsvar_radio);
	        fmt.format("%s = new Ext.form.Radio({", jsvar_radio);
	        ExtConfig config = new ExtConfig(component);
	        config.set("boxLabel", item.getLabel());
	        String configStr = config.toScript();
	        fmt.format(configStr);
	        fmt.format("});\n");
	        fmt.format("if(Ext.get('%s')) {%s.applyToMarkup('%s');}\n", clientId_radio, jsvar_radio, clientId_radio);
	        String onchange = "";
	        UIRadioGroup radioGroup = (UIRadioGroup)component;
	        if(radioGroup.getOnchange() != null){
	            onchange = radioGroup.getOnchange();
	            if(!onchange.endsWith(";")){
	                onchange += ";";
	            }
	        }
            fmt.format("%s.on('check',function(){%s});\n", jsvar_radio, onchange);
        }
        return fmt.toString();
	}
}
