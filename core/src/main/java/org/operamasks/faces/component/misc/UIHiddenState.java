/*
 * $Id: UIHiddenState.java,v 1.2 2007/12/21 03:00:24 daniel Exp $
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
package org.operamasks.faces.component.misc;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.html.FormRenderer;
import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.util.FacesUtils;

public class UIHiddenState extends UIComponentBase
{
    private PhaseId phaseId;
    private Map<String,ValueExpression> states = new HashMap<String,ValueExpression>();

    public UIHiddenState() {
        setRendererType(null);
    }

    public String getFamily() {
        return null;
    }

    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addAttachedState(String name, ValueExpression binding) {
        this.states.put(name, binding);
    }

    public void clearAttachedStates() {
        this.states.clear();
    }

    public void decode(FacesContext context) {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        for (String name : this.states.keySet()) {
            String param = paramMap.get(name);
            if (param != null) {
                ValueExpression binding = this.states.get(name);
                Object value = getConvertedValue(context, binding, param);
                binding.setValue(context.getELContext(), value);
            }
        }
    }

    public void encodeAll(FacesContext context) {
        if (this.phaseId != PhaseId.RENDER_RESPONSE) {
            return; // state stalled
        }

        if (this.states.size() > 0) {
            ResponseWriter out = context.getResponseWriter();
            if (out instanceof AjaxResponseWriter) {
                encodeAjaxValues(context, (AjaxResponseWriter)out);
            } else if (out instanceof AjaxHtmlResponseWriter) {
                encodeAjaxHtmlValues(context, (AjaxHtmlResponseWriter)out);
            } else {
                attachStateInForms(context);
            }
        }
    }

    private void encodeAjaxValues(FacesContext context, AjaxResponseWriter out) {
        for (String name : this.states.keySet()) {
            ValueExpression binding = this.states.get(name);
            String value = getFormattedValue(context, binding);
            out.addRequestParameter(name, value);
        }
    }

    private void encodeAjaxHtmlValues(FacesContext context, AjaxHtmlResponseWriter out) {
        for (String name : this.states.keySet()) {
            ValueExpression binding = this.states.get(name);
            String value = getFormattedValue(context, binding);
            out.addRequestParameter(name, value);
        }
    }

    private void attachStateInForms(FacesContext context) {
        Iterator<UIComponent> kids = FacesUtils.createChildrenIterator(context.getViewRoot(), false);
        while (kids.hasNext()) {
            UIComponent comp = kids.next();
            if ((comp instanceof UIForm) && comp.isRendered()) {
                addHiddenFields(context, (UIForm)comp);
            }
        }
    }

    private void addHiddenFields(FacesContext context, UIForm form) {
        for (String name : this.states.keySet()) {
            ValueExpression binding = this.states.get(name);
            String value = getFormattedValue(context, binding);
            FormRenderer.addHiddenFields(form, name, value);
        }
    }

    private Object getConvertedValue(FacesContext context, ValueExpression binding, String value) {
        Class<?> type = binding.getType(context.getELContext());
        return Coercion.coerce(value, type);
    }

    private String getFormattedValue(FacesContext context, ValueExpression binding) {
        Object value = binding.getValue(context.getELContext());
        return (value == null) ? null : value.toString();
    }
}
