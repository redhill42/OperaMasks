/*
 * $Id: UIToolBar.java,v 1.2 2007/12/11 04:20:12 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.component.widget;

import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.component.widget.toolbar.AddToolBarItem;
import org.operamasks.faces.component.widget.toolbar.ToolBarStateChange;
import org.operamasks.faces.util.FacesUtils;

public class UIToolBar extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.ToolBar";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.ToolBar";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.ToolBar";
    
    private String jsvar;
    private String style;
    private String styleClass;
    private String resourceId;
    private List<ToolBarStateChange> changes;

    public UIToolBar() {
		setRendererType(RENDERER_TYPE);
	}
    
    public UIToolBar(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
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
    
    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            style,
            styleClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    public void addItem(UIMenu menu) {
    	addItem((UIComponent)menu);
    }
    
    public void addItem(UISeparator separator) {
    	addItem((UIComponent)separator);
    }
    
    public void addItem(UICombo combo) {
    	addItem((UIComponent)combo);
    }
    
    public void addItem(UICommand command) {
    	addItem((UIComponent)command);
    }
    
    private void addItem(UIComponent component) {
    	if (FacesUtils.currentPhase() == PhaseId.INVOKE_APPLICATION) {
    		getChanges().add(new AddToolBarItem(this, component));
    		getChildren().add(component);
    	}
    }
    
/*    public void removeItem(UIComponent item) {
    	for (int i = 0; i < getChildren().size(); i++) {
    		UIComponent component = getChildren().get(i);
    		
    		if (component.getId().equals(item.getId())) {
    			getChildren().remove(i);
    			
    			if (FacesUtils.currentPhase() == PhaseId.INVOKE_APPLICATION)
    	    		changes.add(new RemoveToolBarItem(this, component));
    		}
    	}
    }*/

	public List<ToolBarStateChange> getChanges() {
		if (changes == null)
			changes = new ArrayList<ToolBarStateChange>();
		
		return changes;
	}

	public void setChanges(List<ToolBarStateChange> changes) {
		this.changes = changes;
	}
}