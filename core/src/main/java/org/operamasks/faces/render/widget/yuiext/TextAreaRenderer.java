/*
 * $Id: TextAreaRenderer.java,v 1.8 2008/01/04 02:49:14 lishaochuan Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITextArea;
import org.operamasks.faces.render.html.TextareaRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;

public class TextAreaRenderer extends TextareaRenderer implements ResourceProvider {
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.form.TextArea", "Ext.QuickTips");

        String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), component);       
        resource.addVariable(jsvar);        
                
        StringBuilder buf = new StringBuilder();
        encodeTextArea(buf, component, jsvar);        
        
        resource.addInitScript(buf.toString());        
        resource.releaseVariable(jsvar);
    }
    
    private void encodeTextArea(StringBuilder buf, UIComponent component, String jsvar) {        
        buf.append(jsvar).append("=new Ext.form.TextArea({\n");        
        if (component instanceof UITextArea) {
            UITextArea textArea = (UITextArea)component;
            buf.append(textArea.getTextAreaConfig().toScript());
        }
        
        buf.append("});\n");
        
        ExtJsUtils.applyToContainer(buf, FacesContext.getCurrentInstance(), jsvar, component);
    }
    protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }

}
