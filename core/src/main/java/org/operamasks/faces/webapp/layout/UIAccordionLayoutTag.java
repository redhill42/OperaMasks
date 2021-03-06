/**
 * UIAccordionLayout.java
 * DO NOT EDIT THIS FILE
 * This file was automatically generated by org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory
 * at Sat Nov 03 15:46:26 CST 2012
 */

package org.operamasks.faces.webapp.layout;

/**
* @jsp.tag name="accordionLayout" body-content="JSP"
*/
public class UIAccordionLayoutTag extends org.operamasks.faces.webapp.html.HtmlBasicELTag {
    public String getComponentType() {
	return "org.operamasks.faces.component.layout.impl.UIAccordionLayout";
    }

    public String getRendererType() {
	return "org.operamasks.faces.component.layout.impl.UIAccordionLayout";
    }

    private javax.el.ValueExpression titleCollapse;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setTitleCollapse(javax.el.ValueExpression value) {
	this.titleCollapse = value;
    }
    private javax.el.ValueExpression animate;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setAnimate(javax.el.ValueExpression value) {
	this.animate = value;
    }
    private javax.el.ValueExpression activeOnTop;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setActiveOnTop(javax.el.ValueExpression value) {
	this.activeOnTop = value;
    }
    protected void setProperties(javax.faces.component.UIComponent c) {
	super.setProperties(c);
	c.setValueExpression("titleCollapse", this.titleCollapse);
	c.setValueExpression("animate", this.animate);
	c.setValueExpression("activeOnTop", this.activeOnTop);
    }

    public void release() {
	super.release();
	this.titleCollapse = null;
	this.animate = null;
	this.activeOnTop = null;
    }

}
