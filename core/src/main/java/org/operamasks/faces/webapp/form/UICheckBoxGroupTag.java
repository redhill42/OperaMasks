/**
 * UICheckBoxGroup.java
 * DO NOT EDIT THIS FILE
 * This file was automatically generated by org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory
 * at Sat Nov 03 15:46:26 CST 2012
 */

package org.operamasks.faces.webapp.form;

/**
* @jsp.tag name="checkBoxGroup" body-content="JSP"
*/
public class UICheckBoxGroupTag extends org.operamasks.faces.webapp.html.HtmlBasicELTag {
    public String getComponentType() {
	return "org.operamasks.faces.component.form.impl.UICheckBoxGroup";
    }

    public String getRendererType() {
	return "org.operamasks.faces.component.form.impl.UICheckBoxGroup";
    }

    private javax.el.ValueExpression direction;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setDirection(javax.el.ValueExpression value) {
	this.direction = value;
    }
    protected void setProperties(javax.faces.component.UIComponent c) {
	super.setProperties(c);
	c.setValueExpression("direction", this.direction);
    }

    public void release() {
	super.release();
	this.direction = null;
    }

}
