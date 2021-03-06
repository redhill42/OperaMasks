/**
 * UIWindow.java
 * DO NOT EDIT THIS FILE
 * This file was automatically generated by org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory
 * at Sat Nov 03 15:46:26 CST 2012
 */

package org.operamasks.faces.webapp.layout;

/**
* @jsp.tag name="window" body-content="JSP"
*/
public class UIWindowTag extends org.operamasks.faces.webapp.html.HtmlBasicELTag {
    public String getComponentType() {
	return "org.operamasks.faces.component.layout.impl.UIWindow";
    }

    public String getRendererType() {
	return "org.operamasks.faces.component.layout.impl.UIWindow";
    }

    private javax.el.ValueExpression animateTarget;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setAnimateTarget(javax.el.ValueExpression value) {
	this.animateTarget = value;
    }
    private javax.el.ValueExpression baseCls;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setBaseCls(javax.el.ValueExpression value) {
	this.baseCls = value;
    }
    private javax.el.ValueExpression closable;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setClosable(javax.el.ValueExpression value) {
	this.closable = value;
    }
    private javax.el.ValueExpression closeAction;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setCloseAction(javax.el.ValueExpression value) {
	this.closeAction = value;
    }
    private javax.el.ValueExpression constrain;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setConstrain(javax.el.ValueExpression value) {
	this.constrain = value;
    }
    private javax.el.ValueExpression constrainHeader;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setConstrainHeader(javax.el.ValueExpression value) {
	this.constrainHeader = value;
    }
    private javax.el.ValueExpression expandOnShow;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setExpandOnShow(javax.el.ValueExpression value) {
	this.expandOnShow = value;
    }
    private javax.el.ValueExpression maximizable;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setMaximizable(javax.el.ValueExpression value) {
	this.maximizable = value;
    }
    private javax.el.ValueExpression minHeight;
    /**
    * @jsp.attribute type="java.lang.Integer"
    */
    public void setMinHeight(javax.el.ValueExpression value) {
	this.minHeight = value;
    }
    private javax.el.ValueExpression minWidth;
    /**
    * @jsp.attribute type="java.lang.Integer"
    */
    public void setMinWidth(javax.el.ValueExpression value) {
	this.minWidth = value;
    }
    private javax.el.ValueExpression minimizable;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setMinimizable(javax.el.ValueExpression value) {
	this.minimizable = value;
    }
    private javax.el.ValueExpression modal;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setModal(javax.el.ValueExpression value) {
	this.modal = value;
    }
    private javax.el.ValueExpression onEsc;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setOnEsc(javax.el.ValueExpression value) {
	this.onEsc = value;
    }
    private javax.el.ValueExpression plain;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setPlain(javax.el.ValueExpression value) {
	this.plain = value;
    }
    private javax.el.ValueExpression resizable;
    /**
    * @jsp.attribute type="java.lang.Boolean"
    */
    public void setResizable(javax.el.ValueExpression value) {
	this.resizable = value;
    }
    private javax.el.ValueExpression resizeHandles;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setResizeHandles(javax.el.ValueExpression value) {
	this.resizeHandles = value;
    }
    private javax.el.ValueExpression onclose;
    /**
    * @jsp.attribute type="java.lang.String"
    */
    public void setOnclose(javax.el.ValueExpression value) {
	this.onclose = value;
    }
    protected void setProperties(javax.faces.component.UIComponent c) {
	super.setProperties(c);
	c.setValueExpression("animateTarget", this.animateTarget);
	c.setValueExpression("baseCls", this.baseCls);
	c.setValueExpression("closable", this.closable);
	c.setValueExpression("closeAction", this.closeAction);
	c.setValueExpression("constrain", this.constrain);
	c.setValueExpression("constrainHeader", this.constrainHeader);
	c.setValueExpression("expandOnShow", this.expandOnShow);
	c.setValueExpression("maximizable", this.maximizable);
	c.setValueExpression("minHeight", this.minHeight);
	c.setValueExpression("minWidth", this.minWidth);
	c.setValueExpression("minimizable", this.minimizable);
	c.setValueExpression("modal", this.modal);
	c.setValueExpression("onEsc", this.onEsc);
	c.setValueExpression("plain", this.plain);
	c.setValueExpression("resizable", this.resizable);
	c.setValueExpression("resizeHandles", this.resizeHandles);
	c.setValueExpression("onclose", this.onclose);
    }

    public void release() {
	super.release();
	this.animateTarget = null;
	this.baseCls = null;
	this.closable = null;
	this.closeAction = null;
	this.constrain = null;
	this.constrainHeader = null;
	this.expandOnShow = null;
	this.maximizable = null;
	this.minHeight = null;
	this.minWidth = null;
	this.minimizable = null;
	this.modal = null;
	this.onEsc = null;
	this.plain = null;
	this.resizable = null;
	this.resizeHandles = null;
	this.onclose = null;
    }

}
