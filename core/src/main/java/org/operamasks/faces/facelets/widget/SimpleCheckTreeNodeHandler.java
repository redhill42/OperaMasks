package org.operamasks.faces.facelets.widget;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class SimpleCheckTreeNodeHandler extends TreeNodeHandler {
	public SimpleCheckTreeNodeHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.ignore("checkType");
		
        return m;
    }
	
	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		super.onComponentCreated(ctx, c, parent);
		
		TagAttribute checkType = getAttribute("checkType");
		
    	if (checkType == null)
    		return;
    	
    	String checkTypeString = checkType.getValue().toLowerCase();
    	UISimpleCheckTreeNode treeNode = (UISimpleCheckTreeNode)c;
    	
    	if ("checked".equals(checkTypeString)) {
    		treeNode.setCheckType(UISimpleCheckTreeNode.CheckType.CHECKED);
    	} else if ("unchecked".equals(checkTypeString)) {
    		treeNode.setCheckType(UISimpleCheckTreeNode.CheckType.UNCHECKED);
    	} else if ("partly_checked".equals(checkTypeString)) {
    		treeNode.setCheckType(UISimpleCheckTreeNode.CheckType.PARTLY_CHECKED);
    	} else {
    		throw new FacesException("The value of attribute 'checkType' of " + getTagName() + " must be 'checked', 'unchecked' or 'partly_checked'");
    	}
	}

	protected String getTagName() {
		return "simpleCheckTreeNodeTag";
	}
}
