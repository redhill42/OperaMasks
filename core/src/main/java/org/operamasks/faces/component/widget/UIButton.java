package org.operamasks.faces.component.widget;

import static org.operamasks.resources.Resources.UI_MISSING_PARENT_FORM_WARNING;
import static org.operamasks.resources.Resources._T;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.operamasks.faces.component.action.Action;
import org.operamasks.faces.component.action.ActionSupport;
import org.operamasks.faces.el.MethodBindingMethodExpressionAdapter;
import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.event.ModelEvent;
import org.operamasks.faces.event.ModelEventListener;
import org.operamasks.faces.event.ThreadLocalEventBroadcaster;
import org.operamasks.faces.util.FacesUtils;

public class UIButton extends HtmlCommandButton implements ActionSupport, PropertyChangeListener, ModelEventListener{
    
    private static final Logger logger = Logger.getLogger("org.operamasks.faces.view");
    
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.button";
	public static final String RENDERER_TYPE = "javax.faces.Button";
	private Action actionBinding;
	
	public UIButton() {
        super();
        setRendererType(RENDERER_TYPE);
        ThreadLocalEventBroadcaster.getInstance().addEventListenerOnce(EventTypes.BEFORE_RENDER_VIEW, this);
    }

	public UIButton(UIComponentBase parent){
        this();
        FacesUtils.createComponent(parent, this);
	}
	
	public void bindAction(String el){
		FacesContext context = FacesContext.getCurrentInstance();
		MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(context.getELContext(), el, Object.class, new Class[] {});
		this.setActionExpression(expression);
	}
	
	public void bindActionListener(String el){
		FacesContext context = FacesContext.getCurrentInstance();
		MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(context.getELContext(), el, Object.class,  new Class[] { ActionEvent.class });
		this.setActionListener(new MethodBindingMethodExpressionAdapter(expression));
	}

	public Action getActionBinding() {
        if (this.actionBinding != null) {
            return this.actionBinding;
        }
        ValueExpression ve = getValueExpression("actionBinding");
        if (ve != null) {
            Action action = (Action)ve.getValue(getFacesContext().getELContext());
            if (action != null) {
        		for (String key : action.getAttributesKey()) {
        			this.getAttributes().put(key, action.getAttribute(key));
        		}
            	action.addPropertyChangeListener(this);
            }
            return action;
        } else {
            return null;
        }
	}

	public void setActionBinding(Action actionBinding) {
		if (actionBinding == null) {
			return;
		}
		for (String key : actionBinding.getAttributesKey()) {
			this.getAttributes().put(key, actionBinding.getAttribute(key));
		}
		actionBinding.addPropertyChangeListener(this);
		this.actionBinding = actionBinding;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.getAttributes().put(evt.getPropertyName(), evt.getNewValue());
	}
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            actionBinding
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        actionBinding = ((Action)values[i++]);
        // actionBinding不维护propertyChangedListener，每次恢复状态后，重新加载。
        if(actionBinding != null){
            actionBinding.addPropertyChangeListener(this); 
        }
    }

    public void processModelEvent(ModelEvent event) {
        if (EventTypes.BEFORE_RENDER_VIEW.equals(event.getEventType())) {
            if (logger.isLoggable(Level.FINE) && FacesUtils.getParentForm(this) == null) {
                logger.fine(_T(UI_MISSING_PARENT_FORM_WARNING, FacesUtils.getComponentDesc(this)));
            }
        }
    }
}
