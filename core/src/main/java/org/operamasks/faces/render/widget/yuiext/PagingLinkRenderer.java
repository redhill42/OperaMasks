/*
 * $Id: PagingLinkRenderer.java,v 1.6 2007/12/11 04:20:12 jacky Exp $
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
import javax.faces.component.UIData;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Formatter;

import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.faces.util.HtmlEncoder.*;
import static org.operamasks.resources.Resources.*;

public class PagingLinkRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void provideResource(ResourceManager rm, UIComponent component) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIData data = getUIData(context, component);
        if (data == null) {
            return;
        }

        UIPager pager = (UIPager)component;
        String clientId = component.getClientId(context);
        UIForm form = getParentForm(component);

        int pageSize = data.getRows();
        if (pageSize <= 0) {
            pageSize = pager.getPageSize();
            data.setRows(pageSize);
        }

        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String param = paramMap.get(clientId);
        if (param != null && param.length() != 0) {
            try {
                int firstRow = Integer.parseInt(param);
                int maxRows = data.getRowCount();
                if (firstRow < 0) {
                    firstRow = 0;
                } else if (maxRows > 0 && firstRow > maxRows) {
                    firstRow = (maxRows-1) / pageSize * pageSize;
                }
                pager.setStart(firstRow);
                data.setFirst(firstRow);
            } catch (NumberFormatException ex) {/*ignored*/}
        }

        YuiExtResource resource = YuiExtResource.register(rm);
        rm.registerSkinCssResource(PagingLinkRendererHelper.PAGELINK_CSS);
        String var = resource.allocTempVariable();
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        fmt.format("%s = Ext.get('%s').child('ul');\n", var, clientId);
        fmt.format("%s.on('mouseover', function(e){" +
                   "var t = e.getTarget('li', this, true);" +
                   "if(t && t.dom.getAttribute('firstRow') != null){" +
                   "t.addClass('hover');" +
                   "}});\n",
                   var);
        fmt.format("%s.on('mouseout', function(e){" +
                   "var t = e.getTarget('li', this, true);" +
                   "if(t && t.dom.getAttribute('firstRow') != null){" +
                   "t.removeClass('hover');" +
                   "}});\n",
                   var);
        fmt.format("%s.on('click', function(e){" +
                   "var t = e.getTarget('li', this, true);" +
                   "var r = t && t.dom.getAttribute('firstRow');" +
                   "if(r != null){",
                   var);
        if (form == null) {
            fmt.format("window.location.href = '%s?%s='+r;",
                       getActionURL(context), encodeURIComponent(clientId));
        } else {
            buf.append(encodeSubmit(context, form, null, enquote(clientId), "r"));
        }
        fmt.format("}e.stopEvent();});\n");

        resource.releaseVariable(var);
        resource.addInitScript(fmt.toString());
    }
    
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIData data = getUIData(context, component);
        if (data == null) {
            return;
        }

        int first = data.getFirst();
        int pageSize = data.getRows();
        int total = data.getRowCount();
        int activePage = (first + pageSize) / pageSize;
        int pages = (total + pageSize - 1) / pageSize;

        ResponseWriter out = context.getResponseWriter();
        out.startElement("div", component);
        encodeTheme((UIPager)component, out);
        out.writeAttribute("id", component.getClientId(context), "clientId");
        renderPassThruAttributes(out, component);

        out.startElement("ul", component);
        out.writeAttribute("class", "x-paging-list", null);

        String totalText = getText(context, component, "total");
        if (totalText != null) {
            totalText = totalText.replace("{0}", String.valueOf(pages))
                                 .replace("{page}", String.valueOf(activePage))
                                 .replace("{pages}", String.valueOf(pages))
                                 .replace("{start}", String.valueOf(first + 1))
                                 .replace("{end}", String.valueOf(Math.min(first + pageSize, total)))
                                 .replace("{total}", String.valueOf(total));

            out.startElement("li", null);
            out.writeAttribute("class", "x-total-page", null);
            out.write(totalText);
            out.endElement("li");
        }

        Boolean showFirst = (Boolean)component.getAttributes().get("showFirst");
        String firstText = getText(context, component, "first");
        if ((showFirst == null && firstText != null) || (showFirst != null && showFirst)) {
            if (firstText == null) {
                firstText = _T(UI_PAGING_LINK_FIRST_PAGE);
            }
            if (activePage > 1) {
                out.startElement("li", null);
                out.writeAttribute("class", "x-first-page", null);
                out.writeAttribute("firstRow", 0, null);
                out.startElement("a", null);
                out.writeURIAttribute("href", getPageLink(context, component, 0), null);
                out.write(firstText);
                out.endElement("a");
                out.endElement("li");
            } else {
                out.startElement("li", null);
                out.writeAttribute("class", "x-first-page disabled", null);
                out.write(firstText);
                out.endElement("li");
            }
        }

        String prevText = getText(context, component, "previous");
        if (prevText == null) {
            prevText = _T(UI_PAGING_LINK_PREV_PAGE);
        }
        if (activePage > 1) {
            int row = Math.max(first - pageSize, 0);
            out.startElement("li", null);
            out.writeAttribute("class", "x-prev-page", null);
            out.writeAttribute("firstRow", row, null);
            out.startElement("a", null);
            out.writeURIAttribute("href", getPageLink(context, component, row), null);
            out.write(prevText);
            out.endElement("a");
            out.endElement("li");
        } else {
            out.startElement("li", null);
            out.writeAttribute("class", "x-prev-page disabled", null);
            out.write(prevText);
            out.endElement("li");
        }

        Integer shownNumbers = (Integer)component.getAttributes().get("shownNumbers");
        int len = (shownNumbers == null) ? 10 : shownNumbers;
        if (len > 0) {
            String numberText = getText(context, component, "number");

            int startPage = activePage - len/2;
            int endPage = activePage + len/2 - 1;
            if (startPage <= 0) {
                endPage += 1-startPage;
                startPage = 1;
            }
            if (endPage > pages) {
                if ((startPage -= (endPage-pages)) <= 0)
                    startPage = 1;
                endPage = pages;
            }

            int row = (startPage-1) * pageSize;
            for (int page = startPage; page <= endPage; page++, row += pageSize) {
                out.startElement("li", null);
                out.writeAttribute("class", "x-page-number" + (page==activePage ? " selected" : ""), null);
                out.writeAttribute("firstRow", row, null);
                out.startElement("a", null);
                out.writeURIAttribute("href", getPageLink(context, component, row), null);
                if (numberText != null) {
                    out.write(numberText.replace("{0}", String.valueOf(page))
                                        .replace("{page}", String.valueOf(page))
                                        .replace("{start}", String.valueOf(row+1))
                                        .replace("{end}", String.valueOf(Math.min(row+pageSize, total))));
                } else {
                    out.writeText(page, null);
                }
                out.endElement("a");
                out.endElement("li");
            }
        }

        String nextText = getText(context, component, "next");
        if (nextText == null) {
            nextText = _T(UI_PAGING_LINK_NEXT_PAGE);
        }
        if (activePage < pages) {
            int row = first + pageSize;
            out.startElement("li", null);
            out.writeAttribute("class", "x-next-page", null);
            out.writeAttribute("firstRow", row, null);
            out.startElement("a", null);
            out.writeURIAttribute("href", getPageLink(context, component, row), null);
            out.write(nextText);
            out.endElement("a");
            out.endElement("li");
        } else {
            out.startElement("li", null);
            out.writeAttribute("class", "x-next-page disabled", null);
            out.write(nextText);
            out.endElement("li");
        }

        Boolean showLast = (Boolean)component.getAttributes().get("showLast");
        String lastText = getText(context, component, "last");
        if ((showLast == null && lastText != null) || (showLast != null && showLast)) {
            if (lastText == null) {
                lastText = _T(UI_PAGING_LINK_LAST_PAGE);
            }
            if (activePage < pages) {
                int row = (pages-1) * pageSize;
                out.startElement("li", null);
                out.writeAttribute("class", "x-last-page", null);
                out.writeAttribute("firstRow", row, null);
                out.startElement("a", null);
                out.writeURIAttribute("href", getPageLink(context, component, row), null);
                out.write(lastText);
                out.endElement("a");
                out.endElement("li");
            } else {
                out.startElement("li", null);
                out.writeAttribute("class", "x-last-page disabled", null);
                out.write(lastText);
                out.endElement("li");
            }
        }

        out.endElement("ul");
        out.endElement("div");
    }
    
    private void encodeTheme(UIPager pager, ResponseWriter out) throws IOException {
    	String theme = pager.getTheme();
    	if(theme != null){
        	out.writeAttribute("class", PagingLinkRendererHelper.getThemeCssClass(theme), "");
        }
	}

    private UIData getUIData(FacesContext context, UIComponent component) {
        String forId = (String)component.getAttributes().get("for");
        if (forId != null) {
            UIComponent target = FacesUtils.getForComponent(context, forId, component);
            if (target instanceof UIData) {
                return (UIData)target;
            }
        }
        return null;
    }

    private String getPageLink(FacesContext context, UIComponent component, int row) {
        // Use this page link to allow functional even if script disabled
        return getActionURL(context) +
               "?" +
               component.getClientId(context) +
               "=" +
               row;
    }

    private String getText(FacesContext context, UIComponent component, String name) {
        UIComponent facet = component.getFacet(name);
        if (facet != null) {
            return FacesUtils.encodeComponent(context, facet);
        }
        return null;
    }
}
