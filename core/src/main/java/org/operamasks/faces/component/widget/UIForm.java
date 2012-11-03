package org.operamasks.faces.component.widget;

import javax.el.ValueExpression;
import javax.faces.component.html.HtmlForm;

public class UIForm extends HtmlForm {
    public static final String COMPONENT_TYPE = "org.operamasks.faces.component.widget.UIForm";
    
    private String messageTarget;
    
    public String getMessageTarget() {
        if (this.messageTarget != null) {
            return this.messageTarget;
        }
        ValueExpression ve = getValueExpression("messageTarget");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return FormMessageTarget.qtip.toString();
        }
    }

    public void setMessageTarget(String messageTarget) {
        this.messageTarget = messageTarget;
    }

    public Object saveState(javax.faces.context.FacesContext context) {
        return new Object[] { 
            super.saveState(context), 
            this.messageTarget 
        };
    }

    public void restoreState(javax.faces.context.FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        this.messageTarget = (String)values[1];
    }
}
