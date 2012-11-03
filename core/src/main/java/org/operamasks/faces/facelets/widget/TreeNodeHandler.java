package org.operamasks.faces.facelets.widget;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UITreeNode;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class TreeNodeHandler extends ComponentHandler {
    public TreeNodeHandler(ComponentConfig config) {
        super(config);
    }
    
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c,
    		UIComponent parent) {
    	UITreeNode node = (UITreeNode)c;
    	
    	if (node.getAsync()) {
    		node.setAllowsChildren(true);
    		node.setLeaf(false);
    	}
    	
    	if (node.getParent() instanceof UITreeNode) {
    		UITreeNode parentNode = (UITreeNode)node.getParent();
    		parentNode.setAllowsChildren(true);
    		parentNode.setLeaf(false);
    	}
    }
}
