/*
 * $Id: DynamicAccordionLayoutRenderer.java,v 1.7 2008/03/11 03:21:00 lishaochuan Exp $
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
 * 
 */
package org.operamasks.faces.render.layout;

import static org.operamasks.faces.render.layout.LayoutUtils.getFaceletAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import org.operamasks.faces.component.layout.DynamicAccordionLayout;
import org.operamasks.faces.component.layout.DynamicAccordionPanel;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.HtmlEncoder;

/**
 * @deprecated 此类已废弃
 */
@Deprecated
public class DynamicAccordionLayoutRenderer extends AccordionLayoutRenderer
{
    private final static String SELF_CHILDREN = "_selfChildren";
    private final static String SELF_FACETS = "_selfFacets";
    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        DynamicAccordionLayout layout = (DynamicAccordionLayout)component;
        DynamicAccordionPanel dynamicPanel = getDynamiAccordionPanel(layout);
        
        if(dynamicPanel != null) {
            resetChildren(context, dynamicPanel);
            // remove self from parent
            layout.getFacelets().remove(dynamicPanel);
            ResponseWriter out = context.getResponseWriter();

            DataModel model = layout.getDataModel();
            int rows = model.getRowCount();
            int rowIndex = 0;
            for (int curRow = 0; rows == 0 || curRow < rows; curRow++) {
                layout.setRowIndex(rowIndex++);
                if (!model.isRowAvailable()) {
                    break;
                }
                String panelId = dynamicPanel.getClientIds().get(curRow);
                setChildrenId(panelId,dynamicPanel,context,curRow);

                // accordion panel container div
                out.startElement("div", dynamicPanel);
                out.writeAttribute("id", panelId, "clientId");
                if (dynamicPanel != null) {
                    renderPassThruAttributes(out, dynamicPanel, "title");
                }

                // accordion panel title div
                out.startElement("div", dynamicPanel);
                out.writeText(getFaceletAttribute(dynamicPanel, "title", String.class), dynamicPanel, "title");
                out.endElement("div");

                // accordion panel content div
                out.startElement("div", dynamicPanel);
                dynamicPanel.encodeAll(context);
                out.endElement("div");

                // end of accordion panel container div
                out.endElement("div");
                out.write("\n");
                consumeResources(ResourceManager.getInstance(context),dynamicPanel,context);
            }
            layout.setRowIndex(-1);
        }
        super.encodeChildren(context, layout);
    }
    
    @SuppressWarnings("unchecked")
    private void resetChildren(FacesContext context, DynamicAccordionPanel dynamicPanel) {
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        String clientId = dynamicPanel.getClientId(context);
        List<UIComponent> selfChildren = (List<UIComponent>)sessionMap.get(clientId+SELF_CHILDREN);
        Map<String, UIComponent> selfFacets = (Map<String, UIComponent>)sessionMap.get(clientId+SELF_FACETS);
        if(selfChildren == null){
            sessionMap.put(clientId+SELF_CHILDREN, new LinkedList<UIComponent>(dynamicPanel.getChildren()));
        } else {
            dynamicPanel.getChildren().clear();
            dynamicPanel.getChildren().addAll(selfChildren);
        }
        if(selfFacets == null){
            sessionMap.put(clientId+SELF_FACETS, new LinkedHashMap<String, UIComponent>(dynamicPanel.getFacets()));
        } else {
            dynamicPanel.getFacets().clear();
            dynamicPanel.getFacets().putAll(selfFacets);
        }
    }

    private void consumeResources(ResourceManager rm,
            UIComponent panel, FacesContext context) {
        Iterator<UIComponent> kids = panel.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            rm.consumeResources(context, child);
        }
        
    }

    private void setChildrenId(String panelId,
            DynamicAccordionPanel panel , FacesContext context, int curRow) {
        Iterator<UIComponent> kids = panel.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            String childIdentity = String.valueOf(System.identityHashCode(child));
            List<String> childIds = panel.getChildrenClientIds().get(childIdentity);
            String uniqueId = null; 
            if(childIds == null) {
                childIds = new ArrayList<String>();
            }
            if(childIds.size() > curRow) {
                uniqueId = childIds.get(curRow);
            }
            if(uniqueId == null) {
                uniqueId = context.getViewRoot().createUniqueId();
                uniqueId = uniqueId.replaceAll(":","-");
                childIds.add(curRow,uniqueId);
                panel.getChildrenClientIds().put(childIdentity,childIds);
            }
            child.setId(uniqueId);
        }
    }

    @Override
    public void provideResource(ResourceManager rm, UIComponent component) {
        DynamicAccordionLayout layout = (DynamicAccordionLayout)component;
        DynamicAccordionPanel dynamicPanel = getDynamiAccordionPanel(layout);
        // remove self from parent
        layout.getFacelets().remove(dynamicPanel);
        super.provideResource(rm, component);
        if(dynamicPanel == null) {
            return;
        }
        
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.ux.Accordion", "Ext.ux.InfoPanel");
        FacesContext context = FacesContext.getCurrentInstance();
        // encode script
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        String jsvar = resource.allocVariable(layout);
        
        // Create individual accordion panel and add them to the layout
        DataModel model = layout.getDataModel();
        int rows = model.getRowCount();
        int rowIndex = 0;
        for (int curRow = 0; rows == 0 || curRow < rows; curRow++) {
            layout.setRowIndex(rowIndex++);
            if (!model.isRowAvailable()) {
                break;
            }
            DynamicAccordionPanel newOne = new DynamicAccordionPanel();
            String id, var;
            id = newOne.getClientId(context);
            var = resource.allocVariable(newOne);
            dynamicPanel.getClientIds().add(curRow,id);
            dynamicPanel.getJsVars().add(curRow,var);
            fmt.format("%s=new Ext.ux.InfoPanel('%s',{", var, id);
            for (NameAndType nat : AccordionPanelRenderer.PANEL_CONFIGS) {
                Object value = getFaceletAttribute(dynamicPanel, nat.name, nat.type);
                if (value != null) {
                    if (value instanceof String) {
                        value = HtmlEncoder.enquote((String)value);
                    }
                    fmt.format("%s:%s,", nat.name, value);
                }
            }
            if (buf.charAt(buf.length()-1) == ',') {
                buf.setLength(buf.length() - 1);
            }
            buf.append("});\n");

            fmt.format("%s.add(%s);\n", jsvar, var);
            resource.releaseVariable(var);
        }
        rm.setIgnoreChildren(dynamicPanel,true);
        layout.setRowIndex(-1);
        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
    }
    
    private DynamicAccordionPanel getDynamiAccordionPanel(DynamicAccordionLayout layout) {
        // only get the first dynamicAccordionPanel
        for(UIComponent component : layout.getChildren()) {
            if(component instanceof DynamicAccordionPanel) {
                return (DynamicAccordionPanel) component ;
            }
        }
        return null;
    }

}
