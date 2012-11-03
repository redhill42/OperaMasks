/*
 * $$Id: UIFileUpload.java,v 1.2 2007/12/11 04:20:12 jacky Exp $$
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
package org.operamasks.faces.component.widget;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.fileupload.UploadingStatus;
import org.operamasks.faces.util.FacesUtils;

public class UIFileUpload extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.FileUpload";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.FileUpload";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.FileUpload";

    private Boolean required;
    private String requiredMessage;
    private String style;
    private String styleClass;
    private String browseIcon;
    private Long maxSize;
    private String writeTo;
    private MethodExpression uploadListener;
    private String uploadingSerialNumber;
    private Boolean rich;
    private UploadingStatus uploadingStatus;
    
    public UIFileUpload() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UIFileUpload(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
	public String getUploadingSerialNumber() {
		return uploadingSerialNumber;
	}

	public void setUploadingSerialNumber(String uploadingSerialNumber) {
		this.uploadingSerialNumber = uploadingSerialNumber;
	}

    private Object[] states;
	public static final int END_UPLOADING = -1;
    
	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}
	
    public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getRequired() {
        if (this.required != null) {
            return this.required;
        }
        
        ValueExpression ve = getValueExpression("required");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }
    
    public void setRequiredMessage(String message) {
        requiredMessage = message;
    }
    
    public String getRequiredMessage() {
        if (requiredMessage != null) {
            return requiredMessage;
        }

        ValueExpression ve = getValueExpression("requiredMessage");
        if (ve != null) {
            try {
                return ((String) ve.getValue(getFacesContext().getELContext()));
            }
            catch (ELException e) {
                throw new FacesException(e);
            }
        } else {
            return (this.requiredMessage);
        }

    }

    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueExpression ve = getValueExpression("style");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStyle(String style) {
        this.style = style;
    }
    
    public String getStyleClass() {
        if (this.styleClass != null) {
            return this.styleClass;
        }
        ValueExpression ve = getValueExpression("styleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    public String getBrowseIcon() {
        if (this.browseIcon != null) {
            return this.browseIcon;
        }
        ValueExpression ve = getValueExpression("browseIcon");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBrowseIcon(String browseIcon) {
        this.browseIcon = browseIcon;
    }
    
    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }
    
    public String getWriteTo() {
        if (this.writeTo != null) {
            return this.writeTo;
        }
        ValueExpression ve = getValueExpression("writeTo");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setWriteTo(String writeTo) {
        this.writeTo = writeTo;
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
     */
    @Override
    public Object saveState(FacesContext context) {
        if (states == null) {
             states = new Object[11];
        }
      
        states[0] = super.saveState(context);
        states[1] = saveAttachedState(context, style);
        states[2] = saveAttachedState(context, styleClass);
        states[3] = saveAttachedState(context, browseIcon);
        states[4] = saveAttachedState(context, maxSize);
        states[5] = saveAttachedState(context, writeTo);
        states[6] = saveAttachedState(context, uploadListener);
        states[7] = saveAttachedState(context, uploadingSerialNumber);
        states[8] = saveAttachedState(context, rich);
        states[9] = saveAttachedState(context, required);
        states[10] = saveAttachedState(context, requiredMessage);
        
        return (states);
    }
    
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void restoreState(FacesContext context, Object uploadingState) {
        states = (Object[]) uploadingState;
        super.restoreState(context, states[0]);
        style = (String)restoreAttachedState(context, states[1]);
        styleClass = (String)restoreAttachedState(context, states[2]);
        browseIcon = (String)restoreAttachedState(context, states[3]);
        maxSize = (Long)restoreAttachedState(context, states[4]);
        writeTo = (String)restoreAttachedState(context, states[5]);
        uploadListener = (MethodExpression)restoreAttachedState(context, states[6]);
        uploadingSerialNumber = (String)restoreAttachedState(context, states[7]);
        rich = (Boolean)restoreAttachedState(context, states[8]);
        required = (Boolean)restoreAttachedState(context, states[9]);
        requiredMessage = (String)restoreAttachedState(context, states[10]);
    }

	public MethodExpression getUploadListener() {
		return uploadListener;
	}

	public void setUploadListener(MethodExpression uploadListener) {
		this.uploadListener = uploadListener;
	}
	
    public Boolean getRich() {
        if (this.rich != null) {
            return this.rich;
        }
        ValueExpression ve = getValueExpression("rich");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setRich(Boolean rich) {
        this.rich = rich;
    }

	public UploadingStatus getUploadingStatus() {
		return uploadingStatus;
	}

	public void setUploadingStatus(UploadingStatus uploadingStatus) {
		this.uploadingStatus = uploadingStatus;
	}
}