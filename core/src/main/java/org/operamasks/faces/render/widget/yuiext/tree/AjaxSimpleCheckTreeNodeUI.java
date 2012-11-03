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
import org.operamasks.faces.component.widget.tree.state.TreeNodeCheck;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.widget.yuiext.ExtJsUtils;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.org.json.simple.JSONObject;

public class AjaxSimpleCheckTreeNodeUI extends AjaxDefaultTreeNodeUI implements TreeNodeUI {
	@Override
	public String[] getResourceIds() {
		return new String[] {"Ext.tree.SimpleCheckTreeNode"};
	}

	@Override
	public String encodeEventScript(UITree tree, TreeEventType eventType) {
		if (!UISimpleCheckTreeNode.CHECK.equals(eventType)) {
			return super.encodeEventScript(tree, eventType);
		}
		
		FacesContext context = getFacesContext();
		return CheckTreeNodeUtils.encodeCheckEventRegistration(context, tree,
				CheckTreeNodeUtils.getCheckEventHandler(context, tree,
						getCheckEventSubmitScript(context, tree)));
	}

	public static String getCheckEventSubmitScript(FacesContext context, UITree tree) {
		return HtmlRenderer.encodeAjaxSubmit(context, tree,
				HtmlEncoder.enquote(TreeRenderUtils.getEventTypeKey(context, tree)),
				HtmlEncoder.enquote(UISimpleCheckTreeNode.CHECK.getTypeString()),
				HtmlEncoder.enquote(TreeRenderUtils.getNodeIdKey(context, tree)),
				"node.id",
				HtmlEncoder.enquote(CheckTreeNodeUtils.getCheckTypeKey(context, tree)),
				"state");
	}
	
	@Override
	public String getRenderKitId() {
		return "AJAX";
	}
	
	@Override
	public Class<? extends UITreeNode> getNodeClass() {
		return UISimpleCheckTreeNode.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject treeNodeToJSON(UITree tree, UITreeNode treeNode) {
		JSONObject data = super.treeNodeToJSON(tree, treeNode);
		if(Boolean.FALSE.equals(tree.getRootVisible()) && treeNode.getParent() instanceof UITree){
		    data.put("uiProvider", "Ext.tree.RootSimpleCheckboxNodeUI");
        }else{
            data.put("uiProvider", "Ext.tree.SimpleCheckboxNodeUI");
        }
		data.put("check", ((UISimpleCheckTreeNode)treeNode).getCheckType().toString());
		
		return data;
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
	public String getChangeScript(TreeStateChange change, UITree tree, UITreeNode treeNode) {
		if (!TreeNodeCheck.class.equals(change.getClass()))
			return super.getChangeScript(change, tree, treeNode);
		
		StringBuilder buf = new StringBuilder();
		Formatter fmt = new Formatter(buf);
		
		fmt.format("\n%s.getNodeById('%s').getUI().check('%s', true);",
				FacesUtils.getJsvar(getFacesContext(), tree),
				treeNode.getId(),
				((TreeNodeCheck)change).isCheck() ? "checked" : "unchecked"
		);
		
		return buf.toString();
	}
	
	@Override
	public Map<String, Object> getTreeNodeConfig(UITree tree, UITreeNode treeNode) {
		Map<String, Object> treeNodeConfig = super.getTreeNodeConfig(tree, treeNode);
		if(Boolean.FALSE.equals(tree.getRootVisible()) && treeNode.getParent() instanceof UITree){
		    treeNodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.RootSimpleCheckboxNodeUI"));
        }else{
            treeNodeConfig.put("uiProvider", new ExtJsUtils.JsObject("Ext.tree.SimpleCheckboxNodeUI"));
        }
		
		treeNodeConfig.put("check", ((UISimpleCheckTreeNode)treeNode).getCheckType().toString());

		return treeNodeConfig;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class[] getStateChangeClasses() {
		return new Class[] {TreeNodeCheck.class};
	}
}
