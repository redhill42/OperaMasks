package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.component.widget.UIFileUploadProgress;
import org.operamasks.faces.util.FileUploadUtils;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="fileUploadProgress" body-content="empty"
 */
public class FileUploadProgressTag extends HtmlBasicELTag {

	@Override
	public String getComponentType() {
		return UIFileUploadProgress.COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return "org.operamasks.faces.widget.FileUploadProgress";
	}

    /**
     * @jsp.attribute requird="false" type="int"
     */
    public void setInterval(ValueExpression interval) {
        setValueExpression("interval", interval);
    }
    
    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     */
    public void setStartMessage(ValueExpression startMessage) {
        setValueExpression("startMessage", startMessage);
    }
    
    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     */
    public void setCompleteMessage(ValueExpression completeMessage) {
        setValueExpression("completeMessage", completeMessage);
    }
    
    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     */
    public void setUploadingMessage(ValueExpression uploadingMessage) {
        setValueExpression("uploadingMessage", uploadingMessage);
    }
    
    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     */
    public void setErrorMessage(ValueExpression errorMessage) {
        setValueExpression("errorMessage", errorMessage);
    }
    
    @Override
    public int doStartTag() throws JspException {
    	int result = super.doStartTag();
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	UIFileUploadProgress fileUploadProgress = (UIFileUploadProgress)getComponentInstance();

    	FileUploadUtils.decorateFileUploadProgress(context, fileUploadProgress);

    	return result;
    }
}
