/*
 * $Id: AjaxPagingLinkRenderer.java,v 1.13 2008/01/16 06:57:44 yangdong Exp $
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

import static org.operamasks.resources.Resources.UI_PAGING_LINK_FIRST_PAGE;
import static org.operamasks.resources.Resources.UI_PAGING_LINK_LAST_PAGE;
import static org.operamasks.resources.Resources.UI_PAGING_LINK_NEXT_PAGE;
import static org.operamasks.resources.Resources.UI_PAGING_LINK_PREV_PAGE;
import static org.operamasks.resources.Resources._T;

import java.io.IOException;
import java.util.Formatter;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class AjaxPagingLinkRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIPager pager = (UIPager)component;
        String clientId = component.getClientId(context);
        UIData data;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", component);
        encodeTheme((UIPager)component, out);
        out.writeAttribute("id", clientId, "clientId");
        renderPassThruAttributes(out, component);
        out.endElement("div");
        out.write("\n");

        if (pager.getTargetDataStore() == null && (data = getUIData(context, component)) != null) {
            if (out instanceof AjaxResponseWriter) {
                String jsvar = FacesUtils.getJsvar(context, component);
                String instruction = String.format(
                    "OM.ajax.addRequestParameter('%s','%d:%d');\n" +
                    "%s.repaint(%d,%d);\n",
                    clientId, data.getFirst(), data.getRows(),
                    jsvar, data.getFirst(), data.getRowCount());
                ((AjaxResponseWriter)out).writeActionScript(instruction);
            }
        }
    }

    private void encodeTheme(UIPager pager, ResponseWriter out) throws IOException {
    	String theme = pager.getTheme();
    	if(theme != null){
        	out.writeAttribute("class", PagingLinkRendererHelper.getThemeCssClass(theme), "");
        }
	}

	public void provideResource(ResourceManager rm, UIComponent component) {
        final YuiExtResource resource = YuiExtResource.register(rm, "Ext.om.PagingLink");
        final FacesContext context = FacesContext.getCurrentInstance();
        final UIPager pager = (UIPager)component;
        rm.registerSkinCssResource(PagingLinkRendererHelper.PAGELINK_CSS);

        String dsvar = pager.getBindingDataStore();
        if (dsvar != null) {
            String jsvar = resource.allocVariable(component);
            String script = encodeScript(context, pager, jsvar, dsvar, null);
            resource.addInitScript(script);
            return;
        }

        // If the UIData component doesn't support AJAX data store then we
        // will render another paging implementation. Because the Pager may
        // appear before UIData so we must render the paging link script
        // after UIData component has been rendered.
        final UIData data = getUIData(context, pager);
        if (data != null) {
            int pageSize = data.getRows();
            if (pageSize <= 0) {
                pageSize = pager.getPageSize();
                if (pageSize <= 0) {
                    pageSize = pager.getPageSize();
                }
            }
            pager.setPageSize(pageSize);
            data.setRows(pageSize);

            String id = "urn:paging-link:" + pager.getClientId(context);
            rm.registerResource(new AbstractResource(id) {
                public int getPriority() {
                    return resource.getPriority() - 1; // render before init script
                }
                public void encodeEnd(FacesContext context) {
                    if (!pager.isBound()) {
                        String jsvar = resource.allocVariable(pager);
                        String script = encodeScript(context, pager, jsvar, null, data);
                        resource.addInitScript(script);
                    }
                }
            });
        }
    }

    private String encodeScript(FacesContext context, UIPager pager, String jsvar, String dsvar, UIData data) {
        String clientId = pager.getClientId(context);
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        int pageSize = pager.getPageSize();
        if (pageSize <= 0) {
            pageSize = UIPager.DEFAULT_PAGESIZE;
        }

        fmt.format("%s=new Ext.om.PagingLink('%s',{pageSize:%d", jsvar, clientId, pageSize);

        fmt.format(",callback:function(start,limit){");
        if (dsvar != null) {
            fmt.format("%s.load({params:{start:start,limit:limit}});", dsvar);
        } else {
            fmt.format("OM.ajax.addRequestParameter('%s',start+':'+limit);", clientId);
            buf.append(encodeAjaxSubmit(context, pager));
        }
        fmt.format("}");

        String totalText = getText(context, pager, "total");
        if (totalText != null)
            buf.append(",totalText:").append(HtmlEncoder.enquote(totalText));

        Boolean showFirst = (Boolean)pager.getAttributes().get("showFirst");
        String firstText = getText(context, pager, "first");
        if ((showFirst == null && firstText != null) || (showFirst != null && showFirst)) {
            if (firstText == null)
                firstText = _T(UI_PAGING_LINK_FIRST_PAGE);
            buf.append(",showFirst:true,firstText:").append(HtmlEncoder.enquote(firstText));
        }

        Boolean showLast = (Boolean)pager.getAttributes().get("showLast");
        String lastText = getText(context, pager, "last");
        if ((showLast == null && lastText != null) || (showLast != null && showLast)) {
            if (lastText == null)
                lastText = _T(UI_PAGING_LINK_LAST_PAGE);
            buf.append(",showLast:true,lastText:").append(HtmlEncoder.enquote(lastText));
        }

        String prevText = getText(context, pager, "previous");
        if (prevText == null)
            prevText = _T(UI_PAGING_LINK_PREV_PAGE);
        buf.append(",prevText:").append(HtmlEncoder.enquote(prevText));

        String nextText = getText(context, pager, "next");
        if (nextText == null)
            nextText = _T(UI_PAGING_LINK_NEXT_PAGE);
        buf.append(",nextText:").append(HtmlEncoder.enquote(nextText));

        String numberText = getText(context, pager, "number");
        if (numberText != null)
            buf.append(",numberText:").append(HtmlEncoder.enquote(numberText));

        Object shownNumbers = pager.getAttributes().get("shownNumbers");
        if (shownNumbers != null)
            buf.append(",shownNumbers:").append(shownNumbers);

        buf.append("});\n");

        if (dsvar != null) {
            fmt.format("%s.bind(%s);\n", jsvar, dsvar);
        } else {
            fmt.format("OM.ajax.addRequestParameter('%s','%d:%d');\n",
                       clientId, data.getFirst(), data.getRows());
            fmt.format("%s.repaint(%d,%d);\n",
                       jsvar, data.getFirst(), data.getRowCount());
        }

        return buf.toString();
    }

    public void decode(FacesContext context, UIComponent component) {
        String clientId = component.getClientId(context);
        String param = context.getExternalContext().getRequestParameterMap().get(clientId);

        if (param != null && param.length() != 0) {
            UIPager pager = (UIPager)component;
            UIData data = getUIData(context, component);
            if (data != null) {
                try {
                    int sep = param.indexOf(':');
                    int first = Integer.parseInt(param.substring(0, sep));
                    int limit = Integer.parseInt(param.substring(sep+1));
                    int maxRows = data.getRowCount();

                    if (first < 0) {
                        first = 0;
                    } else if (maxRows > 0 && first > maxRows) {
                        first = (maxRows-1) / limit * limit;
                    }

                    pager.setStart(first);
                    pager.setPageSize(limit);
                    data.setFirst(first);
                    data.setRows(limit);
                } catch (NumberFormatException ex) {/*ignored*/}
            }
        }
    }

    private String getText(FacesContext context, UIComponent component, String name) {
        UIComponent facet = component.getFacet(name);
        if (facet != null) {
            return FacesUtils.encodeComponent(context, facet);
        }
        return null;
    }

    private UIData getUIData(FacesContext context, UIComponent component) {
        String forId = (String)component.getAttributes().get("for");
        if (forId != null) {
            Map<String, Object> sessionmap = context.getExternalContext().getSessionMap();
            UIComponent target = (UIComponent) sessionmap.get(forId+UIDataGrid.GRID_COMPONENT_KEY);
            if (target == null) {
                target = FacesUtils.getForComponent(context, forId, component);
            }
            if (target instanceof UIData) {
                return (UIData)target;
            }
        }
        return null;
    }
}
