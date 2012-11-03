/*
 * $Id: AjaxSimpleHtmlEditorRenderer.java,v 1.4 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UISimpleHtmlEditor;
import org.operamasks.faces.render.AjaxRenderer2;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

public class AjaxSimpleHtmlEditorRenderer extends SimpleHtmlEditorRenderer implements AjaxRenderer2 {
    public void encodeAjax(FacesContext context, UIComponent component,
            AjaxResponseWriter out) throws IOException {
        String jsvar = FacesUtils.getJsvar(context, component);
        UISimpleHtmlEditor simpleHtmlEditor = (UISimpleHtmlEditor) component;
        
        String value = (String)simpleHtmlEditor.getValue();
        if (value == null)
        	value = "";
        out.writeScript(jsvar + ".setValue('" + value + "');");
        
        Integer width = simpleHtmlEditor.getWidth();
        Integer height = simpleHtmlEditor.getHeight();
        if (width != null)
        	out.writeScript(jsvar + ".setWidth(" + width + ");");
        if (height != null)
        	out.writeScript(jsvar + ".setHeight(" + height + ");");
    }
}
