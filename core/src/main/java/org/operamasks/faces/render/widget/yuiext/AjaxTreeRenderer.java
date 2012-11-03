/*
 * $Id: AjaxTreeRenderer.java,v 1.38 2008/01/16 02:45:40 yangdong Exp $
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
package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUIFactory;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;

public class AjaxTreeRenderer extends TreeRenderer {
	private static final String LOAD_REQUEST_DATA = "_ajaxTreeLoader";
    private static final String COMPONENT_DOM_NODE = "_componentDomNode";

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

    	UITree tree = (UITree)component;
        if (isAjaxHtmlResponse(context)) {
        	ExtJsUtils.encodeContainerForComponent(context, component, tree.getStyle(),
                tree.getStyleClass());
        }
        
        if (isPostback(context)) {
            if (!isSaveState(tree))
            	removeRootNodeFromSession(tree);
            else
            	saveState(tree);        	
        }
    }
    
    @Override
    protected boolean isPostback(FacesContext context) {
    	return isAjaxResponse(context);
    }
    
    @Override
    public void provideResource(ResourceManager manager, UIComponent component) {
    	if (isAjaxHtmlResponse(getFacesContext())) {
    		AjaxHtmlResponseWriter writer = (AjaxHtmlResponseWriter)getFacesContext().getResponseWriter();
    		writer.setViewStateChanged();
    	}

		super.provideResource(manager, component);
    }
    
	protected void encodeEventListeners(YuiExtResource resource, Formatter formatter,
			UITree tree, String jsvar) {
        Set<TreeEventType> eventTypesForRegistration = getEventTypesForRegistration(tree);
        
        if (eventTypesForRegistration == null || eventTypesForRegistration.size() == 0)
        	return;
        
        formatter.format("\nfunction %1$s() {" +
        		"\n%2$s" +
        		"\n}" +
        		"\n%1$s();",
        		jsvar + "_registerEvents",
        		getEventsRegistrationScript(tree, eventTypesForRegistration)
        );
	}
	
	protected void encodeTreePanel(FacesContext context, Formatter formatter, UITree tree, String jsvar) {
		formatter.format("\n%s = new Ext.tree.TreePanel2(%s);",
        		jsvar,
        		getTreePanelConfig(tree)
        );
		
		formatter.format("\n%s.loader = %s;", jsvar, getDataUrl(getFacesContext(), tree));
		formatter.format("\n%s.selModel = new Ext.tree.DefaultSelectionModel2();", jsvar);
	}
    
    private String getDataUrl(FacesContext context, UITree tree) {
        return String.format("new Ext.tree.FacesTreeLoader({" +
                             "dataUrl: %s," +
                             "baseParams: {'%s': '%s', '%s': %s}" +
                             "})",
                             HtmlEncoder.enquote(getActionURL(context), '\''),
                             LOAD_REQUEST_DATA,
                             tree.getClientId(context),
                             COMPONENT_DOM_NODE,
                             ExtJsUtils.getComponentSource(context, tree)
        );
    }
    
    @SuppressWarnings("serial")
	@Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        
        if (isLoadAsyncDataRequest(context, component)) {
            try {
                processLoadAsyncDataResponse(context, component);
            } catch (IOException e) {
                throw new FacesException("Failed to load tree data");
            }
        }
    }    
    
	@SuppressWarnings("unchecked")
	private void processLoadAsyncDataResponse(FacesContext context, UIComponent component) throws IOException {
        String nodeId = context.getExternalContext().getRequestParameterMap().get("node");
        
        UITree tree = (UITree)component;
        UITreeNode parent = tree.findTreeNodeById(nodeId);
        if (parent.isAsyncDataLoaded())
        	return;
        
        UITreeNode[] treeNodes = ((UITree)component).loadAsyncNodes(parent);
        
        ExternalContext extCtx = context.getExternalContext();
        HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setLocale(context.getViewRoot().getLocale());
        response.setContentType("text/html");
        response.setCharacterEncoding(extCtx.getRequestCharacterEncoding());

        JSONObject json = new JSONObject();
        JSONArray jsonNodes = TreeNodeUIFactory.getInstance().
        		treeNodesToJSON(tree, treeNodes, getRenderKitId());
        json.put("_root", jsonNodes);

        String[] state = FacesUtils.getViewState(context);
        if (state[0] != null) {
            json.put("javax.faces.ViewState", state[0]);
        }

        response.getWriter().write(json.toString());
        context.responseComplete();        
	}

	private boolean isLoadAsyncDataRequest(FacesContext context, UIComponent component) {
		Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
        String requestId = paramMap.get(LOAD_REQUEST_DATA);
        
        return (requestId != null && requestId.equals(clientId));
	}
	
    @Override
    protected String getRenderKitId() {
    	return "AJAX";
    }
}
