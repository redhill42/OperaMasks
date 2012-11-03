/*
 * $Id: AjaxChooseSkinRenderer.java,v 1.5 2007/07/02 07:38:07 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Formatter;
import java.io.IOException;

import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.render.resource.SkinDescriptor;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.component.widget.UIChooseSkin;

public class AjaxChooseSkinRenderer extends ChooseSkinRenderer
{
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxResponse(context)) {
            // refresh page only if skin changed
            if (((UIChooseSkin)component).isLocalValueSet()) {
                renderAjaxResponse(context, component);
            }
        } else {
            super.encodeEnd(context, component);
        }
    }

    private void renderAjaxResponse(FacesContext context, UIComponent component)
        throws IOException
    {
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();

        // set current value of component
        String skin = getCurrentValue(context, component);
        if (skin == null) skin = SkinManager.DEFAULT_SKIN;
        out.writeAttributeScript(component.getClientId(context), "value", skin);

        ResourceManager rm = ResourceManager.getInstance(context);
        SkinManager skinManager = SkinManager.getInstance(context);

        // replace skin related stylesheets
        StringBuilder buf = new StringBuilder();
        Formatter f = new Formatter(buf);
        buf.append("(function(){");

        // remove old skin stylesheets
        buf.append("var doc = document;" +
                   "var h = doc.getElementsByTagName(\"head\")[0];" +
                   "var els = h.childNodes;" +
                   "var el;" +
                   "for (var i = 0; i < els.length; i++) {" +
                   "if (els[i].className == 'x-skin')" +
                   "els[i].parentNode.removeChild(els[i]);" +
                   "}\n");

        // replace default Ext stylesheet, this must be done to refresh style rules
        f.format("el = doc.createElement(\"link\");" +
                 "el.setAttribute(\"class\", \"x-skin\");" +
                 "el.setAttribute(\"rel\", \"stylesheet\");" +
                 "el.setAttribute(\"type\", \"text/css\");" +
                 "el.setAttribute(\"href\", \"%s\");" +
                 "h.appendChild(el);",
                 rm.getSkinResourceURL(skin, "/yuiext/css/ext-all.css"));

        f.format("el = doc.createElement(\"link\");" +
                 "el.setAttribute(\"class\", \"x-skin\");" +
                 "el.setAttribute(\"rel\", \"stylesheet\");" +
                 "el.setAttribute(\"type\", \"text/css\");" +
                 "el.setAttribute(\"href\", \"%s\");" +
                 "h.appendChild(el);",
                 rm.getSkinResourceURL(skin, "/yuiext/css/ext-extra.css"));

        // add new skin stylesheets
        SkinDescriptor skinDesc = skinManager.getSkin(skin);
        if (skinDesc != null) {
            String extraFiles = skinDesc.getProperty("yuiext.files"); // FIXME
            if (extraFiles != null) {
                for (String file : extraFiles.split(",")) {
                    file = file.trim();
                    String url = rm.getSkinResourceURL(skin, file);
                    if (file.endsWith(".js")) {
                        f.format("el = doc.createElement(\"script\");" +
                                 "el.setAttribute(\"class\", \"x-skin\");" +
                                 "el.setAttribute(\"type\", \"text/javascript\");" +
                                 "el.setAttribute(\"src\", \"%s\");" +
                                 "h.appendChild(el);",
                                 url);
                    } else if (file.endsWith(".css")) {
                        f.format("el = doc.createElement(\"link\");" +
                                 "el.setAttribute(\"class\", \"x-skin\");" +
                                 "el.setAttribute(\"rel\", \"stylesheet\");" +
                                 "el.setAttribute(\"type\", \"text/css\");" +
                                 "el.setAttribute(\"href\", \"%s\");" +
                                 "h.appendChild(el);",
                                 url);
                    }
                }
            }
        }

        // refresh document
        // TODO: save and restore state of components
        buf.append("window.location.reload(false);\n");

        buf.append("})();\n");
        out.writeScript(buf.toString());
    }
}
