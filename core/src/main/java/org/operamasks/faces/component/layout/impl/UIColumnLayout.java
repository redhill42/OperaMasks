/**
 * UIColumnLayout.java
 * DO NOT EDIT THIS FILE
 * This file was automatically generated by org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory
 * at Sat Nov 03 15:46:26 CST 2012
 */

package org.operamasks.faces.component.layout.impl;

import org.operamasks.faces.component.layout.base.UIColumnLayoutBase;

@javax.annotation.Generated(value="org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory", date="2012-11-03T15:46:26Z")
public class UIColumnLayout extends UIColumnLayoutBase {
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.component.layout.impl.UIColumnLayout";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.component.layout.impl.UIColumnLayout";

    public UIColumnLayout() {
	super.setRendererType("org.operamasks.faces.component.layout.impl.UIColumnLayout");
    }

    public String getFamily() {
	return COMPONENT_FAMILY;
    }

    public Object saveState(javax.faces.context.FacesContext context) {
	return new Object[] {
	    super.saveState(context),
	};
    }

    public void restoreState(javax.faces.context.FacesContext context, Object state) {
	Object[] values = (Object[])state;
	super.restoreState(context, values[0]);
    }

}