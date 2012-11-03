package org.operamasks.faces.component.widget;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.validator.ClientValidator;
import org.operamasks.resources.Resources;

public class DateValidator implements Validator, ClientValidator, StateHolder {
	private String format;
	
	public DateValidator() {
	}
	
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
	}
	
	public String getValidatorInstanceScript(FacesContext context,
			UIComponent component) {
		String clientId = component.getClientId(context);
		String message = Resources._T(Resources.JSF_VALIDATE_ILLEGAL_DATE_FORMAT,
				FacesUtils.getLabel(context, component));
		String display = FacesUtils.getMessageComponentId(context, component);
		display = (display == null) ? "null" : "" + HtmlEncoder.encode(display) + "'";
		
		return String.format("new DateValidator('%s', '%s', %s, '%s')", clientId,
				HtmlEncoder.encode(message), display, format);
	}

	public String getValidatorScript(FacesContext context,
			UIComponent component) {
		return null;
	}

	public boolean isTransient() {
		return false;
	}

	public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        format = (String) values[0];
	}

	public Object saveState(FacesContext context) {
        Object values[] = new Object[1];
        values[0] = format;

        return (values);
	}

	public void setTransient(boolean newTransientValue) {}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
