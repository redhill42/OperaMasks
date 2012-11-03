/*
 * $Id: MenuRenderer.java,v 1.8 2007/12/11 04:20:12 jacky Exp $
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

import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.encodeItemConfig;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.getItemText;
import static org.operamasks.faces.render.widget.yuiext.MenuRendererHelper.getParentMenu;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.component.widget.menu.UICheckMenuItem;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;
import org.operamasks.faces.component.widget.menu.UILinkMenuItem;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.component.widget.menu.UIRadioMenuItem;
import org.operamasks.faces.component.widget.menu.UITextMenuItem;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;

public class MenuRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        YuiExtResource resource = YuiExtResource.register(rm);

        Boolean isNew = (Boolean) component.getAttributes().get("isNew");
        if ((isNew == null || !isNew) && isAjaxResponse(context)){
            return;
        }
        
        encodeMenu(context, resource, component);

        // replace renderer for child items
        for (UIComponent kid : component.getChildren()) {
            if (kid.isRendered()) {
                String rendererType = getMenuItemRendererType(kid);
                if (rendererType != null) {
                    kid.setRendererType(rendererType);
                }
            }
        }

        // need state transfer for ajax request
        ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxHtmlResponseWriter) {
            ((AjaxHtmlResponseWriter)out).setViewStateChanged();
        }
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        if (null == context || null == component) {
            throw new NullPointerException();
        }
        if (!component.isRendered()) {
            return;
        }

        for (UIComponent child : component.getChildren()) {
            Boolean isNew = (Boolean) child.getAttributes().get("isNew");
            if (isNew == null || !isNew) {
                continue;
            }
            StringWriter strWriter = new StringWriter();
            RenderKit renderKit = context.getRenderKit();
            ResponseWriter out = context.getResponseWriter();
            ResponseWriter inner = renderKit.createResponseWriter(strWriter, null, "UTF-8");
            context.setResponseWriter(inner);
            ResourceManager rm = ResourceManager.getInstance(context);
            try {
                inner.startDocument();
                rm.consumeResources(context, child);
                rm.encodeEnd(context);
                rm.reset();
                inner.endDocument();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null) {
                context.setResponseWriter(out);
            }
            ComponentOperationManager.getInstance(context).addEndScript(strWriter.toString());
            child.getAttributes().remove("isNew");
        }
    }

    protected void encodeMenu(FacesContext context, YuiExtResource resource, UIComponent component) {
        String clientId = component.getClientId(context);
        String jsvar = FacesUtils.getJsvar(context, component);

        Formatter fmt = new Formatter();
        fmt.format("%s = new Ext.menu.Menu({id:'%s'});\n", jsvar, clientId);

        UIMenu parentMenu = getParentMenu(component);
        if (parentMenu != null) {
            String text = getItemText(context, component, false);
            String parentJsVar = FacesUtils.getJsvar(context, parentMenu);
            String itemJsVar = jsvar + "_item";
            fmt.format("%s = new Ext.menu.Item({", itemJsVar);
            encodeItemConfig(fmt, context, component, text);
            fmt.format(",menu:%s", jsvar);
            fmt.format("});\n");
            fmt.format("%s.addItem(%s);", parentJsVar, itemJsVar);
        }
        
        component.getAttributes().remove("isNew");

        resource.addPackageDependency("Ext.menu.Menu");
        resource.addVariable(jsvar);
        resource.addInitScript(fmt.toString());
    }

    protected String getMenuItemRendererType(UIComponent item) {
        String family = item.getFamily();
        String rendererType = item.getRendererType();

        // Command link component rendered as a command menu item
        if ("javax.faces.Command".equals(family) && "javax.faces.Link".equals(rendererType)) {
            return UICommandMenuItem.MENU_RENDERER_TYPE;
        }

        // Output link component rendered as a link menu item
        if ("javax.faces.Output".equals(family) && "javax.faces.Link".equals(rendererType)) {
            return UILinkMenuItem.MENU_RENDERER_TYPE;
        }

        // Output text component rendered as a text menu item
        if ("javax.faces.Output".equals(family) && "javax.faces.Text".equals(rendererType)) {
            return UITextMenuItem.MENU_RENDERER_TYPE;
        }

        // Checkbox rendered as a check menu item
        if ("javax.faces.SelectBoolean".equals(family) && "javax.faces.Checkbox".equals(rendererType)) {
            return UICheckMenuItem.MENU_RENDERER_TYPE;
        }

        // Radio button rendered as a radio menu item
        if ("javax.faces.SelectOne".equals(family) && "javax.faces.Radio".equals(rendererType)) {
            return UIRadioMenuItem.MENU_RENDERER_TYPE;
        }

        // UISeparator rendered as a menu separator
        if (UISeparator.COMPONENT_FAMILY.equals(family)) {
            return UISeparator.MENU_RENDERER_TYPE;
        }

        // Default renderer type
        return null;
    }
    
    public void addMenu(FacesContext context, UIComponent component) {
        Boolean isNew = (Boolean) component.getAttributes().get("isNew");
        if (isNew == null || isNew) {
            return;
        }
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        Integer index = (Integer) cm.getAttributes().get(component.getId()+":addMenu:index");
        UIComponent newOne = (UIComponent) cm.getAttributes().get(component.getId()+":addMenu:menu");
        
        if (index < 0) {
            index = 0;
        }
        component.getChildren().add(newOne);
        
        StringWriter strWriter = new StringWriter();
        RenderKit renderKit = context.getRenderKit();
        ResponseWriter out = context.getResponseWriter();
        ResponseWriter inner = renderKit.createResponseWriter(strWriter, null, "UTF-8");
        context.setResponseWriter(inner);
        ResourceManager rm = ResourceManager.getInstance(context);
        try {
            inner.startDocument();
            rm.consumeResources(context, newOne);
            rm.encodeEnd(context);
            rm.reset();
            inner.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out != null) {
            context.setResponseWriter(out);
        }
        cm.addEndScript(strWriter.toString());
    }

}
