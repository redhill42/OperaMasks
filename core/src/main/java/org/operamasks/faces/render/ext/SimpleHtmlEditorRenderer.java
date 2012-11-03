/*
 * $Id: SimpleHtmlEditorRenderer.java,v 1.3 2008/01/26 03:22:57 yangdong Exp $
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
import java.util.Formatter;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UISimpleHtmlEditor;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;

public class SimpleHtmlEditorRenderer extends FieldRenderer {
    private final static String EXT_CLASS = "Ext.form.HtmlEditor";

    @Override
    protected String getExtClass(UIComponent component) {
        return EXT_CLASS;
    }

    @Override
    protected String[] getPackageDependencies() {
        return new String[] { 
            "Ext.form", 
            "Ext.Toolbar", 
            "Ext.QuickTips", 
            "Ext.menu.Menu", 
            "Ext.ColorPalette" 
        };
    }

    public void encodeBegin(FacesContext context, UIComponent component, HtmlResponseWriter out) throws IOException {
        super.encodeBegin(context, component, out);
        out.startElement("textarea", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        out.endElement("textarea");
    }

    @Override
    protected String getInitScript(UIComponent component, String jsvar) {
        Formatter fmt = createFormatter();
        ExtJsUtils.applyToContainer(fmt, FacesContext.getCurrentInstance(), jsvar, component);
        return fmt.toString();
    }

    @Override
    protected ConfigOptions getConfigOptions(FacesContext context, UIComponent component) {
        UISimpleHtmlEditor simpleHtmlEditor = (UISimpleHtmlEditor) component;
        ConfigOptions configOptions = createConfigOptions().add("value", simpleHtmlEditor.getValue());
        configOptions.setComponent(component);
        Map<String, Object> attrs = simpleHtmlEditor.getAttrs();
        for (String key : attrs.keySet()) {
            configOptions.add(key, attrs.get(key));
        }
        return configOptions;
    }

    @Override
    protected boolean encodeMarkup() {
        return false;
    }

    protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        return true;
    }
}
