/*
 * $Id: TextFieldRenderer.java,v 1.14 2008/04/15 10:04:27 patrick Exp $
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

import org.operamasks.faces.component.widget.UITextField;
import org.operamasks.faces.render.html.TextRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已被{@link org.operamasks.faces.render.form.TextFieldRenderHandler}代替
 */
@Deprecated
public class TextFieldRenderer extends TextRenderer implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.form.TextField", "Ext.QuickTips");
        
        String jsvar = resource.allocVariable(component);
        StringBuilder buf = new StringBuilder();
        encodeTextField(buf, component, jsvar);
        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
    }
    
    private void encodeTextField(StringBuilder buf, UIComponent component, String jsvar) {        
        FacesContext context = FacesContext.getCurrentInstance();

        buf.append(jsvar).append("=new Ext.form.TextField({");
        if (component instanceof UITextField) {
            UITextField textField = (UITextField)component;
            String config = textField.getTextConfig().toScript();
            buf.append(config);
            buf.append("});\n");
        } else {
            buf.append("});\n");
        }
        
        ExtJsUtils.applyToContainer(buf, context, jsvar, component);

        String value = getCurrentValue(context, component);
        if (value != null && value.length() != 0) {
            buf.append(jsvar).append(".setValue(").append(HtmlEncoder.enquote(value)).append(");");
        } else {
            buf.append(jsvar).append(".reset();");
        }
    }

	protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }
}
