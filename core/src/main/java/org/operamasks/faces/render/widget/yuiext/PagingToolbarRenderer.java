/*
 * $Id: PagingToolbarRenderer.java,v 1.16 2008/03/13 12:28:58 jacky Exp $
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

import java.io.IOException;
import java.util.Formatter;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class PagingToolbarRenderer extends HtmlRenderer
    implements ResourceProvider
{
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
      	out.startElement("div", component);
       	out.writeAttribute("id", component.getClientId(context), "clientId");
       	renderPassThruAttributes(out, component);
	}
	
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();
       	out.endElement("div");
       	out.write("\n");

        UIPager pager = (UIPager)component;
        if (pager.getTargetDataStore() == null) {
            UIData data = getUIData(context, component);
            if (data != null) {
                if (out instanceof AjaxResponseWriter) {
                    String jsvar = FacesUtils.getJsvar(context, component);
                    ((AjaxResponseWriter)out).writeActionScript(
                        String.format(
                            "%s.ds.count=%d;" +
                            "%s.ds.totalCount=%d;" +
                            "%s.onLoad(%s.ds,null,{params:{start:%d}});\n",
                            jsvar, data.getRows(),
                            jsvar, data.getRowCount(),
                            jsvar, jsvar, data.getFirst()));
                    ((AjaxResponseWriter)out).setViewStateChanged();
                } else if (out instanceof AjaxHtmlResponseWriter) {
                    ((AjaxHtmlResponseWriter)out).setViewStateChanged();
                }
            }
        }
    }

    public void provideResource(final ResourceManager rm, final UIComponent component) {
        final YuiExtResource resource = YuiExtResource.register(rm, "Ext.PagingToolbar");
		final FacesContext context = FacesContext.getCurrentInstance();
        final UIPagingToolbar pager = (UIPagingToolbar)component;

        String dsvar = pager.getBindingDataStore();
        
        if (dsvar != null) {
        	encodeScriptWithDsvar(rm, component, resource, dsvar);
        } else {
            encodeScriptWithoutDsvar(rm, resource, context, pager);        	
        }
        
        ToolBarUtils.adjustToolBarItemsRenderer((UIPagingToolbar)component);
    }

	private void encodeScriptWithoutDsvar(final ResourceManager rm,
			final YuiExtResource resource, final FacesContext context,
			final UIPagingToolbar pager) {
		
		// If the UIData component doesn't support AJAX data store then we
		// will use a faked data store. Because the Pager may appear before
		// UIData so we must render the paging toolbar script after UIData
		// component has been rendered.
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

		    String id = "urn:paging-toolbar:" + pager.getClientId(context);
		    rm.registerResource(new AbstractResource(id) {
		        public int getPriority() {
		            return LOW_PRIORITY - 200; // render before init script
		        }
		        public void encodeBegin(FacesContext context) {
		            if (!pager.isBound() && getParentUpdater(pager) == null) {
		                String jsvar = resource.allocVariable(pager);
		                String script = encodeScript(context, pager, jsvar, null, data);
		                resource.addInitScript(script);
		            }
		        }
		    });
		}
	}

    private AjaxUpdater getParentUpdater(UIComponent component) {
    	UIComponent parent = component;
    	while ((parent = parent.getParent()) != null) {
    		if (parent instanceof AjaxUpdater) {
    			return (AjaxUpdater) parent;
    		}
    	}
		return null;
	}
    
	private String getResourceId(final UIComponent component) {
		return "urn:pagingToolBar:" + component.getClientId(FacesContext.getCurrentInstance());
	}

	private void encodeScriptWithDsvar(ResourceManager rm, final UIComponent component,
			final YuiExtResource resource, final String dsvar) {
        final UIPagingToolbar pager = (UIPagingToolbar)component;

		rm.registerResource(new AbstractResource(getResourceId(pager)) {
    		@Override
    		public int getPriority() {
    			return LOW_PRIORITY - 200;
    		}
    	
    		@Override
    		public void encodeBegin(FacesContext context) throws IOException {
    	        String jsvar = resource.allocVariable(component);
    	        String script = encodeScript(context, pager, jsvar, dsvar, null);
    	        resource.addInitScript(script);
    		}
    	});

	}

    private String encodeScript(FacesContext context, UIPagingToolbar pager, String jsvar, String dsvar, UIData data) {
        String clientId = pager.getClientId(context);
        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        int pageSize = pager.getPageSize();
        if (pageSize <= 0) {
            pageSize = UIPager.DEFAULT_PAGESIZE;
        }
        
        if (dsvar == null) {
            // Create a faked data store to pass paging information.
            UIForm form = getParentForm(pager);
            dsvar = String.format(
                "{" +
                    "count:%d,totalCount:%d," +
                    "getCount:function(){return this.count;}," +
                    "getTotalCount:function(){return this.totalCount;}," +
                    "load:function(o){OM.ajax.submit(%s,%s,{'%s':o.params.start},false);}," +
                    "on:function(){},un:function(){}" +
                "}",
                pageSize, data.getRowCount(),
                (form == null) ? "null" : "document.forms['" + form.getClientId(context) + "']",
                HtmlEncoder.enquote(getActionURL(context)),
                clientId
            );
        }
        
        fmt.format("if (typeof %s == 'undefined') {%s=new Ext.PagingToolbar({store:%s,pageSize:%d",
                   jsvar, jsvar, dsvar, pageSize);

        String displayMsg = pager.getDisplayMsg();
        if (displayMsg != null) {
            buf.append(",displayMsg:").append(HtmlEncoder.enquote(displayMsg, '\''));
            buf.append(",displayInfo: 'aaa'");
        }

        String emptyMsg = pager.getEmptyMsg();
        if (emptyMsg != null)
            buf.append(",emptyMsg:").append(HtmlEncoder.enquote(emptyMsg, '\''));

        String beforePageText = pager.getBeforePageText();
        if (beforePageText != null)
            buf.append(",beforePageText:").append(HtmlEncoder.enquote(beforePageText, '\''));

        String afterPageText = pager.getAfterPageText();
        if (afterPageText != null)
            buf.append(",afterPageText:").append(HtmlEncoder.enquote(afterPageText, '\''));

        String firstText = pager.getFirstText();
        if (firstText != null)
            buf.append(",firstText:").append(HtmlEncoder.enquote(firstText, '\''));

        String prevText = pager.getPrevText();
        if (prevText != null)
            buf.append(",prevText:").append(HtmlEncoder.enquote(prevText, '\''));

        String nextText = pager.getNextText();
        if (nextText != null)
            buf.append(",nextText:").append(HtmlEncoder.enquote(nextText, '\''));

        String lastText = pager.getLastText();
        if (lastText != null)
            buf.append(",lastText:").append(HtmlEncoder.enquote(lastText, '\''));

        String refreshText = pager.getRefreshText();
        if (refreshText != null)
            buf.append(",refreshText:").append(HtmlEncoder.enquote(refreshText, '\''));

        buf.append("});}\n");

        if (data != null) {
            fmt.format("%s.onLoad(%s.ds,null,{params:{start:%d,limit:%d}});\n",
                       jsvar, jsvar, data.getFirst(), data.getRows());
        }
        
        fmt.format(ToolBarUtils.renderToolbar(context, pager));

        return buf.toString();
    }

    public void decode(FacesContext context, UIComponent component) {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String param = paramMap.get(component.getClientId(context));
        if (param != null && param.length() != 0) {
            UIData data = getUIData(context, component);
            if (data != null) {
                try {
                    UIPager pager = (UIPager)component;
                    int firstRow = Integer.parseInt(param);
                    int maxRows = data.getRowCount();
                    int pageSize = pager.getPageSize();
                    if (firstRow < 0) {
                        firstRow = 0;
                    } else if (maxRows > 0 && firstRow > maxRows) {
                        firstRow = (maxRows-1) / pageSize * pageSize;
                    }
                    pager.setStart(firstRow);
                    data.setFirst(firstRow);
                } catch (NumberFormatException ex) {/*ignored*/}
            }
        }
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
