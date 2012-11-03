package org.operamasks.faces.render.widget.yuiext.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.util.HtmlEncoder;

public class SimpleCheckTreeNodeUI extends DefaultTreeNodeUI implements TreeNodeUI {
	@Override
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.SimpleCheckTreeNode"};
	}
	
	@Override
	public void beginEncodeResource(YuiExtResource resource, Formatter formatter, UITree tree,
			UITreeNode parent, UITreeNode treeNode) {
		CheckTreeNodeUtils.beforeEncodeResource(resource, formatter, tree, parent, treeNode);
	}

	@Override
	public Map<String, Object> decode(FacesContext context, UITree tree,
				UITreeNode treeNode, TreeEventType eventType) {
		if (!UISimpleCheckTreeNode.CHECK.equals(eventType))
			return null;
		
		String checkType = context.getExternalContext().getRequestParameterMap(
			).get(CheckTreeNodeUtils.getCheckTypeKey(context, tree));
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("checkType", "checked".equals(checkType) ? true : false);
		
		return params;
	}
	
	
	@Override
	public Class<? extends UITreeNode> getNodeClass() {
		return UISimpleCheckTreeNode.class;
	}
	
	@Override
	public TreeEventType[] getEventTypesForRegistration(UITree tree) {
		List<TreeEventType> eventTypes = new ArrayList<TreeEventType>(
				Arrays.asList(super.getEventTypesForRegistration(tree)));
		
		if (!eventTypes.contains(UISimpleCheckTreeNode.CHECK))
			eventTypes.add(UISimpleCheckTreeNode.CHECK);
		
		return eventTypes.toArray(new TreeEventType[eventTypes.size()]);
	}
	
	@Override
	public String encodeEventScript(UITree tree, TreeEventType eventType) {
		if (!UISimpleCheckTreeNode.CHECK.equals(eventType))
			return super.encodeEventScript(tree, eventType);
		
		FacesContext context = getFacesContext();
		return CheckTreeNodeUtils.encodeCheckEventRegistration(context, tree,
				CheckTreeNodeUtils.getCheckEventHandler(context, tree,
						getCheckEventSubmitScript(context, tree)));
	}
	
	public static String getCheckEventSubmitScript(FacesContext context, UITree tree) {
		return HtmlRenderer.encodeSubmit(context, HtmlRenderer.getParentForm(tree), null,
				HtmlEncoder.enquote(TreeRenderUtils.getEventTypeKey(context, tree)),
				HtmlEncoder.enquote(UISimpleCheckTreeNode.CHECK.getTypeString()),
				HtmlEncoder.enquote(TreeRenderUtils.getNodeIdKey(context, tree)),
				"node.id",
				HtmlEncoder.enquote(CheckTreeNodeUtils.getCheckTypeKey(context, tree)),
				"state") +
				"\nreturn false;";
	}
}