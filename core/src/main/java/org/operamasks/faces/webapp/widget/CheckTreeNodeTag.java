package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UICheckTreeNode;

/**
 * @jsp.tag name = "checkTreeNode" body-content = "JSP"
 */
public class CheckTreeNodeTag extends SimpleCheckTreeNodeTag {
    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getComponentType()
     */
    @Override
    public String getComponentType() {
        return UICheckTreeNode.COMPONENT_TYPE;
    }

    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return null;
    }
    
    @Override
    protected void setCheckTypeValue(UIComponent component) {
    	String checkTypeString = ((String)checkType.getValue(FacesContext.getCurrentInstance(
    			).getELContext())).toLowerCase();
    	UICheckTreeNode treeNode = (UICheckTreeNode)component;
    	
    	if ("checked".equals(checkTypeString)) {
    		treeNode.setCheckType(UICheckTreeNode.CheckType.CHECKED);
    	} else if ("unchecked".equals(checkTypeString)) {
    		treeNode.setCheckType(UICheckTreeNode.CheckType.UNCHECKED);
    	} else if ("partly_checked".equals(checkTypeString)) {
    		treeNode.setCheckType(UICheckTreeNode.CheckType.PARTLY_CHECKED);
    	} else {
    		throw new FacesException("The value of attribute 'checkType' of checkTreeNodeTag must be 'checked', 'unchecked' or 'partly_checked'");
    	}
    }
}