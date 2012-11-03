/*
 * $Id: DataViewRenderer.java,v 1.9 2008/01/08 05:17:19 lishaochuan Exp $
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
import javax.faces.component.UIColumn;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;

import java.io.IOException;
import java.util.List;
import java.util.Formatter;

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.UIDataView;
import org.operamasks.faces.util.HtmlEncoder;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.*;
import static org.operamasks.faces.util.FacesUtils.*;

public class DataViewRenderer extends HtmlRenderer
    implements ResourceProvider
{
    protected static final String SELECTED_ROW_PARAM = NamingContainer.SEPARATOR_CHAR + "_selectedRow";

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource.register(rm, "Ext.View", "Ext.data.Store");
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIDataView view = (UIDataView)component;
        view.setRowIndex(-1); // reset data

        String clientId;
        synchronized (view) {
            view.setRowIndex(-1); // reset data
            clientId = component.getClientId(context);
        }

        ResponseWriter out = context.getResponseWriter();

        String containerId = view.getContainer();
        // encode view container
        if(containerId == null) {
            out.startElement("div", component);
            out.writeAttribute("id", clientId, "clientId");
            renderPassThruAttributes(out, component, "rows");
            out.endElement("div");
            out.write("\n");
        }

        // encode data view script, must do it here to synchronize with pagers
        encodeScript(context, component);
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        // No contents in container
    }

    private void encodeScript(FacesContext context, UIComponent component) {
        String template = getTemplate(context, component);
        if (template == null) {
            throw new FacesException("Missing required template attribute or facet.");
        }

        ResourceManager rm = ResourceManager.getInstance(context);
        YuiExtResource resource = (YuiExtResource)rm.getRegisteredResource(YuiExtResource.RESOURCE_ID);
        assert resource != null;

        UIDataView view = (UIDataView)component;
        String clientId;
        synchronized (view) {
            view.setRowIndex(-1); // reset data
            clientId = component.getClientId(context);
        }

        List<UIColumn> columns = getColumns(component);

        String jsvar = resource.allocVariable(component);
        String dsvar = resource.allocTempVariable();

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        // Create data store
        fmt.format("%s = new Ext.data.SimpleStore({data:[\n", dsvar);
        buf.append(encodeArrayData(context, view, columns));
        buf.append("],\nfields:[");
        buf.append(encodeRecordDefinition(columns));
        buf.append("]});\n");

        String containerId = view.getContainer();
        if(containerId == null) {
            containerId = clientId;
        }
        // Create the data view component
        fmt.format("%s = new Ext.DataView({itemSelector:'',store:%s,tpl:new Ext.XTemplate('<tpl for=\".\">',%s,'</tpl>')", 
        		jsvar, dsvar, HtmlEncoder.enquote(template.trim()));
        encodeViewConfig(fmt, view, resource);
        buf.append("});\n");
        
        // Create the panel component
        fmt.format("new Ext.Panel({renderTo:'%s', items:%s, border:false, bodyBorder:false", containerId, jsvar);
        buf.append("});\n");

        resource.releaseVariable(dsvar);
        resource.releaseVariable(jsvar);
        resource.addInitScript(buf.toString());
    }

    protected void encodeViewConfig(Formatter fmt, UIDataView view, YuiExtResource resource) {
        UIComponent empty = view.getFacet("empty");
        if (empty != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            String emptyText = encodeComponent(context, empty);
            fmt.format(",emptyText:%s", HtmlEncoder.enquote(emptyText));
        }
    }

    protected String getTemplate(FacesContext context, UIComponent component) {
        String template = (String)component.getAttributes().get("template");
        if (template != null) {
            return template;
        }

        UIComponent facet = component.getFacets().get("template");
        if (facet != null) {
            return encodeComponent(context, facet);
        }

        return null;
    }
}
