package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;

/**
 * @jsp.tag name = "simpleCheckTreeNode" body-content = "JSP"
 */
public class SimpleCheckTreeNodeTag extends TreeNodeTag {
	protected ValueExpression checkType;
	
    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getComponentType()
     */
    @Override
    public String getComponentType() {
        return UISimpleCheckTreeNode.COMPONENT_TYPE;
    }

    /* (non-Javadoc)
     * @see javax.faces.webapp.UIComponentTagBase#getRendererType()
     */
    @Override
    public String getRendererType() {
        return null;
    }
    
    /**
     * @jsp.attribute name="checked" required = "false" type = "java.lang.Boolean"
     */
    public void setChecked(ValueExpression checked) {
        setValueExpression("checked", checked);
    }
    
    /**
     * @jsp.attribute name="checkType" required = "false" type = "java.lang.String"
     */
    public void setCheckType(ValueExpression checkType) {
        this.checkType = checkType;
    }
    
    @Override
    protected void setProperties(UIComponent component) {
    	super.setProperties(component);
    	
    	if (checkType == null)
    		return;
    	
    	setCheckTypeValue(component);
    }

	protected void setCheckTypeValue(UIComponent component) {
		String checkTypeString = ((String)checkType.getValue(FacesContext.getCurrentInstance(
    			).getELContext())).toLowerCase();
    	UISimpleCheckTreeNode treeNode = (UISimpleCheckTreeNode)component;
    	
    	if ("checked".equals(checkTypeString)) {
    		treeNode.setCheckType(UISimpleCheckTreeNode.CheckType.CHECKED);
    	} else if ("unchecked".equals(checkTypeString)) {
    		treeNode.setCheckType(UISimpleCheckTreeNode.CheckType.UNCHECKED);
    	} else {
    		throw new FacesException("The value of attribute 'checkType' of simpleCheckTreeNodeTag must be 'checked', 'unchecked'");
    	}
	}
}
