/*
 * $Id: CheckBoxGroupRenderHandler.java,v 1.2 2008/04/29 07:40:25 lishaochuan Exp $
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
import org.operamasks.faces.component.form.impl.UICheckBoxGroup;
import org.operamasks.faces.component.widget.ExtConfig;
import org.operamasks.faces.util.FacesUtils;

@DependPackages("Ext.form")
public class CheckBoxGroupRenderHandler extends AbstractSelectGroupRenderHandler {
	private final static String ALL_CHECKBOX_ITEMNAMES = "_all_checkBox_itemNames";
	
	@ProcessDecodes
	public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        // save submitted value
        String clientId = component.getClientId(context);
        Map<String,String> requestMap = context.getExternalContext().getRequestParameterMap();
        String checkBoxNames = requestMap.get(clientId+ALL_CHECKBOX_ITEMNAMES);
        List<SelectItem> newSelectItems = new ArrayList<SelectItem>();
        for(String checkBoxName : checkBoxNames.split(",")){
        	Boolean newValue = "on".equals(requestMap.get(checkBoxName)) ? true : false;
        	String label = checkBoxName.replace(clientId + "_", "");
        	newSelectItems.add(new SelectItem(newValue, label));
        }
        SelectItemsUtil.updateSelectItems(component,newSelectItems);
    }
	
	@Override
	protected String buildHtmls(FacesContext context, UIComponent component){
		String clientId = component.getClientId(context);
        String checkBoxClientId = clientId + ALL_CHECKBOX_ITEMNAMES;
        UICheckBoxGroup group = (UICheckBoxGroup)component;
        Formatter fmt = new Formatter(new StringBuffer());
        fmt.format("<input type='hidden' id='%s' name='%s' value='%s'/>", 
        		checkBoxClientId,checkBoxClientId,getAllCheckBoxNames(context, component));
        
        List<SelectItem> selectItems = SelectItemsUtil.getSelectItems(component);
        int index = 1;
        for(SelectItem item : selectItems){
        	Boolean isChecked = Boolean.parseBoolean(item.getValue().toString());
        	String strChecked = "";
            if(isChecked){
            	strChecked = " checked='true'";
            }
            String checkBoxName = generateSelectBoxId(clientId, index);
            index++;
        	fmt.format("<input type='checkbox' id='%s' name='%s'%s/>", 
        			checkBoxName, checkBoxName, strChecked);
        	if("v".equals(group.getDirection()) || "vertical".equals(group.getDirection())){
        		fmt.format("<br/>");
        	}
        }
        return fmt.toString();
	}
	private String getAllCheckBoxNames(FacesContext context, UIComponent component) {
		List<SelectItem> selectItems = SelectItemsUtil.getSelectItems(component);
		StringBuffer names = new StringBuffer();
		for(SelectItem item : selectItems){
			names.append(component.getClientId(context));
			names.append("_");
			names.append(item.getLabel());
			names.append(",");
		}
		return names.toString();
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
        	String clientId_checkBox = generateSelectBoxId(clientId,index);
        	String jsvar_checkBox = generateSelectBoxId(jsvar,index);
        	index++;
        	jsvarNames.add(jsvar_checkBox);
	        fmt.format("%s = new Ext.form.Checkbox({", jsvar_checkBox);
	        ExtConfig config = new ExtConfig(component);
	        config.set("boxLabel", item.getLabel());
	        config.set("checked", Boolean.parseBoolean(item.getValue().toString()));
	        String configStr = config.toScript();
	        fmt.format(configStr);
	        fmt.format("});\n");
	        fmt.format("if(Ext.get('%s')) {%s.applyToMarkup('%s');}\n", clientId_checkBox, jsvar_checkBox, clientId_checkBox);
	        
        }
        return fmt.toString();
	}
}
