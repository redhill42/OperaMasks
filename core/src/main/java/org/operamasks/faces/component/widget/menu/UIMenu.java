/*
 * $Id: UIMenu.java,v 1.5 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.component.widget.menu;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.event.MenuActionEvent;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

public class UIMenu extends UICommand
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Menu";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Menu";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Menu";

    public static final Class[] MENUITEM_TYPES = new Class[]{
        UICommandMenuItem.class,
        UILinkMenuItem.class,
        UITextMenuItem.class,
        UICheckMenuItem.class,
        UIRadioMenuItem.class,
        UISeparator.class
    };
    
    public UIMenu() {
        setRendererType(RENDERER_TYPE);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private String jsvar;

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

    private MethodExpression menuAction;

    public MethodExpression getMenuAction() {
        return menuAction;
    }

    public void setMenuAction(MethodExpression menuAction) {
        this.menuAction = menuAction;
    }

    // For sub menu

    private String label;

    public String getLabel() {
        if (this.label != null) {
            return this.label;
        }
        ValueExpression ve = getValueExpression("label");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String image;

    public String getImage() {
        if (this.image != null) {
            return this.image;
        }
        ValueExpression ve = getValueExpression("image");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }

    private Boolean disabled;

    public boolean isDisabled() {
        if (this.disabled != null) {
            return this.disabled;
        }
        ValueExpression ve = getValueExpression("disabled");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private String style;

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

    private String styleClass;

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

    private String disabledClass;

    public String getDisabledClass() {
        if (this.disabledClass != null) {
            return this.disabledClass;
        }
        ValueExpression ve = getValueExpression("disabledClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDisabledClass(String disabledClass) {
        this.disabledClass = disabledClass;
    }

    private String activeClass;

    public String getActiveClass() {
        if (this.activeClass != null) {
            return this.activeClass;
        }
        ValueExpression ve = getValueExpression("activeClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setActiveClass(String activeClass) {
        this.activeClass = activeClass;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, menuAction),
            jsvar,
            label,
            image,
            disabled,
            style,
            styleClass,
            disabledClass,
            activeClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        menuAction = (MethodExpression)restoreAttachedState(context, values[i++]);
        jsvar = (String)values[i++];
        label = (String)values[i++];
        image = (String)values[i++];
        disabled = (Boolean)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        disabledClass = (String)values[i++];
        activeClass = (String)values[i++];
    }

    @Override
    public void queueEvent(FacesEvent event) {
        super.queueEvent(event);

        // wraps with menu action event
        if (event instanceof MenuActionEvent) {
            UIComponent item = ((MenuActionEvent)event).getItemComponent();
            MenuActionEvent menuEvent = new MenuActionEvent(this, item);
            invokeAction(menuEvent);
        } else if (event instanceof ActionEvent) {
            MenuActionEvent menuEvent = new MenuActionEvent(this, event.getComponent());
            invokeAction(menuEvent);
        }
    }

    private void invokeAction(MenuActionEvent menuEvent) {
        // Invoke the menu action method
        MethodExpression action = getMenuAction();
        if (action != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent item = menuEvent.getItemComponent();
            String fromAction = action.getExpressionString();
            String outcome = null;

            try {
                Object result = action.invoke(context.getELContext(), new Object[]{item});
                if (result != null) {
                    outcome = result.toString();
                }
            } catch (ELException ex) {
                throw new FacesException(action.getExpressionString() + ": " + ex.getMessage(), ex);
            }

            if (outcome != null) {
                // Perform the navigation handling
                NavigationHandler nh = context.getApplication().getNavigationHandler();
                nh.handleNavigation(context, fromAction, outcome);
                context.renderResponse(); // skip remaining phase in the lifecycle
            }
        }
    }
    
    public void addMenu(UIMenu menu) {
        addMenu(0, menu);
    }
    
    @SuppressWarnings("static-access")
    public void addMenu(int index, UIMenu menu) {
        if (menu == null) {
            throw new NullPointerException("the menu to be added is null");
        }
        if (index < 0) {
            index = 0;
        }
        menu.getAttributes().put("isNew", Boolean.TRUE);
        getChildren().add(menu);
    }
    
    public void addMenuItem(UIComponent item) {
        addMenuItem(0, item);
    }
    
    @SuppressWarnings("static-access")
    public void addMenuItem(int index, UIComponent item) {
        validateMenuItem(item);
        if (index < 0) {
            index = 0;
        }
        getChildren().add(index, item);
    }

    public void removeMenu(UIMenu menu) {
        getChildren().remove(menu);
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(context, this));
        cm.getAttributes().put("itemVar", FacesUtils.getJsvar(context, menu)+"_item");
        cm.invoke(context, "removeMenu", this);
    }

    public void removeMenuItem(UIComponent item) {
        validateMenuItem(item);
        getChildren().remove(item);
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(context, this));
        cm.getAttributes().put("itemVar", FacesUtils.getJsvar(context, item));
        cm.invoke(context, "removeMenuItem", this);
    }
    
    public void removeAll() {
        getChildren().clear();
        FacesContext context = FacesContext.getCurrentInstance();
        ComponentOperationManager cm = ComponentOperationManager.getInstance(context);
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(context, this));
        cm.invoke(context, "removeAll", this);
    }

    @SuppressWarnings("unchecked")
    private void validateMenuItem(UIComponent item) {
        if (item == null) {
            throw new NullPointerException("the menuItem to be added is null");
        }
        boolean valid = false;
        Class itemClass = item.getClass();
        for (Class clz : MENUITEM_TYPES) {
            if(itemClass.isAssignableFrom(clz)) {
                valid = true;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("the menuItem's type is invlid");
        }
    }
}
