/*
 * $Id: UISimpleDataGrid.java,v 1.2 2007/12/11 04:20:13 jacky Exp $
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

package org.operamasks.faces.component.widget.grid;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.page.PagedUIData;
import org.operamasks.faces.util.FacesUtils;

public class UISimpleDataGrid extends PagedUIData {
	public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.SimpleDataGrid";
	public static final String RENDERER_TYPE = "org.operamasks.faces.widget.SimpleDataGrid";

	public UISimpleDataGrid() {
		setRendererType(RENDERER_TYPE);
	}

	public UISimpleDataGrid(UIComponent parent) {
		this();
		FacesUtils.createComponent(parent, this);
	}

	private String jsvar;

	public String getJsvar() {
		if (this.jsvar != null) {
			return this.jsvar;
		}
		ValueExpression ve = getValueExpression("jsvar");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		} else {
			return null;
		}
	}

	public void setJsvar(String jsvar) {
		this.jsvar = jsvar;
	}

	private String style;

	public String getStyle() {
		if (this.style != null) {
			return this.style;
		}
		ValueExpression ve = getValueExpression("style");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		} else {
			return "";
		}
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	private int[] selections;

	public int[] getSelections() {
		if (this.selections != null) {
			return this.selections;
		}
		ValueExpression ve = getValueExpression("selections");
		if (ve != null) {
			return (int[]) ve.getValue(getFacesContext().getELContext());
		} else {
			return null;
		}
	}

	public void setSelections(int[] selections) {
		this.selections = selections;
	}

	private Boolean showSelectionColumn;

	public Boolean getShowSelectionColumn() {
		if (this.showSelectionColumn != null) {
			return this.showSelectionColumn;
		}
		
		ValueExpression ve = getValueExpression("showSelectionColumn");
		if (ve != null) {
			return (Boolean) ve.getValue(getFacesContext().getELContext());
		} else {
			return false;
		}
	}

	public void setShowSelectionColumn(Boolean showSelectionColumn) {
		this.showSelectionColumn = showSelectionColumn;
	}
	
	private int selectionColumnIndex;

	public int getSelectionColumnIndex() {
		if(this.selectionColumnIndex >= 0){
			return this.selectionColumnIndex;
		}
		ValueExpression ve = getValueExpression("selectionColumnIndex");
		if (ve != null) {
			return (Integer) ve.getValue(getFacesContext().getELContext());
		} else {
			return 0;
		}
	}

	public void setSelectionColumnIndex(int selectionColumnIndex) {
		this.selectionColumnIndex = selectionColumnIndex;
	}
	
	public Object saveState(FacesContext context) {
		return new Object[] { 
				super.saveState(context), 
				jsvar, 
				selectionColumnIndex,
				showSelectionColumn
		};
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		int i = 0;
		super.restoreState(context, values[i++]);
		jsvar = (String) values[i++];
		selectionColumnIndex = (Integer)values[i++];
		showSelectionColumn = (Boolean)values[i++];
	}
	
	public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        try {
            updateModel(context);
        } catch (RuntimeException ex) {
            context.renderResponse();
            throw ex;
        }
    }

	public void updateModel(FacesContext context) {
		ValueExpression ve;
		if ((ve = getValueExpression("selections")) != null) {
			try {
				ve.setValue(context.getELContext(), selections);
				selections = null;
			} catch (Exception ex) {
				FacesMessage message = new FacesMessage(ex.getMessage());
				context.addMessage(getClientId(context), message);
			}
		}
	}

}
