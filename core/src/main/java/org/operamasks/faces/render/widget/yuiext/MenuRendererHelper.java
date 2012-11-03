/*
 * $Id: MenuRendererHelper.java,v 1.5 2008/04/25 01:44:41 lishaochuan Exp $
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
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;
import java.util.Formatter;
import java.io.StringWriter;
import java.io.IOException;

import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.render.html.HtmlRenderer;

public class MenuRendererHelper
{
    static UIMenu getParentMenu(UIComponent component) {
        component = component.getParent();
        while (component != null) {
            if (component instanceof UIMenu)
                return (UIMenu)component;
            component = component.getParent();
        }
        return null;
    }

    /**
     * Encode common menu item configuration.
     */
    static void encodeItemConfig(Formatter fmt, FacesContext context, UIComponent item, String text) {
        String icon = getItemIcon(context, item);
        String styleClass = (String)item.getAttributes().get("styleClass");
        String disabledClass = (String)item.getAttributes().get("disabledClass");
        String activeClass = (String)item.getAttributes().get("activeClass");

        fmt.format("text:%s", HtmlEncoder.enquote(text, '\''));
        fmt.format(",plugins:new Ext.ux.plugins.MenuItemPlugin()");
        if (icon != null)
            fmt.format(",icon:%s", HtmlEncoder.enquote(icon, '\''));
        if (HtmlRenderer.isDisabled(item))
            fmt.format(",disabled:true");
        if (styleClass != null)
            fmt.format(",itemCls:'%s'", styleClass);
        if (disabledClass != null)
            fmt.format(",disabledClass:'%s'", disabledClass);
        if (activeClass != null)
            fmt.format(",activeClass:'%s'", activeClass);
    }

    static String getItemText(FacesContext context, UIComponent item, boolean fromValue) {
        String text = (String)item.getAttributes().get("label");
        if (text == null && fromValue) {
            Object value = item.getAttributes().get("value");
            if (value != null) {
                text = value.toString();
            }
        }

        if (text == null) {
            text = getInnerItemText(context, item);
        }

        String style = (String)item.getAttributes().get("style");
        if (style != null && style.length() != 0) {
            // wrap with span
            text = "<span style=" + HtmlEncoder.enquote(style, '"') +
                   ">" + text + "</span>";
        }

        return text;
    }

    static String getInnerItemText(FacesContext context, UIComponent item) {
        // encode body of component to get inner text
        ResponseWriter curWriter = context.getResponseWriter();
        StringWriter strWriter = new StringWriter();
        ResponseWriter bufWriter = curWriter.cloneWithWriter(strWriter);

        try {
            context.setResponseWriter(bufWriter);
            for (UIComponent kid : item.getChildren()) {
                kid.encodeAll(context);
            }
            context.setResponseWriter(curWriter);
        } catch (IOException ex) {
            throw new FacesException(ex);
        }

        return strWriter.toString();
    }

    static String getItemIcon(FacesContext context, UIComponent item) {
        String image = (String)item.getAttributes().get("image");
        if (image != null) {
            return context.getApplication().getViewHandler().getResourceURL(context, image);
        } else {
            return null;
        }
    }
}
