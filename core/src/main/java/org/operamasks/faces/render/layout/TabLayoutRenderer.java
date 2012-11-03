/*
 * $Id: TabLayoutRenderer.java,v 1.8 2008/03/11 03:21:00 lishaochuan Exp $
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

package org.operamasks.faces.render.layout;

import static org.operamasks.faces.render.layout.LayoutUtils.getFaceletAttribute;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.layout.TabLayout;
import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已经被org.operamasks.faces.render.layout.TabLayoutRenderHandler代替
 */
@Deprecated
public class TabLayoutRenderer extends HtmlRenderer
    implements ResourceProvider
{
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        TabLayout layout = (TabLayout)component;
        String clientId = component.getClientId(context);
        List<Facelet> facelets = layout.getFacelets();
        ResponseWriter out = context.getResponseWriter();

        // render layout div start
        out.startElement("div", component);
        out.writeAttribute("id", clientId, "clientId");
        renderPassThruAttributes(out, component);

        // render facelets
        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);
            if (facelet instanceof UIComponent) {
                UIComponent tab = (UIComponent)facelet;
                out.startElement("div", tab);
                out.writeAttribute("id", tab.getClientId(context), "clientId");
                renderPassThruAttributes(out, tab, "title,disabled");
                facelet.encodeAll(context);
                out.endElement("div");
                out.write("\n");
            } else {
                String id = clientId + NamingContainer.SEPARATOR_CHAR + i;
                out.startElement("div", null);
                out.writeAttribute("id", id, null);
                facelet.encodeAll(context);
                out.endElement("div");
                out.write("\n");
            }
        }

        // render layout div end
        out.endElement("div");
        out.write("\n");
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.TabPanel");

        FacesContext context = FacesContext.getCurrentInstance();
        TabLayout layout = (TabLayout)component;
        String clientId = component.getClientId(context);
        List<Facelet> facelets = layout.getFacelets();

        // render script
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        String jsvar = resource.allocVariable(component);
        String tvar = resource.allocTempVariable();

        // Create and configure Ext.TabPanel object
        fmt.format("%s=new Ext.TabPanel({\n", jsvar);
        fmt.format("applyTo:'%s',", clientId);
        fmt.format("deferredRender:false,");
        fmt.format("activeTab: 0,");

        String tabPosition = layout.getTabPosition();
        boolean resizeTabs = layout.getResizeTabs();
        int minTabWidth = layout.getMinTabWidth();
        int maxTabWidth = layout.getMaxTabWidth();
        int preferredTabWidth = layout.getPreferredTabWidth();

        if (tabPosition != null) {
            fmt.format("tabPosition:'%s',", tabPosition);
        }
        if (resizeTabs || minTabWidth > 0 || maxTabWidth > 0 || preferredTabWidth > 0) {
            buf.append("resizeTabs:true,");
            if (layout.getMonitorResize())
                buf.append("monitorResize:true,");
            if (minTabWidth > 0)
                fmt.format("minTabWidth:%d,", minTabWidth);
            if (maxTabWidth > 0)
                fmt.format("maxTabWidth:%d,", maxTabWidth);
            if (preferredTabWidth > 0)
                fmt.format("preferredTabWidth:%d,", preferredTabWidth);
        }
        if (buf.charAt(buf.length()-1) == ',') {
            buf.deleteCharAt(buf.length()-1);
        }
        buf.append("});\n");

        String bodyStyle = (String)component.getAttributes().get("bodyStyle");
        String bodyStyleClass = (String)component.getAttributes().get("bodyStyleClass");
        String itemBodyStyle = (String)component.getAttributes().get("itemBodyStyle");
        String itemBodyStyleClass = (String)component.getAttributes().get("itemBodyStyleClass");

        if (bodyStyle != null)
            fmt.format("%s.bodyEl.applyStyles(%s);\n", jsvar, HtmlEncoder.enquote(bodyStyle, '\''));
        if (bodyStyleClass != null)
            fmt.format("%s.bodyEl.addClass('%s');\n", jsvar, bodyStyleClass);

        // Add tabs to TabPanel
        buf.append(jsvar).append(".beginUpdate();\n");

        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);

            String id;
            if (facelet instanceof UIComponent) {
                id = ((UIComponent)facelet).getClientId(context);
            } else {
                id = clientId + NamingContainer.SEPARATOR_CHAR + i;
            }

            String title = getFaceletAttribute(facelet, "title", String.class);
            String tooltip = getFaceletAttribute(facelet, "tooltip", String.class);
            Boolean closable = getFaceletAttribute(facelet, "closable", Boolean.class);
            Boolean disabled = getFaceletAttribute(facelet, "disabled", Boolean.class);
            Boolean autoScroll = getFaceletAttribute(facelet, "autoScroll", Boolean.class);
            autoScroll = autoScroll == null ? true : autoScroll.booleanValue();
            Object onactivate = facelet.getAttributes().get("onactivate");
            Object ondeactivate = facelet.getAttributes().get("ondeactivate");
            Object onclose = facelet.getAttributes().get("onclose");
            Object onbeforeclose = facelet.getAttributes().get("onbeforeclose");

            if (title == null) title = "";
            title = HtmlEncoder.enquote(title, '\'');

            fmt.format("%s=%s.add(new Ext.Panel({\n", tvar, jsvar);
            fmt.format("contentEl:'%s',\n", id);
            fmt.format("title:%s,\n", title);
            fmt.format("autoScroll:%b,\n", autoScroll);
            fmt.format("closable:%b\n", closable);
            fmt.format("}));\n");

//            if (tooltip != null)
//                fmt.format("%s.setTooltip(%s);\n", tvar, HtmlEncoder.enquote(tooltip, '\''));
            if (disabled != null && disabled)
                fmt.format("%s.disable();\n", tvar);

            if (itemBodyStyle != null)
                fmt.format("%s.bodyEl.applyStyles(%s);\n", tvar, HtmlEncoder.enquote(itemBodyStyle, '\''));
            if (itemBodyStyleClass != null)
                fmt.format("%s.bodyEl.addClass('%s');\n", tvar, itemBodyStyleClass);

            if (onactivate != null)
                fmt.format("%s.on('activate',function(){%s});\n", tvar, onactivate);
            if (ondeactivate != null)
                fmt.format("%s.on('deactivate',function(){%s});\n", tvar, ondeactivate);
            if (onclose != null)
                fmt.format("%s.on('close',function(){%s});\n", tvar, onclose);
            if (onbeforeclose != null) {
                fmt.format("%s.on('beforeclose',function($1,e){" +
                           "e.cancel=function(){%s}.apply($1)==false;});\n",
                           tvar, onbeforeclose);
            }
        }

        String syncHeight = getSyncHeight(component);
        if (syncHeight != null)
            fmt.format("%s.syncHeight(%s);\n", jsvar, syncHeight);

        if (facelets.size() > 0)
            fmt.format("%s.setActiveTab(0);\n", jsvar);

        fmt.format("%s.endUpdate();\n", jsvar);

        resource.addInitScript(buf.toString());
        resource.releaseVariable(tvar);
        resource.releaseVariable(jsvar);
    }

    private String getSyncHeight(UIComponent component) {
        // The syncHeight attribute can be one of following:
        //   true|false: height is synchronized with container height
        //   number:     height is synchronized with the give number value
        //   other:      invalid
        //
        // returns:
        //   null:       no sync
        //   "":         default sync
        //   "num":      sync with "num" value

        Object syncHeight = component.getAttributes().get("syncHeight");

        if (syncHeight == null) {
            if (component.getAttributes().get("height") != null) {
                return "";
            } else {
                return null;
            }
        }

        if (syncHeight instanceof Boolean) {
            return ((Boolean)syncHeight) ? "" : null;
        }

        if (syncHeight instanceof Integer) {
            return syncHeight.toString();
        }

        String s = syncHeight.toString().trim();
        if (s.length() == 0) {
            return "";
        } else if (s.equalsIgnoreCase("true")) {
            return "";
        } else if (s.equalsIgnoreCase("false")) {
            return null;
        } else {
            try {
                Integer.parseInt(s);
                return s;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
