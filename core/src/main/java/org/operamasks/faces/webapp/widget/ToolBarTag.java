package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;

import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="toolBar" body-content="JSP"
 */
public class ToolBarTag extends HtmlBasicELTag {

	@Override
	public String getComponentType() {
		return UIToolBar.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return "org.operamasks.faces.widget.ToolBar";
	}
	
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }
}
