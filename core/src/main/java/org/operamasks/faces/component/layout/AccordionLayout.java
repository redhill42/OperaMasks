/*
 * $Id: AccordionLayout.java,v 1.5 2008/03/11 03:21:00 lishaochuan Exp $
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
package org.operamasks.faces.component.layout;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.layout.impl.UIAccordionLayout代替
 */
@Deprecated
public class AccordionLayout extends LayoutManagerSupport{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.AccordionLayout";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.LayoutManager";
    public static final String RENDERER_TYPE = "org.operamasks.faces.layout.AccordionLayout";

    public AccordionLayout() {
        setRendererType( RENDERER_TYPE ) ;
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    private String jsvar;
    private String container;
    private Boolean draggable ;
    private Boolean fitHeight ;
    private String initialHeight ;
    private String desktop ;
    private Boolean fitToFrame ;
    private Boolean fitContainer ;

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

    public String getContainer() {
        if (this.container != null) {
            return this.container;
        }
        ValueExpression ve = getValueExpression("container");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public Boolean getDraggable() {
        if (this.draggable != null) {
            return this.draggable;
        }
        ValueExpression ve = getValueExpression("draggable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDraggable(Boolean draggable) {
        this.draggable = draggable;
    }

    public Boolean getFitHeight() {
        if (this.fitHeight != null) {
            return this.fitHeight;
        }
        ValueExpression ve = getValueExpression("fitHeight");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFitHeight(Boolean fitHeight) {
        this.fitHeight = fitHeight;
    }

    public String getInitialHeight() {
        if (this.initialHeight != null) {
            return this.initialHeight;
        }
        ValueExpression ve = getValueExpression("initialHeight");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setInitialHeight(String initialHeight) {
        this.initialHeight = initialHeight;
    }

    public String getDesktop() {
        if (this.desktop != null) {
            return this.desktop;
        }
        ValueExpression ve = getValueExpression("desktop");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDesktop(String desktop) {
        this.desktop = desktop;
    }

    public Boolean getFitToFrame() {
        if (this.fitToFrame != null) {
            return this.fitToFrame;
        }
        ValueExpression ve = getValueExpression("fitToFrame");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFitToFrame(Boolean fitToFrame) {
        this.fitToFrame = fitToFrame;
    }

    public Boolean getFitContainer() {
        if (this.fitContainer != null) {
            return this.fitContainer;
        }
        ValueExpression ve = getValueExpression("fitContainer");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFitContainer(Boolean fitContainer) {
        this.fitContainer = fitContainer;
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar         ,
            container     ,
            draggable     ,
            fitHeight     ,
            initialHeight ,
            desktop       ,
            fitToFrame    ,
            fitContainer  
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar         = (String) values[i++];
        container     = (String) values[i++];
        draggable     = (Boolean)values[i++];
        fitHeight     = (Boolean)values[i++];
        initialHeight = (String) values[i++];
        desktop       = (String) values[i++];
        fitToFrame    = (Boolean)values[i++];
        fitContainer  = (Boolean)values[i++];
    }

}
