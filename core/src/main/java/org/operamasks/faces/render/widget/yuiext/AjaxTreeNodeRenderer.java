package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.state.TreeStateChange;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.widget.yuiext.tree.TreeNodeUIFactory;


public class AjaxTreeNodeRenderer extends TreeNodeRenderer {
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        
        if (!isAjaxResponse(context))
        	return;
        

        UITreeNode treeNode = (UITreeNode)component;
/*        if (!isStatusChanged(treeNode)) {
    		return;        	
        }*/
        
        AjaxResponseWriter writer = (AjaxResponseWriter)context.getResponseWriter();
		writer.setViewStateChanged();
		List<TreeStateChange> changes = treeNode.getChanges();
		
		UITree tree = treeNode.getTree();
		if (changes.size() > 0) {
			for (int i = 0; i < changes.size(); i++) {
				TreeStateChange change = changes.get(i);
				String script = TreeNodeUIFactory.getInstance().getChangeScript(change, tree,
						treeNode, getRenderKitId());
				
				if (script != null && !script.equals(""))
					writer.writeScript(script);
			}
		}
		
		super.encodeBegin(context, component);
	}
	
/*    private boolean isStatusChanged(UITreeNode treeNode) {
		return treeNode.getChanges().size() > 0 ||
					treeNode.isExpandCall() ||
						treeNode.isCollapseCall() ||
					treeNode.isSelectCall();
	}*/

	private boolean isAjaxResponse(FacesContext context) {
    	return context.getResponseWriter() instanceof AjaxResponseWriter;
	}
	
	@Override
	protected String getRenderKitId() {
		return "AJAX";
	}
}
