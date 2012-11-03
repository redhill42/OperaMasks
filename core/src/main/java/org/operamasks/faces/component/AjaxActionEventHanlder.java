/*
 * $Id: AjaxActionEventHanlder.java,v 1.2 2008/04/16 02:26:36 lishaochuan Exp $
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

package org.operamasks.faces.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.AjaxActionEvent;
import org.operamasks.faces.util.FacesUtils;


public class AjaxActionEventHanlder implements Serializable, Cloneable {

    private static final long serialVersionUID = -1065034877736460926L;
    
    private UIComponent component = null;

    public AjaxActionEventHanlder(UIComponent component) {
        this.component = component;
    }

    public String toScript() {
    	 Formatter fmt = new Formatter(new StringBuffer());
    	
        Class<?> compClass = component.getClass();
        List<Field> eventFields = new ArrayList<Field>();
        for(Class<?> clz = compClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            Field[] fields = clz.getDeclaredFields();
            for(Field f : fields) {
                if (f.getAnnotation(AjaxActionEvent.class) != null) {
                    eventFields.add(f);
                }
            }
        }
        for(Field f : eventFields) {
            String event = f.getAnnotation(AjaxActionEvent.class).eventName();
            if("".equals(event)){
                event = f.getName();
            }
        	String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);
        	Object ajaxActionScript = component.getAttributes().get(f.getName());
        	if(ajaxActionScript != null){
        		fmt.format("%s.on('%s',function(){%s});\n", jsvar, event.replace("on", ""), ajaxActionScript);
        	}
        }
        return fmt.toString();
    }
}
