package org.operamasks.faces.component.action;

import java.util.EventObject;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * Action事件
 *
 */
public class ActionEvent extends EventObject
{

	private static final long serialVersionUID = -2267122627651859461L;
	private PhaseId phaseId;
	private FacesContext context;

	public ActionEvent(Object source) {
		super(source);
	}
	
	public ActionEvent(UIComponent component, FacesContext context,
			PhaseId phaseId) {
		super(component);
		this.phaseId = phaseId;
		this.context = context;
	}
	
	public PhaseId getPhaseId() {
		return this.phaseId;
	}
	
	public FacesContext getFacesContext() {
		return this.context;
	}
	
}
