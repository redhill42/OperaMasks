/*
 * $Id: UIDataView.java,v 1.7 2008/01/09 08:52:45 jacky Exp $
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

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.page.PagedUIData;
import org.operamasks.faces.util.FacesUtils;

public class UIDataView extends PagedUIData
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.DataView";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.View";
    public static final String REQUEST_DATA_PARAM = "_ajaxDataProxy_view";
    public static final String COMPONENT_KEY = "_viewComponent";
    public static final Object SERVER_ROW_INDEX = "_serverRowIndex";
    public static final String FIRST_ROW_KEY = "_grid_first_row";
    public static final String ROWS_KEY = "_grid_rows";

    public UIDataView() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UIDataView(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    private String jsvar;
    private String template;
    private String container;
    private Boolean async;

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

    public String getTemplate() {
        if (this.template != null) {
            return this.template;
        }
        ValueExpression ve = getValueExpression("template");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTemplate(String template) {
        this.template = template;
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

    public Boolean getAsync() {
        if (this.async != null) {
            return this.async;
        }
        ValueExpression ve = getValueExpression("async");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }
    /**
     * Set a flag to force reload data.
     */
    private boolean needReload;

    public boolean isNeedReload() {
        return this.needReload;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public void reload() {
        this.needReload = true;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            template,
            container
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        template = (String)values[i++];
        container = (String)values[i++];
    }
}
