package org.operamasks.faces.component.widget;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class UIFileUploadProgress extends UIComponentBase {
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.FileUploadProgress";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.FileUploadProgress";
    
    private Integer interval;
    private String startMessage;
    private String uploadingMessage;
    private String completeMessage;
    private String errorMessage;
	
	public UIFileUploadProgress() {
    	setRendererType("org.operamasks.faces.widget.FileUploadProgress");
	}
    
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

    public int getInterval() {
        if (this.interval != null) {
            return this.interval;
        }
        ValueExpression ve = getValueExpression("interval");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return 1;
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            interval,
            errorMessage,
            startMessage,
            uploadingMessage,
            completeMessage
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        interval = (Integer)values[i++];
        errorMessage = (String)values[i++];
        startMessage = (String)values[i++];
        uploadingMessage = (String)values[i++];
        completeMessage = (String)values[i++];
    }
    
    public String getStartMessage() {
        if (this.startMessage != null) {
            return this.startMessage;
        }
        ValueExpression ve = getValueExpression("startMessage");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public String getUploadingMessage() {
        if (this.uploadingMessage != null) {
            return this.uploadingMessage;
        }
        ValueExpression ve = getValueExpression("uploadingMessage");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
	}

	public void setUploadingMessage(String uploadingMessage) {
		this.uploadingMessage = uploadingMessage;
	}

	public String getCompleteMessage() {
        if (this.completeMessage != null) {
            return this.completeMessage;
        }
        ValueExpression ve = getValueExpression("completeMessage");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
	}

	public void setCompleteMessage(String completeMessage) {
		this.completeMessage = completeMessage;
	}
	
	public String getErrorMessage() {
        if (this.errorMessage != null) {
            return this.errorMessage;
        }
        ValueExpression ve = getValueExpression("errorMessage");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
