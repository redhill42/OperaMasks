/*
 * $Id: ForEach.java,v 1.4 2007/12/11 04:20:13 jacky Exp $
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
 * 
 */
package org.operamasks.faces.component.widget.invisible;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;

import org.operamasks.faces.render.ajax.AjaxResponseWriter;

public class ForEach extends UIData
{
    public static final String COMPONENT_TYPE   = "org.operamasks.faces.widget.ForEach";
    public static final String RENDERER_TYPE    = "org.operamasks.faces.widget.ForEach";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Invisible";
    public ForEach() {
        setRendererType(RENDERER_TYPE);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    private String  indexVar;
    private Integer step;
    private Map<String,SavedState> saved = new HashMap<String,SavedState>();
    private boolean resetId;

    public String getIndexVar() {
        if (this.indexVar != null) {
            return this.indexVar;
        }
        ValueExpression ve = getValueExpression("indexVar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setIndexVar(String indexVar) {
        this.indexVar = indexVar;
    }

    public Integer getStep() {
        if (this.step != null) {
            return this.step;
        }
        ValueExpression ve = getValueExpression("step");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setStep(Integer step) {
        this.step = step;
    }
    

    @Override
    public void queueEvent(FacesEvent event) {
        if (event == null) {
            throw new NullPointerException();
        }
        UIComponent parent = getParent();
        if (parent == null) {
            throw new IllegalStateException();
        } else {
            parent.queueEvent(event);
        }
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            indexVar ,
            step,
            saved
        };
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        indexVar    = (String) values[i++];
        step        = (Integer) values[i++];
        saved       = (Map<String, SavedState>) values[i++];

    }
    
    @Override
    public void setRowIndex(int rowIndex) {
        super.setRowIndex(rowIndex);
        saveDescendantState();
        if (indexVar != null) {
            Map<String, Object> requestMap =
                 getFacesContext().getExternalContext().getRequestMap();
            if (getDataModel().isRowAvailable()) {
                requestMap.put(indexVar, super.getRowIndex());
            } else {
                requestMap.remove(indexVar);
                super.setRowIndex(-1);
            }
        }
        restoreDescendantState();
    }
    
    private void saveDescendantState() {
        FacesContext context = getFacesContext();
        Iterator kids = getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            saveDescendantState(kid, context);
        }

    }


    /**
     * <p>Save state information for the specified component and its
     * descendants.</p>
     *
     * @param component Component for which to save state information
     * @param context {@link FacesContext} for the current request
     */
    private void saveDescendantState(UIComponent component,
                                     FacesContext context) {

        // Save state for this component (if it is a EditableValueHolder)
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            SavedState state = saved.get(clientId);
            if (state == null) {
                state = new SavedState();
                saved.put(clientId, state);
            }
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        }

        // Save state for children of this component
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            saveDescendantState((UIComponent) kids.next(), context);
        }
        // Save state for facets of this component
        Iterator facetNames = component.getFacets().keySet().iterator();
        while (facetNames.hasNext()) {
            UIComponent c = component.getFacet( (String) facetNames.next() );
            if (c!=null)
                saveDescendantState(c, context);
        }

    }
    
    public String getClientId(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
        String baseClientId = super.getClientId(context);
        int rowIndex = super.getRowIndex();
        String ext = "" + NamingContainer.SEPARATOR_CHAR + rowIndex;
        if(baseClientId.endsWith(ext)) {
            baseClientId = baseClientId.substring(0,baseClientId.length()-ext.length());;
        }
        if(context.getResponseWriter() instanceof AjaxResponseWriter && getRows() != 0) {
            rowIndex = rowIndex % getRows();
        }
        if (rowIndex >= 0) {
            return (baseClientId + NamingContainer.SEPARATOR_CHAR + rowIndex);
        } else {
            return (baseClientId);
        }
    }


    private void restoreDescendantState() {
        FacesContext context = getFacesContext();
        Iterator kids = getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            restoreDescendantState(kid, context);
        }

    }


    /**
     * <p>Restore state information for the specified component and its
     * descendants.</p>
     *
     * @param component Component for which to restore state information
     * @param context {@link FacesContext} for the current request
     */
    private void restoreDescendantState(UIComponent component,
                                        FacesContext context) {

        // Reset the client identifier for this component
        String id = component.getId();
        component.setId(id); // Forces client id to be reset

        // Restore state for this component (if it is a EditableValueHolder)
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            SavedState state = saved.get(clientId);
            if (state == null) {
                state = new SavedState();
            }
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            // This *must* be set after the call to setValue(), since
            // calling setValue() always resets "localValueSet" to true.
            input.setLocalValueSet(state.isLocalValueSet());
        }

        // Restore state for children of this component
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            restoreDescendantState((UIComponent) kids.next(), context);
        }
        // Restore state for facets of this component
        Iterator facetNames = component.getFacets().keySet().iterator();
        while (facetNames.hasNext()) {
            UIComponent c = component.getFacet( (String) facetNames.next() );
            if (c!=null)
                restoreDescendantState(c, context);
        }

    }

    public boolean isResetId() {
        return resetId;
    }

    public void setResetId(boolean resetId) {
        this.resetId = resetId;
    }
}

class SavedState implements Serializable {

    private static final long serialVersionUID = 2920252657338389849L;
    private Object submittedValue;

    Object getSubmittedValue() {
        return (this.submittedValue);
    }
    void setSubmittedValue(Object submittedValue) {
        this.submittedValue = submittedValue;
    }

    private boolean valid = true;
    boolean isValid() {
        return (this.valid);
    }
    void setValid(boolean valid) {
        this.valid = valid;
    }

    private Object value;
    Object getValue() {
        return (this.value);
    }
    public void setValue(Object value) {
        this.value = value;
    }

    private boolean localValueSet;
    boolean isLocalValueSet() {
        return (this.localValueSet);
    }
    public void setLocalValueSet(boolean localValueSet) {
        this.localValueSet = localValueSet;
    }

    public String toString() {
        return ("submittedValue: " + submittedValue + 
                " value: " + value + 
                " localValueSet: " + localValueSet);
    }

}
