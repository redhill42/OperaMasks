/*
 * $Id: ChooseSkinRenderer.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

package org.operamasks.faces.render.widget;

import javax.faces.model.SelectItem;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.servlet.http.Cookie;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;

import org.operamasks.faces.render.html.UIInputRenderer;
import org.operamasks.faces.render.resource.SkinDescriptor;
import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;

public class ChooseSkinRenderer extends UIInputRenderer
    implements ResourceProvider
{
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("select", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("name", component.getClientId(context), "clientId");
        renderPassThruAttributes(out, component);
        out.writeText("\n", null);
        renderOptions(context, component);
        out.endElement("select");
    }

    protected void renderOptions(FacesContext context, UIComponent component)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        List<SelectItem> items = getSelectItems(context);
        String currentValue = getCurrentValue(context, component);

        for (SelectItem item : items) {
            out.startElement("option", null);
            out.writeAttribute("value", item.getValue(), null);
            if (currentValue.equals(item.getValue()))
                out.writeAttribute("selected", true, null);
            if (item.getDescription() != null)
                out.writeAttribute("title", item.getDescription(), null);
            out.writeText(item.getLabel(), null);
            out.endElement("option");
            out.writeText("\n", null);
        }
    }

    public List<SelectItem> getSelectItems(FacesContext context) {
        SkinManager skinManager = SkinManager.getInstance(context);
        Map<String,SkinDescriptor> skins = skinManager.getSkins();
        List<SelectItem> result = new ArrayList<SelectItem>(skins.size());

        // get all available skins from SkinManager
        for (String name : skins.keySet()) {
            SkinDescriptor skin = skins.get(name);
            String label = skin.getDisplayName() != null ? skin.getDisplayName() : skin.getName();
            String description = skin.getDescription();
            result.add(new SelectItem(name, label, description));
        }

        // sort the list by display name
        Collections.sort(result, new Comparator<SelectItem>(){
            public int compare(SelectItem a, SelectItem b) {
                return a.getLabel().compareTo(b.getLabel());
            }});

        // the "default" skin takes the first place
        for (SelectItem item : result) {
            if (SkinManager.DEFAULT_SKIN.equals(item.getValue())) {
                result.remove(item);
                result.add(0, item);
                break;
            }
        }

        return result;
    }

    @Override
    public String getCurrentValue(FacesContext context, UIComponent component) {
        String value = super.getCurrentValue(context, component);
        if (value == null)
            value = SkinManager.DEFAULT_SKIN;
        return value;
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        // set cookie skin before render the page
        String cookie = (String)component.getAttributes().get("cookie");
        if (cookie != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Cookie c = (Cookie)context.getExternalContext().getRequestCookieMap().get(cookie);
            if (c != null) {
                String skin = c.getValue();
                if (skin != null && skin.length() != 0) {
                    SkinManager.setCurrentSkin(context, skin);
                }
            }
        }
    }
}
