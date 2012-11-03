/*
 * $Id: BorderLayoutRenderer.java,v 1.17 2008/03/11 03:21:00 lishaochuan Exp $
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

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.layout.UIBorderLayout;
import org.operamasks.faces.component.layout.RegionConfig;
import org.operamasks.faces.component.layout.Region;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.layout.Facelet;

import static org.operamasks.faces.render.layout.LayoutUtils.*;

/**
 * @deprecated 此类已经被org.operamasks.faces.render.layout.BorderLayoutRenderHandler代替
 */
@Deprecated
public class BorderLayoutRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource.register(rm, "Ext.BorderLayout");
        String id = "urn:borderLayout:" + component.getClientId(FacesContext.getCurrentInstance());
        rm.registerResource(new AbstractResource(id) {
            public int getPriority() {
                // just after system resources but before user resources
                // so user resources can override style rules.
                return LOW_PRIORITY - 100;
            }
            public void encodeBegin(FacesContext context) throws IOException {
                ResponseWriter out = context.getResponseWriter();
                out.startElement("style", null);
                out.writeAttribute("type", "text/css", null);
                out.write("\n");
                out.write("html,body{\n");
                out.write("height:100%;\n");
                out.write("}\n");
                out.endElement("style");
                out.write("\n");
            }
        });
    }

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

        UIBorderLayout layout = (UIBorderLayout)component;
        String clientId = layout.getClientId(context);
        List<Facelet> facelets = layout.getFacelets();

        ResponseWriter out = context.getResponseWriter();

        // render container div if necessary
        if (layout.getContainer() == null) {
            out.startElement("div", component);
            out.writeAttribute("id", clientId, "clientId");
            renderPassThruAttributes(out, component);
            out.endElement("div");
        }
        
        String key = LAYOUT_CONTEXT_ATTR;
        AjaxUpdater updater = getParentUpdater(component);
        if (updater != null) {
            key += updater.getClientId(context);
            LayoutContext parentLctx = getLayoutContext(LAYOUT_CONTEXT_ATTR);
            if (parentLctx.layout != null) {
                parentLctx.nested = layout;
            }
            layout.setContainer(updater.getClientId(context));
        }

        // handle nested layouts
        LayoutContext lctx = getLayoutContext(key);
        UIBorderLayout previous = lctx.push(layout);
        int panelIndex = 0;
        Region preRegion = null;
        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);

            String id;
            if (facelet instanceof UIComponent) {
                id = ((UIComponent)facelet).getClientId(context);
            } else {
                id = clientId + NamingContainer.SEPARATOR_CHAR + i;
            }

            Region region = getFaceletRegion(facelet);
            
            if (preRegion != null && preRegion.name().equals(region.name())) {
                panelIndex++;
            }
            else {
                panelIndex = 0;
            }

            ContentPanel prePanel = lctx.peek();
            String parentId = null;
            if (prePanel != null && prePanel.layout != layout) {
                parentId = prePanel.id;
            }
            // register facelet to be encoded as content panel
            ContentPanel panel = lctx.addPanel(id, region, facelet,parentId);
            if (prePanel == null) {
                prePanel = lctx.push(panel);
            }
            // encode facelet div
            out.startElement("div", null);
            out.writeAttribute("id", id, null);
            out.writeAttribute("class", "x-layout-inactive-content", null);
            Boolean fitToFrame = getFaceletAttribute(facelet, "fitToFrame", Boolean.class);
            if (fitToFrame != null && fitToFrame) {
                out.writeAttribute("style", "width:100%;height:100%", null); 
            }
            facelet.encodeAll(context);
            if (prePanel != null) {
                lctx.pop();
            }
            out.endElement("div");
            out.write("\n");

            facelet.getAttributes().put("panelIndex", panelIndex);
            preRegion = region;
            lctx.nested = null;
        }

        lctx.pop(previous);

        // The top-level layout responsible to encode scripts
        if (previous == null) {
            encodeScript(context, lctx, layout);
            lctx.reset();
        }

        // Encode ajax script
        if (isAjaxResponse(context) && (previous == null)) {
            encodeAjaxScript(context, layout);
        }
    }
    
    private AjaxUpdater getParentUpdater(UIComponent component) {
        while (component != null) {
            if (component instanceof AjaxUpdater)
                return (AjaxUpdater)component;
            component = component.getParent();
        }
        return null;
    }

    private void encodeScript(FacesContext context, LayoutContext lctx, UIBorderLayout topLevel)
        throws IOException
    {
        StringBuilder buf = new StringBuilder();

        // declare script variables
        for (UIBorderLayout layout : lctx.layouts) {
            String jsvar = FacesUtils.getJsvar(context, layout);
            buf.append("\nvar ").append(jsvar).append(",");
            buf.append(getRegionVar(jsvar,Region.north)).append(",");
            buf.append(getRegionVar(jsvar,Region.south)).append(",");
            buf.append(getRegionVar(jsvar,Region.west)).append(",");
            buf.append(getRegionVar(jsvar,Region.east)).append(",");
            buf.append(getRegionVar(jsvar,Region.center)).append(";");;
        }

        buf.append("\nExt.onReady(function(){\n");

        // encode BorderLayouts
        for (UIBorderLayout layout : lctx.layouts) {
            Set<Region> panelRegions = new HashSet<Region>();
            for (Facelet facelet : layout.getFacelets()) {
                panelRegions.add(getFaceletRegion(facelet));
            }
            encodeBorderLayout(buf, context, layout, panelRegions);
            encodeContentPanel(buf, context, lctx.regionPanels.get(layout), layout);
        }

        for (UIBorderLayout layout : lctx.layouts) {
            UIBorderLayout parentLayout = getParentLayout(layout);
            Set<Region> panelRegions = new HashSet<Region>();
            for (Facelet facelet : layout.getFacelets()) {
                panelRegions.add(getFaceletRegion(facelet));
            }
            String jsvar = FacesUtils.getJsvar(context, layout);
            Formatter fmt = new Formatter(buf);
            fmt.format("%s.layout = 'border';\n", jsvar);
            for (Region region : Region.values()) {
                RegionConfig config = layout.getRegionConfig(region);
                if (config != null || panelRegions.contains(region)) {
                    fmt.format("%s.add(%s);\n", jsvar, getRegionVar(jsvar, region));
                }
            }
            if (parentLayout == null) {
                fmt.format("%s.render();\n", jsvar);
            }
        }

        // post config layout regions
        for (UIBorderLayout layout : lctx.layouts) {
            postConfigRegion(buf, context, layout, Region.north);
            postConfigRegion(buf, context, layout, Region.south);
            postConfigRegion(buf, context, layout, Region.west);
            postConfigRegion(buf, context, layout, Region.east);
            postConfigRegion(buf, context, layout, Region.center);
            
            UIBorderLayout parentLayout = getParentLayout(layout);
            if (parentLayout != null) {
                String topLevelJsVar = FacesUtils.getJsvar(context, topLevel);
                String parentJsVar = FacesUtils.getJsvar(context, parentLayout);
                String jsvar = FacesUtils.getJsvar(context, layout);
                Map<Region, List<ContentPanel>> regionPanels = lctx.regionPanels.get(layout);
                Formatter fmt = new Formatter(buf);
                String preParentId = null;
                Set<Region> panelRegions = new HashSet<Region>();
                for (Facelet facelet : layout.getFacelets()) {
                    panelRegions.add(getFaceletRegion(facelet));
                }
                for (Region region : regionPanels.keySet()) {
                    for (ContentPanel panel : regionPanels.get(region)) {
                        if (panel.parentId != null && !panel.parentId.equals(preParentId)) {
                            fmt.format("var %s_targetPanel = %s.find('contentEl','%s')[0];\n", parentJsVar, topLevelJsVar, panel.parentId);
                            for (Region currentRegion : regionPanels.keySet()) {
                                RegionConfig config = layout.getRegionConfig(currentRegion);
                                if (config != null || panelRegions.contains(currentRegion)) {
                                    fmt.format("%s_targetPanel.add(%s);\n", parentJsVar, getRegionVar(jsvar, currentRegion));
                                }
                            }
                            fmt.format("%s_targetPanel.setLayout(new Ext.layout.BorderLayout());\n", parentJsVar);
                            fmt.format("%s.doLayout();\n", topLevelJsVar);
                            preParentId = panel.parentId;
                        }
                    }

                }
            }
        }

        buf.append("});\n");

        // write out script
        ResponseWriter out = context.getResponseWriter();
        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.write(buf.toString());
        out.endElement("script");
    }

    private UIBorderLayout getParentLayout(UIBorderLayout layout) {
        UIComponent component = layout.getParent();
        while (component != null) {
            if (component instanceof UIBorderLayout)
                return (UIBorderLayout)component;
            component = component.getParent();
        }
        return null;
    }

    private void encodeContentPanel(StringBuilder buf, FacesContext context,
            Map<Region, List<ContentPanel>> regionPanels, UIBorderLayout layout) {
        Formatter fmt = new Formatter(buf);
        for (Region region : regionPanels.keySet()) {
            List<ContentPanel> panels = regionPanels.get(region);
            int panelSize = panels.size();
            String jsvar = FacesUtils.getJsvar(context, layout);
            if (panelSize == 1) {
                ContentPanel panel = panels.get(0);
                fmt.format("Ext.apply(%s,\n", getRegionVar(jsvar, region));
                encodeContentPanel(buf, context, panel);
                fmt.format(");\n");
                fmt.format("%s.initComponent();\n", getRegionVar(jsvar, region));
            } else if (panelSize > 1) {
                fmt.format("%s.add(\n", getRegionVar(jsvar, region));
                fmt.format("new Ext.TabPanel({\n");
                fmt.format("deferredRender:false,border:false,activeTab:0,\n");
                fmt.format("tabPosition:'%s',\n",layout.getRegionConfig(region).getTabPosition());
                fmt.format("items:[\n");
                for (ContentPanel panel : panels) {
                    encodeContentPanel(buf, context, panel);
                    buf.append(",\n");
                }
                if (buf.charAt(buf.length()-2) == ',')
                    buf.deleteCharAt(buf.length()-2);
                fmt.format("]}));\n");
            }
        }
    }

    private String getRegionVar(String jsvar, Region region) {
        return jsvar + "_" + region.name();
    }

    private void encodeAjaxScript(FacesContext context, UIBorderLayout layout)
        throws IOException
    {
        List<Facelet> facelets = layout.getFacelets();
        String clientId = layout.getClientId(context);
        String jsvar = FacesUtils.getJsvar(context, layout);
        Formatter fmt = new Formatter();

        for (int i = 0; i < facelets.size(); i++) {
            Facelet facelet = facelets.get(i);

            String id = null;
            if (facelet instanceof UIComponent) {
                id = ((UIComponent)facelet).getClientId(context);
            } else {
                id = clientId + NamingContainer.SEPARATOR_CHAR + i;
            }

            String title = getFaceletAttribute(facelet, "title", String.class);
            if (title != null) {
                fmt.format("%s.find('contentEl','%s')[0].setTitle(%s);\n",
                        jsvar, id, HtmlEncoder.enquote(title));
            }
        }

        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.writeScript(fmt.toString());
    }

    private void encodeBorderLayout(StringBuilder buf, FacesContext context, UIBorderLayout layout, Set<Region> panelRegions) {
        String jsvar = FacesUtils.getJsvar(context, layout);

        String container = layout.getContainer();
        if (container == null) {
            container = "'" + layout.getClientId(context) + "'";
        } else if (!container.equals("document.body")) {
            container = "'" + container + "'";
        }

        Formatter fmt = new Formatter(buf);
        fmt.format("%s = new Ext.Viewport({\n", jsvar);
        fmt.format("contentEl:%s\n", container);
        fmt.format("});\n", jsvar);
                
        configRegion(buf, layout, panelRegions, Region.north);
        configRegion(buf, layout, panelRegions, Region.south);
        configRegion(buf, layout, panelRegions, Region.west);
        configRegion(buf, layout, panelRegions, Region.east);
        configRegion(buf, layout, panelRegions, Region.center);
    }

    private void encodeContentPanel(StringBuilder buf, FacesContext context, ContentPanel panel) {
        String title = getFaceletAttribute(panel.facelet, "title", String.class);
        Boolean autoScroll = getFaceletAttribute(panel.facelet, "autoScroll", Boolean.class);
        Boolean fitToFrame = getFaceletAttribute(panel.facelet, "fitToFrame", Boolean.class);
        Boolean closable = getFaceletAttribute(panel.facelet, "closable", Boolean.class);

        buf.append("{contentEl:'");
        buf.append(panel.id);
        buf.append("'");
        if (title != null)
            buf.append(",title:'").append(title).append("',");
        if (autoScroll != null)
            buf.append("autoScroll:").append(autoScroll).append(",");
        if (fitToFrame != null)
            buf.append("fitToFrame:").append(fitToFrame).append(",");
        if (closable != null)
            buf.append("closable:").append(closable).append(",");

        if (buf.charAt(buf.length()-1) == ',')
            buf.deleteCharAt(buf.length()-1);
        buf.append("}\n");
    }

    private void configRegion(StringBuilder buf, UIBorderLayout layout,
                              Set<Region> panelRegions, Region region)
    {
        RegionConfig config = layout.getRegionConfig(region);
        if (config != null || panelRegions.contains(region)) {
            Formatter fmt = new Formatter(buf);
            String jsvar = FacesUtils.getJsvar(FacesContext.getCurrentInstance(), layout);
            fmt.format("%s = new Ext.Panel({\n", getRegionVar(jsvar, region));
            fmt.format("region:'%s',\n", region.name());
            fmt.format("layout:'fit'\n", region.name());
            
            String script = config.toScript();
            if (script != null && !"".equals(script))
                buf.append(",").append(script);
            fmt.format("});\n");
            if (getParentLayout(layout) == null) {
                fmt.format("%s.add(%s);\n", jsvar, getRegionVar(jsvar, region));
            }
        }
    }

    private void postConfigRegion(StringBuilder buf, FacesContext context,
                                  UIBorderLayout layout, Region region)
    {
        RegionConfig config = layout.getRegionConfig(region);
        if (config != null) {
            String jsvar = FacesUtils.getJsvar(context, layout);
            if (config.isHidden() || config.isCollapsed())
                buf.append(jsvar).append(".find('region','").append(region.name())
                   .append("')[0].collapse();\n");
        }
    }

    private Region getFaceletRegion(Facelet facelet) {
        // if a "target" attribute present, then use it.
        Object target = facelet.getAttributes().get("target");
        if (target != null) {
            if (target instanceof Region)
                return (Region)target;
            return Region.valueOf(target.toString());
        }

        // parse the constraints
        if (facelet.getConstraints() != null) {
            String constraints = facelet.getConstraints().toString();
            int sep = constraints.indexOf(";");
            if (sep != -1)
                constraints = constraints.substring(0, sep);
            try {
                return Region.valueOf(constraints.trim());
            } catch (IllegalArgumentException ex) {/*fallthrough*/}
        }

        // heuristicly derive region from facelet name
        if (facelet.getName() != null) {
            String name = facelet.getName();
            if (name.equals("north") || name.equals("header") || name.equals("logo"))
                return Region.north;
            if (name.equals("south") || name.equals("footer") || name.equals("copyright"))
                return Region.south;
            if (name.equals("west") || name.equals("navigation") || name.equals("menu"))
                return Region.west;
            if (name.equals("east"))
                return Region.east;
        }

        // default to put content into center
        return Region.center;
    }

    // Inner classes that handle nested layout

    private static class ContentPanel {
        /** The panel id */
        String id;

        /** The panel region. */
        Region region;

        /** The layout that this panel belongs. */
        UIBorderLayout layout;

        /** The nested layout that this panel contains. */
        UIBorderLayout nested;

        /** The facelet to render panel. */
        Facelet facelet;
        
        String parentId;
    }

    private static class LayoutContext {
        /** The current layout to be rendered. */
        UIBorderLayout layout;

        /** Set by nested layout. */
        UIBorderLayout nested;

        /** Register all layouts to be rendered. */
        List<UIBorderLayout> layouts = new ArrayList<UIBorderLayout>();

        /** Register all content panels to be rendered. */
        List<ContentPanel> panels = new ArrayList<ContentPanel>();
        
        Stack<ContentPanel> prePanel = new Stack<ContentPanel>();
        
        Map<UIBorderLayout,Map<Region,List<ContentPanel>>> regionPanels = new HashMap<UIBorderLayout,Map<Region,List<ContentPanel>>>();
        
        LayoutContext() {
        }
        public ContentPanel peek() {
            return this.prePanel.empty() ? null : this.prePanel.peek();
        }
        public UIBorderLayout push(UIBorderLayout layout) {
            UIBorderLayout previous = this.layout;
            this.layout = layout;
            this.nested = null;
            layouts.add(layout);
            return previous;
        }

        public ContentPanel push(ContentPanel panel) {
            return this.prePanel.push(panel);
        }

        
        public ContentPanel pop() {
            return this.prePanel.empty() ? null : this.prePanel.pop();
        }

        public void pop(UIBorderLayout previous) {
            this.nested = this.layout;
            this.layout = previous;
        }

        public ContentPanel addPanel(String id, Region region, Facelet facelet, String parentId) {
            ContentPanel panel = new ContentPanel();
            panel.layout = this.layout;
            panel.nested = this.nested;
            panel.id = id;
            if (this.prePanel != null) {
                panel.parentId = parentId;
            }
            panel.region = region;
            panel.facelet = facelet;
            panels.add(panel);
            Map<Region,List<ContentPanel>> lrp = regionPanels.get(this.layout);
            if (lrp == null) {
                lrp = new HashMap<Region, List<ContentPanel>>();
                lrp.put(Region.north, new ArrayList<ContentPanel>());
                lrp.put(Region.south, new ArrayList<ContentPanel>());
                lrp.put(Region.west, new ArrayList<ContentPanel>());
                lrp.put(Region.east, new ArrayList<ContentPanel>());
                lrp.put(Region.center, new ArrayList<ContentPanel>());
            }
            lrp.get(region).add(panel);
            regionPanels.put(this.layout, lrp);
            return panel;
        }

        public void reset() {
            layout = null;
            nested = null;
            layouts.clear();
            panels.clear();
        }
    }

    private static final String LAYOUT_CONTEXT_ATTR = "org.operamasks.faces.BORDER_LAYOUT_STACK";

    private static LayoutContext getLayoutContext(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();

        LayoutContext lctx = (LayoutContext)requestMap.get(key);
        if (lctx == null) {
            lctx = new LayoutContext();
            requestMap.put(key, lctx);
        }
        return lctx;
    }
}
