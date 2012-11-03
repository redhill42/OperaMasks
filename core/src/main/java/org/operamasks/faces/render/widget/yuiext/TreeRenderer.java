/*
 * $Id: TreeRenderer.java,v 1.6 2008/01/15 07:31:22 yangdong Exp $
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUI;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUIFactory;
import org.operamasks.faces.util.FacesUtils;

public class TreeRenderer extends HtmlRenderer implements ResourceProvider {
	public static final String KEY_ROOT_NODE_POSTFIX = "_root_node";
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UITree tree = (UITree)component;
        
       	ExtJsUtils.encodeContainerForComponent(context, tree, tree.getStyle(),
               tree.getStyleClass());
       	
       	if (isPostback(context)) {
       		if (!isSaveState(tree))
       			removeRootNodeFromSession(tree);
       		else
       			saveState(tree);
       	}
	}
	
	protected void removeRootNodeFromSession(UITree tree) {
		getFacesContext().getExternalContext().getSessionMap().remove(getRootNodeKey(tree));
	}

	public void provideResource(ResourceManager manager, UIComponent component) {
    	YuiExtResource resource = registerResource(manager);
		encodeResource(resource, component);
	}

	protected YuiExtResource registerResource(ResourceManager manager) {
		Set<String> resourceIds = new HashSet<String>();
		
		for (TreeNodeUI ui : TreeNodeUIFactory.getInstance().getUIs(getRenderKitId())) {
			for (String resourceId : ui.getResourceIds()) {
				resourceIds.add(resourceId);
			}
		}
		
		YuiExtResource resource = YuiExtResource.register(manager,
    			resourceIds.toArray(new String[resourceIds.size()]));
		
		return resource;
	}

	protected void encodeResource(YuiExtResource resource, UIComponent component) {
		UITree tree = (UITree)component;

		boolean restored = false;
		
		if (isSaveState(tree) && !isPostback(getFacesContext())) {
			restored = restoreState(tree);
		}
		
		if (tree.getInitAction() != null) {
			if (!restored)
				tree.getInitAction().invoke(getFacesContext().getELContext(),
						new Object[] {getFacesContext(), tree});
			
			tree.setInitAction(null);
		}
		
		checkRootNode(tree);
		encodeComponent(resource, tree);
	}
	
    protected boolean isPostback(FacesContext context) {
        RenderKit renderKit = context.getRenderKit();

        if (renderKit == null) {
            String renderKitId = context.getApplication().getViewHandler().calculateRenderKitId(context);
            renderKit = FacesUtils.getRenderKit(context, renderKitId);
        }

        if (renderKit == null) {
            return false;
        }

        return renderKit.getResponseStateManager().isPostback(context);
    }

	protected void saveState(UITree tree) {
		putRootNodeToSession(tree);
	}

	private boolean restoreState(UITree tree) {
		UITreeNode rootNode = getRootNodeFromSession(tree);
			
		if (rootNode != null) {
			tree.setRootNode(rootNode);
			return true;
		}
		
		return false;
	}

	private void checkRootNode(UITree tree) {
		if (tree.getRootNode() != null)
			return;
		
		if (tree.getAsyncData() == null) {
			throw new FacesException("Can't initialize tree root node. Please set 'asyncData' attribute" +
					" or add some nested treeNode tags.");
		}
		
		tree.loadAsyncRootNode();
		
		if (tree.getRootNode() == null)
			throw new FacesException("Tree component gets a null root node from 'asyncData' method.");
	}

	private void putRootNodeToSession(UITree tree) {
		getFacesContext().getExternalContext().getSessionMap().put(
				getRootNodeKey(tree), tree.getRootNode());
	}

	private UITreeNode getRootNodeFromSession(UITree tree) {
		return (UITreeNode)getFacesContext().getExternalContext(
				).getSessionMap().get(getRootNodeKey(tree));
	}

	private String getRootNodeKey(UITree tree) {
		return FacesUtils.getUIViewRoot(tree).getViewId() + "_" + tree.getClientId(
				FacesContext.getCurrentInstance()) + KEY_ROOT_NODE_POSTFIX;
	}

	protected void encodeComponent(YuiExtResource resource, UITree tree) {
		StringBuilder buf = new StringBuilder();
		Formatter formatter = new Formatter(buf);
		
		String jsvar = FacesUtils.getJsvar(getFacesContext(), tree);
		resource.addVariable(jsvar);
		
        encodeTreePanel(getFacesContext(), formatter, tree, jsvar);
        encodeTreeNodes(resource, tree, formatter);
        encodeEventListeners(resource, formatter, tree, jsvar);
        
        resource.addInitScript(buf.toString());

        resource.releaseVariable(jsvar);
	}

	protected void encodeEventListeners(YuiExtResource resource, Formatter formatter,
			UITree tree, String treeJsvar) {
		UIForm form = getParentForm(tree);
		
		// Tree component can't submit view state to server If it isn't nested within a
		// form. So we need't register any tree event.
		if (form == null)
			return;
		
        Set<TreeEventType> eventTypesForRegistration = getEventTypesForRegistration(tree);
        
        if (eventTypesForRegistration == null || eventTypesForRegistration.size() == 0)
        	return;
        
        formatter.format("\nfunction %1$s() {" +
        		"\n%2$s" +
        		"\n}" +
        		"\n%1$s();",
        		treeJsvar + "_registerEvents",
        		getEventsRegistrationScript(tree, eventTypesForRegistration)
        );
	}

	protected Set<TreeEventType> getEventTypesForRegistration(UITree tree) {
		return TreeNodeUIFactory.getInstance(
        		).getEventTypesForRegistration(tree, getRenderKitId());
	}
	
	protected String getEventsRegistrationScript(UITree tree, Set<TreeEventType> registeredEventTypes) {
		return TreeNodeUIFactory.getInstance().encodeEventsScript(tree,
				registeredEventTypes, getRenderKitId());
	}
	
	protected String getRenderKitId() {
		return "HTML_BASIC";
	}

	private void encodeTreeNodes(YuiExtResource resource, UITree tree, Formatter formatter) {
		UITreeNode rootNode = tree.getRootNode();
        TreeNodeRenderer renderer = getTreeNodeRenderer(rootNode);
        renderer.encodeResource(resource, formatter, tree, null, rootNode);
	}

	private TreeNodeRenderer getTreeNodeRenderer(UITreeNode node) {
		return (TreeNodeRenderer)getFacesContext().getRenderKit(
				).getRenderer(node.getFamily(), node.getRendererType());
	}
	
	protected void encodeTreePanel(FacesContext context, Formatter formatter, UITree tree, String jsvar) {
		formatter.format("\n%s = new Ext.tree.TreePanel2(%s);",
        		jsvar,
        		getTreePanelConfig(tree)
        );
		
		formatter.format("\n%s.selModel = new Ext.tree.DefaultSelectionModel2();", jsvar);
	}
	
	protected String getTreePanelConfig(UITree tree) {
		Map<String, Object> config = new HashMap<String, Object>();
        config.put("animate", tree.getAnimate());
        config.put("enableDrag", tree.getEnableDrag());
        config.put("enableDrop", tree.getEnableDrop());
        config.put("lines", tree.getLines());
        config.put("containerScroll", tree.getContainerScroll());
        config.put("rootVisible", tree.getRootVisible());
        config.put("border", new ExtJsUtils.JsObject("false"));
        
        return ExtJsUtils.createJsArray(config);
	}
	
    protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
    
	protected boolean isSaveState(UIComponent component) {
		return Boolean.TRUE.equals(((UITree)component).getSaveState());
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		UITree tree = (UITree)component;
		
		if (tree.getRootNode() != null) {
			tree.getRootNode().encodeAll(context);
		}
	}
}
