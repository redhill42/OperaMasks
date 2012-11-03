/*
 * $Id: ToolBarBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
 
package demo;

import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class ToolBarBean {
	private String response;
    private SelectItem[] colors = {
            new SelectItem("Red", "Red Color"),
            new SelectItem("Green", "Green Color"),
            new SelectItem("Blue", "Blue Color")
        };
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void valueChange(ValueChangeEvent event) {
		response = event.getNewValue() + " selected";
	}
	
	public void click() {
		response = "Click me";
	}
	
    public String menuAction(UIComponent item) {
        String label = (String)item.getAttributes().get("label");
        response = item.getId() + " (" + label + ") selected";

        return null;
    }

    public SelectItem[] getColors() {
        return colors;
    }
}
