/*
 * $$Id: FileUploadTag.java,v 1.3 2007/12/14 09:01:09 yangdong Exp $$
 *
 * Copyright (c) 2006-2007 Operamasks Community.
 * Copyright (c) 2000-2007 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.webapp.widget;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UIFileUpload;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name = "fileUpload" body-content = "JSP"
 *
 */
public class FileUploadTag extends HtmlBasicELTag {
	public static final String RENDERER_TYPE = "org.operamasks.faces.widget.FileUpload";
	private MethodExpression uploadListener;
	private boolean writeToOrUploadListenerSet;
	private ValueExpression maxSize;
	
	@Override
	public String getComponentType() {
		return UIFileUpload.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
	
	/**
     * @jsp.attribute type="java.lang.Boolean" required="false"
     */
    public void setRequired(ValueExpression required) {
        setValueExpression("required", required);
    }

    /**
     * @jsp.attribute required = "false" type = "java.lang.String"
     */
    public void setRequiredMessage(ValueExpression requiredMessage) {
        setValueExpression("requiredMessage", requiredMessage);
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
	
    /**
     * @jsp.attribute type="java.lang.String"
     */
	public void setBrowseIcon(ValueExpression browseIcon) {
		setValueExpression("browseIcon", browseIcon);
	}
	
    /**
     * @jsp.attribute type="java.lang.String" required="false" rtexprvalue="false"
     */
	public void setMaxSize(ValueExpression maxSize) {
		this.maxSize = maxSize;
	}
	
    /**
     * @jsp.attribute type="java.lang.String"
     */
	public void setWriteTo(ValueExpression writeTo) {
		setValueExpression("writeTo", writeTo);
		
		if (writeTo != null)
			writeToOrUploadListenerSet = true;
		else
			writeToOrUploadListenerSet = false;
	}
	
    /**
     * @jsp.attribute method-signature="void process(org.operamasks.faces.component.widget.fileupload.FileUploadItem)"
     */
	public void setUploadListener(MethodExpression uploadListener) {
		this.uploadListener = uploadListener;
		
		if (uploadListener != null)
			writeToOrUploadListenerSet = true;
		else
			writeToOrUploadListenerSet = false;
	}
	
	@Override
	protected void setProperties(UIComponent component) {
		if (!writeToOrUploadListenerSet) {
			throw new FacesException("Neither attribute 'writeTo' or 'uploadListener' is set.");
		}
		
		super.setProperties(component);
		
		UIFileUpload fileUpload = (UIFileUpload)component;
		if (maxSize != null) {
			String maxSizeString = maxSize.getExpressionString();
			fileUpload.setMaxSize(FacesUtils.getMaxSizeInBytes(maxSizeString));
		}
		
		fileUpload.setUploadListener(uploadListener);
	}
	
	/**
     * @jsp.attribute type="java.lang.Boolean" required="false" rtexprvalue="false"
     */
	public void setRich(ValueExpression rich) {
		setValueExpression("rich", rich);
	}
}