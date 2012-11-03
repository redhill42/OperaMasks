package org.operamasks.faces.util;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.operamasks.faces.component.widget.UICombo;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;
import org.operamasks.faces.component.widget.menu.UILinkMenuItem;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.el.MethodBindingMethodExpressionAdapter;
import org.operamasks.util.BeanUtils;

import static org.operamasks.resources.Resources.*;

public class UIComponentFactory {
	private Map<String, Object> attributes;
	
	public UIComponentFactory addAttribute(String name, Object value) {
		getAttributes().put(name, value);
		
		return this;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public Map<String, Object> getAttributes() {
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		
		return attributes;
	}
	
	public UIComponent createComponent(FacesContext context, String componentType) {
	    Application app = context.getApplication();
	    UIComponent component = app.createComponent(componentType);
	    component.setId(context.getViewRoot().createUniqueId());
	    
	    for (Map.Entry<String, Object> attribute : getAttributes().entrySet()) {
	    	String name = attribute.getKey();
	    	Object value = attribute.getValue();
	    	
	    	try {
				Method method = BeanUtils.getWriteMethod(component.getClass(), name);
				
				if (method == null)
					continue;
				
				Class<?>[] params = method.getParameterTypes();
				if (params.length != 1)
					continue;
				
				// We can't determine the signature of method, so ignore it.
				if ((MethodExpression.class.isAssignableFrom(params[0]) ||
						MethodBinding.class.isAssignableFrom(params[0])) && value instanceof String) {
					continue;
				}
				
				if ((value instanceof  String) && FacesUtils.isValueExpression((String)value)) {
					value = FacesUtils.evaluateExpressionGet((String)value, params[0]);
				}
				
				method.invoke(component, value);
			} catch (IntrospectionException e) {
				throw new FacesException("Class " + component.getClass().getName() +
					" doesn't have a write method for attribute " + name);
			} catch (Exception e) {
				throw new FacesException("Failed to set attribute " + name + " to component " +
						component.getClass().getName(), e);
			}
	    }
	    
	    return component;
	}
	
	public HtmlCommandButton createButton(FacesContext context) {
		HtmlCommandButton command = (HtmlCommandButton)createComponent(context,
				HtmlCommandButton.COMPONENT_TYPE);
		
		setActionExpression(context, command);
		setActionListenerExpression(context, command);
		
		return command;
	}

	private void setActionExpression(FacesContext context, UICommand command) {
		Object action = getAttributes().get("action");
		
		if (isMethodExpression(action)) {
			MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(
					context.getELContext(), (String)action, Object.class, new Class[] {});
			
			setMethodExpression(expression, command, "action");
		}
	}
	
	public UICombo createCombo(FacesContext context) {
		UICombo combo = (UICombo)createComponent(context,
				UICombo.COMPONENT_TYPE);
		
		Object validator = getAttributes().get("validator");
		
		if (isMethodExpression(validator)) {
			MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(
					context.getELContext(), (String)validator, Void.class,
					new Class[] {FacesContext.class, UIComponent.class, Object.class});
			
			setMethodExpression(expression, combo, "validator");
		}
		
		Object valueChangeListener = getAttributes().get("valueChangeListener");
		if (isMethodExpression(valueChangeListener)) {
			MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(
					context.getELContext(), (String)valueChangeListener,
					Void.class, new Class[] {ValueChangeEvent.class});
			
			setMethodExpression(expression, combo, "valueChangeListener");
		}
		
		return combo;
	}
	
	private static boolean isMethodExpression(Object expression) {
		return expression != null && expression instanceof String && FacesUtils.isValueExpression((String)expression);
	}

	private static void setMethodExpression(MethodExpression expression, UIComponent component, String name) {
		try {
			Method method = BeanUtils.getWriteMethod(component.getClass(), name);
			
			Class[] params = method.getParameterTypes();
			if (params.length != 1)
				return;
			
			if (MethodBinding.class.isAssignableFrom(params[0])) {
				method.invoke(component, new MethodBindingMethodExpressionAdapter(expression));
			} else {
				method.invoke(component, expression);
			}
		} catch (IntrospectionException e) {
			throw new FacesException("Class " + component.getClass().getName() +
					" doesn't have a write method for attribute " + name);
		} catch (Exception e) {
			throw new FacesException("Failed to set attribute " + name + " to component " +
					component.getClass().getName(), e);
		}
	}
	
	public UISelectItems createUISelectItems(FacesContext context) {
		return (UISelectItems)createComponent(context, UISelectItems.COMPONENT_TYPE);
	}
	
	public void clear() {
		getAttributes().clear();
	}
	
	public UIMenu createMenu(FacesContext context) {
		UIMenu menu = (UIMenu)createComponent(context, UIMenu.COMPONENT_TYPE);
		
		Object action = getAttributes().get("action");
		
		if (isMethodExpression(action)) {
			MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(
					context.getELContext(), (String)action, Object.class, new Class[] {UIComponent.class});
			
			setMethodExpression(expression, menu, "menuAction");
		}
		
		setActionListenerExpression(context, menu);
		menu.getAttributes().put("isNew", true);
		
		return menu;
	}
	
	public UICommandMenuItem createCommandMenuItem(FacesContext context) {
		UICommandMenuItem menuItem = (UICommandMenuItem)createComponent(context, UICommandMenuItem.COMPONENT_TYPE);
		
		setActionExpression(context, menuItem);
		setActionListenerExpression(context, menuItem);
		
		return menuItem;
	}

	private void setActionListenerExpression(FacesContext context,
			UICommand command) {
		Object actionListener = getAttributes().get("actionListener");
		if (isMethodExpression(actionListener)) {
			MethodExpression expression = context.getApplication().getExpressionFactory().createMethodExpression(
					context.getELContext(), (String)actionListener, Void.class, new Class[] {ActionEvent.class});
			
			setMethodExpression(expression, command, "actionListener");
		}
	}

	public UILinkMenuItem createLinkMenuItem(FacesContext context) {
		return (UILinkMenuItem)createComponent(context, UILinkMenuItem.COMPONENT_TYPE);
	}
}